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
public class DummyActivity extends AbstractAppActivity {
	
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!GlobalContext.getInstance().startedApp) {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }

        finish();
    }
    
    
    
   
    
	
}