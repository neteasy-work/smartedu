package com.engc.smartedu.dao.feedback;

import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;

import com.alibaba.fastjson.JSONObject;
import com.engc.smartedu.bean.FeedBack;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpMethod;
import com.engc.smartedu.support.http.HttpUtility;
import com.engc.smartedu.support.utils.TimeTool;
import com.engc.smartedu.support.utils.TimeUtil;
import com.engc.smartedu.support.utils.Utility;

/**
 *  意见反馈
 * @author Admin
 *
 */
public class FeedBackDao {

	
	public static FeedBack addFeedBack(FeedBack feedBack)
			throws AppException {
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("author", feedBack.getAuthor());
			map.put("authorCode", feedBack.getAuthorCode());
			map.put("content", feedBack.getContent());
		    map.put("operatTime", TimeUtil.getCurrentTime());
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
