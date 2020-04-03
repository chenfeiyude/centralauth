package com.feiyu4fun.centralauth.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CentralAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication auth) {
    	if(!auth.isAuthenticated()){
    		// this exception will trigger CartellAuthenticationEntryPoint
    		throw new BadCredentialsException("Authorization Required");
        }
        
    	return (UsernamePasswordAuthenticationToken)auth;
    }
 
    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
