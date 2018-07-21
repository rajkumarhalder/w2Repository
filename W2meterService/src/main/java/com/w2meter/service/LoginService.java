package com.w2meter.service;

import com.w2meter.entity.UserInfo;

public interface LoginService {

	public Long registerUser(UserInfo userInfo); 
	public UserInfo getUser(Long userId);
	public Long updateUser(UserInfo userInfo); 
}
