package com.w2meter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.w2meter.entity.UserDetails;

@Repository
@SuppressWarnings("unchecked")
public interface UserRepository  extends CrudRepository<UserDetails, Long>{

	public UserDetails save(UserDetails userInfo);
	public UserDetails findByUserId(Long userId);
	
}
