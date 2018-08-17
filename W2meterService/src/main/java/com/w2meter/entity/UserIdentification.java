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
@Table(name="useridentification",schema="w2meter")
public class UserIdentification {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ")
    @SequenceGenerator(sequenceName = "w2meter.userdetails_seq", allocationSize = 1, name = "USER_SEQ")
	
	@Column(name="id")
	private Long id;
	@Column(name="mobile_no")
	private Long mobileNo;
	@Column(name="country_code")
	private String countryCode;
	@Column(name="current_otp")
	private String currentOtp;
	@Column(name="current_token")
	private String currentToken;
	@Column(name="mobileno_with_countrycode")
	private String mobileNoWithCountryCode;
	@Column(name="old_otp")
	private String oldOtp;
	@Column(name="deviceMac")
	private String device_mac;
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
	public Long getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(Long mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCurrentOtp() {
		return currentOtp;
	}
	public void setCurrentOtp(String currentOtp) {
		this.currentOtp = currentOtp;
	}
	public String getOldOtp() {
		return oldOtp;
	}
	public void setOldOtp(String oldOtp) {
		this.oldOtp = oldOtp;
	}
	public String getDevice_mac() {
		return device_mac;
	}
	public void setDevice_mac(String device_mac) {
		this.device_mac = device_mac;
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCurrentToken() {
		return currentToken;
	}
	public void setCurrentToken(String currentToken) {
		this.currentToken = currentToken;
	}
	public String getMobileNoWithCountryCode() {
		return mobileNoWithCountryCode;
	}
	public void setMobileNoWithCountryCode(String mobileNoWithCountryCode) {
		this.mobileNoWithCountryCode = mobileNoWithCountryCode;
	}
	
}
