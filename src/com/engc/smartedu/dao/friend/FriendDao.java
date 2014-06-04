package com.engc.smartedu.dao.friend;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.engc.eop.ClientRequest;
import com.engc.eop.CompositeResponse;
import com.engc.smartedu.bean.SortModel;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpUtility;
import com.engc.smartedu.support.utils.EopClientConstants;

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
	 * 
	 * @param orgId
	 * @return
	 * @throws AppException
	 */
	public static List<SortModel> getFriendsByOrgId(String orgId)
			throws AppException {

		ClientRequest request = HttpUtility.getInstance().getEopClient()
				.buildClientRequest();
		request.addParam("orgId", orgId);
		// Map<String, String> params = new HashMap<String, String>();
		// params.put("orgId", orgId);
		List<SortModel> list=null;
		try {
			CompositeResponse<?> res = request.post(URLS.GET_FRIENDS_BY_ORGID,
					EopClientConstants.VERSION);
			// String result = HttpUtility.getInstance().executeNormalTask(
			// HttpMethod.Post, URLS.GET_FRIENDS_BY_ORGID, params);
			if (res.isSuccessful()) {
				list = JSON.parseArray(
						res.getResponseContent(), SortModel.class);
			}
			return list;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}
}
