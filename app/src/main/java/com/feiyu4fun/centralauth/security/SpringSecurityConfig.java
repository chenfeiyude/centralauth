package com.feiyu4fun.centralauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationEntryPoint authEntryPoint;
	
	@Autowired
	private CentralAuthenticationProvider centralAuthenticationProvider;
	
	@Autowired
	private CentralAuthenticationFilter centralAuthenticationFilter;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// only apply new filter to the new spring api urls
		http.antMatcher("/api/**");
		// we have handled fixation ourself, so disable spring one
		http.sessionManagement().sessionFixation().none();
		http.csrf().disable()
//			.cors().and()
			.authorizeRequests()
			.antMatchers("/api/**").authenticated()
			.and().httpBasic().authenticationEntryPoint(authEntryPoint)
			.and().addFilterBefore(centralAuthenticationFilter, BasicAuthenticationFilter.class)
         ;   
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(centralAuthenticationProvider);
	}
	 
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring().antMatchers(
	    		"/api/auth/**",
	    		"/api/auth/login**", 
	    		"/api/auth/register**"
	    		);
	}

	// I have handled cors in nginx
	// https://howtodoinjava.com/spring5/webmvc/spring-mvc-cors-configuration/
//	@Bean
//	CorsConfigurationSource corsConfigurationSource() {
//		CorsConfiguration configuration = new CorsConfiguration();
//		configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://feiyu4fun.com", "http://www.feiyu4fun.com"));
//		configuration.setAllowedMethods(Arrays.asList("GET","POST","OPTIONS"));
//		configuration.setAllowCredentials(true);
//		configuration.setAllowedHeaders(Arrays.asList("X-Requested-With"));
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", configuration);
//		return source;
//	}
}
