package com.w2meter.service.iml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.w2meter.entity.UserDetails;
import com.w2meter.entity.UserIdentification;
import com.w2meter.repository.UserIdentificationRepository;
import com.w2meter.repository.UserRepository;
import com.w2meter.service.UserService;


@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository ;
	
	@Autowired
	private UserIdentificationRepository userIdentificationRepository ;
	

	@Override
	public Object registerUser(UserIdentification userIdentification) {

		return userIdentificationRepository.save(userIdentification);
	}

	@Override
	public Object updateUserDetail(UserDetails userDetails) {
		return userRepository.save(userDetails);
	}
	
	@Override
	public Object findExistingUserIdentification(UserIdentification userIdentification) {
		return userIdentificationRepository.findByCountryCodeInAndMobileNoIn(userIdentification.getCountryCode(), userIdentification.getMobileNo());
	}

	@Override
	public Object getUserDetail(Long userId) {
		return userRepository.findByUserId(userId);
	}

}
