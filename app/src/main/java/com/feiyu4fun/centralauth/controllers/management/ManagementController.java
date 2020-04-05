package com.feiyu4fun.centralauth.controllers.management;

import com.feiyu4fun.centralauth.dtos.management.UserDTO;
import com.feiyu4fun.centralauth.interfaces.management.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value="/api/management")
@Validated
@Slf4j
public class ManagementController {
	
	@Autowired
	private AuthService authService;
	
	/**
	 * api/management/user/get
	 *
	 * @param request
	 * @return user json
	 */
	@RequestMapping(method=RequestMethod.GET, value="/user/get")
    public UserDTO getUser(HttpServletRequest request) {
		return authService.getUserFromSession(request);
	}
}
