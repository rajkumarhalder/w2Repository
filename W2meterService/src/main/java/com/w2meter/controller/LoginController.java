package com.w2meter.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.w2meter.entity.UserInfo;
import com.w2meter.service.LoginService;

@RestController
@RequestMapping("/home")
public class LoginController {
	
	
	@Autowired
	private LoginService loginService;

	@RequestMapping("/getuserid")
	public Object registerUser(HttpServletRequest request,
			                   HttpServletResponse response,
			                   @RequestBody UserInfo userInfo) {
		Map<String,Long> hmap=null;
		try {
			hmap=new HashMap<>();
			Long userId= loginService.registerUser(userInfo);
			hmap.put("userid", userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmap;
	}


	@RequestMapping("/getuserdetail")
	public UserInfo getUserDetail(HttpServletRequest request,
			                    HttpServletResponse response,
			                    @RequestParam("userid") Long userId ) {
		UserInfo userInfo=null;
		try {
			userInfo=loginService.getUser(userId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}

	
	@RequestMapping("/updateuser")
	public Object updateUser(HttpServletRequest request,
			                   HttpServletResponse response,
			                   @RequestBody UserInfo userInfo) {

		Map<String,Object> map=null;
		try {
			map=new HashMap<>();
			loginService.updateUser(userInfo);
			boolean updateFlag=true;
			map.put("updateflag", updateFlag);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
