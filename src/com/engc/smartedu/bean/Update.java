package com.engc.smartedu.bean;

import java.io.Serializable;

public class Update implements Serializable {


	private int versioncode;
	private String versionname;
	private String downloadurl;
	private String updatelog;
	private String appname;
	private String apkName;
	private String operationTime;



	

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(String operationTime) {
		this.operationTime = operationTime;
	}

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



}
