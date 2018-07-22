package com.w2meter.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.w2meter.entity.UserDetails;
import com.w2meter.entity.UserIdentification;

@Repository
@SuppressWarnings("unchecked")
public interface UserRepository  extends CrudRepository<UserDetails, Long>{

	public UserDetails save(UserDetails userInfo);
	public UserDetails findByUserId(Long userId);
	public List<UserDetails> findByUserIdIn(List<Long> userId);
	public List<UserDetails> findBymobileNo(List<String> mobileNo);
	
	
}
