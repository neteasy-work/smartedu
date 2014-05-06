package com.engc.smartedu.othercomponent.chat;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: IConnectionStatusCallback.java
 * @Package: com.engc.smartedu.service
 * @Description: 监测网络状态 
 * @author: wutao  
 * @date: 2014-4-9 上午10:12:35
 */
public interface IConnectionStatusCallback {
	public void connectionStatusChanged(int connectedState, String reason);

}
