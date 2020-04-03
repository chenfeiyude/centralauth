package com.feiyu4fun.centralauth.services.management;

import static org.junit.Assert.*;

import com.feiyu4fun.centralauth.dtos.management.UserDTO;
import com.feiyu4fun.centralauth.enums.UserState;
import org.apache.http.auth.AuthenticationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.feiyu4fun.centralauth.interfaces.management.AuthService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthServiceTest {
	
	@Autowired
	private AuthService authService;
	
	@Test
	public void loginPass() throws AuthenticationException {
		UserDTO userDTO = authService.login("chenfeiyu0402@gmail.com", "123");
		assertNotNull(userDTO);
		Assert.assertEquals(UserState.live.toString(), userDTO.getState());
	}
	
	@Test(expected = AuthenticationException.class)
	public void loginFaild() throws AuthenticationException {
		authService.login("test", "wrong password");
	}
	
	@Test
	public void register() {
		UserDTO userDTO = authService.register("test_register", "123");
		assertNotNull(userDTO);
		assertEquals(UserState.live.toString(), userDTO.getState());
	}
	
}
