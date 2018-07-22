package com.w2meter.util;

import java.util.Date;

import com.w2meter.dto.AppInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenUtil {

	public static String createToken(String userId,String mobileNumber,String countryCode) {
		String jwt=null;
		Long currentMilis=new Date().getTime();
		Long tokenExpireTime=currentMilis+(1000*3600*24*365);

		jwt = Jwts.builder()
				.setSubject("w2meter")
				.setExpiration(new Date(tokenExpireTime))
				.claim("userId", userId)
				.claim("mobileNumber", mobileNumber)
				.claim("countryCode", countryCode)
				.signWith(SignatureAlgorithm.HS256,"w2meter").compact();
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
			e.printStackTrace();
		}
		return appInfo;

	}

}
