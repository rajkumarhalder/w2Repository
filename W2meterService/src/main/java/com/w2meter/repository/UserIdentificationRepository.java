package com.w2meter.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.w2meter.entity.UserIdentification;
@Repository
@SuppressWarnings("unchecked")
public interface UserIdentificationRepository extends CrudRepository<UserIdentification, Long>{
	
	public UserIdentification save(UserIdentification userIdentification);
	public UserIdentification findByCountryCodeInAndMobileNoIn(String countryCode, Long mobileNo);
	public List<UserIdentification> findByMobileNoWithCountryCodeIn(List<String> filteredContact);
	public List<UserIdentification> findByCountryCodeIn(String countryCode);
	public List<UserIdentification> findBymobileNoIn(List<Long> mobileNo);

}
