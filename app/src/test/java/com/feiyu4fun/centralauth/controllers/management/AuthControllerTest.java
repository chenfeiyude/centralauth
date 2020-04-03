package com.feiyu4fun.centralauth.controllers.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import com.feiyu4fun.centralauth.dtos.management.UserDTO;
import com.feiyu4fun.centralauth.models.request.AuthUser;
import org.apache.http.auth.AuthenticationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import com.feiyu4fun.centralauth.interfaces.management.AuthService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthControllerTest {

	@MockBean
	private AuthService authService;
	
	@Autowired
	private AuthController authController;
	
	private AuthUser authUser;
	private MockHttpServletRequest request = new MockHttpServletRequest();
	private UserDTO userDTO;
	
	@Before
	public void setUp() throws Exception {
		authUser = new AuthUser();
		authUser.setUsername("chenfeiyu0402@gmail.com");
		authUser.setPassword("123");
		
		userDTO = new UserDTO();
		userDTO.setUsername(authUser.getUsername());
	}
	
	@Test
	public void contextLoad() throws Exception {
		assertThat(authController).isNotNull();
	}
	
	@Test
	public void loginSuccess() throws AuthenticationException {
		Mockito.when(authService.login(authUser.getUsername(), authUser.getPassword())).thenReturn(userDTO);
		UserDTO userDTO = authController.login(authUser, request);
		assertNotNull(userDTO);
		assertEquals(userDTO.getUsername(), authUser.getUsername());
	}

	@Test(expected = AuthenticationException.class)
	public void loginFailed() throws AuthenticationException {
		AuthUser fakeUser = new AuthUser();
		Mockito.when(authService.login(fakeUser.getUsername(), fakeUser.getPassword())).thenThrow(new AuthenticationException());
		authController.login(new AuthUser(), request);
	}
}
