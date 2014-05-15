package com.engc.smartedu.bean;

import java.io.Serializable;

public class Update implements Serializable {
	public final static String UTF8 = "UTF-8";
	public final static String NODE_ROOT = "meeting";

	private int versioncode;
	private String versionname;
	private String downloadurl;
	private String updatelog;
	private String appname;

	public String getOperatTime() {
		return operatTime;
	}

	public void setOperatTime(String operatTime) {
		this.operatTime = operatTime;
	}

	private String operatTime;

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public int getVersioncode() {
		return versioncode;
	}

	public void setVersioncode(int versioncode) {
		this.versioncode = versioncode;
	}

	public String getVersionname() {
		return versionname;
	}

	public void setVersionname(String versionname) {
		this.versionname = versionname;
	}

	public String getDownloadurl() {
		return downloadurl;
	}

	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}

	public String getUpdatelog() {
		return updatelog;
	}

	public void setUpdatelog(String updatelog) {
		this.updatelog = updatelog;
	}

	public static String getUtf8() {
		return UTF8;
	}

	public static String getNodeRoot() {
		return NODE_ROOT;
	}

}
