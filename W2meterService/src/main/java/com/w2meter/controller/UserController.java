package com.w2meter.controller;

import java.io.File;
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
			if(null!=profilePic) {
				byte[] bytes = profilePic.getBytes();
				path = Paths.get(W2meterConstant.PROFILE_PIC_URL + userId+"\\"+profilePic.getOriginalFilename());
				if(!new File(W2meterConstant.PROFILE_PIC_URL + userId).exists())
					new File(W2meterConstant.PROFILE_PIC_URL + userId).mkdirs();
				Files.write(path, bytes);
			}
			userDetails.setPrifilePicUrl(path.toString());

			userService.updateUserDetail(userDetails);

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

				UserIdentification existingUserIdentification=(UserIdentification) userService.findExistingUserIdentification(userIdentification);

				if(null==existingUserIdentification)
					userIdentification=(UserIdentification) userService.registerUser(userIdentification);
				else
					userIdentification=existingUserIdentification;

				String identificationToken=TokenUtil.createToken(String.valueOf(userIdentification.getId()), String.valueOf(userIdentification.getMobileNo()),userIdentification.getCountryCode());

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
			SimpleDateFormat sdf=new SimpleDateFormat(W2meterConstant.DATE_FORMAT);
			Map<String,String> userDetailMap=new HashMap<>();
			userDetailMap.put("name", userDetails.getName());
			userDetailMap.put("about", userDetails.getAbout());
			userDetailMap.put("profilePic", userDetails.getPrifilePicUrl());
			userDetailMap.put("gender", userDetails.getGender());
			userDetailMap.put("dateOfBirth", sdf.format(userDetails.getDateOfBirth()));

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

			userService.postVote(voteDetails);

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
			@RequestParam(value="groupname") String groupName,
			@RequestParam(value="groupicon") MultipartFile groupIcon,
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

			GroupDetails groupDetails=null;
			if(null!=groupName && !groupName.isEmpty() && Integer.parseInt(groupId)==0) {
				groupDetails=new GroupDetails();
				groupDetails.setGroupName(groupName);
				groupDetails.setMemberIds(String.valueOf(userId));

				Path path=null;
				if(null!=groupIcon) {
					byte[] bytes = groupIcon.getBytes();
					path = Paths.get(W2meterConstant.PROFILE_PIC_URL + userId+"\\"+W2meterConstant.GROUP_ICON_LOCATION_TEMP+"\\"+groupIcon.getOriginalFilename());
					if(!new File(W2meterConstant.PROFILE_PIC_URL + userId+"\\"+W2meterConstant.GROUP_ICON_LOCATION_TEMP).exists())
						new File(W2meterConstant.PROFILE_PIC_URL + userId+"\\"+W2meterConstant.GROUP_ICON_LOCATION_TEMP).mkdirs();
					Files.write(path, bytes);
				}

				groupDetails=userService.saveGroup(groupDetails);

				if(null !=groupDetails && null!=groupDetails.getGroupId()) {
					if(!new File(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()).exists())
						new File(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()).mkdirs();

					path.toFile().renameTo(new File(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()+"\\"+groupIcon.getOriginalFilename()));

					groupDetails.setMemberIds(String.valueOf(userId));
					groupDetails.setGroupIconUrl(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()+"\\"+groupIcon.getOriginalFilename());

					userService.saveGroup(groupDetails);
				}
			}

			else if(Integer.parseInt(groupId)>0) {

				groupDetails=new GroupDetails();
				groupDetails.setGroupId(Long.parseLong(groupId));
				if(null!=groupName && !groupName.isEmpty())
					groupDetails.setGroupName(groupName);
				Path path=null;
				if(null!=groupIcon) {
					path = Paths.get(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()+"\\"+groupIcon.getOriginalFilename());
					if(!new File(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()).exists())
						new File(W2meterConstant.GROUP_ICON_URL + groupDetails.getGroupId()).mkdirs();

					Files.write(path, groupIcon.getBytes());
					groupDetails.setGroupIconUrl(path.toString());
				}
				userService.saveGroup(groupDetails);

			}
			responseDto.setStatusCode(W2meterConstant.STATUS_CODE_SUCCESS);
			responseDto.setStatus(W2meterConstant.REST_API_STATUS_SUCCESS);
			responseDto.setIdentificationToken(request.getHeader("identificationToken"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}


	@RequestMapping("/addordeletememberingroup")
	public Object updateGroupMemeber(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="groupid") String groupId,
			@RequestParam(value="membersids") String membersids) {

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

			if(Integer.parseInt(groupId)>0 && null!=membersids && !membersids.isEmpty()) {
				groupDetails=new GroupDetails();
				groupDetails.setGroupId(Long.parseLong(groupId));
				groupDetails.setMemberIds(membersids);
				groupDetails.setUpdateId(userId);
				userService.saveGroup(groupDetails);

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


}
