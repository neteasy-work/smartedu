package com.engc.smartedu.dao.feedback;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.engc.smartedu.bean.FeedBack;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpMethod;
import com.engc.smartedu.support.http.HttpUtility;

/**
 *  意见反馈
 * @author Admin
 *
 */
public class FeedBackDao {

	
	public static FeedBack checkVersion(FeedBack feedback)
			throws AppException {
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("appName", "SP");
			String result = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, URLS.FEED_BACK, map);
			FeedBack fd = JSONObject.parseObject(result, FeedBack.class);
			return fd;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
}
