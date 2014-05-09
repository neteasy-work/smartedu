package com.engc.smartedu.dao.login;

import java.util.HashMap;
import java.util.Map;

import com.engc.smartedu.bean.User;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpMethod;
import com.engc.smartedu.support.http.HttpUtility;
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

}
