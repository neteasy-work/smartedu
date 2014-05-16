package com.engc.smartedu.dao.friend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.engc.smartedu.bean.FriendBean;
import com.engc.smartedu.bean.FriendListBean;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpMethod;
import com.engc.smartedu.support.http.HttpUtility;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * 
 * @Title: FriendDao.java
 * @Package: com.engc.smartedu.dao.friend
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-5-16 下午3:22:17
 */
public class FriendDao {

	/**
	 * 根据orgid 获取 好友列表 
	 * @param orgId
	 * @return
	 * @throws AppException
	 */
	public static FriendListBean getFriendsByOrgId(String orgId) throws AppException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("orgId", orgId);
		FriendListBean friendList = new FriendListBean();
		try {
			String result = HttpUtility.getInstance().executeNormalTask(
					HttpMethod.Post, URLS.GET_FRIENDS_BY_ORGID, params);
			List<FriendBean> list = JSON.parseArray(result, FriendBean.class);
			friendList.getFriendsList().addAll(list);
			return friendList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}

}
