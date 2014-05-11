package com.engc.smartedu.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 假期 结果集   bean
 * @author Admin
 *
 */
public class LeaveRecordList extends Entity {

	public final static int CATALOG_ALL = 1;
	public final static int CATALOG_INTEGRATION = 2;
	public final static int CATALOG_SOFTWARE = 3;

	private int catalog;

	public int getCatalog() {
		return catalog;
	}

	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}

	private int pageSize;
	private int holidaysCount;
	private List<LeaveBean> holidayslist = new ArrayList<LeaveBean>();

	public int getPageSize() {
		return pageSize;
	}

	public int getHolidaysCount() {
		return holidaysCount;
	}

	public void setHolidaysCount(int holidaysCount) {
		this.holidaysCount = holidaysCount;
	}

	public List<LeaveBean> getHolidayslist() {
		return holidayslist;
	}

	public void setHolidayslist(List<LeaveBean> holidayslist) {
		this.holidayslist = holidayslist;
	}

	


}
