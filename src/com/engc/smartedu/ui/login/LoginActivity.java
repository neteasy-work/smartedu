package com.engc.smartedu.ui.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.engc.smartedu.R;
import com.engc.smartedu.othercomponent.chat.AppService;
import com.engc.smartedu.othercomponent.chat.IConnectionStatusCallback;
import com.engc.smartedu.support.utils.AppLogger;
import com.engc.smartedu.support.utils.DialogUtil;
import com.engc.smartedu.support.utils.PreferenceConstants;
import com.engc.smartedu.support.utils.PreferenceUtils;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.main.MainTimeLineActivity;


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
		IConnectionStatusCallback {

	private static final int LOGIN_OUT_TIME = 0;
	public static final String LOGIN_ACTION = "com.engc.smartedu.action.LOGIN";
	private Dialog mLoginDialog;
	private ConnectionOutTimeProcess mLoginOutTimeProcess;
	private AppService appService;
	
	private AutoCompleteTextView actAccountName, actPassWord;
	private ImageView imgLogin;
	private String mAccount;
	private String mPassword;
	

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOGIN_OUT_TIME:
				if (mLoginOutTimeProcess != null
						&& mLoginOutTimeProcess.running)
					mLoginOutTimeProcess.stop();
				if (mLoginDialog != null && mLoginDialog.isShowing())
					mLoginDialog.dismiss();
				Utility.ToastMessage(LoginActivity.this, R.string.timeout_try_again);
				break;

			default:
				break;
			}
		}

	};
	
	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			appService = ((AppService.AppBinder) service).getService();
			appService.registerConnectionStatusCallback(LoginActivity.this);
			// 开始连接xmpp服务器
		}

		@Override	
		public void onServiceDisconnected(ComponentName name) {
			appService.unRegisterConnectionStatusCallback();
			appService = null;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(LoginActivity.this, AppService.class));
		bindXMPPService();
		setContentView(R.layout.login);
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (TextUtils.equals(PreferenceUtils.getPrefString(this,
				PreferenceConstants.APP_VERSION, ""),
				"V1.0.1")
				&& !TextUtils.isEmpty(PreferenceUtils.getPrefString(this,
						PreferenceConstants.ACCOUNT, ""))) {
		} else {
			PreferenceUtils.setPrefString(this,
					PreferenceConstants.APP_VERSION,
					"V1.0.1");
		}
		
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindXMPPService();
		if (mLoginOutTimeProcess != null) {
			mLoginOutTimeProcess.stop();
			mLoginOutTimeProcess = null;
		}
	}

	private void initView() {
		
		actAccountName = (AutoCompleteTextView) findViewById(R.id.login_account);
		actPassWord= (AutoCompleteTextView) findViewById(R.id.login_password);
		imgLogin = (ImageView) findViewById(R.id.btnlogin);
		String account = PreferenceUtils.getPrefString(this,	
				PreferenceConstants.ACCOUNT, "");
		String password = PreferenceUtils.getPrefString(this,
				PreferenceConstants.PASSWORD, "");
		//if (!TextUtils.isEmpty(account))
			actAccountName.setText("taowu78@gmail.com");
		//if (!TextUtils.isEmpty(password))
			actPassWord.setText("wutao824351");
		imgLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mAccount = actAccountName.getText().toString();
				mAccount = splitAndSaveServer(mAccount);
				mPassword = actPassWord.getText().toString();
			
				/*if (mLoginOutTimeProcess != null && !mLoginOutTimeProcess.running)
					mLoginOutTimeProcess.start();
				if (mLoginDialog != null && !mLoginDialog.isShowing())
					mLoginDialog.show();
				if (appService != null) {
					appService.Login(mAccount, mPassword);
				}*/
				clearNotification();
				
			}
		});
		/*actAccountName.addTextChangedListener(this);*/
 		mLoginDialog = DialogUtil.getLoginDialog(this);
		mLoginOutTimeProcess = new ConnectionOutTimeProcess();
	}

	public void onLoginClick(View v) {
		mAccount = actAccountName.getText().toString();
		mAccount = splitAndSaveServer(mAccount);
		mPassword = actPassWord.getText().toString();
		/*if (TextUtils.isEmpty(mAccount)) {
			T.showShort(this, R.string.null_account_prompt);
			return;
		}
		if (TextUtils.isEmpty(mPassword)) {
			T.showShort(this, R.string.password_input_prompt);
			return;
		}*/
		if (mLoginOutTimeProcess != null && !mLoginOutTimeProcess.running)
			mLoginOutTimeProcess.start();
		if (mLoginDialog != null && !mLoginDialog.isShowing())
			mLoginDialog.show();
		if (appService != null) {
			appService.Login(mAccount, mPassword);
		}
	}
	
	/**
	 * 清除所有通知栏
	 */
	private void clearNotification(){
		
		NotificationManager nm=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancelAll();
		
		Utility.ToastMessage(LoginActivity.this, Build.MODEL+"\n"+Build.VERSION.RELEASE+"\n"+Build.BRAND+"\n"+Build.DEVICE);
	}

	private String splitAndSaveServer(String account) {
		if (!account.contains("@"))
			return account;
		String customServer = PreferenceUtils.getPrefString(this,
				PreferenceConstants.CUSTOM_SERVER, "");
		String[] res = account.split("@");
		String userName = res[0];
		String server = res[1];
		// check for gmail.com and other google hosted jabber accounts
		if ("gmail.com".equals(server) || "googlemail.com".equals(server)
				|| PreferenceConstants.GMAIL_SERVER.equals(customServer)) {
			// work around for gmail's incompatible jabber implementation:
			// send the whole JID as the login, connect to talk.google.com
			userName = account;

		}
		PreferenceUtils.setPrefString(this, PreferenceConstants.Server, server);
		return userName;
	}

	private void unbindXMPPService() {
		try {
			unbindService(mServiceConnection);
			AppLogger.i(LoginActivity.class, "[SERVICE] Unbind");
		} catch (IllegalArgumentException e) {
			AppLogger.e(LoginActivity.class, "Service wasn't bound!");
		}
	}

	private void bindXMPPService() {
		AppLogger.i(LoginActivity.class, "[SERVICE] Unbind");
		Intent mServiceIntent = new Intent(this, AppService.class);
		mServiceIntent.setAction(LOGIN_ACTION);
		bindService(mServiceIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	/*@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}*/

/*	@Override
	public void afterTextChanged(Editable s) {
		try {
			XMPPHelper.verifyJabberID(s);
			mLoginBtn.setEnabled(true);
			mAccountEt.setTextColor(Color.parseColor("#ff333333"));
		} catch (AdressMalformedException e) {
			mLoginBtn.setEnabled(false);
			mAccountEt.setTextColor(Color.RED);
		}
	}*/
	
	private void save2Preferences() {
		/*boolean isAutoSavePassword = mAutoSavePasswordCK.isChecked();
		boolean isUseTls = mUseTlsCK.isChecked();
		boolean isSilenceLogin = mSilenceLoginCK.isChecked();
		boolean isHideLogin = mHideLoginCK.isChecked();*/
		PreferenceUtils.setPrefString(this, PreferenceConstants.ACCOUNT,
				mAccount);// 帐号是一直保存的
		//if (isAutoSavePassword)
			PreferenceUtils.setPrefString(this, PreferenceConstants.PASSWORD,
					mPassword);
		//else
			//PreferenceUtils.setPrefString(this, PreferenceConstants.PASSWORD,
			//		"");

		//PreferenceUtils.setPrefBoolean(this, PreferenceConstants.REQUIRE_TLS,
			//	isUseTls);
		//PreferenceUtils.setPrefBoolean(this, PreferenceConstants.SCLIENTNOTIFY,
			//	isSilenceLogin);
		//if (isHideLogin)
		//	PreferenceUtils.setPrefString(this,
			//		PreferenceConstants.STATUS_MODE, PreferenceConstants.XA);
		//else
			//PreferenceUtils.setPrefString(this,
				//	PreferenceConstants.STATUS_MODE,
				//	PreferenceConstants.AVAILABLE);
	}


	/**
	 * 网络状态改变
	 */
	@Override
	public void connectionStatusChanged(int connectedState, String reason) {
		if (mLoginDialog != null && mLoginDialog.isShowing()) {
			mLoginDialog.dismiss();
		}
		if (mLoginOutTimeProcess != null && mLoginOutTimeProcess.running) {
			mLoginOutTimeProcess.stop();
			mLoginOutTimeProcess = null;
		}
		if(connectedState==AppService.CONNECTED){
			save2Preferences();
			startActivity(new Intent(this,MainTimeLineActivity.class));
			finish();
		}else if(connectedState==AppService.DISCONNECTED){
				Utility.ToastMessage(LoginActivity.this, getString(R.string.request_failed)+reason);
		}

	}

	// 登录超时处理线程
	class ConnectionOutTimeProcess implements Runnable {
		public boolean running = false;
		private long startTime = 0L;
		private Thread thread = null;

		public ConnectionOutTimeProcess() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			while (true) {
				if (!this.running)
					return;
				if (System.currentTimeMillis() - startTime > 20 * 1000L) {
					mHandler.sendEmptyMessage(LOGIN_OUT_TIME);
				}
				try {

					Thread.sleep(10L);

				} catch (Exception e) {

				}
			}

		}

		public void start() {
			this.thread = new Thread(this);
			this.running = true;
			this.startTime = System.currentTimeMillis();
			this.thread.start();
		}

		public void stop() {
			this.running = false;
			this.thread = null;
			this.startTime = 0L;

		}

	}

}
