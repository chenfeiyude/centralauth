package com.feiyu4fun.centralauth.dtos.management;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.feiyu4fun.centralauth.enums.UserState;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.feiyu4fun.centralauth.dtos.common.BaseDTO;

@Entity
@Table(name ="user")
@Data
public class UserDTO extends BaseDTO {
    private String uid = UUID.randomUUID().toString();;
    private String username;
    @JsonIgnore
    private String password;
    private String token;
    private String state = UserState.invalidate.toString(); // new user is invalidated by default
    @JsonIgnore
    private String log = "";
    private LocalDateTime lastLogin;
    private LocalDateTime created = LocalDateTime.now();
    
	public boolean isLive() {
		return UserState.live.toString().equals(state);
	}
	public void generateToken() {
		token = UUID.randomUUID().toString();
	}
	public void appendLog(String log) {
		
		this.log += "\n" + LocalDateTime.now() + " " + log + "\n";
	}
}
