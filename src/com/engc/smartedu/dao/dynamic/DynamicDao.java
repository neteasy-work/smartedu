package com.engc.smartedu.dao.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.engc.eop.ClientRequest;
import com.engc.eop.CompositeResponse;
import com.engc.smartedu.bean.Comment;
import com.engc.smartedu.bean.CommentList;
import com.engc.smartedu.bean.DynamicBean;
import com.engc.smartedu.bean.DynamicListBean;
import com.engc.smartedu.bean.LeaveBean;
import com.engc.smartedu.bean.LeaveRecordList;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpUtility;
import com.engc.smartedu.support.utils.EopClientConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * 
 * @Title: DynamicDao.java
 * @Package: com.engc.smartedu.dao.dynamic
 * @Description: 动态数据访问层
 * @author: Administrator
 * @date: 2014-6-17 上午8:57:15
 */
public class DynamicDao {
	public static DynamicListBean getDynamicList(String pageNum,
			String schoolOrgid) throws AppException {
		ClientRequest request = HttpUtility.getInstance().getEopClient()
				.buildClientRequest();
		request.addParam("schoolOrgid", schoolOrgid);
		request.addParam("pageNum", pageNum);
		request.addParam("pageSize", "20");
		DynamicListBean dynamicList = new DynamicListBean();
		List<DynamicBean> list = new ArrayList<DynamicBean>();
		List<Map<String, Object>> mapParams = null;
		try {

			CompositeResponse<?> res = request.post(URLS.GET_DYNAMIC,
					EopClientConstants.VERSION);
			if (res.isSuccessful()) {
				/*
				 * list = JSON.parseArray(res.getResponseContent(),
				 * HashMap.class);
				 */
				// list=JSON.parseArray(res.getResponseContent(),
				// Map<Object,Object>());

				mapParams = JSON.parseObject(res.getResponseContent(),
						new TypeReference<List<Map<String, Object>>>() {
						});

			}
			for (int i = 0; i < mapParams.size(); i++) {
				Map<String, Object> temp = mapParams.get(i);
				Comment coment = new Comment();
				if (temp.get("commentList") != null) {
					List<Comment> cList = JSON.parseArray(
							temp.get("commentList").toString(), Comment.class);
					dynamicList.getCommentList().addAll(cList);
					/*
					 * if (commitMap != null) { for (int z = 0; z <
					 * commitMap.size(); z++) {
					 * coment.setCid(commitMap.get("cid").toString()); } }
					 */
				}

			}

			// String result = HttpUtility.getInstance().executeNormalTask(
			// HttpMethod.Post, URLS.GET_HOLIDAYS_RECORF_FOR_STUDENT, map);
			// List<LeaveBean> list = JSON.parseArray(result, LeaveBean.class);

			if (list.size() > 0)
				dynamicList.getDynamicList().addAll(list);
			return dynamicList;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}

}
