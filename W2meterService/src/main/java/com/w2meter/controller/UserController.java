package com.w2meter.controller;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.w2meter.dto.AppInfo;
import com.w2meter.dto.ResponseDto;
import com.w2meter.entity.GroupDetails;
import com.w2meter.entity.UserDetails;
import com.w2meter.entity.UserIdentification;
import com.w2meter.entity.VoteDetails;
import com.w2meter.service.UserService;
import com.w2meter.util.TokenUtil;
import com.w2meter.util.W2meterConstant;

@RestController
@RequestMapping("/home")

@SuppressWarnings({"unchecked"})
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping("/saveorupdateprofile")
	public Object saveOrUpdateUserProfile(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="name",required=false) String name ,
			@RequestParam(value="dateOfBirth",required=false) String dateOfBirth,
			@RequestParam(value="gender",required=false) String gender,
			@RequestParam(value="about",required=false) String about,
			@RequestParam(value="profilePic",required=false) MultipartFile profilePic) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
			} catch (Exception e) {
				throw e;
			}

			Long userId=appInfo.getUserId();

			SimpleDateFormat sdf=new SimpleDateFormat(W2meterConstant.DATE_FORMAT);
			UserDetails userDetails=new UserDetails();
			userDetails.setUserId(userId);
			if(null!=name)
				userDetails.setName(name);
			if(null!=dateOfBirth)
				userDetails.setDateOfBirth(sdf.parse(dateOfBirth));
			if(null!=gender) {
				if(gender.toUpperCase().startsWith(W2meterConstant.GENDER_MALE))
					userDetails.setGender(W2meterConstant.GENDER_MALE);
				else if(gender.toUpperCase().startsWith(W2meterConstant.GENDER_FEMALE))
					userDetails.setGender(W2meterConstant.GENDER_FEMALE);
			}

			if(null!=about)
				userDetails.setAbout(about);

			Path path=null;
			try {

				if(null!=profilePic) {
					byte[] bytes = profilePic.getBytes();
					path = Paths.get(W2meterConstant.PROFILE_PIC_URL + userId+"/"+profilePic.getOriginalFilename());
					if(!new File(W2meterConstant.PROFILE_PIC_URL + userId).exists())
						new File(W2meterConstant.PROFILE_PIC_URL + userId).mkdirs();
					Files.write(path, bytes);
				}
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				responseDto.setData(pw.toString());
			}
			if(null!=path)
				userDetails.setPrifilePicUrl(path.toString());

			userDetails.setMobileNo(appInfo.getCountryCode()+appInfo.getMobileNumber()+"");
			userDetails=(UserDetails) userService.updateUserDetail(userDetails);
			
			Map<String,Object> userDetailMap=new HashMap<>();
			userDetailMap.put("name", userDetails.getName());
			userDetailMap.put("about", userDetails.getAbout());
			
			if(null!=userDetails.getPrifilePicUrl()) {
				userDetailMap.put("profilePic", W2meterConstant.SERVER_BASE_URL+userDetails.getPrifilePicUrl().replaceAll(W2meterConstant.TOMCAT_HOME, "/"));
			}
			else
				userDetailMap.put("profilePic", null);
			
			userDetailMap.put("gender", userDetails.getGender());
			if(null!=userDetails.getDateOfBirth())
				userDetailMap.put("dateOfBirth", sdf.format(userDetails.getDateOfBirth()));
			else
				userDetailMap.put("dateOfBirth", null);
			
			userDetailMap.put("userId", userId);

			responseDto.setData(userDetailMap);
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setIdentificationToken(request.getHeader("identificationToken"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}


	@RequestMapping("/registeruser")
	public ResponseDto registerUser(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("countryCode") String countryCode,
			@RequestParam("mobileNumber") String mobileNumber,
			@RequestParam("otp") String currentOtp) {
		
		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			if(null!=countryCode && !countryCode.isEmpty() 
					&& null!=mobileNumber && !mobileNumber.isEmpty()
					&& null!=currentOtp && !currentOtp.isEmpty()) {
				UserIdentification userIdentification=new  UserIdentification();

				userIdentification.setCountryCode(countryCode);
				userIdentification.setMobileNo(Long.valueOf(mobileNumber));
				userIdentification.setCurrentOtp(currentOtp);
				userIdentification.setMobileNoWithCountryCode(countryCode+mobileNumber);

				UserIdentification existingUserIdentification=(UserIdentification) userService.findExistingUserIdentification(userIdentification);

				if(null==existingUserIdentification)
					userIdentification=(UserIdentification) userService.registerUser(userIdentification);
				else
					userIdentification=existingUserIdentification;

				String identificationToken=TokenUtil.createToken(String.valueOf(userIdentification.getId()), String.valueOf(userIdentification.getMobileNo()),userIdentification.getCountryCode());

				userIdentification.setCurrentToken(identificationToken);
				userService.registerUser(userIdentification);
				
				responseDto.setIdentificationToken(identificationToken);
				responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
				responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}


	@RequestMapping("/getuserdetails")
	public Object getUserDetails(HttpServletRequest request,
			HttpServletResponse response) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
			} catch (Exception e) {
				throw e;
			}

			Long userId=appInfo.getUserId();

			UserDetails userDetails=(UserDetails) userService.getUserDetail(userId);
			
			if(null==userDetails)
				userDetails=new UserDetails();
			
			SimpleDateFormat sdf=new SimpleDateFormat(W2meterConstant.DATE_FORMAT);
			Map<String,Object> userDetailMap=new HashMap<>();
			userDetailMap.put("name", userDetails.getName());
			userDetailMap.put("about", userDetails.getAbout());
			
			if(null!=userDetails.getPrifilePicUrl()) {
				
				userDetailMap.put("profilePic", W2meterConstant.SERVER_BASE_URL+userDetails.getPrifilePicUrl().replaceAll(W2meterConstant.TOMCAT_HOME, "/"));
			}
			else
				userDetailMap.put("profilePic", null);
			
			userDetailMap.put("gender", userDetails.getGender());
			if(null!=userDetails.getDateOfBirth())
				userDetailMap.put("dateOfBirth", sdf.format(userDetails.getDateOfBirth()));
			else
				userDetailMap.put("dateOfBirth", null);
			
			userDetailMap.put("userId", userId);

			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setData(userDetailMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}


	@RequestMapping("/submitvote")
	public Object submitVote(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="todayrating") String todayrating ) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
			} catch (Exception e) {
				throw e;
			}

			Long userId=appInfo.getUserId();

			VoteDetails voteDetails=new VoteDetails();
			voteDetails.setUserId(userId);
			voteDetails.setVoteValue(Integer.parseInt(todayrating));
			voteDetails.setCountryCode(appInfo.getCountryCode());

			Object object=userService.postVote(voteDetails, appInfo);
			
			responseDto.setData(object);

			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setIdentificationToken(request.getHeader("identificationToken"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}


	@RequestMapping("/createnewgroup")
	public Object createGroup(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="groupname",required=false) String groupName,
			@RequestParam(value="groupicon",required=false) MultipartFile groupIcon,
			@RequestParam(value="groupid",required=false) String groupId) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
			} catch (Exception e) {
				throw e;
			}

			Long userId=appInfo.getUserId();
			Path path=null;
			if(null!=groupIcon) {
				byte[] bytes = groupIcon.getBytes();
				path = Paths.get(W2meterConstant.PROFILE_PIC_URL + userId+"/"+W2meterConstant.GROUP_ICON_LOCATION_TEMP+"/"+groupIcon.getOriginalFilename());
				if(!new File(W2meterConstant.PROFILE_PIC_URL + userId+"/"+W2meterConstant.GROUP_ICON_LOCATION_TEMP).exists())
					new File(W2meterConstant.PROFILE_PIC_URL + userId+"/"+W2meterConstant.GROUP_ICON_LOCATION_TEMP).mkdirs();
				Files.write(path, bytes);
			}

			GroupDetails groupDetails=null;
			
			if(null!=groupName && !groupName.isEmpty() && Integer.parseInt(groupId)==0) {
				groupDetails=new GroupDetails();
				groupDetails.setGroupName(groupName);
				groupDetails.setMemberIds(String.valueOf(userId));
				groupDetails.setCreateId(userId);
				groupDetails=userService.saveGroup(groupDetails, W2meterConstant.OPERATION_FLAG_ADD, appInfo);
				
				if(null!=path) {
					if(!new File(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()).exists())
						new File(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()).mkdirs();

					path.toFile().renameTo(new File(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()+"/"+groupIcon.getOriginalFilename()));

					groupDetails.setGroupIconUrl(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()+"/"+groupIcon.getOriginalFilename());
					groupDetails=userService.saveGroup(groupDetails, W2meterConstant.OPERATION_FLAG_ADD, appInfo);
				}
			}

			else if(Integer.parseInt(groupId)>0) {

				groupDetails=new GroupDetails();
				groupDetails.setGroupId(Long.parseLong(groupId));
				if(null!=groupName && !groupName.isEmpty())
					groupDetails.setGroupName(groupName);
				if(null!=groupIcon) {
					path = Paths.get(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()+"/"+groupIcon.getOriginalFilename());
					if(!new File(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()).exists())
						new File(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()).mkdirs();

					Files.write(path, groupIcon.getBytes());
					groupDetails.setGroupIconUrl(path.toString());
				}
				groupDetails.setCreateId(userId);
				groupDetails=userService.saveGroup(groupDetails, W2meterConstant.OPERATION_FLAG_ADD, appInfo);

			}
			
			Map<String,Object> groupDetailMap=new HashMap<>();
			groupDetailMap.put("groupid", groupDetails.getGroupId());
			groupDetailMap.put("groupname", groupDetails.getGroupName());
			if(null!=groupDetails.getGroupIconUrl()) {
				groupDetailMap.put("groupicon", W2meterConstant.SERVER_BASE_URL+groupDetails.getGroupIconUrl().replaceAll(W2meterConstant.TOMCAT_HOME, "/"));
			}
			else
			groupDetailMap.put("groupicon", groupDetails.getGroupIconUrl());
			
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setIdentificationToken(request.getHeader("identificationToken"));
			responseDto.setData(groupDetailMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}


	@RequestMapping("/addordeletememberingroup")
	public Object updateGroupMemeber(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="groupid") String groupId,
			@RequestParam(value="memberId") String memberId,
			@RequestParam(value="operationFlag") String operationFlag) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
			} catch (Exception e) {
				throw e;
			}
			
			Long userId=appInfo.getUserId();
			GroupDetails groupDetails=null;
			if(Integer.parseInt(groupId)>0 && null!=memberId && null!=operationFlag) {
				groupDetails=new GroupDetails();
				groupDetails.setGroupId(Long.parseLong(groupId));
				groupDetails.setMemberIds(memberId);
				userService.updateGroupMembers(groupDetails, operationFlag, appInfo);
			}
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setIdentificationToken(request.getHeader("identificationToken"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}


	@RequestMapping("/getgroupdetails")
	public Object getGroupDetails(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="groupid") String groupId) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
			} catch (Exception e) {
				throw e;
			}

			Long userId=appInfo.getUserId();

			Map<String,Object> groupInfoMap=(Map<String, Object>) userService.getGroupDetailswithMemebers(Long.parseLong(groupId));

			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setData(groupInfoMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}


	@RequestMapping("/getallgroups")
	public Object getAllGroups(HttpServletRequest request,
			HttpServletResponse response) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
			} catch (Exception e) {
				throw e;
			}
			Long userId=appInfo.getUserId();

			List<Object> groupList= (List<Object>) userService.getGroupDetailsByCreateId(userId);
			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setData(groupList);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}

	@RequestMapping("/groupmembers")
	public Object getGroupDetailswithMembers(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="groupid") String groupId) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
			} catch (Exception e) {
				throw e;
			}

			Long userId=appInfo.getUserId();

			Map<String,Object> groupInfoMap=(Map<String, Object>) userService.getGroupDetailswithMemebers(Long.parseLong(groupId));

			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setData(groupInfoMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}


	@RequestMapping("/getexistingandnotexistingusers")
	public Object getExistingAndNotExistingUsers(HttpServletRequest request,
			                                     HttpServletResponse response,
			                                     @RequestParam(value="mycontacts") String myContacts) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
			} catch (Exception e) {
				throw e;
			}


			Long userId=appInfo.getUserId();

			List<String> contactList=new  ArrayList<>();
			Object obj=null;
			if(null!=myContacts) {
				String[] contact=myContacts.split(",");

				for (String string : contact) {
					contactList.add(string);
				}
				obj=userService.getExistingAndNotExistingUsers(contactList, appInfo);
			}
			
			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setData(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}


	@RequestMapping("/getgraphdetails")
	public Object getDataForGraph(HttpServletRequest request,
			                      HttpServletResponse response) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
			} catch (Exception e) {
				throw e;
			}

			Object obj=userService.getGraphData(appInfo);

			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setData(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}
	
	
	@RequestMapping("/deletegroup")
	public Object deleteGroup(HttpServletRequest request,
			                  HttpServletResponse response,
			                  @RequestParam(value="groupId") String groupId) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
			} catch (Exception e) {
				throw e;
			}

			userService.deleteGroup(Long.valueOf(groupId), appInfo);

			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}
	
	@RequestMapping("/mycountryusers")
	public Object getMyCountryUsers(HttpServletRequest request,
			                        HttpServletResponse response,
			                        @RequestParam("countryCode") String countryCode) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				//appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
				appInfo=new AppInfo();
				appInfo.setCountryCode(countryCode);
			} catch (Exception e) {
				throw e;
			}

			List<UserDetails> listOfMyCountryUsers=userService.getMyCountryUsers(appInfo);

			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setData(listOfMyCountryUsers);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}
	
	
	@RequestMapping("/myworldusers")
	public Object getMyWorldUsers(HttpServletRequest request,
			                  HttpServletResponse response) {

		ResponseDto responseDto=null;
		try {
			responseDto=new ResponseDto();
			AppInfo appInfo=null;
			try {
				//appInfo=TokenUtil.getTokendetail(request.getHeader("identificationToken"));
				appInfo=new AppInfo();
			} catch (Exception e) {
				throw e;
			}

			List<UserDetails> listOfMyWorldUsers=userService.getWorldUsers(appInfo);

			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setData(listOfMyWorldUsers);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}
	
	
}
