package com.engc.smartedu.ui.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.engc.smartedu.support.utils.PreferenceConstants;
import com.engc.smartedu.support.utils.PreferenceUtils;
import com.engc.smartedu.support.utils.SharePreferenceUtil;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
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
public class LoginActivity extends AbstractAppActivity implements OnClickListener,PushMessageReceiver.EventHandler
		 {

	private static final int LOGIN_OUT_TIME = 0;
	public static final String LOGIN_ACTION = "com.engc.smartedu.action.LOGIN";
	private Dialog mLoginDialog;
	private ConnectionOutTimeProcess mLoginOutTimeProcess;
	
	private AutoCompleteTextView actAccountName, actPassWord;
	private ImageView imgLogin;
	private String mAccount;
	private String mPassword;
	private GlobalContext global;
	private Gson mGson;
	private UserDB mUserDB;
	private SharePreferenceUtil mSpUtil;
	private View mNetErrorView;
	private String userCode,passWord;

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
	private void initData(){
		global=GlobalContext.getInstance();
		mLoginOutTimeProcess=new ConnectionOutTimeProcess();
		mSpUtil=global.getSpUtil();
		mGson=global.getGson();
		mUserDB=global.getUserDB();
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





	/**
	 * 初始化 视图
	 */
	private void initView() {
		mNetErrorView = findViewById(R.id.net_status_bar_top);
		actAccountName = (AutoCompleteTextView) findViewById(R.id.login_account);
		actPassWord= (AutoCompleteTextView) findViewById(R.id.login_password);
		imgLogin = (ImageView) findViewById(R.id.btnlogin);
		String account = PreferenceUtils.getPrefString(this,	
				PreferenceConstants.ACCOUNT, "");
		String password = PreferenceUtils.getPrefString(this,
				PreferenceConstants.PASSWORD, "");
		imgLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					User user=LoginDao.Login(userCode, passWord);
				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private Dialog mConnectServerDialog;
	
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
				Utility.ToastMessage(getApplicationContext(), R.string.login_user_name_hint);
				return;
			}
		     passWord=actPassWord.getText().toString();
			if(TextUtils.isEmpty(passWord)){
				Utility.ToastMessage(getApplicationContext(), R.string.login_password_hint);
				return;
			}
			
			
			PushManager.startWork(getApplicationContext(),
					PushConstants.LOGIN_TYPE_API_KEY, GlobalContext.API_KEY);// 无baidu帐号登录,以apiKey随机获取一个id
			mConnectServerDialog = DialogUtil.getRequestDialog(this, "正在登陆中...");
			mConnectServerDialog.show();
			mConnectServerDialog.setCancelable(false);// 返回键不能取消
			if (mLoginOutTimeProcess != null && !mLoginOutTimeProcess.running)
				mLoginOutTimeProcess.start();
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

					User user=LoginDao.Login(userCode, passWord);
					

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
							DummyActivity.class));
					if (mConnectServerDialog != null
							&& mConnectServerDialog.isShowing())
						mConnectServerDialog.dismiss();

					if (mLoginOutTimeProcess != null
							&& mLoginOutTimeProcess.running)
						mLoginOutTimeProcess.stop();
					AppLogger.d("Set Tag","连接服务器成功");
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
