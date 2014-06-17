package com.engc.smartedu.bean;

import java.util.ArrayList;
import java.util.List;

public class CommentList extends Entity{
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
	private int commentCount;
	private List<Comment> commentList = new ArrayList<Comment>();

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public List<Comment> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
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
