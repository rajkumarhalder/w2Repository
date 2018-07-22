package com.w2meter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.w2meter.entity.VoteDetails;

@Repository
@SuppressWarnings({"unchecked"})
public interface VotingRepository extends CrudRepository<VoteDetails, Long>{
	
	public VoteDetails save(VoteDetails voteDetails);

}
