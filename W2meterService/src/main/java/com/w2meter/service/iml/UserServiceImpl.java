package com.w2meter.service.iml;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.w2meter.dto.AppInfo;
import com.w2meter.entity.GroupDetails;
import com.w2meter.entity.UserDetails;
import com.w2meter.entity.UserIdentification;
import com.w2meter.entity.VoteDetails;
import com.w2meter.repository.GroupDetailsRepository;
import com.w2meter.repository.UserIdentificationRepository;
import com.w2meter.repository.UserRepository;
import com.w2meter.repository.VotingRepository;
import com.w2meter.service.UserService;


@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository ;
	
	@Autowired
	private UserIdentificationRepository userIdentificationRepository ;
	
	@Autowired
	private VotingRepository votingRepository;
	
	@Autowired
	private GroupDetailsRepository groupDetailsRepository;
	
	@Override
	public Object registerUser(UserIdentification userIdentification) {

		return userIdentificationRepository.save(userIdentification);
	}

	@Override
	public Object updateUserDetail(UserDetails userDetails) {
		return userRepository.save(userDetails);
	}
	
	@Override
	public Object findExistingUserIdentification(UserIdentification userIdentification) {
		return userIdentificationRepository.findByCountryCodeInAndMobileNoIn(userIdentification.getCountryCode(), userIdentification.getMobileNo());
	}

	@Override
	public Object getUserDetail(Long userId) {
		return userRepository.findByUserId(userId);
	}

	@Override
	public void postVote(VoteDetails voteDetails) {
		voteDetails.setVotingDate(new Date());

		votingRepository.save(voteDetails);
	}
	
	@Override
	public GroupDetails saveGroup(GroupDetails groupDetails) {
		
		if(groupDetails.getGroupId()>0) {
			GroupDetails localGroupDetails=getGroupDetails(groupDetails.getGroupId());
			if(null!=groupDetails.getGroupName() && !groupDetails.getGroupName().isEmpty())
				localGroupDetails.setGroupName(groupDetails.getGroupName());
			if(null!=groupDetails.getGroupIconUrl() && !groupDetails.getGroupIconUrl().isEmpty())
				localGroupDetails.setGroupIconUrl(groupDetails.getGroupIconUrl());
			if(null!=groupDetails.getMemberIds() && !groupDetails.getMemberIds().isEmpty())
				localGroupDetails.setMemberIds(groupDetails.getMemberIds());

			return groupDetailsRepository.save(localGroupDetails);
		}
		else
		return groupDetailsRepository.save(groupDetails);

	}
	
	@Override
	public GroupDetails getGroupDetails(Long groupId) {

		return groupDetailsRepository.getGroupDetailsByGroupId(groupId);

	}
	
	@Override
	public Object getGroupDetailsByCreateId(Long createId) {

		List<Object> listOfGroup=new ArrayList<>();
		List<GroupDetails> listOfGroupDetails=groupDetailsRepository.findByCreateId(createId);

		for (GroupDetails groupDetails : listOfGroupDetails) {
			Map<String,Object> userDetailMap=new HashMap<>();
			userDetailMap.put("groupname", groupDetails.getGroupName());
			userDetailMap.put("groupicon", groupDetails.getGroupIconUrl());
			userDetailMap.put("groupid", groupDetails.getGroupId());

			listOfGroup.add(userDetailMap);
		}
		return listOfGroup;
	}
	
	@Override
	public Object getGroupDetailswithMemebers(Long groupId) {
		
		Map<String,Object> groupInfoMap=new LinkedHashMap<>();
		
		GroupDetails groupDetails=groupDetailsRepository.getGroupDetailsByGroupId(groupId);
		
		groupInfoMap.put("groupname", groupDetails.getGroupName());
		groupInfoMap.put("grouid", groupDetails.getGroupId());
		if(null!=groupDetails && null!=groupDetails.getMemberIds()){
			List<Long> memberIdList=new ArrayList<>();
			
			String[] ids=groupDetails.getMemberIds().split(",");
			
			for (String string : ids) {
				memberIdList.add(Long.parseLong(string));
			}
			
			
			List<UserDetails> listOfUserDetails=userRepository.findByUserIdIn(memberIdList);
			List<Object> listOfMemebers=new ArrayList<>();
			for (UserDetails userDetails : listOfUserDetails) {
				Map<String,String> userDetailMap=new HashMap<>();
				userDetailMap.put("name", userDetails.getName());
				userDetailMap.put("mobileNo", userDetails.getAbout());
				userDetailMap.put("profilePic", userDetails.getPrifilePicUrl());

				listOfMemebers.add(userDetailMap);
			}
			groupInfoMap.put("groupMembers", listOfMemebers);
		}
		

		return groupDetailsRepository.getGroupDetailsByGroupId(groupId);

	}
	
	
	@Override
	public Object getExistingAndNotExistingUsers(List<String> contactNoList,AppInfo info) {

		Map<String,Object> userList=new HashMap<>();

		List<String> filteredContact=new ArrayList<>();

		List<Object> existingUsersList=new ArrayList<>();

		for (String string : contactNoList) {

			if(string.startsWith("+"))
				filteredContact.add(string);
			if(string.startsWith("0")) {
				string.replaceFirst("0", "+91");
				contactNoList.add(string);
			}
			else
				contactNoList.add(info.getCountryCode()+string);

			List<UserDetails> listOfUserDetails=userRepository.findBymobileNo(contactNoList);

			for (UserDetails userDetails : listOfUserDetails) {
				Map<String,Object> contact=new HashMap<>();

				contact.put("userid", userDetails.getUserId());
				contact.put("name", userDetails.getName());
				contact.put("contactno", userDetails.getMobileNo());
				contact.put("prifilePicUrl", userDetails.getPrifilePicUrl());

				existingUsersList.add(contact);
			}
			contactNoList.removeAll(existingUsersList);	
		}

		userList.put("registeredUsersList", existingUsersList);
		userList.put("notRegisteredUsersList", contactNoList);


		return userList;
	}
	
}
