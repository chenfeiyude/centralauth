package com.feiyu4fun.centralauth.services.management;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.feiyu4fun.centralauth.dtos.management.UserDTO;
import com.feiyu4fun.centralauth.enums.UserState;
import com.feiyu4fun.centralauth.interfaces.daos.management.UserJpaDAO;
import com.feiyu4fun.centralauth.interfaces.management.AuthService;

@Service("authService")
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

	public class Credentials {
		
		public static final String KEY_API_KEY = "api-key";
		public static final String KEY_TOKEN = "token";
		public static final String KEY_USERNAME = "username";
		public static final String KEY_REQUEST_FROM = "request-from";
		
		private String login;
		private String password;

		public Credentials(String login, String password) {
			this.login = login;
			this.password = password;
		}
		
		public String getLogin() {
			return login;
		}
		public String getPassword() {
			return password;
		}
	}
	
	@Autowired
	private UserJpaDAO userJpaDAO;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDTO register(String username, String password) {
		UserDTO userDTO = userJpaDAO.findByUsername(username);
		if (userDTO != null) {
			throw new IllegalArgumentException("This username has already been registered");
		}

		userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(passwordEncoder.encode(password));
		userDTO.setState(UserState.live.toString());// TODO changed to validate and send validate email later
		userDTO.generateToken();
		userDTO.appendLog("Register new account");
		userJpaDAO.save(userDTO);
		
		return userDTO;
	}

	@Override
	public UserDTO login(String username, String password) throws AuthenticationException {
		UserDTO userDTO = userJpaDAO.findByUsername(username);
		if (userDTO == null || !userDTO.isLive()) {
			throw new AuthenticationException("Invalid username");
		}
		else if (!passwordEncoder.matches(password, userDTO.getPassword())) {
			throw new AuthenticationException("username and password not match");
		}
		userDTO.generateToken();
		userDTO.setLastLogin(LocalDateTime.now());
		userJpaDAO.save(userDTO);
		
		return userDTO;
	}

	@Override
	public UserDTO authenticate(HttpServletRequest request) {
		Credentials cred = getCredentialsBasicAuthentication(request);
		String ip = request.getRemoteAddr();
		if (cred==null) {
			String token = request.getHeader(Credentials.KEY_TOKEN);
			String username = request.getHeader(Credentials.KEY_USERNAME);
			if (token != null && username != null) {
				log.info("User: " +username+ " token = "+token+" connects from IP: "+ip);
			}
			return validateTokenUser(username, token, request);
		}
		log.info("User: "+cred.getLogin()+" connects from IP: "+ip);
		return validateLiveUser(cred);
	}
	
	private UserDTO validateLiveUser(Credentials cred)
	{
		if(cred==null)
			return null;
		
		String username=cred.getLogin();
		if(StringUtils.isEmpty(username))
			return null;
		
		UserDTO user = userJpaDAO.findByUsername(username);
		if (user == null || !user.isLive())
		{
			log.error("validateLiveUser: Username "+username+" is disabled or couldn't be found in DB");
			return null;
		}
		
		if (!passwordEncoder.matches(cred.getPassword(), user.getPassword()))
		{
			log.info("validateLiveUser received incorrect password for: "+username);
			return null;
		}
		
		log.info("validateLiveUser Authenticated: "+username);
		return user;
	}
	
	private UserDTO validateTokenUser(String username, String token, HttpServletRequest request) {
		if(StringUtils.isEmpty(token)) 
			return null;
		UserDTO user = userJpaDAO.findByUsernameAndToken(username, token);
		if(user == null) {
			log.warn("No user found for username: " + username + " token: " + token);
		}
		else {
			HttpSession session = request.getSession();
			String sessionUsername = (String) session.getAttribute(Credentials.KEY_USERNAME);
			String sessionToken = (String) session.getAttribute(Credentials.KEY_TOKEN);
			if (!username.equals(sessionUsername) || !token.equals(sessionToken)) {
				//session not correct
				log.info("Invalid session username:" + sessionUsername + " token:" + sessionToken);
				user = null;
			}
			else {
				log.info("Found user "+user.getUsername()+" with api key "+token);
			}
			
		}
		return user;
	}
	
	private Credentials getCredentialsBasicAuthentication(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null) 
		{
			StringTokenizer st = new StringTokenizer(authHeader);
			if (st.hasMoreTokens()) 
			{
				String basic = st.nextToken();
			
				if (basic.equalsIgnoreCase("Basic")) 
				{
					try {
						String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
					
						int p = credentials.indexOf(":");
						if (p != -1) 
						{
							String login = credentials.substring(0, p).trim();
							String password = credentials.substring(p + 1).trim();
							return new Credentials(login, password);
						} 
						else 
							log.error("Invalid authentication token");
						
					} catch (UnsupportedEncodingException e) {
						log.warn("Couldn't retrieve authentication", e);
					}
				}
			}
		}
		return null;
	}

	@Override
	public UserDTO getUserFromSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			throw new SessionAuthenticationException("Session expired");
		}
		
		String username = (String) session.getAttribute(Credentials.KEY_USERNAME);
		String token = (String) session.getAttribute(Credentials.KEY_TOKEN);
		
		UserDTO userDTO = userJpaDAO.findByUsernameAndToken(username, token);
		if (userDTO == null) {
			throw new SessionAuthenticationException("Invalid username and token");
		}
		
		return userDTO;
	}

	@Override
	public boolean checkEmail(String username) {
		UserDTO userDTO = userJpaDAO.findByUsername(username);
		
		return userDTO != null && userDTO.isLive();
	}

	@Override
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			try {
				// reset token
				UserDTO userDTO = getUserFromSession(request);
				userDTO.generateToken();
				userJpaDAO.save(userDTO);
			} catch (SessionAuthenticationException e) {
				// not do any thing, expired any way
			}
			
			session.invalidate();
		}
	}
	
	
}
