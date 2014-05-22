package com.engc.smartedu.dao.feedback;

import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.engc.eop.ClientRequest;
import com.engc.eop.CompositeResponse;
import com.engc.eop.EopClient;
import com.engc.smartedu.bean.FeedBack;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpMethod;
import com.engc.smartedu.support.http.HttpUtility;
import com.engc.smartedu.support.utils.EopClientConstants;
import com.engc.smartedu.support.utils.TimeTool;
import com.engc.smartedu.support.utils.TimeUtil;
import com.engc.smartedu.support.utils.Utility;

/**
 *  意见反馈
 * @author Admin
 *
 */
public class FeedBackDao {

	
	public static FeedBack addFeedBack(String author,String authorCode,String content)
			throws AppException {
		FeedBack fd=null;
		try {
			ClientRequest request=HttpUtility.getInstance().getEopClient().buildClientRequest();
			request.addParam("authorName", author);
			request.addParam("authorCode", authorCode);
			request.addParam("content", content);
			request.addParam("operationTime", TimeUtil.getCurrentTime());
			
			/*Map<String, String> map = new HashMap<String, String>();
			map.put("author", feedBack.getAuthor());
			map.put("authorCode", feedBack.getAuthorCode());
			map.put("content", feedBack.getContent());
		    map.put("operatTime", TimeUtil.getCurrentTime());*/
			CompositeResponse<?> res=request.post(URLS.FEED_BACK, EopClientConstants.VERSION);
			if(res.isSuccessful()){
				fd=JSON.parseObject(res.getResponseContent(),FeedBack.class);
				
			}
			//String result = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, URLS.FEED_BACK, map);
			// fd = JSON.parseObject(result, FeedBack.class);
			return fd;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
}
