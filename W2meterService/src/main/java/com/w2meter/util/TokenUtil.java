package com.w2meter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.w2meter.controller.UserController;
import com.w2meter.dto.AppInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenUtil.class);
	
	public static String createToken(String userId,String mobileNumber,String countryCode) {
		String jwt=null;
		try {
			jwt = Jwts.builder()
					.setSubject("w2meter")
					.claim("userId", userId)
					.claim("mobileNumber", mobileNumber)
					.claim("countryCode", countryCode)
					.signWith(SignatureAlgorithm.HS256,"w2meter").compact();
		} catch (Exception e) {
			LOGGER.error("Exception Occured during jwt token creation "+e);
			throw e;
		}
		
		return jwt;

	}

	public static AppInfo getTokendetail(String jwtToken) {
		AppInfo appInfo=null;
		try {

			Claims claims = Jwts.parser()
					.setSigningKey("w2meter")
					.parseClaimsJws(jwtToken)
					.getBody();
			appInfo= new AppInfo();
			appInfo.setUserId(Long.valueOf((String) claims.get("userId")));
			appInfo.setMobileNumber(String.valueOf( claims.get("mobileNumber")));
			appInfo.setCountryCode((String) claims.get("countryCode"));

		} catch (Exception e) {
			LOGGER.error("Exception Occured during jwt token decryption "+e);
			throw e;
		}
		return appInfo;

	}

}
