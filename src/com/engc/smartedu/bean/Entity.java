package com.engc.smartedu.bean;

import java.io.Serializable;

/**
 * 实体基类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public abstract class Entity  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int id;

	public int getId() {
		return id;
	}

	protected String cacheKey;

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public String IsError;
	private String Message;

	public String getIsError() {
		return IsError;
	}

	public void setIsError(String isError) {
		IsError = isError;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}
}
