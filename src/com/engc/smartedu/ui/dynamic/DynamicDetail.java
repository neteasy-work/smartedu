package com.engc.smartedu.ui.dynamic;

import java.util.List;
import java.util.TooManyListenersException;

import org.jivesoftware.smackx.pubsub.GetItemsRequest;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.Comment;
import com.engc.smartedu.support.lib.TimeLineAvatarImageView;
import com.engc.smartedu.support.lib.TimeLineImageView;
import com.engc.smartedu.support.utils.BitmapManager;
import com.engc.smartedu.support.utils.TimeTool;
import com.engc.smartedu.ui.adapter.CommentAdapter;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.send.WriteCommentActivity;
import com.engc.smartedu.ui.send.WriteRepostActivity;

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
	private ListView cListView;
	private TextView txtHeadTitle, txtReplyComent;
	private List<Comment> list;
	private TimeLineImageView contentPic;

	private MenuItem enableCommentOri;
	private MenuItem enableRepost;

	private boolean savedEnableCommentOri;
	private boolean savedEnableRepost;
	private FrameLayout contentPicLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dynamic_detial);
		initView();
		initData();
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("正文");

	}

	/**
	 * 显示回复评论对话框
	 * 
	 * @param headTitle
	 */
	private void showReplyCommentDialog(String headTitle) {
		final AlertDialog alg = new AlertDialog.Builder(DynamicDetail.this)
				.create();
		alg.show();
		Window window = alg.getWindow();
		window.setContentView(R.layout.comment_dialog);
		txtHeadTitle = (TextView) window
				.findViewById(R.id.txt_info_header_title);
		txtHeadTitle.setText(headTitle);
		txtReplyComent = (TextView) window.findViewById(R.id.txtreplycomment);
		txtReplyComent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DynamicDetail.this,
						WriteReplyToComment.class);
				startActivity(intent);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(
				R.menu.actionbar_menu_dynamic_detail, menu);
		/*
		 * enableCommentOri = menu.findItem(R.id.menu_enable_ori_comment);
		 * enableRepost = menu.findItem(R.id.menu_enable_repost);
		 * 
		 * enableCommentOri.setChecked(savedEnableCommentOri);
		 * enableRepost.setChecked(savedEnableRepost);
		 */
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

			break;
		case R.id.menu_refresh: //刷新
			Intent intent = new Intent(this, WriteRepostActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_comment:

			intent = new Intent(this, WriteReplyToComment.class);
			intent.putExtra("content", content.getText().toString());
			intent.putExtra("dynamicId", getIntent()
					.getStringExtra("dynamicId"));
			intent.putExtra("userName", getIntent().getStringExtra("userName"));
			startActivity(intent);

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
		contentPicLayout = (FrameLayout) findViewById(R.id.repost_and_pic);
		imgFace = (TimeLineAvatarImageView) findViewById(R.id.avatar);
		cListView = (ListView) findViewById(R.id.commentList);
		contentPic = (TimeLineImageView) findViewById(R.id.content_pic);
		cListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (list != null)
					showReplyCommentDialog(list.get(position).getUserName());

			}

		});

	}

	private void initData() {

		userName.setText(getIntent().getStringExtra("userName"));
		content.setText(getIntent().getStringExtra("content"));
		date.setText(TimeTool.getListTime(Long.valueOf(getIntent()
				.getStringExtra("date"))));
		String url = getIntent().getStringExtra("contentpic");
		if (!"".equals(url)) {
			//Bitmap bitmap = new BitmapManager().getBitmapFromCache(url);
			new BitmapManager().loadBitmap(url, contentPic);

		} else {
			contentPicLayout.setVisibility(View.GONE);
		}

		imgFace.setBackgroundResource(R.drawable.honey_face);
		list = (List<Comment>) getIntent().getSerializableExtra("commentList");
		if (list != null)
			cListView.setAdapter(new CommentAdapter(DynamicDetail.this, list,
					R.layout.timeline_listview_item_simple_layout));

	}

}
