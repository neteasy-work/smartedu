package com.engc.smartedu.ui.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.engc.smartedu.R;
import com.engc.smartedu.baidupush.client.PushMessageReceiver;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.dao.login.LoginDao;
import com.engc.smartedu.othercomponent.chat.SendMsgAsyncTask;
import com.engc.smartedu.othercomponent.chat.SendMsgAsyncTask.OnSendScuessListener;
import com.engc.smartedu.support.database.UserDB;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.utils.AppLogger;
import com.engc.smartedu.support.utils.DialogUtil;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.IntentHelper;
import com.engc.smartedu.support.utils.PreferenceConstants;
import com.engc.smartedu.support.utils.PreferenceUtils;
import com.engc.smartedu.support.utils.SharePreferenceUtil;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.main.MainTimeLineActivity;
import com.google.gson.Gson;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * 
 * @Title: LoginActivity.java
 * @Package: com.engc.smartedu.ui.login
 * @Description: 登录 activity
 * @author: wutao
 * @date: 2014-3-18 上午9:25:41
 */
@SuppressLint("NewApi")
public class LoginActivity extends AbstractAppActivity implements
		OnClickListener, PushMessageReceiver.EventHandler {

	public static final String LOGIN_ACTION = "com.engc.smartedu.action.LOGIN";

	private AutoCompleteTextView actAccountName, actPassWord;
	private ImageView imgLogin;
	private GlobalContext global;
	private Gson mGson;
	private UserDB mUserDB;
	private SharePreferenceUtil mSpUtil;
	private View mNetErrorView;
	private String userCode, passWord;
	private Dialog loginDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		initData();
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utility.isConnected(this))
			mNetErrorView.setVisibility(View.VISIBLE);
		else
			mNetErrorView.setVisibility(View.GONE);
	}

	/**
	 * 初始化推送数据
	 */
	private void initData() {
		global = GlobalContext.getInstance();
		mSpUtil = global.getSpUtil();
		mGson = global.getGson();
		mUserDB = global.getUserDB();
		PushMessageReceiver.ehList.add(this); //监听推送消息

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (task != null)
			task.stop();
		PushMessageReceiver.ehList.remove(this);// 注销推送的消息
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * 初始化 视图
	 */
	private void initView() {
		mNetErrorView = findViewById(R.id.net_status_bar_top);
		mNetErrorView.setOnClickListener(this);
		actAccountName = (AutoCompleteTextView) findViewById(R.id.login_account);
		actPassWord = (AutoCompleteTextView) findViewById(R.id.login_password);
		imgLogin = (ImageView) findViewById(R.id.btnlogin);
		imgLogin.setOnClickListener(this);

	}

	/**
	 * 登陆 handler
	 * 
	 * @param accountName
	 * @param accountPwd
	 */
	private void login(final String accountName, final String accountPwd) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {

					User user = (User) msg.obj;
					//LoginDao.saveLoginInfo(LoginActivity.this,user);
					 //save2Preferences();
					PushManager.startWork(getApplicationContext(),
							PushConstants.LOGIN_TYPE_API_KEY, user.getUsercode());// 无baidu帐号登录,以apiKey随机获取一个id
					mSpUtil.setUserCode(user.getUsercode());
					mSpUtil.setUserName(user.getUsername());
					mSpUtil.setTag("man");
					mSpUtil.setHeadIcon(user.getHeadpic()); 
				    loginDialog.cancel();
					Intent intent = new Intent(LoginActivity.this,
							MainTimeLineActivity.class);
					startActivity(intent);
					if (user != null) {

					}
				} else if (msg.what == 0) {
					Utility.ToastMessage(LoginActivity.this,
							String.valueOf(msg.obj));
					loginDialog.cancel();

				} else if (msg.what == -1) {
					loginDialog.cancel();

				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();

				try {
					User user = LoginDao.Login(accountName, accountPwd);
					if (user != null) {
						if (user.getUsercode() != null) {
							LoginDao.saveLoginInfo(LoginActivity.this,user);
							msg.what = 1;// 成功
							msg.obj = user;

						} else {
							msg.what = 0;
							msg.obj = user.getMessage();
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnlogin:
			if (!Utility.isConnected(this)) {
				Utility.ToastMessage(this, R.string.network_not_connected);
				return;
			}
			userCode = actAccountName.getText().toString();
			if (TextUtils.isEmpty(userCode)) {
				Utility.ToastMessage(getApplicationContext(),
						R.string.login_user_name_hint);
				return;
			}
			passWord = actPassWord.getText().toString();
			if (TextUtils.isEmpty(passWord)) {
				Utility.ToastMessage(getApplicationContext(),
						R.string.login_password_hint);
				return;
			}

			loginDialog = DialogUtil.getRequestDialog(LoginActivity.this,
					"正在登陆中");
			loginDialog.show();
			Utility.initAnim(v.getContext(), (ImageView) loginDialog
					.findViewById(R.id.auth_loading_icon), R.anim.rotate);
			login(userCode, passWord);
			

			break;
		case R.id.net_status_bar_info_top:
			// 跳转到网络设置
			startActivity(new Intent(
					android.provider.Settings.ACTION_WIFI_SETTINGS));
			break;
		default:
			break;
		}
	}

	/**
	 * 保存用户名和密码
	 */
	private void save2Preferences() {

		PreferenceUtils.setPrefString(this, PreferenceConstants.ACCOUNT,
				userCode);// 帐号是一直保存的

		PreferenceUtils.setPrefString(this, PreferenceConstants.PASSWORD,
				userCode);
		

	}

	@Override
	public void onMessage(com.engc.smartedu.bean.Message message) {
		// TODO Auto-generated method stub

	}

	private SendMsgAsyncTask task;

	@Override
	public void onBind(String method, int errorCode, String content) {
		if (errorCode == 0) {// 如果绑定账号成功，由于第一次运行，给同一tag的人推送一条新人消息
			User u = new User(mSpUtil.getUserCode(), mSpUtil.getChannelId(),
					mSpUtil.getUserName(), mSpUtil.getHeadIcon());
			mUserDB.addUser(u);// 把自己添加到数据库
			com.engc.smartedu.bean.Message msgItem = new com.engc.smartedu.bean.Message(
					System.currentTimeMillis(), "hi", mSpUtil.getTag());
			task = new SendMsgAsyncTask(mGson.toJson(msgItem), "");
			task.setOnSendScuessListener(new OnSendScuessListener() {

				@Override
				public void sendScuess() {
					startActivity(new Intent(LoginActivity.this,
							MainTimeLineActivity.class));
					//AppLogger.d("Set Tag", "连接服务器成功");
					Utility.ToastMessage(LoginActivity.this, "链接服务器成功");
					finish();
				}
			});
			task.send();
		}

	}

	@Override
	public void onNotify(String title, String content) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNetChange(boolean isNetConnected) {

		if (!isNetConnected) {
			Utility.ToastMessage(this, R.string.network_not_connected);
			mNetErrorView.setVisibility(View.VISIBLE);
		} else {
			mNetErrorView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onNewFriend(User u) {
		// TODO Auto-generated method stub

	}

}
