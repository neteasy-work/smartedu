package com.engc.smartedu.othercomponent.chat;

import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

import com.engc.smartedu.baidupush.server.BaiduPush;
import com.engc.smartedu.support.utils.AppLogger;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.NetUtility;
import com.engc.smartedu.support.utils.Utility;



public class SendMsgAsyncTask {
	private BaiduPush mBaiduPush;
	private String mMessage;
	private Handler mHandler;
	private MyAsyncTask mTask;
	private String mUserId;
	private OnSendScuessListener mListener;

	public interface OnSendScuessListener {
		void sendScuess();
	}

	public void setOnSendScuessListener(OnSendScuessListener listener) {
		this.mListener = listener;
	}

	Runnable reSend = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			AppLogger.i("resend msg...");
			send();//重发
		}
	};

	public SendMsgAsyncTask(String jsonMsg,String useId) {
		// TODO Auto-generated constructor stub
		mBaiduPush = GlobalContext.getInstance().getBaiduPush();
		mMessage = jsonMsg;
		mUserId = useId;
		mHandler = new Handler();
	}

	// 发送
	public void send() {
		if (NetUtility.isConnected(GlobalContext.getInstance())) {//如果网络可用
			mTask = new MyAsyncTask();
			mTask.execute();
		} else {
			Utility.ToastMessage(GlobalContext.getInstance(), "网络发生异常，请检查");
		}
	}

	// 停止
	public void stop() {
		if (mTask != null)
			mTask.cancel(true);
	}

	class MyAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... message) {
			String result = "";
			if(TextUtils.isEmpty(mUserId))
				result = mBaiduPush.PushMessage(mMessage);
			else
				result = mBaiduPush.PushMessage(mMessage, mUserId);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			AppLogger.i("send msg result:"+result);
			if (result.contains(BaiduPush.SEND_MSG_ERROR)) {// 如果消息发送失败，则100ms后重发
				mHandler.postDelayed(reSend, 100);
			} else {
				if (mListener != null)
					mListener.sendScuess();
			}
		}
	}
}
