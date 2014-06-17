package com.engc.smartedu.ui.dynamic;

import java.util.TooManyListenersException;

import org.jivesoftware.smackx.pubsub.GetItemsRequest;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.support.lib.TimeLineAvatarImageView;
import com.engc.smartedu.support.utils.TimeTool;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * 
 * @Title: DynamicDetail.java
 * @Package: com.engc.smartedu.ui.dynamic
 * @Description: 动态详细
 * @author: Administrator
 * @date: 2014-6-17 下午3:26:29
 */
@SuppressLint("NewApi")
public class DynamicDetail extends AbstractAppActivity {
	private TextView userName;
	private TextView content;
	private TextView date;
	private TimeLineAvatarImageView imgFace;
	private TextView commentCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dynamic);
		initView();
		initData();
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("正文");

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

	/**
	 * 初始化视图
	 */
	private void initView() {
		userName = (TextView) findViewById(R.id.username);
		content = (TextView) findViewById(R.id.content);
		date = (TextView) findViewById(R.id.time);
		imgFace = (TimeLineAvatarImageView) findViewById(R.id.avatar);
	}

	private void initData() {

		userName.setText(getIntent().getStringExtra("userName"));
		content.setText(getIntent().getStringExtra("content"));
		date.setText(TimeTool.getListTime(Long.valueOf(getIntent()
				.getStringExtra("date"))));
		imgFace.setBackgroundResource(R.drawable.honey_face);
	}

}
