package com.w2meter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.w2meter.entity.UserIdentification;
@Repository
@SuppressWarnings("unchecked")
public interface UserIdentificationRepository extends CrudRepository<UserIdentification, Long>{
	
	public UserIdentification save(UserIdentification userIdentification);
	UserIdentification findByCountryCodeInAndMobileNoIn(String countryCode, Long mobileNo);

}
