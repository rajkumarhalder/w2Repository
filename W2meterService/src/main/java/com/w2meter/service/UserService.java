package com.w2meter.service;

import java.util.List;

import com.w2meter.dto.AppInfo;
import com.w2meter.entity.GroupDetails;
import com.w2meter.entity.UserDetails;
import com.w2meter.entity.UserIdentification;
import com.w2meter.entity.VoteDetails;

public interface UserService {

	public Object registerUser(UserIdentification userIdentification);

	public Object updateUserDetail(UserDetails userDetails);

	public Object findExistingUserIdentification(UserIdentification userIdentification);

	public Object getUserDetail(Long userId);

	public Object postVote(VoteDetails voteDetails, AppInfo appInfo);

	public GroupDetails saveGroup(GroupDetails groupDetails, String operationFlag, AppInfo info);

	public GroupDetails getGroupDetails(Long groupId);

	public Object getGroupDetailswithMemebers(Long groupId);

	public Object getGroupDetailsByCreateId(Long createId);

	public Object getExistingAndNotExistingUsers(List<String> contactNoList,AppInfo info);

	public void updateGroupMembers(GroupDetails groupDetails, String operationFlag, AppInfo info);

	public Object getGraphData(AppInfo info);

	public Object getGroupDetailsByUserId(Long userId);

	public void deleteGroup(Long groupId, AppInfo info);

	public List<UserDetails> getWorldUsers(AppInfo info);

	public List<UserDetails> getMyCountryUsers(AppInfo info); 
}
