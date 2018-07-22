package com.w2meter.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
import com.w2meter.entity.UserDetails;
import com.w2meter.entity.UserIdentification;
import com.w2meter.service.UserService;
import com.w2meter.util.TokenUtil;
import com.w2meter.util.W2meterConstant;

@RestController
@RequestMapping("/home")
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

}
