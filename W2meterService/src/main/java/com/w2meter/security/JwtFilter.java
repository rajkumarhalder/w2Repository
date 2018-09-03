package com.w2meter.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.w2meter.dto.AppInfo;
import com.w2meter.util.TokenUtil;

@Component
public class JwtFilter implements Filter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);
	
	List<String> avoidUrls=new ArrayList<>();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		avoidUrls.add("registeruser");
		avoidUrls.add("myworldusers");
		avoidUrls.add("mycountryusers");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		AppInfo appInfo=null;
		String url = ((HttpServletRequest)request).getRequestURL().toString();
		String targetAction=url.split("/")[url.split("/").length-1];
		if(!avoidUrls.contains(targetAction)) {
			try {
				appInfo=TokenUtil.getTokendetail(req.getHeader("identificationToken"));
				req.setAttribute("appInfo", appInfo);
			} catch (Exception e) {
				LOGGER.error("Exception Occured Inside JwtFilter doFilter() method"+e);
				res.sendError(9999, "Authentication error");
			}
		}
		else {
			appInfo=new AppInfo();
			appInfo.setUserId(0L);
			req.setAttribute("appInfo", appInfo);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
		
	}

}
