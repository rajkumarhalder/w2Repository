package com.w2meter.service.iml;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import com.w2meter.util.W2meterConstant;
import com.w2meter.util.W2meterUtil;


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

		UserDetails exisingUserDetails=(UserDetails) getUserDetail(userDetails.getUserId());
		if(null!=exisingUserDetails) {

			if(null!=userDetails.getName())
				exisingUserDetails.setName(userDetails.getName());
			if(null!=userDetails.getDateOfBirth())
				exisingUserDetails.setDateOfBirth(userDetails.getDateOfBirth());
			if(null!=userDetails.getGender())
				exisingUserDetails.setGender(userDetails.getGender());
			if(null!=userDetails.getAbout())
				exisingUserDetails.setAbout(userDetails.getAbout());
			if(null!=userDetails.getPrifilePicUrl())
				exisingUserDetails.setPrifilePicUrl(userDetails.getPrifilePicUrl());
		}
		else
			exisingUserDetails=userDetails;

		return userRepository.save(exisingUserDetails);
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
	public Object postVote(VoteDetails voteDetails, AppInfo appInfo) {

		Map<String,Object> resultMap=new HashMap<>();

		Date today=new Date();
		VoteDetails voteDetailsLocal=votingRepository.findByuserIdInAndVotingDateIn(appInfo.getUserId(), today);

		if(null!=voteDetailsLocal) {
			voteDetailsLocal.setVoteValue(voteDetails.getVoteValue());
			voteDetailsLocal.setUpdateDate(new Date());
			voteDetailsLocal.setUpdateId(appInfo.getUserId());
			votingRepository.save(voteDetailsLocal);
		}
		else
		{
			voteDetails.setVotingDate(new Date());
			voteDetails.setCreateDate(new Date());
			voteDetails.setCreateId(appInfo.getUserId());
			votingRepository.save(voteDetails);
		}

		List<VoteDetails> listCountry=null;
		List<VoteDetails> listWorld=null;
		try {
			Date date=new Date();
			listCountry = votingRepository.findByCountryCodeInAndVotingDateIn(appInfo.getCountryCode(),date );
			listWorld=votingRepository.findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("happynessOverCountey",  W2meterUtil.getStatistics(appInfo, listCountry));
		resultMap.put("happynessOverWorld",  W2meterUtil.getStatistics(appInfo, listWorld));

		return resultMap;

	}

	@Override
	public GroupDetails saveGroup(GroupDetails groupDetails, String operationFlag, AppInfo info) {

		groupDetails.setIsActive(W2meterConstant.ACTIVE_DATA);
		if(null!=groupDetails.getGroupId() && groupDetails.getGroupId()>0) {
			GroupDetails localGroupDetails=getGroupDetails(groupDetails.getGroupId());
			if(null!=groupDetails.getGroupName() && !groupDetails.getGroupName().isEmpty())
				localGroupDetails.setGroupName(groupDetails.getGroupName());
			if(null!=groupDetails.getGroupIconUrl() && !groupDetails.getGroupIconUrl().isEmpty())
				localGroupDetails.setGroupIconUrl(groupDetails.getGroupIconUrl());
			localGroupDetails.setCreateId(groupDetails.getCreateId());
			localGroupDetails.setIsActive(W2meterConstant.ACTIVE_DATA);
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
	public Object getGroupDetailsByCreateId(Long userId) {

		List<Object> listOfGroup=new ArrayList<>();
		
		//List<GroupDetails> listOfGroupDetails=(List<GroupDetails>) groupDetailsRepository.findAll();
		
		List<GroupDetails> listOfGroupDetails=groupDetailsRepository.findByCreateId(userId);

		for (GroupDetails groupDetails : listOfGroupDetails) {
			
			if(groupDetails.getIsActive()==W2meterConstant.ACTIVE_DATA) {


				List<String> list=Arrays.asList(groupDetails.getMemberIds().split(","));

				if(list.contains(String.valueOf(userId))) {
					Map<String,Object> userDetailMap=new HashMap<>();
					userDetailMap.put("groupname", groupDetails.getGroupName());
					userDetailMap.put("groupid", groupDetails.getGroupId());

					if(null!=groupDetails.getGroupIconUrl()) {
						userDetailMap.put("groupicon", W2meterConstant.SERVER_BASE_URL+groupDetails.getGroupIconUrl().replaceAll(W2meterConstant.TOMCAT_HOME, "/"));
					}
					else
						userDetailMap.put("groupicon", null);

					listOfGroup.add(userDetailMap);
				}

			
			}
		}
		return listOfGroup;
	}

	@Override
	public Object getGroupDetailswithMemebers(Long groupId) {

		Map<String,Object> groupInfoMap=new LinkedHashMap<>();

		GroupDetails groupDetails=groupDetailsRepository.getGroupDetailsByGroupId(groupId);

		groupInfoMap.put("groupname", groupDetails.getGroupName());
		groupInfoMap.put("grouid", groupDetails.getGroupId());

		if(null!=groupDetails.getGroupIconUrl()) {
			groupInfoMap.put("groupicon", W2meterConstant.SERVER_BASE_URL+groupDetails.getGroupIconUrl().replaceAll(W2meterConstant.TOMCAT_HOME, "/"));
		}
		else
			groupInfoMap.put("groupicon", null);
		if(null!=groupDetails && null!=groupDetails.getMemberIds()){
			List<Long> memberIdList=new ArrayList<>();

			String[] ids=groupDetails.getMemberIds().split(",");

			for (String string : ids) {
				memberIdList.add(Long.parseLong(string));
			}


			List<UserDetails> listOfUserDetails=userRepository.findByUserIdIn(memberIdList);
			List<Object> listOfMemebers=new ArrayList<>();
			for (UserDetails userDetails : listOfUserDetails) {
				Map<String,Object> userDetailMap=new HashMap<>();
				userDetailMap.put("name", userDetails.getName());
				userDetailMap.put("userId", userDetails.getUserId());
				userDetailMap.put("mobileNo", userDetails.getMobileNo());

				if(null!=userDetails.getPrifilePicUrl()) {
					userDetailMap.put("profilePic", W2meterConstant.SERVER_BASE_URL+userDetails.getPrifilePicUrl().replaceAll(W2meterConstant.TOMCAT_HOME, "/"));
				}
				else
					userDetailMap.put("profilePic", null);

				listOfMemebers.add(userDetailMap);
			}
			groupInfoMap.put("groupMembers", listOfMemebers);
		}


		return groupInfoMap;

	}


	@Override
	public Object getExistingAndNotExistingUsers(List<String> contactNoList,AppInfo info) {

		Map<String,Object> userList=new HashMap<>();

		List<String> filteredContact=new ArrayList<>();
		List<String> existingContact=new ArrayList<>();

		List<Object> existingUsersList=new ArrayList<>();

		for (String string : contactNoList) {

			if(string.startsWith("+"))
				filteredContact.add(string);
			else if(string.startsWith("0")) {
				string.replaceFirst("0", "+91");
				filteredContact.add(string);
			}
			else
				filteredContact.add(info.getCountryCode()+string);

		}
		
		List<UserIdentification> listOfUserIdentification=userIdentificationRepository.findBymobileNoWithCountryCodeIn(filteredContact);
		
		List<UserDetails> listOfUserDetails=userRepository.findByMobileNoIn(filteredContact);

		Map<Long,Object> phoneNoVsUserId=new HashMap<>();
		for (UserIdentification userIdentification : listOfUserIdentification) {
			phoneNoVsUserId.put(userIdentification.getId(), userIdentification.getMobileNoWithCountryCode());
		}
		
		
		for (UserDetails userDetails : listOfUserDetails) {
			
			phoneNoVsUserId.remove(userDetails.getUserId());
			Map<String,Object> contact=new HashMap<>();

			contact.put("userid", userDetails.getUserId());
			contact.put("name", userDetails.getName());
			contact.put("contactno", userDetails.getMobileNo());
			if(null!=userDetails.getPrifilePicUrl()) {
				contact.put("prifilePicUrl", W2meterConstant.SERVER_BASE_URL+userDetails.getPrifilePicUrl().replaceAll(W2meterConstant.TOMCAT_HOME, "/"));
			}
			else
				contact.put("prifilePicUrl", null);

			existingUsersList.add(contact);
			existingContact.add(userDetails.getMobileNo());
		}
		
		if(!phoneNoVsUserId.isEmpty()) {
			for (Entry<Long, Object> entry : phoneNoVsUserId.entrySet()) {
				Map<String,Object> contact=new HashMap<>();
				contact.put("userid", entry.getKey());
				contact.put("name", entry.getValue());
				contact.put("contactno", entry.getValue());
				contact.put("prifilePicUrl", null);
				
				existingUsersList.add(contact);
				existingContact.add(entry.getValue()+"");
			}
		}
		
		contactNoList.removeAll(existingContact);

		userList.put("registeredUsersList", existingUsersList);
		userList.put("notRegisteredUsersList", contactNoList);


		return userList;
	}


	@Override
	public void updateGroupMembers(GroupDetails groupDetails, String operationFlag, AppInfo info) {

		if(null!=groupDetails.getGroupId() && groupDetails.getGroupId()>0) {
			GroupDetails localGroupDetails=getGroupDetails(groupDetails.getGroupId());

			if(null!=groupDetails.getMemberIds() && !groupDetails.getMemberIds().isEmpty()) {

				String members[]=localGroupDetails.getMemberIds().split(",");

				List<String> list=Arrays.asList(members);
				Set<String> setval=new HashSet<>(list);

				if(W2meterConstant.OPERATION_FLAG_ADD.equalsIgnoreCase(operationFlag)) {
					if(!setval.contains(groupDetails.getMemberIds()))
						setval.add(groupDetails.getMemberIds());
				}
				else if(W2meterConstant.OPERATION_FLAG_REMOVE.equalsIgnoreCase(operationFlag)) {
					if(setval.contains(groupDetails.getMemberIds()))
						setval.remove(groupDetails.getMemberIds());
				}
				String currentGroupMember="";
				for (String string : setval) {
					if(!currentGroupMember.isEmpty())
						currentGroupMember=currentGroupMember+","+string;

					else
						currentGroupMember=string;
				}
				localGroupDetails.setMemberIds(currentGroupMember);
				localGroupDetails.setUpdateId(info.getUserId());
			}
			groupDetailsRepository.save(localGroupDetails);
		}

	}

	
	@Override
	public Object getGraphData(AppInfo info) {

		Map<String,Object> graphHmap=new HashMap<>();

		SimpleDateFormat sdf=new SimpleDateFormat(W2meterConstant.DATE_FORMAT);
		try {
			List<VoteDetails> listOfMydata=votingRepository.findByuserIdIn(info.getUserId());

			List<VoteDetails> listOfMyCountrydata=votingRepository.findByCountryCodeIn(info.getCountryCode());

			List<VoteDetails> listOfworld=votingRepository.findAll();

			Map<String,Object> myDataForGraph=new HashMap<>();
			if(null!=listOfMydata && !listOfMydata.isEmpty()) {

				myDataForGraph.put("noOfDays", listOfMydata.size());
				myDataForGraph.put("noOfUsers", 1);

				List<Object> listOfData=new ArrayList<>();
				for (VoteDetails voteDetails : listOfMydata) {
					Map<String,Object> myData=new HashMap<>();
					myData.put("date", sdf.format(voteDetails.getVotingDate()));
					myData.put("value", voteDetails.getVoteValue());
					listOfData.add(myData);
					myDataForGraph.put("data", listOfData);
				}
			}
			graphHmap.put("myDataForGraph", myDataForGraph);

			Map<String,Object> myCountryDataForGraph=new HashMap<>();
			if(null!=listOfMyCountrydata && !listOfMyCountrydata.isEmpty()) {

				List<Object> listOfData=new ArrayList<>();

				Set<Long> users=new HashSet<>();
				Map<String,Object> dates=new HashMap<>();
				for (VoteDetails voteDetails : listOfMyCountrydata) {
					users.add(voteDetails.getUserId());

					if(dates.containsKey(sdf.format(voteDetails.getVotingDate()))) {
						List<VoteDetails> list=(List<VoteDetails>) dates.get(sdf.format(voteDetails.getVotingDate()));
						list.add(voteDetails);
						dates.put(sdf.format(voteDetails.getVotingDate()), list);

					}
					else {
						List<VoteDetails> list=new ArrayList<>();
						list.add(voteDetails);
						dates.put(sdf.format(voteDetails.getVotingDate()), list);
					}

				}
				

				for (Entry<String, Object> entry : dates.entrySet()) {
					Map<String,Object> myData=new HashMap<>();
					myData.put("date", entry.getKey());

					BigDecimal voteValue=new BigDecimal(0);
					List<VoteDetails> list=(List<VoteDetails>) entry.getValue();
					for (VoteDetails votedetail : list) {
						voteValue=voteValue.add(new BigDecimal(votedetail.getVoteValue()));
					}

					myData.put("value", voteValue.divide(new BigDecimal(list.size()),2, RoundingMode.HALF_UP));
					listOfData.add(myData);
				}
				
				myCountryDataForGraph.put("data", listOfData);

				myCountryDataForGraph.put("noOfDays", dates.size());
				myCountryDataForGraph.put("noOfUsers", users.size());
			}
			graphHmap.put("myCountryDataForGraph", myCountryDataForGraph);

			Map<String,Object> myWorldDataForGraph=new HashMap<>();
			if(null!=listOfworld && !listOfworld.isEmpty()) {

				List<Object> listOfData=new ArrayList<>();

				Set<Long> users=new HashSet<>();
				Map<String,Object> dates=new HashMap<>();
				for (VoteDetails voteDetails : listOfworld) {
					users.add(voteDetails.getUserId());

					if(dates.containsKey(sdf.format(voteDetails.getVotingDate()))) {
						List<VoteDetails> list=(List<VoteDetails>) dates.get(sdf.format(voteDetails.getVotingDate()));
						list.add(voteDetails);
						dates.put(sdf.format(voteDetails.getVotingDate()), list);

					}
					else {
						List<VoteDetails> list=new ArrayList<>();
						list.add(voteDetails);
						dates.put(sdf.format(voteDetails.getVotingDate()), list);
					}

				}


				for (Entry<String, Object> entry : dates.entrySet()) {
					Map<String,Object> myData=new HashMap<>();
					myData.put("date", entry.getKey());

					BigDecimal voteValue=new BigDecimal(0);
					List<VoteDetails> list=(List<VoteDetails>) entry.getValue();
					for (VoteDetails votedetail : list) {
						voteValue=voteValue.add(new BigDecimal(votedetail.getVoteValue()));
					}

					myData.put("value", voteValue.divide(new BigDecimal(list.size()),2, RoundingMode.HALF_UP));
					listOfData.add(myData);
				}

				myWorldDataForGraph.put("data", listOfData);

				myWorldDataForGraph.put("noOfDays", dates.size());
				myWorldDataForGraph.put("noOfUsers", users.size());

			}
			graphHmap.put("myWorldDataForGraph", myWorldDataForGraph);
			
			List<Object> groupList= (List<Object>) getGroupDetailsByUserId(info.getUserId());

			Map<String,Object> groupsObj=new HashMap<>();
			groupsObj.put("groups", groupList);
			graphHmap.put("myGroupDataForGraph", groupsObj);
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return graphHmap;

	}
	
	
	
	
	@Override
	public Object getGroupDetailsByUserId(Long userId) {

		SimpleDateFormat sdf=new SimpleDateFormat(W2meterConstant.DATE_FORMAT);
		List<Object> listOfGroup=new ArrayList<>();
		
		//List<GroupDetails> listOfGroupDetails=(List<GroupDetails>) groupDetailsRepository.findAll();
		List<GroupDetails> listOfGroupDetails=groupDetailsRepository.findByCreateId(userId);
		
		List<Object> groups=new ArrayList<>();
		for (GroupDetails groupDetails : listOfGroupDetails) {
			
			if(groupDetails.getIsActive()==W2meterConstant.ACTIVE_DATA) {


				List<String> list=Arrays.asList(groupDetails.getMemberIds().split(","));

				List<Long> usersList=new  ArrayList<>();

				if(list.contains(String.valueOf(userId))) {
					for (String string : list) {
						usersList.add(Long.valueOf(string));
					}
					Map<String,Object> userDetailMap=new HashMap<>();
					userDetailMap.put("groupname", groupDetails.getGroupName());
					userDetailMap.put("groupid", groupDetails.getGroupId());

					if(null!=groupDetails.getGroupIconUrl()) {
						userDetailMap.put("groupicon", W2meterConstant.SERVER_BASE_URL+groupDetails.getGroupIconUrl().replaceAll(W2meterConstant.TOMCAT_HOME, "/"));
					}
					else
						userDetailMap.put("groupicon", null);

					listOfGroup.add(userDetailMap);
					
					List<VoteDetails> listOfVotingDetails=votingRepository.findByuserIdIn(usersList);
					
					Map<String,Object> myGroupDataForGraph=new HashMap<>();
					if(null!=listOfVotingDetails && !listOfVotingDetails.isEmpty()) {

						List<Object> listOfData=new ArrayList<>();

						Set<Long> users=new HashSet<>();
						Map<String,Object> dates=new HashMap<>();
						for (VoteDetails voteDetails : listOfVotingDetails) {
							users.add(voteDetails.getUserId());

							if(dates.containsKey(sdf.format(voteDetails.getVotingDate()))) {
								List<VoteDetails> listvote=(List<VoteDetails>) dates.get(sdf.format(voteDetails.getVotingDate()));
								listvote.add(voteDetails);
								dates.put(sdf.format(voteDetails.getVotingDate()), listvote);
							}
							else {
								List<VoteDetails> listvote=new ArrayList<>();
								listvote.add(voteDetails);
								dates.put(sdf.format(voteDetails.getVotingDate()), listvote);
							}
						}

						for (Entry<String, Object> entry : dates.entrySet()) {
							Map<String,Object> myData=new HashMap<>();
							myData.put("date", entry.getKey());

							BigDecimal voteValue=new BigDecimal(0);
							List<VoteDetails> listvote=(List<VoteDetails>) entry.getValue();
							for (VoteDetails votedetail : listvote) {
								voteValue=voteValue.add(new BigDecimal(votedetail.getVoteValue()));
							}

							myData.put("value", voteValue.divide(new BigDecimal(list.size()),2, RoundingMode.HALF_UP));
							listOfData.add(myData);
						}

						myGroupDataForGraph.put("data", listOfData);

						myGroupDataForGraph.put("noOfDays", dates.size());
						myGroupDataForGraph.put("noOfUsers", users.size());
						myGroupDataForGraph.put("groupName", userDetailMap.get("groupname"));
						myGroupDataForGraph.put("groupIcon", userDetailMap.get("groupicon"));
						myGroupDataForGraph.put("groupId", userDetailMap.get("groupid"));

					}
					groups.add(myGroupDataForGraph);

				}

			
			}
		}
		return groups;
	}

	
	@Override
	public void deleteGroup(Long groupId, AppInfo info) {

		GroupDetails localGroupDetails=groupDetailsRepository.getGroupDetailsByGroupId(groupId);

		if(null!=localGroupDetails) {
			localGroupDetails.setIsActive(W2meterConstant.INACTIVE_DATA);
			localGroupDetails.setUpdateDate(new Date());
			localGroupDetails.setUpdateId(info.getUserId());

			groupDetailsRepository.save(localGroupDetails);
		}

	}
	
	@Override
	public List<UserDetails> getMyCountryUsers(AppInfo info) {

		List<UserIdentification> listOfUserIdentification=null;
		List<UserDetails> myCountryUsers=null;

		listOfUserIdentification=userIdentificationRepository.findByCountryCodeIn(info.getCountryCode());

		if(null!=listOfUserIdentification && !listOfUserIdentification.isEmpty()) {
			List<Long> userIds=new ArrayList<>();
			for (UserIdentification userIdentification : listOfUserIdentification) {
				userIds.add(userIdentification.getId());
			}
			myCountryUsers=userRepository.findByUserIdIn(userIds);
		}
		return myCountryUsers;

	}
	
	
	
	@Override
	public List<UserDetails> getWorldUsers(AppInfo info) {

		List<UserIdentification> listOfUserIdentification=null;
		List<UserDetails> myWorldUsers=null;

		listOfUserIdentification=(List<UserIdentification>) userIdentificationRepository.findAll();

		listOfUserIdentification=userIdentificationRepository.findByCountryCodeIn(info.getCountryCode());

		if(null!=listOfUserIdentification && !listOfUserIdentification.isEmpty()) {
			List<Long> userIds=new ArrayList<>();
			for (UserIdentification userIdentification : listOfUserIdentification) {
				userIds.add(userIdentification.getId());
			}
			myWorldUsers=userRepository.findByUserIdIn(userIds);
		}
		return myWorldUsers;
		
		
	}

	
}
