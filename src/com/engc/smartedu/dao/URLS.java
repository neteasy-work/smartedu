package com.engc.smartedu.dao;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: URLS.java
 * @Package: com.engc.smartedu.dao
 * @Description: URL 
 * @author: Administrator  
 * @date: 2014-5-9 上午11:32:23
 */
public class URLS {
	
	public final static String HOST = "";
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";

	public final static String BASEHOST = "http://www.szzhjy.net/StudentCenter/";
	//public final static String BASEHOST = "http://211.103.78.13:5088/StudentCenter/";
	// public final static String
	// BASEHOST="http://211.103.78.13:6055/StudentCenter/";

	// 服务器根目录地址

	// //服务器根目录地址
	public final static String HOST_SERVER = ""; // 服务器根地址
	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";

	// 调用服务端推送方法
	public final static String PushTest = BASEHOST + "pushtest/sendpush.do";
	// 登录
	public final static String Login = BASEHOST + "sysLogin/loginFromMobile.do";
	// 请假
	public final static String APPLY_HOLIDAYS = BASEHOST
			+ "LeaveManage/forPhoneLeave.do";

	// 审核假期
	public final static String AUDIT_HOLIDAYS = BASEHOST
			+ "LeaveManage/ApplyLeaveforPhone.do";

	// 根据记录ID 查询申请请假信息
	public final static String GET_APPLY_HOLIDAYS_INFO_BY_RECORDID = BASEHOST
			+ "LeaveManage/getLeaveInfoforPhone.do";
	// 更改当前卡 状态
	public final static String CHANGE_CARD_STATUS = BASEHOST
			+ "cardOperate/CardReportLossforphone.do";

	// 查询当前 学生的请假
	public final static String GET_HOLIDAYS_RECORF_FOR_STUDENT = BASEHOST
			+ "LeaveManage/getLeaveRecord.do";

	// 获得应用版本号
	public final static String GET_APP_VERSION = BASEHOST
			+ "appVersion/getAppVersion";

	// 获得历史家庭作业
	public final static String GET_BEFORE_HOMEWORK = BASEHOST
			+ "homeworkCtrl/homeWorkBefoListForMobile";

	// 获得今天家庭作业
	public final static String GET_TODAY_HOMEWORK = BASEHOST
			+ "homeworkCtrl/homeWorkTodayListForMobile";

	//意见反馈
	public final static String FEED_BACK=BASEHOST+"";
	/**
	 * 解析url获得objId
	 * 
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjId(String path, String url_type) {
		String objId = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if (str.contains(URL_SPLITTER)) {
			tmp = str.split(URL_SPLITTER);
			objId = tmp[0];
		} else {
			objId = str;
		}
		return objId;
	}

	/**
	 * 解析url获得objKey
	 * 
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjKey(String path, String url_type) {
		path = URLDecoder.decode(path);
		String objKey = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if (str.contains("?")) {
			tmp = str.split("?");
			objKey = tmp[0];
		} else {
			objKey = str;
		}
		return objKey;
	}

	/**
	 * 对URL进行格式处理
	 * 
	 * @param path
	 * @return
	 */
	private final static String formatURL(String path) {
		if (path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}

}
