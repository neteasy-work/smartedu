package com.engc.smartedu.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: DynamicListBean.java
 * @Package: com.engc.smartedu.bean
 * @Description: 动态list Bean
 * @author: Administrator  
 * @date: 2014-6-17 上午9:14:29
 */
public class DynamicListBean extends Entity{
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
	private int DynamicCount;
	private List<DynamicBean> dynamicList = new ArrayList<DynamicBean>();
	private List<Comment> commentList=new ArrayList<Comment>();

	
	public List<Comment> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}

	public int getDynamicCount() {
		return DynamicCount;
	}

	public void setDynamicCount(int dynamicCount) {
		DynamicCount = dynamicCount;
	}

	public List<DynamicBean> getDynamicList() {
		return dynamicList;
	}

	public void setDynamicList(List<DynamicBean> dynamicList) {
		this.dynamicList = dynamicList;
	}

	public static int getCatalogAll() {
		return CATALOG_ALL;
	}

	public static int getCatalogIntegration() {
		return CATALOG_INTEGRATION;
	}

	public static int getCatalogSoftware() {
		return CATALOG_SOFTWARE;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	

}
