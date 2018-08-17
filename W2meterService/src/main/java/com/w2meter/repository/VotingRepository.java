package com.w2meter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.w2meter.entity.VoteDetails;

@Repository
@SuppressWarnings({"unchecked"})
public interface VotingRepository extends CrudRepository<VoteDetails, Long>{
	
	public VoteDetails save(VoteDetails voteDetails);
	
	public List<VoteDetails> findByCountryCodeInAndVotingDateIn(String countryCode, Date votingDate);
	
	public List<VoteDetails> findByuserIdIn(Long userId);
	
	public List<VoteDetails> findByCountryCodeIn(String countryCode);
	
	public List<VoteDetails> findAll();
	
	public VoteDetails findByuserIdInAndVotingDateIn(Long userId,Date votingdate);
	
	public List<VoteDetails> findByuserIdIn(List<Long> userIds);

	
	
}
