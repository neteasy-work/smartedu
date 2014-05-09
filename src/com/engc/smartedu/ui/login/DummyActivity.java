package com.engc.smartedu.ui.login;

import android.content.Intent;
import android.os.Bundle;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.engc.smartedu.R;
import com.engc.smartedu.baidupush.client.PushMessageReceiver;
import com.engc.smartedu.bean.Message;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.othercomponent.chat.SendMsgAsyncTask;
import com.engc.smartedu.othercomponent.chat.SendMsgAsyncTask.OnSendScuessListener;
import com.engc.smartedu.support.database.UserDB;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.SharePreferenceUtil;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.main.MainTimeLineActivity;
import com.google.gson.Gson;

/**
 * User: qii
 * Date: 12-9-1
 * <p/>
 * fuck android
 * <p/>
 * 1.open developer option - dont save activity
 * 2.open this app, them open write weibo activity
 * 3.press home button to reach android home screen
 * 4.press this app's icon from home screen or app launcher to return
 * 5.it restarts! the write weibo activity interface is disappeared! fuck fuck
 * 6.dummyactivity is a workaround to solve this bug
 * <p/>
 * test on android version 4.0 4.2
 */
public class DummyActivity extends AbstractAppActivity implements PushMessageReceiver.EventHandler{
	
	private GlobalContext global;
	private SharePreferenceUtil mSpUtil;
	private Gson mGson;
	private UserDB mUserDB;
	private SendMsgAsyncTask task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initView();
        if (!GlobalContext.getInstance().startedApp) {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }

        finish();
    }
    
    private void initData() {
		// TODO Auto-generated method stub
    	global=GlobalContext.getInstance();
		mSpUtil = global.getSpUtil();
		mGson = global.getGson();
		mUserDB = global.getUserDB();
		PushMessageReceiver.ehList.add(this);// 监听推送的消息
	}
    
    private void initView(){
    	mSpUtil.setUserName("小强");
		mSpUtil.setHeadIcon("http://www.baidu.com");
		mSpUtil.setTag("man");
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, GlobalContext.API_KEY);// 无baidu帐号登录,以apiKey随机获取一个id
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
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBind(String method, int errorCode, String content) {
		if (errorCode == 0) {// 如果绑定账号成功，由于第一次运行，给同一tag的人推送一条新人消息
			User u = new User(mSpUtil.getUserCode(), mSpUtil.getChannelId(),
					mSpUtil.getUserName(), mSpUtil.getHeadIcon());
			mUserDB.addUser(u);// 把自己添加到数据库
			Message msgItem = new Message(
					System.currentTimeMillis(), "hi", mSpUtil.getTag());
			task = new SendMsgAsyncTask(mGson.toJson(msgItem), "");
			task.setOnSendScuessListener(new OnSendScuessListener() {

				@Override
				public void sendScuess() {
					startActivity(new Intent(DummyActivity.this,
						   MainTimeLineActivity.class));
					

					
					Utility.ToastMessage(global, "首次启动，注入tag 成功");
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
		if (!isNetConnected) 
			Utility.ToastMessage(this, "网络连接发生异常，请检查网络");
		
	}

	@Override
	public void onNewFriend(User u) {
		// TODO Auto-generated method stub
		
	}
}