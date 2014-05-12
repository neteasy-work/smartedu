package com.engc.smartedu.dao.leave;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.engc.smartedu.bean.LeaveBean;
import com.engc.smartedu.bean.LeaveRecordList;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.dao.URLS;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.http.HttpMethod;
import com.engc.smartedu.support.http.HttpUtility;
import com.google.gson.Gson;

/**
 * 请假Dao
 * @author Admin
 *
 */
public class LeaveDao {
	
	/**
	 * 请假  
	 * @param userCode
	 * @param orgId
	 * @param telNo
	 * @param startDate
	 * @param endDate
	 * @param days
	 * @param remark
	 * @param holidayType
	 * @param userType
	 * @return
	 * @throws AppException
	 */
	public static User ApplyHolidays(
			final String userCode, final String orgId, final String telNo,
			final String startDate, final String endDate, final double days,
			final String remark, final int holidayType, final int userType)
			throws AppException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("usercode", userCode);
		params.put("orgid", orgId);
		params.put("leavetype", String.valueOf(holidayType));
		params.put("telno", telNo);
		params.put("begindate", startDate);
		params.put("enddate", endDate);
		params.put("dayno", String.valueOf(days));
		params.put("leavereason", remark);
		params.put("usertype", String.valueOf(userType));
		User user = null;
		try {
			String result = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, URLS.APPLY_HOLIDAYS, params);
			Gson gson = new Gson();
			user = gson.fromJson(result, User.class);
			return user;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 审核假期
	 * @param auditStatus
	 * @param auditContent
	 * @param userCode
	 * @param recordId
	 * @return
	 * @throws AppException
	 */
	public static User AuditHolidays(
			final String auditStatus, final String auditContent,
			final String userCode, final String recordId) throws AppException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("usercode", userCode);
		params.put("id", recordId);
		params.put("status", auditStatus);
		params.put("idea", auditContent);
		User user = null;
		try {
			String result = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, URLS.AUDIT_HOLIDAYS, params);
			Gson gson=new Gson();
			user = gson.fromJson(result, User.class);
			return user;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 更具ID 查询 未审核的假期记录
	 * 
	 * @param context
	 * @param id
	 * @return
	 * @throws AppException
	 */
	public static LeaveBean GetHolidayById( final String id)
			throws AppException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		LeaveBean leave = null;
		try {
			String result = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post,URLS.GET_APPLY_HOLIDAYS_INFO_BY_RECORDID, params);
	        Gson gson=new Gson();
	        leave = gson.fromJson(result, LeaveBean.class);
			return leave;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获得 所有 请假 审核 记录
	 * 
	 * @param appContext
	 * @param pageIndex
	 * @param pageSize
	 * @param userCode
	 * @param status
	 * @return
	 * @throws AppException
	 */
	public static LeaveRecordList getHolidayRecordList(
			final int pageIndex, final int pageSize, final String userCode,
			final String status) throws AppException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("pageIndex", "1");
		if (userCode != "0")
			map.put("usercode", userCode);
		if (status != null)
			map.put("status", status);
		LeaveRecordList recordList = new LeaveRecordList();
		try {

			String result = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post,
					URLS.GET_HOLIDAYS_RECORF_FOR_STUDENT, map);
			List<LeaveBean> list = JSON.parseArray(result, LeaveBean.class);
			recordList.getHolidayslist().addAll(list);
			return recordList;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}


}
