package com.engc.smartedu.bean;

import java.util.List;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * 
 * @Title: DynamicBean.java
 * @Package: com.engc.smartedu.bean
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-17 上午8:59:10
 */
public class DynamicBean extends Entity {
	private int commentCount;
	private String content;
	private String createDate;
	private int delfalg;
	private int feelgood;
	private int islock;
	private int sortnum;
	private String tid;
	private String userCode;
	private String userName;
	private String thumbnail;

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	private List<Comment> commentList;

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public String getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(String commentDate) {
		this.commentDate = commentDate;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	private String cid;
	private String commentContent;
	private String commentDate;
	private String topicId;

	public int getCommentCount() {
		return commentCount;
	}

	public List<Comment> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getDelfalg() {
		return delfalg;
	}

	public void setDelfalg(int delfalg) {
		this.delfalg = delfalg;
	}

	public int getFeelgood() {
		return feelgood;
	}

	public void setFeelgood(int feelgood) {
		this.feelgood = feelgood;
	}

	public int getIslock() {
		return islock;
	}

	public void setIslock(int islock) {
		this.islock = islock;
	}

	public int getSortnum() {
		return sortnum;
	}

	public void setSortnum(int sortnum) {
		this.sortnum = sortnum;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
