package com.engc.smartedu.bean;
/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: Comment.java
 * @Package: com.engc.smartedu.bean
 * @Description: 评论bean
 * @author: Administrator  
 * @date: 2014-6-17 上午9:04:10
 */
public class Comment extends Entity{
	private String cid;
	private String commentContent;
	private String commentDate;
	private String topicId;
	private String userName;
	private String userCode;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	

}
