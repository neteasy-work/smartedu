package com.engc.smartedu.dao.login;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.engc.eop.ClientRequest;
import com.engc.eop.CompositeResponse;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpMethod;
import com.engc.smartedu.support.http.HttpUtility;
import com.engc.smartedu.support.utils.EopClientConstants;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.PreferenceUtils;
import com.engc.smartedu.support.utils.SharePreferenceUtil;
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
		//Map<String, String> params = new HashMap<String, String>();
		//params.put("userName", userName);
		//params.put("passWord", passWord);
		ClientRequest request=HttpUtility.getInstance().getEopClient().buildClientRequest();
		request.addParam("userName", userName);
		request.addParam("passWord", passWord);
		
			
		
		User user = null;
		try {
			/*String result = HttpUtility.getInstance().executeNormalTask(
					HttpMethod.Post, URLS.Login, params);*/
			CompositeResponse<?> res=request.post(URLS.Login, EopClientConstants.VERSION);
			if(res.isSuccessful()){
				user=JSON.parseObject(res.getResponseContent(),User.class);
			}
			//Gson gson = new Gson();
			//user = gson.fromJson(result, User.class);
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
	public static void saveLoginInfo(Context context, final User user) {

		PreferenceUtils.setPrefString(context, "user.usercode", String
				.valueOf(user.getUsercode() != null ? user.getUsercode() : ""));
		PreferenceUtils.setPrefString(context, "user.name",
				user.getUsername() != null ? user.getUsername() : "");
		PreferenceUtils.setPrefString(context, "user.cardstatusName", String
				.valueOf(user.getEntityname() != null ? user.getEntityname()
						: "0"));	
		PreferenceUtils.setPrefString(context, "user.cardstatusCode", String
				.valueOf(user.getCardstatus() != 0 ? user.getCardstatus() : 0));
		PreferenceUtils.setPrefString(context, "user.accountBalance", String
				.valueOf(user.getCurrdbmoney() != null ? user.getCurrdbmoney()
						: 0));
		PreferenceUtils.setPrefString(context, "user.orgid",
				user.getOrgid() != null ? user.getOrgid() : "");
		PreferenceUtils.setPrefString(context, "user.face", String.valueOf(user
				.getHeadpic() != null ? user.getHeadpic() : ""));
		PreferenceUtils.setPrefString(context, "user.parentphone",
				user.getParentphone() != null ? user.getParentphone() : "");
		PreferenceUtils.setPrefString(context, "user.sonname",
				user.getSonname() != null ? user.getSonname() : "");
		PreferenceUtils.setPrefString(context, "user.soncode",
				user.getSoncode() != null ? user.getSoncode() : "");
		PreferenceUtils.setPrefString(context, "user.usertype", String
				.valueOf(user.getUsertype() != 0 ? user.getUsertype() : 0));

	}

	/**
	 * 清除登录信息
	 */
	public static void cleanLoginInfo() {
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
	public static User getLoginInfo(Context context) {
		User lu = new User();

		lu.setUsercode(PreferenceUtils.getPrefString(context, "user.usercode",
				""));
		lu.setUsername(PreferenceUtils.getPrefString(context, "user.name", ""));
		lu.setEntityname(PreferenceUtils.getPrefString(context,
				"user.cardstatusName", ""));
		lu.setCardstatus(Integer.parseInt(PreferenceUtils.getPrefString(context,"user.cardstatusCode","")));
		lu.setCurrdbmoney(PreferenceUtils.getPrefString(context,
				"user.accountBalance",""));
		lu.setHeadpic(PreferenceUtils.getPrefString(context,"user.face",""));
		lu.setOrgid(PreferenceUtils.getPrefString(context,"user.orgid",""));
		lu.setParentphone(PreferenceUtils.getPrefString(context,
				"user.parentphone",""));
		lu.setSoncode(PreferenceUtils.getPrefString(context,"user.soncode",""));
		lu.setSonname(PreferenceUtils.getPrefString(context,"user.sonname",""));
		lu.setUsertype(Integer.parseInt(PreferenceUtils.getPrefString(context,"user.usertype","")));
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
	public static User ChangeCardStatus(final String userCode,
			final int basicStatus, final int cardstatusone) throws AppException {
		ClientRequest request=HttpUtility.getInstance().getEopClient().buildClientRequest();
		request.addParam("usercode", userCode);
		request.addParam("status", String.valueOf(basicStatus));
		request.addParam("cardstatusone", String.valueOf(cardstatusone));
		
		
		/*Map<String, String> params = new HashMap<String, String>();
		params.put("usercode", userCode);
		params.put("status", String.valueOf(basicStatus));
		params.put("cardstatusone", String.valueOf(cardstatusone));*/
		User user = null;
		try {
			//String result = HttpUtility.getInstance().executeNormalTask(
					//HttpMethod.Post, URLS.CHANGE_CARD_STATUS, params);
			CompositeResponse<?> res=request.post(URLS.CHANGE_CARD_STATUS, EopClientConstants.VERSION);
			if(res.isSuccessful()){
				user=JSON.parseObject(res.getResponseContent(),User.class);
			}
			return user;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

}
