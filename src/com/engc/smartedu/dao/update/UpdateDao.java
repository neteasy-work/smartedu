package com.engc.smartedu.dao.update;

import com.alibaba.fastjson.JSON;
import com.engc.eop.ClientRequest;
import com.engc.eop.CompositeResponse;
import com.engc.smartedu.bean.Update;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpUtility;
import com.engc.smartedu.support.utils.EopClientConstants;

/**
 * 应用升级 dao
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
	public static Update checkVersion() throws AppException {
		Update update = null;
		try {
			ClientRequest request = HttpUtility.getInstance().getEopClient()
					.buildClientRequest();
			// Map<String, String> map = new HashMap<String, String>();
			CompositeResponse<?> res = request.post(URLS.GET_APP_VERSION,
					EopClientConstants.VERSION);
			if (res.isSuccessful()) {
				update = JSON.parseObject(res.getResponseContent(),
						Update.class);
			}
			// String
			// result=HttpUtility.getInstance().executeNormalTask(HttpMethod.Post,
			// URLS.GET_APP_VERSION, map);
			// Update update = JSONObject.parseObject(result, Update.class);
			return update;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
}
