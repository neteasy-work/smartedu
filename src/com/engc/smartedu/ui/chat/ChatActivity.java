package com.engc.smartedu.ui.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.baidupush.client.PushMessageReceiver;
import com.engc.smartedu.bean.Message;
import com.engc.smartedu.bean.MessageItem;
import com.engc.smartedu.bean.RecentItem;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.othercomponent.chat.SendMsgAsyncTask;
import com.engc.smartedu.support.database.MessageDB;
import com.engc.smartedu.support.database.RecentDB;
import com.engc.smartedu.support.utils.AppLogger;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.HomeWatcher;
import com.engc.smartedu.support.utils.HomeWatcher.OnHomePressedListener;
import com.engc.smartedu.support.utils.SharePreferenceUtil;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.adapter.FaceAdapter;
import com.engc.smartedu.ui.adapter.FacePageAdapter;
import com.engc.smartedu.ui.adapter.MessageAdapter;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.leave.LeaveActivity;
import com.engc.smartedu.ui.main.MainTimeLineActivity;
import com.engc.smartedu.ui.msglistview.MsgListView;
import com.engc.smartedu.ui.msglistview.MsgListView.IXListViewListener;
import com.engc.smartedu.widget.CirclePageIndicator;
import com.engc.smartedu.widget.JazzyViewPager;
import com.engc.smartedu.widget.JazzyViewPager.TransitionEffect;
import com.google.gson.Gson;

