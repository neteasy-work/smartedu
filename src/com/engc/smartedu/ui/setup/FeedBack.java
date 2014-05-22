package com.engc.smartedu.ui.setup;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.engc.smartedu.R;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.dao.feedback.FeedBackDao;
import com.engc.smartedu.dao.login.LoginDao;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.utils.DialogUtil;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.login.LoginActivity;
import com.engc.smartedu.ui.main.MainTimeLineActivity;

/**
 * 意见反馈
 * 
 * @author Admin
 * 
 */

@SuppressLint("NewApi")
public class FeedBack extends AbstractAppActivity {
	private AutoCompleteTextView actFeedback;
	private Dialog requestDialog;
	private String feedBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("意见反馈");
		initView();
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

	private void initView() {
		actFeedback = (AutoCompleteTextView) findViewById(R.id.actfeedback);

	}

	private void initData() {
		feedBack = actFeedback.getText().toString();
		if (TextUtils.isEmpty(feedBack)) {
			Utility.ToastMessage(getApplicationContext(),
					R.string.feedback_content_hint);
			return;
		}

		requestDialog = DialogUtil.getRequestDialog(FeedBack.this, "正在提交...");
		requestDialog.show();
		Utility.initAnim(FeedBack.this,
				(ImageView) requestDialog.findViewById(R.id.auth_loading_icon),
				R.anim.rotate);
		sendFeedBack(feedBack);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem sendItem = menu.add(0, 0, 0, "发送");
		sendItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		sendItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				initData();

				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);

	}

	/**
	 * 发送意见反馈
	 * 
	 * @param accountName
	 * @param accountPwd
	 */
	private void sendFeedBack(final String content) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {

					com.engc.smartedu.bean.FeedBack fd = (com.engc.smartedu.bean.FeedBack) msg.obj;
					requestDialog.cancel();
					Utility.ToastMessage(FeedBack.this, fd.getMessage());
					/*
					 * Intent intent = new Intent(F.this,
					 * MainTimeLineActivity.class); startActivity(intent);
					 */

				} else if (msg.what == 0) {
					Utility.ToastMessage(FeedBack.this, String.valueOf(msg.obj));
					requestDialog.cancel();

				} else if (msg.what == -1) {
					requestDialog.cancel();

				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();

				try {
					com.engc.smartedu.bean.FeedBack fd = FeedBackDao
							.addFeedBack(LoginDao.getLoginInfo(FeedBack.this)
									.getUsername(),
									LoginDao.getLoginInfo(FeedBack.this)
											.getUsercode(), content);
					if (fd != null) {
						if (!fd.IsError.equals("true")) {

							msg.what = 1;// 成功
							msg.obj = fd;

						} else {
							msg.what = 0;
							msg.obj = fd.getMessage();
						}
					} else {
						msg.what = 0;
						msg.obj = getString(R.string.http_exception_error);
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}

				handler.sendMessage(msg);
			}
		}.start();
	}
}
