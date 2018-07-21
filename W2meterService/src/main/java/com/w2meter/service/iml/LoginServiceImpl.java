package com.w2meter.service.iml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.w2meter.entity.UserInfo;
import com.w2meter.repository.LoginRepository;
import com.w2meter.service.LoginService;


@Service
public class LoginServiceImpl implements LoginService{

	@Autowired
	private LoginRepository loginRepository ;


	@Override
	public Long registerUser(UserInfo userInfo) {
		
		Long userId=Long.valueOf(userInfo.getMobileNumber());
		userInfo.setUserId(userId);
		userInfo=loginRepository.save(userInfo);
		return userInfo.getUserId();
	}

	@Override
	public UserInfo getUser(Long userId) {
		return loginRepository.findByuserId(userId);
	}

	@Override
	public Long updateUser(UserInfo userInfo) {
		Long userId=Long.valueOf(userInfo.getMobileNumber());
		
		UserInfo userInfoExistance=loginRepository.findByuserId(userId);
		
		userInfoExistance.setDateofbirth(userInfo.getDateofbirth());
		userInfoExistance.setGender(userInfo.getGender());
		userInfoExistance.setName(userInfo.getName());
		userInfoExistance.setSurname(userInfo.getSurname());
		
		userInfo=loginRepository.save(userInfoExistance);
		return userInfo.getUserId();
	}

}
