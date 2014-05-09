package com.engc.smartedu.bean;

import java.io.Serializable;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * 
 * @Title: User.java
 * @Package: com.engc.smartedu.bean
 * @Description: User 实体
 * @author: Administrator
 * @date: 2014-5-9 下午3:36:18
 */

public class User implements Serializable {

	private String usercode;
	private String username;
	private int usertype;
	private String password;
	private int ismgr;
	private String headpic;
	private String entityname;
	private String currdbmoney;
	private int cardstatus;
	private String orgid;
	private static final long serialVersionUID = 1L;
	private String channelId;

	

	public User(String usercode, String channelId, String username, String headicon) {
		this.usercode = usercode;
		this.channelId = channelId;
		this.username = username;
		this.headpic = headicon;

	}

	public User() {

	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getUsertype() {
		return usertype;
	}

	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getIsmgr() {
		return ismgr;
	}

	public void setIsmgr(int ismgr) {
		this.ismgr = ismgr;
	}

	public String getHeadpic() {
		return headpic;
	}

	public void setHeadpic(String headpic) {
		this.headpic = headpic;
	}

	public String getEntityname() {
		return entityname;
	}

	public void setEntityname(String entityname) {
		this.entityname = entityname;
	}

	public String getCurrdbmoney() {
		return currdbmoney;
	}

	public void setCurrdbmoney(String currdbmoney) {
		this.currdbmoney = currdbmoney;
	}

	public int getCardstatus() {
		return cardstatus;
	}

	public void setCardstatus(int cardstatus) {
		this.cardstatus = cardstatus;
	}

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

}