@SuppressLint("NewApi")
public class ChatActivity extends AbstractAppActivity implements
		OnTouchListener, OnClickListener, PushMessageReceiver.EventHandler,
		OnHomePressedListener, IXListViewListener {

	public static final int NEW_MESSAGE = 0x001;// 收到消息
	private TransitionEffect mEffects[] = { TransitionEffect.Standard,
			TransitionEffect.Tablet, TransitionEffect.CubeIn,
			TransitionEffect.CubeOut, TransitionEffect.FlipVertical,
			TransitionEffect.FlipHorizontal, TransitionEffect.Stack,
			TransitionEffect.ZoomIn, TransitionEffect.ZoomOut,
			TransitionEffect.RotateUp, TransitionEffect.RotateDown,
			TransitionEffect.Accordion, };// 表情翻页效果

	private GlobalContext global;

	public static final String INTENT_EXTRA_USERNAME = ChatActivity.class
			.getName() + ".username";// 昵称对应的key
	private JazzyViewPager mFaceViewPager;// 表情选择ViewPager
	private int mCurrentPage = 0;// 当前表情页
	private boolean mIsFaceShow = false;// 是否显示表情
	private Button mSendMsgBtn;// 发送消息button
	private ImageButton mFaceSwitchBtn;// 切换键盘和表情的button
	private TextView mTitleNameView;// 标题栏
	// private ImageView mTitleStatusView;
	private WindowManager.LayoutParams mWindowNanagerParams;
	private InputMethodManager mInputMethodManager;
	private static int MsgPagerNum;
	private MessageDB mMsgDB;
	private RecentDB mRecentDB;
	private int currentPage = 0;
	private boolean isFaceShow = false;
	private ImageButton faceBtn;
	private EditText msgEt;
	private LinearLayout faceLinearLayout;
	private WindowManager.LayoutParams params;
	private List<String> keys;
	private MessageAdapter adapter;
	private MsgListView mMsgListView;
	private SharePreferenceUtil mSpUtil;
	private User mFromUser;
	private TextView mTitle, mTitleLeftBtn, mTitleRightBtn;
	private HomeWatcher mHomeWatcher;
	private Gson mGson;
	private Message notificationMessage; // 通知栏 中 信息
	private ActionBar actionBar;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == NEW_MESSAGE) {
				// String message = (String) msg.obj;
				com.engc.smartedu.bean.Message msgItem = (com.engc.smartedu.bean.Message) msg.obj;
				String userId = msgItem.getUsercode();
				if (!userId.equals(mFromUser.getUsercode()))// 如果不是当前正在聊天对象的消息，不处理
					return;

				// int headId = msgItem.getHead_id();
				/*
				 * try { headId = Integer
				 * .parseInt(JsonUtil.getFromUserHead(message)); } catch
				 * (Exception e) { L.e("head is not integer  " + e); }
				 */
				// TODO Auto-generated method stub
				MessageItem item = new MessageItem(
						MessageItem.MESSAGE_TYPE_TEXT, msgItem.getUsername(),
						System.currentTimeMillis(), msgItem.getMessage(), "",
						true, 0);
				adapter.upDateMsg(item);
				mMsgDB.saveMsg(msgItem.getUsercode(), item);
				RecentItem recentItem = new RecentItem(userId, "",
						msgItem.getUsername(), msgItem.getMessage(), 0,
						System.currentTimeMillis());
				mRecentDB.saveRecent(recentItem);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		initData(); // 初始化 本地数据
		initView();// 初始化view
		initFacePage(); // 初始化表情 page
		if (notificationMessage != null)
			showNotificationMessageToDB();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(this);
		mHomeWatcher.startWatch();
		PushMessageReceiver.ehList.add(this);// 监听推送的消息
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainTimeLineActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
            break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mInputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
		faceLinearLayout.setVisibility(View.GONE);
		isFaceShow = false;
		super.onPause();
		mHomeWatcher.setOnHomePressedListener(null);
		mHomeWatcher.stopWatch();
		PushMessageReceiver.ehList.remove(this);// 移除监听
	}

	/**
	 * 点击通知栏 将信息保存到Recent DB 中 并刷新 chat 窗体
	 */
	private void showNotificationMessageToDB() {
		actionBar.setTitle(notificationMessage.getUsername());
		MessageItem item = new MessageItem(MessageItem.MESSAGE_TYPE_TEXT,
				notificationMessage.getUsername(), System.currentTimeMillis(),
				notificationMessage.getMessage(), "", true, 0);
		adapter.upDateMsg(item);
		mMsgDB.saveMsg(notificationMessage.getUsercode(), item);
		RecentItem recentItem = new RecentItem(
				notificationMessage.getUsercode(), "",
				notificationMessage.getUsername(),
				notificationMessage.getMessage(), 0, System.currentTimeMillis());
		mRecentDB.saveRecent(recentItem);
	}

	/**
	 * 初始化SQLite 中基本数据
	 */
	private void initData() {
		mFromUser = (User) getIntent().getSerializableExtra("user");
		if (mFromUser == null) {// 如果为空，直接关闭
			finish();
		}
		notificationMessage = (Message) getIntent().getSerializableExtra(
				"message");
		actionBar.setTitle(mFromUser.getUsername());
		global = GlobalContext.getInstance();
		mSpUtil = global.getSpUtil();
		mGson = global.getGson();
		mMsgDB = global.getMessageDB();
		mRecentDB = global.getRecentDB();
		Set<String> keySet = global.getFaceMap().keySet();
		keys = new ArrayList<String>();
		keys.addAll(keySet);
		MsgPagerNum = 0;
		adapter = new MessageAdapter(this, initMsgData());
	}

	/**
	 * 加载消息历史，从数据库中读出
	 */
	private List<MessageItem> initMsgData() {
		List<MessageItem> list = mMsgDB.getMsg(mFromUser.getUsercode(),
				MsgPagerNum);
		List<MessageItem> msgList = new ArrayList<MessageItem>();// 消息对象数组
		if (list.size() > 0) {
			for (MessageItem entity : list) {
				if (entity.getName().equals("")) {
					entity.setName(mFromUser.getUsername());
				}
				// if (entity.getHeadImg() < 0) {
				// entity.setHeadImg(mFromUser.getHeadIcon());
				// }
				msgList.add(entity);
			}
		}
		return msgList;

	}

	private void initView() {
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mWindowNanagerParams = getWindow().getAttributes();

		mMsgListView = (MsgListView) findViewById(R.id.msg_listView);
		// 触摸ListView隐藏表情和输入法
		// 触摸ListView隐藏表情和输入法
		mMsgListView.setOnTouchListener(this);
		mMsgListView.setPullLoadEnable(false);
		mMsgListView.setXListViewListener(this);
		mMsgListView.setAdapter(adapter);
		mMsgListView.setSelection(adapter.getCount() - 1);
		mSendMsgBtn = (Button) findViewById(R.id.send);
		mFaceSwitchBtn = (ImageButton) findViewById(R.id.face_switch_btn);
		msgEt = (EditText) findViewById(R.id.input);
		msgEt.setOnTouchListener(this);
		faceLinearLayout = (LinearLayout) findViewById(R.id.face_ll);
		mFaceViewPager = (JazzyViewPager) findViewById(R.id.face_pager);
		msgEt.setOnTouchListener(this);

		// mTitleNameView = (TextView) findViewById(R.id.ivTitleName);
		// mTitleNameView.setText(mFromUser.getUsername());
		msgEt.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (params.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
							|| isFaceShow) {
						faceLinearLayout.setVisibility(View.GONE);
						isFaceShow = false;
						// imm.showSoftInput(msgEt, 0);
						return true;
					}
				}
				return false;
			}
		});
		msgEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					mSendMsgBtn.setEnabled(true);
				} else {
					mSendMsgBtn.setEnabled(false);
				}
			}
		});
		mFaceSwitchBtn.setOnClickListener(this);
		mSendMsgBtn.setOnClickListener(this);
	}

	/**
	 * 初始化表情page
	 */
	private void initFacePage() {
		// TODO Auto-generated method stub
		List<View> lv = new ArrayList<View>();
		for (int i = 0; i < global.NUM_PAGE; ++i)
			lv.add(getGridView(i));
		FacePageAdapter adapter = new FacePageAdapter(lv, mFaceViewPager);
		mFaceViewPager.setAdapter(adapter);
		mFaceViewPager.setCurrentItem(currentPage);
		mFaceViewPager.setTransitionEffect(mEffects[mSpUtil.getFaceEffect()]);
		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mFaceViewPager);
		adapter.notifyDataSetChanged();
		faceLinearLayout.setVisibility(View.GONE);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentPage = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// do nothing
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// do nothing
			}
		});

	}

	/**
	 * 获得表情 gridview
	 * 
	 * @param i
	 * @return
	 */
	private GridView getGridView(int i) {
		// TODO Auto-generated method stub
		GridView gv = new GridView(this);
		gv.setNumColumns(7);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
		gv.setBackgroundColor(Color.TRANSPARENT);
		gv.setCacheColorHint(Color.TRANSPARENT);
		gv.setHorizontalSpacing(1);
		gv.setVerticalSpacing(1);
		gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		gv.setGravity(Gravity.CENTER);
		gv.setAdapter(new FaceAdapter(this, i));
		gv.setOnTouchListener(forbidenScroll());
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == global.NUM) {// 删除键的位置
					int selection = msgEt.getSelectionStart();
					String text = msgEt.getText().toString();
					if (selection > 0) {
						String text2 = text.substring(selection - 1);
						if ("]".equals(text2)) {
							int start = text.lastIndexOf("[");
							int end = selection;
							msgEt.getText().delete(start, end);
							return;
						}
						msgEt.getText().delete(selection - 1, selection);
					}
				} else {
					int count = currentPage * global.NUM + arg2;
					// 注释的部分，在EditText中显示字符串
					// String ori = msgEt.getText().toString();
					// int index = msgEt.getSelectionStart();
					// StringBuilder stringBuilder = new StringBuilder(ori);
					// stringBuilder.insert(index, keys.get(count));
					// msgEt.setText(stringBuilder.toString());
					// msgEt.setSelection(index + keys.get(count).length());

					// 下面这部分，在EditText中显示表情
					Bitmap bitmap = BitmapFactory.decodeResource(
							getResources(), (Integer) global.getFaceMap()
									.values().toArray()[count]);
					if (bitmap != null) {
						int rawHeigh = bitmap.getHeight();
						int rawWidth = bitmap.getHeight();
						int newHeight = 40;
						int newWidth = 40;
						// 计算缩放因子
						float heightScale = ((float) newHeight) / rawHeigh;
						float widthScale = ((float) newWidth) / rawWidth;
						// 新建立矩阵
						Matrix matrix = new Matrix();
						matrix.postScale(heightScale, widthScale);
						// 设置图片的旋转角度
						// matrix.postRotate(-30);
						// 设置图片的倾斜
						// matrix.postSkew(0.1f, 0.1f);
						// 将图片大小压缩
						// 压缩后图片的宽和高以及kB大小均会变化
						Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
								rawWidth, rawHeigh, matrix, true);
						ImageSpan imageSpan = new ImageSpan(ChatActivity.this,
								newBitmap);
						String emojiStr = keys.get(count);
						SpannableString spannableString = new SpannableString(
								emojiStr);
						spannableString.setSpan(imageSpan,
								emojiStr.indexOf('['),
								emojiStr.indexOf(']') + 1,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						msgEt.append(spannableString);
					} else {
						String ori = msgEt.getText().toString();
						int index = msgEt.getSelectionStart();
						StringBuilder stringBuilder = new StringBuilder(ori);
						stringBuilder.insert(index, keys.get(count));
						msgEt.setText(stringBuilder.toString());
						msgEt.setSelection(index + keys.get(count).length());
					}
				}
			}
		});
		return gv;
	}

	// 防止乱pageview乱滚动
	private OnTouchListener forbidenScroll() {
		return new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.face_switch_btn:
			if (!mIsFaceShow) {
				mInputMethodManager.hideSoftInputFromWindow(
						msgEt.getWindowToken(), 0);
				try {
					Thread.sleep(80);// 解决此时会黑一下屏幕的问题
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				faceLinearLayout.setVisibility(View.VISIBLE);
				mFaceSwitchBtn.setImageResource(R.drawable.aio_keyboard);
				mIsFaceShow = true;
			} else {
				faceLinearLayout.setVisibility(View.GONE);
				mInputMethodManager.showSoftInput(msgEt, 0);
				mFaceSwitchBtn
						.setImageResource(R.drawable.qzone_edit_face_drawable);
				mIsFaceShow = false;
			}

			
			break;
		case R.id.send:// 发送消息

			String msg = msgEt.getText().toString();
			MessageItem item = new MessageItem(MessageItem.MESSAGE_TYPE_TEXT,
					mSpUtil.getUserName(), System.currentTimeMillis(), msg,
					mSpUtil.getHeadIcon(), false, 0);
			adapter.upDateMsg(item);
			// if (adapter.getCount() - 10 > 10) {
			// L.i("begin to remove...");
			// adapter.removeHeadMsg();
			// MsgPagerNum--;
			// }
			mMsgListView.setSelection(adapter.getCount() - 1);
			mMsgDB.saveMsg(mFromUser.getUsercode(), item);
			msgEt.setText("");
			com.engc.smartedu.bean.Message msgItem = new com.engc.smartedu.bean.Message(
					System.currentTimeMillis(), msg, "");
			new SendMsgAsyncTask(mGson.toJson(msgItem), mFromUser.getUsercode())
					.send();
			RecentItem recentItem = new RecentItem(mFromUser.getUsercode(), "",
					mFromUser.getUsername(), msg, 0, System.currentTimeMillis());
			mRecentDB.saveRecent(recentItem);
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.msg_listView:
			mInputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(),
					0);
			mFaceSwitchBtn
					.setImageResource(R.drawable.qzone_edit_face_drawable);
			faceLinearLayout.setVisibility(View.GONE);
			mIsFaceShow = false;
			break;
		case R.id.input:
			mInputMethodManager.showSoftInput(msgEt, 0);
			mFaceSwitchBtn
					.setImageResource(R.drawable.qzone_edit_face_drawable);
			faceLinearLayout.setVisibility(View.GONE);
			mIsFaceShow = false;
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onRefresh() {

		MsgPagerNum++;
		List<MessageItem> msgList = initMsgData();
		int position = adapter.getCount();
		adapter.setMessageList(msgList);
		mMsgListView.stopRefresh();
		mMsgListView.setSelection(adapter.getCount() - position - 1);
		AppLogger.i("MsgPagerNum = " + MsgPagerNum + ", adapter.getCount() = "
				+ adapter.getCount());
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHomePressed() {
		global.showNotification();

	}

	@Override
	public void onHomeLongPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(Message message) {
		android.os.Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
		handlerMsg.obj = message;
		handler.sendMessage(handlerMsg);

	}

	@Override
	public void onBind(String method, int errorCode, String content) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(String title, String content) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		if (!isNetConnected)
			Utility.ToastMessage(this, "网络连接已断开");

	}

	@Override
	public void onNewFriend(User u) {
		// TODO Auto-generated method stub

	}

}
