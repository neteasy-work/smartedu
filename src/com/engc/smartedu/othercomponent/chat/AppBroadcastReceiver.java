package com.engc.smartedu.othercomponent.chat;

import java.util.ArrayList;

import com.engc.smartedu.support.utils.AppLogger;
import com.engc.smartedu.support.utils.PreferenceConstants;
import com.engc.smartedu.support.utils.PreferenceUtils;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: AppBroadcastReceiver.java
 * @Package: com.engc.smartedu.lib
 * @Description: 应用BroadcastReceiver
 * @author: Administrator  
 * @date: 2014-4-9 下午1:17:49
 */
public class AppBroadcastReceiver extends BroadcastReceiver{
	 public static final String BOOT_COMPLETED_ACTION = "com.engc.smartedu.action.BOOT_COMPLETED";
	    public static ArrayList<EventHandler> mListeners = new ArrayList<EventHandler>();

	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getAction();
		AppLogger.i("action="+action);
		if(TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)){
			if(mListeners.size()>0){ //通知接口完成加载
				for(EventHandler handler:mListeners){
					handler.onNetChange();
				}
				
			}
		}else if(intent.getAction().equals(Intent.ACTION_SHUTDOWN)){
			AppLogger.d("System shutdown,stopping service");
			Intent appServiceIntent=new Intent(context,AppService.class);
			context.stopService(appServiceIntent);
		}else{
			if(!TextUtils.isEmpty(PreferenceUtils.getPrefString(context, PreferenceConstants.PASSWORD, ""))&&PreferenceUtils.getPrefBoolean(context, PreferenceConstants.AUTO_START, true)){
				Intent i=new Intent(context,AppService.class);
				i.setAction(BOOT_COMPLETED_ACTION);
				context.startService(i);
				
			}
		}
		
		
	}
	
	  public static abstract interface EventHandler {

	        public abstract void onNetChange();
	    }

}
