package com.engc.smartedu.ui.friends;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.widget.ImageLoader;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: FriendInfo.java
 * @Package: com.engc.smartedu.ui.friends
 * @Description: 好友详情
 * @author: Administrator  
 * @date: 2014-5-22 下午3:40:39
 */
public class FriendInfo extends AbstractAppActivity {
	private TextView txtUserName;
	private ImageView imgFace;
	private String userName,userCode,imgURL;
	public ImageLoader imageLoader;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_info);
		userName=getIntent().getStringExtra("userName");
		userCode=getIntent().getStringExtra("userCode");
		imgURL=getIntent().getStringExtra("imgFace");
		initView();
		ActionBar ab=getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setTitle("详细资料");
		
	}

	private void initView(){
		txtUserName=(TextView) findViewById(R.id.name);
		imgFace=(ImageView) findViewById(R.id.avatar);
	    txtUserName.setText(userName);
	    imageLoader= new ImageLoader(FriendInfo.this);
	    imageLoader.DisplayImage(imgURL, imgFace);	
		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);

	}
	

	

}
