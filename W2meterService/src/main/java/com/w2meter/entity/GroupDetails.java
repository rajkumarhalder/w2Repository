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
@Table(name="groupdetails",schema="w2meter")
public class GroupDetails {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_SEQ")
    @SequenceGenerator(sequenceName = "w2meter.groupdetails_seq", allocationSize = 1, name = "GROUP_SEQ")
	
	@Column(name="group_id")
	private Long groupId;
	@Column(name="member_ids")
	private String memberIds;
	@Column(name="group_icon_url")
	private String groupIconUrl;
	@Column(name="group_name")
	private String groupName;
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
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	public String getMemberIds() {
		return memberIds;
	}
	public void setMemberIds(String memberIds) {
		this.memberIds = memberIds;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
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
	public String getGroupIconUrl() {
		return groupIconUrl;
	}
	public void setGroupIconUrl(String groupIconUrl) {
		this.groupIconUrl = groupIconUrl;
	}

}
