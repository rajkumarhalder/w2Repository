package com.w2meter.service;

import com.w2meter.entity.UserDetails;
import com.w2meter.entity.UserIdentification;

public interface UserService {

	public Object registerUser(UserIdentification userIdentification);

	public Object updateUserDetail(UserDetails userDetails);

	public Object findExistingUserIdentification(UserIdentification userIdentification);

	public Object getUserDetail(Long userId); 
}
