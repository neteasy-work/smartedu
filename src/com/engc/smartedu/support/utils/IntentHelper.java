package com.engc.smartedu.support.utils;

import android.content.Context;
import android.content.Intent;

import com.engc.smartedu.ui.main.MainTimeLineActivity;

/**
 * 意图跳转类
 * @author Admin
 *
 */
public class IntentHelper {
	public static void showMainTime(Context currentContext){
		Intent intent = new Intent(currentContext, MainTimeLineActivity.class);
		currentContext.startActivity(intent);
		
	}
	

}
