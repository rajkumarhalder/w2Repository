package com.w2meter.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.w2meter.entity.GroupDetails;

@SuppressWarnings({"unchecked"})
public interface GroupDetailsRepository extends CrudRepository<GroupDetails, Long>{

	public GroupDetails save(GroupDetails groupDetails);

	public GroupDetails getGroupDetailsByGroupId(Long groupId);
	
	public List<GroupDetails> findByCreateId(Long createId);
	
	
}
