package com.w2meter.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="votedetails",schema="w2meter")
public class VoteDetails {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VOTE_SEQ")
    @SequenceGenerator(sequenceName = "w2meter.votedetails_seq", allocationSize = 1, name = "VOTE_SEQ")
	@Column(name="vote_id")
	private Long voteId;
	
	@Column(name="userid")
	private Long userId;
	@Column(name="country_code")
	private String countryCode;
	@Column(name="votevalue")
	private String voteValue;
	@Column(name="voting_date")
	private String votingDate;
	@Column(name="createdate")
	private Date createDate ;
	@Column(name="createid")
	private Long createId ;
	@Column(name="updatedate")
	private Date updateDate;
	@Column(name="updateid")
	private Long updateId;
	@Column(name="is_active")
	private int isActive ;
	public Long getVoteId() {
		return voteId;
	}
	public void setVoteId(Long voteId) {
		this.voteId = voteId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getVoteValue() {
		return voteValue;
	}
	public void setVoteValue(String voteValue) {
		this.voteValue = voteValue;
	}
	public String getVotingDate() {
		return votingDate;
	}
	public void setVotingDate(String votingDate) {
		this.votingDate = votingDate;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Long getCreateId() {
		return createId;
	}
	public void setCreateId(Long createId) {
		this.createId = createId;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Long getUpdateId() {
		return updateId;
	}
	public void setUpdateId(Long updateId) {
		this.updateId = updateId;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	
	

}
