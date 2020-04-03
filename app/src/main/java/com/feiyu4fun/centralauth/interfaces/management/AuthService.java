package com.feiyu4fun.centralauth.interfaces.management;



import javax.servlet.http.HttpServletRequest;

import com.feiyu4fun.centralauth.dtos.management.UserDTO;
import org.apache.http.auth.AuthenticationException;

public interface AuthService {
	UserDTO authenticate(HttpServletRequest request);
	void logout(HttpServletRequest request);
	UserDTO register(String username, String password);
	boolean checkEmail(String username);
	UserDTO login(String username, String password) throws AuthenticationException;
	UserDTO getUserFromSession(HttpServletRequest request);
}
