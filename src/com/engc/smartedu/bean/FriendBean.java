package com.engc.smartedu.bean;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: FriendBean.java
 * @Package: com.engc.smartedu.bean
 * @Description: 好友 实体
 * @author: Administrator  
 * @date: 2014-5-16 下午3:08:28
 */
public class FriendBean extends Entity {
	
	private String userId;
	private String userCode;
	private String headIcon;
	private String orgName;
	private String orgId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getHeadIcon() {
		return headIcon;
	}
	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	private String status;
	private String userType;
	private String userName;
	
	

}
