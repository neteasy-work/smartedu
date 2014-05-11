package com.engc.smartedu.dao.login;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpMethod;
import com.engc.smartedu.support.http.HttpUtility;
import com.engc.smartedu.support.utils.GlobalContext;
import com.google.gson.Gson;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * 
 * @Title: LoginDao.java
 * @Package: com.engc.smartedu.dao.login
 * @Description: 登陆 数据访问层
 * @author: Administrator
 * @date: 2014-5-9 下午2:55:30
 */
public class LoginDao {
	public static User Login(final String userName, final String passWord)
			throws AppException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		params.put("passWord", passWord);
		User user = null;
		try {
			String result = HttpUtility.getInstance().executeNormalTask(
					HttpMethod.Post, URLS.Login, params);
			Gson gson = new Gson();
			user = gson.fromJson(result, User.class);
			return user;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}

	/**
	 * 保存登录信息
	 * 
	 * @param user
	 */
	public static void saveLoginInfo(final User user) {
		GlobalContext.getInstance().setProperties(new Properties() {
			{
				String accountName = user.getUsername();
				setProperty("user.usercode",
						String.valueOf(user.getUsercode() != null ? user
								.getUsercode() : ""));
				setProperty("user.name",
						user.getUsername() != null ? user.getUsername() : "");
				setProperty("user.cardstatusName", String.valueOf(user
						.getEntityname() != null ? user.getEntityname() : "0"));
				setProperty("user.cardstatusCode", String.valueOf(user
						.getCardstatus() != 0 ? user.getCardstatus() : 0));
				setProperty("user.accountBalance", String.valueOf(user
						.getCurrdbmoney() != null ? user.getCurrdbmoney() : 0));
				setProperty("user.orgid",
						user.getOrgid() != null ? user.getOrgid() : "");
				setProperty("user.face",
						String.valueOf(user.getHeadpic() != null ? user
								.getHeadpic() : ""));
				setProperty("user.parentphone",
						user.getParentphone() != null ? user.getParentphone()
								: "");
				setProperty("user.sonname",
						user.getSonname() != null ? user.getSonname() : "");
				setProperty("user.soncode",
						user.getSoncode() != null ? user.getSoncode() : "");
				setProperty("user.usertype",
						String.valueOf(user.getUsertype() != 0 ? user
								.getUsertype() : 0));
			}
		});
	}

	/**
	 * 清除登录信息
	 */
	public static  void cleanLoginInfo() {
		GlobalContext.getInstance().removeProperty("user.usercode",
				"user.name", "user.cardstatusName", "user.cardstatusCode",
				"user.accountBalance", "user.face", "user.orgid",
				"user.parentphone", "user.sonname", "user.soncode",
				"user.usertype");
	}

	/**
	 * 获取登录信息
	 * 
	 * @return
	 */
	public static  User getLoginInfo() {
		User lu = new User();

		lu.setUsercode(GlobalContext.getInstance().getProperty("user.usercode"));
		lu.setUsername(GlobalContext.getInstance().getProperty("user.name"));
		lu.setEntityname(GlobalContext.getInstance().getProperty("user.cardstatusName"));
		lu.setCardstatus(Integer.parseInt(GlobalContext.getInstance().getProperty("user.cardstatusCode")));
		lu.setCurrdbmoney(GlobalContext.getInstance().getProperty("user.accountBalance"));
		lu.setHeadpic(GlobalContext.getInstance().getProperty("user.face"));
		lu.setOrgid(GlobalContext.getInstance().getProperty("user.orgid"));
		lu.setParentphone(GlobalContext.getInstance().getProperty("user.parentphone"));
		lu.setSoncode(GlobalContext.getInstance().getProperty("user.soncode"));
		lu.setSonname(GlobalContext.getInstance().getProperty("user.sonname"));
		lu.setUsertype(Integer.parseInt(GlobalContext.getInstance().getProperty("user.usertype")));
		return lu;
	}

	/**
	 * 更改当前 卡状态
	 * 
	 * @param context
	 * @param userCode
	 * @param basicStatus
	 * @param cardstatusone
	 * @return
	 * @throws AppException
	 */
	public static User ChangeCardStatus(
			final String userCode, final int basicStatus,
			final int cardstatusone) throws AppException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("usercode", userCode);
		params.put("status", String.valueOf(basicStatus));
		params.put("cardstatusone", String.valueOf(cardstatusone));
		User user = null;
		try {
			String result = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post,URLS.CHANGE_CARD_STATUS, params);
			user = JSON.parseObject(result, User.class);
			return user;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

}
