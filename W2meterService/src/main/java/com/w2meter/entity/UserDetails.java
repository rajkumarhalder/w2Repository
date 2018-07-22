package com.w2meter.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="userdetails",schema="w2meter")
public class UserDetails {

	@Id
	@Column(name="user_id")
	private Long userId ;
	@Column(name="name")
	private String name ;
	@Column(name="email")
	private String email ;
	@Column(name="mobileno")
	private Long mobileNo ;
	@Column(name="about")
	private String about;
	@Column(name="gender")
	private String gender ;
	@Column(name="dateofbirth")
	private Date dateOfBirth ;
	@Column(name="prifile_pic_url")
	private String prifilePicUrl;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(Long mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
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
	public String getPrifilePicUrl() {
		return prifilePicUrl;
	}
	public void setPrifilePicUrl(String prifilePicUrl) {
		this.prifilePicUrl = prifilePicUrl;
	}
	
	
	

}
