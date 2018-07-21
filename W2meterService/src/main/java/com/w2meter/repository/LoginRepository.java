package com.w2meter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.w2meter.entity.UserInfo;

@Repository
public interface LoginRepository  extends CrudRepository<UserInfo, Long>{

	public UserInfo save(UserInfo userInfo);
	public UserInfo findByuserId(Long id);
}
