package com.engc.smartedu.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: FriendListBean.java
 * @Package: com.engc.smartedu.bean
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-5-16 下午4:02:25
 */
public class FriendListBean extends Entity {
	
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
	private int friendsCount;
	private List<FriendBean> friendsList = new ArrayList<FriendBean>();

	public int getPageSize() {
		return pageSize;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public List<FriendBean> getFriendsList() {
		return friendsList;
	}

	public void setFriendsList(List<FriendBean> friendsList) {
		this.friendsList = friendsList;
	}

	

	

}
