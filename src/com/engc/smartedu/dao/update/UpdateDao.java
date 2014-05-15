package com.engc.smartedu.dao.update;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.engc.smartedu.bean.Update;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpMethod;
import com.engc.smartedu.support.http.HttpUtility;

/**
 * 应用升级  dao
 * 
 * @author Admin
 *
 */
public class UpdateDao {

	/**
	 * 检查版本更新
	 * 
	 * @param url
	 * @return
	 */
	public static Update checkVersion()
			throws AppException {
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("appName", "SP");
			String result=HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, URLS.GET_APP_VERSION, map);
			Update update = JSONObject.parseObject(result, Update.class);
			return update;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
}
