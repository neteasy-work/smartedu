package com.engc.smartedu.bean;

import java.io.Serializable;

import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.SharePreferenceUtil;
import com.google.gson.annotations.Expose;


public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	private String usercode;
	@Expose
	private String channel_id;
	@Expose
	private String username;
	@Expose
	private String head_icon;
	@Expose
	private long time_samp;
	@Expose
	private String message;
	@Expose
	private String tag;

	public Message(long time_samp, String message, String tag) {
		super();
		SharePreferenceUtil util = GlobalContext.getInstance().getSpUtil();
		this.usercode = util.getUserCode();
		this.channel_id = util.getChannelId();
		this.username = util.getUserName();
		this.head_icon = util.getHeadIcon();
		this.time_samp = time_samp;
		this.message = message;
		this.tag = tag;
	}

	

	public String getUsercode() {
		return usercode;
	}



	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	


	public String getHead_icon() {
		return head_icon;
	}



	public void setHead_icon(String head_icon) {
		this.head_icon = head_icon;
	}



	public String getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

	

	public long getTime_samp() {
		return time_samp;
	}

	public void setTime_samp(long time_samp) {
		this.time_samp = time_samp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return "Message [usercode=" + usercode + ", channel_id=" + channel_id
				+ ", username=" + username + ", head_icon=" + head_icon + ", time_samp="
				+ time_samp + ", message=" + message + ", tag=" + tag + "]";
	}

}
