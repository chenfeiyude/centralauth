package com.feiyu4fun.centralauth.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.feiyu4fun.centralauth.services.management.AuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CentralAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
	private static final String REQUEST_FROM_WEB = "web";
	
	@Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
      throws IOException, ServletException {
		String requestFrom = request.getHeader(AuthServiceImpl.Credentials.KEY_REQUEST_FROM);
		
		if(REQUEST_FROM_WEB.equals(requestFrom) )
			log.info("Request from web, we don't add WWW-Authenticate into header");
		else
			response.addHeader("WWW-Authenticate", "Basic realm=" +getRealmName());
      
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        PrintWriter writer = response.getWriter();
        writer.println("HTTP Status 401 - " + authEx.getMessage());
    }
	
	@Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("Central Auth API");
        super.afterPropertiesSet();
    }

}
