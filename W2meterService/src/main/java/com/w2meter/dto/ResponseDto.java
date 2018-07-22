package com.w2meter.dto;

public class ResponseDto {
	
	private int statusCode;
	private String status;
	private String identificationToken;
	private Object data;
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIdentificationToken() {
		return identificationToken;
	}
	public void setIdentificationToken(String identificationToken) {
		this.identificationToken = identificationToken;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
	
}
