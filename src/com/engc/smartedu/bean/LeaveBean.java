package com.engc.smartedu.bean;

/**
 * 假期 bean
 * @author Admin
 *
 */
public class LeaveBean extends Entity {

	private String usercode;
	private String holidays;
	private String holidayType;
	private String telno;
	private String orgname;
	private String begindate;
	private String enddate;
	private String username;
	private String holidayRemark;
	private String leavetime;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLeavetime() {
		return leavetime;
	}

	public void setLeavetime(String leavetime) {
		this.leavetime = leavetime;
	}

	public String getLeavestatus() {
		return leavestatus;
	}

	public void setLeavestatus(String leavestatus) {
		this.leavestatus = leavestatus;
	}

	private String leavestatus;

	public String getLeavetype() {
		return leavetype;
	}

	public void setLeavetype(String leavetype) {
		this.leavetype = leavetype;
	}

	private String leavetype;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getHolidays() {
		return holidays;
	}

	public void setHolidays(String holidays) {
		this.holidays = holidays;
	}

	public String getHolidayType() {
		return holidayType;
	}

	public void setHolidayType(String holidayType) {
		this.holidayType = holidayType;
	}

	public String getTelno() {
		return telno;
	}

	public void setTelno(String telno) {
		this.telno = telno;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public String getBegindate() {
		return begindate;
	}

	public void setBegindate(String begindate) {
		this.begindate = begindate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getHolidayRemark() {
		return holidayRemark;
	}

	public void setHolidayRemark(String holidayRemark) {
		this.holidayRemark = holidayRemark;
	}
}
