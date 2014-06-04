package com.engc.smartedu.ui.main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.engc.smartedu.R;
import com.engc.smartedu.baidupush.client.PushMessageReceiver;
import com.engc.smartedu.bean.AccountBean;
import com.engc.smartedu.bean.MessageItem;
import com.engc.smartedu.bean.RecentItem;
import com.engc.smartedu.bean.UnreadBean;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.bean.UserBean;
import com.engc.smartedu.dao.unread.UnreadDao;
import com.engc.smartedu.othercomponent.ClearCacheTask;
import com.engc.smartedu.othercomponent.chat.AppService;
import com.engc.smartedu.othercomponent.chat.IConnectionStatusCallback;
import com.engc.smartedu.othercomponent.notification.UnreadMsgReceiver;
import com.engc.smartedu.support.database.MessageDB;
import com.engc.smartedu.support.database.RecentDB;
import com.engc.smartedu.support.database.UserDB;
import com.engc.smartedu.support.error.WeiboException;
import com.engc.smartedu.support.lib.AppFragmentPagerAdapter;
import com.engc.smartedu.support.lib.MyAsyncTask;
import com.engc.smartedu.support.settinghelper.SettingUtility;
import com.engc.smartedu.support.utils.AppLogger;
import com.engc.smartedu.support.utils.EncryptUtil;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.HomeWatcher;
import com.engc.smartedu.support.utils.HomeWatcher.OnHomePressedListener;
import com.engc.smartedu.support.utils.PreferenceUtils;
import com.engc.smartedu.support.utils.SharePreferenceUtil;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.adapter.RecentAdapter;
import com.engc.smartedu.ui.basefragment.AbstractTimeLineFragment;
import com.engc.smartedu.ui.dm.DMUserListFragment;
import com.engc.smartedu.ui.fragment.LeftMenuFragment;
import com.engc.smartedu.ui.fragment.RightMenuFragment;
import com.engc.smartedu.ui.home.HomeFragment;
import com.engc.smartedu.ui.interfaces.IAccountInfo;
import com.engc.smartedu.ui.interfaces.IUserInfo;
import com.engc.smartedu.ui.interfaces.MainTitmeLineAppActivity;
import com.engc.smartedu.ui.login.AccountActivity;
import com.engc.smartedu.ui.maintimeline.CommentsTimeLineFragment;
import com.engc.smartedu.ui.maintimeline.FriendsTimeLineFragment;
import com.engc.smartedu.ui.maintimeline.MentionsTimeLineFragment;
import com.engc.smartedu.ui.maintimeline.MyStatussTimeLineFragment;
import com.engc.smartedu.ui.preference.SettingActivity;
import com.engc.smartedu.ui.search.SearchMainActivity;
import com.engc.smartedu.ui.userinfo.MyInfoActivity;
import com.engc.smartedu.widget.FloatView;
import com.engc.smartedu.widget.MenuItemView;
import com.engc.smartedu.widget.PathView;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * 
 * @Title: MainTimeLineActivity.java
 * @Package: com.engc.smartedu.ui.main
 * @Description: 首页时间轴
 * @author: wutao
 * @date: 2014-5-4 下午4:11:04
 */
@SuppressLint("NewApi")
public class MainTimeLineActivity extends MainTitmeLineAppActivity implements
		IUserInfo, IAccountInfo, OnClickListener, IConnectionStatusCallback,
		PushMessageReceiver.EventHandler,OnHomePressedListener ,PathView.OnItemClickListener{

	private ViewPager mViewPager;

	private AccountBean accountBean;

	private GetUnreadCountTask getUnreadCountTask;

	private NewMsgBroadcastReceiver newMsgBroadcastReceiver;

	private ScheduledExecutorService newMsgScheduledExecutorService;

	protected SlidingMenu mSlidingMenu;
	private ImageButton ivTitleBtnLeft, ivTitleBtnRight; // 头部 左右 切换按钮

	private android.support.v4.app.Fragment mContent;
	public static TextView titleText; // 标题
	public static int actionBarHeight;
	private int mScreenWidth; // 当前设备屏幕宽度

	private Handler mainHandler = new Handler();

	public static final int NEW_MESSAGE = 0x000;// 有新消息
	public static final int NEW_FRIEND = 0x001;// 有好友加入
	
	private TextView mEmpty;
	private LinkedList<RecentItem> mRecentDatas;
	private RecentAdapter mAdapter;
	private GlobalContext global;
	private UserDB mUserDB;
	private MessageDB mMsgDB;
	private RecentDB mRecentDB;
	private SharePreferenceUtil mSpUtil;
	private Gson mGson;
	private View mNetErrorView;
	private RightMenuFragment mRightFragment;
	private HomeWatcher mHomeWatcher;
	private ListView mListView;
	private MenuItemView myViewLB;
	
	 private WindowManager windowManager = null; 
	    private WindowManager.LayoutParams windowManagerParams = null; 
	    private FloatView floatView = null; 

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NEW_FRIEND:
				User u = (User) msg.obj;
				AppLogger.d("new friend handler","step to new friend *********************");
				// mUserDB.addUser(u);
				if (mRightFragment == null)
					mRightFragment = (RightMenuFragment) getSupportFragmentManager()
							.findFragmentById(R.id.main_right_fragment);
				mRightFragment.updateAdapter();// 更新
				Utility.ToastMessage(global, "好友列表已更新!");
				break;
			case NEW_MESSAGE:
				// String message = (String) msg.obj;
				AppLogger.d("new message handler","step to new message *********************");
				com.engc.smartedu.bean.Message msgItem = (com.engc.smartedu.bean.Message) msg.obj;
				String userId = msgItem.getUsercode();
				String nick = msgItem.getUsername();
				String content = msgItem.getMessage();
				//int headId = msgItem.getHead_id();
				// try {
				// headId = Integer
				// .parseInt(JsonUtil.getFromUserHead(message));
				// } catch (Exception e) {
				// L.e("head is not integer  " + e);
				// }
				if (mUserDB.selectInfo(userId) == null) {// 如果不存在此好友，则添加到数据库
					User user = new User(userId, msgItem.getChannel_id(), nick,
							"");
					mUserDB.addUser(user);
					mRightFragment = (RightMenuFragment) getSupportFragmentManager()
							.findFragmentById(R.id.main_right_fragment);
					mRightFragment.updateAdapter();// 更新一下好友列表
				}
				// TODO Auto-generated method stub
				MessageItem item = new MessageItem(
						MessageItem.MESSAGE_TYPE_TEXT, nick,
						System.currentTimeMillis(), content, "", true, 1);
				mMsgDB.saveMsg(userId, item);
				// 保存到最近会话列表
				RecentItem recentItem = new RecentItem(userId, "", nick,
						content, 0, System.currentTimeMillis());
				mRecentDB.saveRecent(recentItem);
				mAdapter.addFirst(recentItem);
				Utility.ToastMessage(global, nick + ":" + content);
				break;
			default:
				break;
			}
		}
	};
	public String getToken() {
		return accountBean.getAccess_token();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("account", accountBean);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(MainTimeLineActivity.this, AppService.class)); // 开启服务
		initSlidingMenu();
		
		setContentView(R.layout.viewpager_layout);

		if (savedInstanceState != null) {
			accountBean = (AccountBean) savedInstanceState
					.getSerializable("account");
		} else {
			Intent intent = getIntent();
			accountBean = (AccountBean) intent.getSerializableExtra("account");
		}

		if (accountBean == null)
			accountBean = GlobalContext.getInstance().getAccountBean();

		GlobalContext.getInstance().setAccountBean(accountBean);
		SettingUtility.setDefaultAccountId(accountBean.getUid());

		initRightSlidingMenuView();
		initData();  //初始化本地sqlite 数据 
        initView(); //初始化  网络异常视图
		buildPhoneInterface();
		getPackageInfo();
		//initConvenientView();
		
		
		
		

		Executors.newSingleThreadScheduledExecutor().schedule(
				new ClearCacheTask(), 8000, TimeUnit.SECONDS);
	}
	
	/**
	 * 获得当前客户端 版本号  并将其存储
	 */
	private void getPackageInfo(){
		//获取客户端版本信息
        try { 
        	
        	PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
        	PreferenceUtils.setPrefString(MainTimeLineActivity.this, "APP_Version_Code", info.versionName);
        } catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(null, menu);
		ActionBar actionBar = getActionBar();
		actionBarHeight = actionBar.getHeight();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		View view = LayoutInflater.from(this)
				.inflate(R.layout.main_title, null);
		actionBar.setCustomView(view, params);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);

		ivTitleBtnRight = (ImageButton) this.findViewById(R.id.ivTitleBtnRight);
		ivTitleBtnLeft = (ImageButton) this.findViewById(R.id.ivTitleBtnLeft);
		titleText = (TextView) this.findViewById(R.id.ivTitleName);
		ivTitleBtnLeft.setOnClickListener(this);
		ivTitleBtnRight.setOnClickListener(this);

		return true;
	}

	
	/**
	 * 初始化快捷菜单
	 */
	private void initConvenientView() {
	/*	PathView mPathView = (PathView) this
				.findViewById(R.id.mPathView_uitilsmodem);
		ImageButton startMenu = new ImageButton(MainTimeLineActivity.this);
		startMenu.setBackgroundResource(R.drawable.start_menu_btn);
		mPathView.setStartMenu(startMenu);
		
		mPathView.setItems(new View[0]);
		mPathView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainTimeLineActivity.this, WriteWeiboActivity.class);
                intent.putExtra("token", getToken());
                intent.putExtra("account", getAccount());
                startActivity(intent);
				
			}
		});*/
	 floatView=new FloatView(getApplicationContext());
	 floatView.setImageResource(R.drawable.white_weibo_menuitem_button);
	 windowManager=(WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	 windowManagerParams=((GlobalContext)getApplication()).getWindowParams();
	 windowManagerParams.type=android.view.WindowManager.LayoutParams.TYPE_PHONE;
     windowManagerParams.format = PixelFormat.RGBA_8888; 
     windowManagerParams.flags = android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL 
     | android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; 
     windowManagerParams.gravity = Gravity.CENTER_VERTICAL | Gravity.BOTTOM; 
     windowManagerParams.x = 0; 
     windowManagerParams.y = 0; 
     windowManagerParams.width = LayoutParams.WRAP_CONTENT; 
     windowManagerParams.height = LayoutParams.WRAP_CONTENT; 
     windowManager.addView(floatView, windowManagerParams);
	 
		
		
	}

	/**
	 * 初始化slidingview
	 */
	private void initRightSlidingMenuView() {
		// /初始化Rightmenu
		android.support.v4.app.FragmentTransaction FragementTransaction = getSupportFragmentManager()
				.beginTransaction();
		mSlidingMenu.setSecondaryMenu(R.layout.main_right_layout);
		android.support.v4.app.Fragment mFragRight = new RightMenuFragment();
		FragementTransaction.replace(R.id.main_right_fragment, mFragRight);
		FragementTransaction.commit();
		mSlidingMenu.setRightMenuOffset(mScreenWidth / 6);
	}

	private void getUnreadCount() {
		if (Utility.isTaskStopped(getUnreadCountTask)) {
			getUnreadCountTask = new GetUnreadCountTask();
			getUnreadCountTask
					.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	/**
	 * 初始化slidingmenu
	 */
	private void initSlidingMenu() {
		// int mScreenWidth=Utility.getScreenWidth();//获取屏幕宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		mContent = new HomeFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();
		setBehindContentView(R.layout.main_left_layout);
		android.support.v4.app.FragmentTransaction mFragementTransaction = getSupportFragmentManager()
				.beginTransaction();
		android.support.v4.app.Fragment mFrag = new LeftMenuFragment();
		mFragementTransaction.replace(R.id.main_left_fragment, mFrag);
		mFragementTransaction.commit();
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);// 设置是左滑还是右滑，还是左右都可以滑，
		// mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// 设置菜单宽度
		mSlidingMenu.setBehindOffset(mScreenWidth / 2);

		mSlidingMenu.setFadeDegree(0.35f);// 设置淡入淡出的比例
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置手势模式
		// mSlidingMenu.setShadowDrawable(R.drawable.shadow);// 设置左菜单阴影图片
		mSlidingMenu.setFadeEnabled(true);// 设置滑动时菜单的是否淡入淡出
		mSlidingMenu.setBehindScrollScale(0.333f);// 设置滑动时拖拽效果

	}

	private void buildPhoneInterface() {
		buildViewPager();
		buildActionBarAndViewPagerTitles();
		// / buildTabTitle(getIntent());
	}

	/**
	 * 左侧菜单点击切换首页的内容
	 */
	public void switchContent(android.support.v4.app.Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		AccountBean newAccountBean = (AccountBean) intent
				.getSerializableExtra("account");
		if (newAccountBean == null) {
			return;
		}

		if (newAccountBean.getUid().equals(accountBean.getUid())) {
			accountBean = newAccountBean;
			GlobalContext.getInstance().setAccountBean(accountBean);
			// buildTabTitle(intent);
		} else {
			overridePendingTransition(0, 0);
			finish();
			overridePendingTransition(0, 0);
			startActivity(intent);
			overridePendingTransition(0, 0);
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GlobalContext.getInstance().startedApp = false;
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_my_info:
			intent = new Intent(this, MyInfoActivity.class);
			intent.putExtra("token", getToken());
			intent.putExtra("user", getUser());
			intent.putExtra("account", getAccount());
			startActivity(intent);
			return true;

		case R.id.menu_account:
			intent = new Intent(this, AccountActivity.class);
			intent.putExtra("launcher", false);
			startActivity(intent);
			finish();
			return true;
		case R.id.menu_search:
			startActivity(new Intent(this, SearchMainActivity.class));

			return true;

		case R.id.menu_setting:
			startActivity(new Intent(this, SettingActivity.class));
			return true;

		}

		return super.onOptionsItemSelected(item);
	}

	private void buildViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		TimeLinePagerAdapter adapter = new TimeLinePagerAdapter(
				getFragmentManager());
		// mViewPager.setOffscreenPageLimit(5);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(onPageChangeListener);
	}

	private AbstractTimeLineFragment getHomeFragment() {
		return ((AbstractTimeLineFragment) getFragmentManager()
				.findFragmentByTag(FriendsTimeLineFragment.class.getName()));
	}

	private MentionsTimeLineFragment getMentionFragment() {
		return ((MentionsTimeLineFragment) getFragmentManager()
				.findFragmentByTag(MentionsTimeLineFragment.class.getName()));
	}

	private CommentsTimeLineFragment getCommentFragment() {
		return ((CommentsTimeLineFragment) getFragmentManager()
				.findFragmentByTag(CommentsTimeLineFragment.class.getName()));
	}

	private AbstractTimeLineFragment getMyFragment() {
		return ((AbstractTimeLineFragment) getFragmentManager()
				.findFragmentByTag(MyStatussTimeLineFragment.class.getName()));
	}

	private AbstractTimeLineFragment getDMFragment() {
		return ((AbstractTimeLineFragment) getFragmentManager()
				.findFragmentByTag(DMUserListFragment.class.getName()));
	}

	private void buildActionBarAndViewPagerTitles() {
		ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_CUSTOM);
		// actionBar.setCustomView(resId)rr
		// actionBar.setTitle("智慧教育");
		if (SettingUtility.getAppTheme() == R.style.AppTheme_Four
				&& getResources().getBoolean(R.bool.is_phone))
			// actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.ab_solid_custom_blue_inverse_holo));
			if (getResources().getBoolean(R.bool.is_phone)) {
				actionBar.setDisplayShowTitleEnabled(false);
				actionBar.setDisplayShowHomeEnabled(false);
			}

		MainTabListener tabListener = new MainTabListener();

		actionBar.addTab(actionBar.newTab().setText(getString(R.string.home))
				.setTabListener(tabListener));

		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.mentions))
				.setTabListener(tabListener));

		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.comments))
				.setTabListener(tabListener));

		if (getResources().getBoolean(R.bool.blackmagic)) {
			actionBar.addTab(actionBar.newTab().setText(getString(R.string.dm))
					.setTabListener(tabListener));
		} else {
			actionBar.addTab(actionBar.newTab().setText(getString(R.string.me))
					.setTabListener(tabListener));
		}

	}

	ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			getActionBar().setSelectedNavigationItem(position);
		}
	};

	private class MainTabListener implements ActionBar.TabListener {
		boolean home = false;
		boolean mentions = false;
		boolean comments = false;
		boolean my = false;

		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

			/**
			 * workaround for fragment option menu bug
			 * 
			 * http://stackoverflow.com/questions/9338122/action-items-from-
			 * viewpager-initial-fragment-not-being-displayed
			 * 
			 */
			if (mViewPager.getCurrentItem() != tab.getPosition())
				mViewPager.setCurrentItem(tab.getPosition());

			if (getHomeFragment() != null) {
				getHomeFragment().clearActionMode();
			}

			if (getMentionFragment() != null) {
				getMentionFragment().clearActionMode();
			}

			if (getCommentFragment() != null) {
				getCommentFragment().clearActionMode();
			}

			if (getMyFragment() != null) {
				getMyFragment().clearActionMode();
			}

			switch (tab.getPosition()) {
			case 0:
				home = true;
				break;
			case 1:
				mentions = true;
				break;
			case 2:
				comments = true;
				break;
			case 3:
				my = true;
				break;
			}

		}

		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
			switch (tab.getPosition()) {
			case 0:
				home = false;
				break;
			case 1:
				mentions = false;
				break;
			case 2:
				comments = false;
				break;
			case 3:
				my = false;
				break;
			}
		}

		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

			switch (tab.getPosition()) {
			case 0:
				if (home) {
					Utility.stopListViewScrollingAndScrollToTop(getHomeFragment()
							.getListView());
				}
				break;
			case 1:
				if (mentions) {
					Utility.stopListViewScrollingAndScrollToTop(getMentionFragment()
							.getListView());
				}
				break;
			case 2:
				if (comments) {
					Utility.stopListViewScrollingAndScrollToTop(getCommentFragment()
							.getListView());
				}
				break;
			case 3:
				if (my) {

					AbstractTimeLineFragment fragment;

					if (getResources().getBoolean(R.bool.blackmagic)) {
						fragment = getDMFragment();
					} else {
						fragment = getMyFragment();
					}
					Utility.stopListViewScrollingAndScrollToTop(fragment
							.getListView());
				}
				break;
			}
		}
	}

	;

	@Override
	public UserBean getUser() {
		return accountBean.getInfo();

	}

	@Override
	public AccountBean getAccount() {
		return accountBean;
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(UnreadMsgReceiver.ACTION);
		filter.setPriority(1);
		newMsgBroadcastReceiver = new NewMsgBroadcastReceiver();
		registerReceiver(newMsgBroadcastReceiver, filter);

		newMsgScheduledExecutorService = Executors
				.newSingleThreadScheduledExecutor();
		newMsgScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				getUnreadCount();
			}
		}, 10, 50, TimeUnit.SECONDS);
		
		if (!PushManager.isPushEnabled(this))
			PushManager.resumeWork(this);
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(this);
		mHomeWatcher.startWatch();
		if (!Utility.isConnected(this))
			mNetErrorView.setVisibility(View.VISIBLE);
		else {
			mNetErrorView.setVisibility(View.GONE);
		}
		PushMessageReceiver.ehList.add(this);
		initRecentData();
		global.getNotificationManager().cancel(
				PushMessageReceiver.NOTIFY_ID);
		PushMessageReceiver.mNewNum = 0;
		

		/*
		 * getContentResolver().registerContentObserver(
		 * RosterProvider.CONTENT_URI, true, mRosterObserver);
		 * setStatusImage(isConnected()); if (!isConnected())
		 * mTitleNameView.setText(R.string.login_prompt_no);
		 * mRosterAdapter.requery(); XXBroadcastReceiver.mListeners.add(this);
		 * if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE)
		 * mNetErrorView.setVisibility(View.VISIBLE); else
		 * mNetErrorView.setVisibility(View.GONE); ChangeLog cl = new
		 * ChangeLog(this); if (cl != null && cl.firstRun()) {
		 * cl.getFullLogDialog().show(); }
		 */

	}

	/**
	 * 初始化 本地SQLite 数据库 中 数据 
	 */
	private void initData() {
		global = GlobalContext.getInstance();
		mSpUtil = global.getSpUtil();
		mGson = global.getGson();
		mUserDB = global.getUserDB();
		mMsgDB = global.getMessageDB();
		mRecentDB = global.getRecentDB();
	
	}
	
	private void initRecentData() {
		// TODO Auto-generated method stub
		mRecentDatas = mRecentDB.getRecentList();
		mAdapter = new RecentAdapter(this, mRecentDatas, mListView);
		//mListView.setAdapter(mAdapter);

	}
	

	public void upDateList() {
		initRecentData();
	}
	
	/**
	 * 初始化 网络异常视图
	 */
	private void initView(){
		mNetErrorView = findViewById(R.id.net_status_bar_top);
		/*myViewLB = (MenuItemView)findViewById(R.id.myViewRB);
		myViewLB.setPosition(MenuItemView.POSITION_LEFT_TOP);
		myViewLB.setRadius(80);*/
		mNetErrorView.setOnClickListener(this);
		/*myViewLB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainTimeLineActivity.this, WriteWeiboActivity.class);
                intent.putExtra("token", getToken());
                intent.putExtra("account", getAccount());
                startActivity(intent);
				
			}
		});*/
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(newMsgBroadcastReceiver);
		newMsgScheduledExecutorService.shutdownNow();
		if (getUnreadCountTask != null)
			getUnreadCountTask.cancel(true);
		mHomeWatcher.setOnHomePressedListener(null);
		mHomeWatcher.stopWatch();
		PushMessageReceiver.ehList.remove(this);// 暂停就移除监听
	}

	private class TimeLinePagerAdapter extends AppFragmentPagerAdapter {

		List<Fragment> list = new ArrayList<Fragment>();

		public TimeLinePagerAdapter(FragmentManager fm) {
			super(fm);
			if (getHomeFragment() == null) {
				list.add(new FriendsTimeLineFragment(getAccount(), getUser(),
						getToken()));
			} else {
				list.add(getHomeFragment());
			}

			/*
			 * if (getMentionFragment() == null) { list.add(new
			 * MentionsTimeLineFragment(getAccount(), getUser(), getToken())); }
			 * else { list.add(getMentionFragment()); }
			 * 
			 * if (getCommentFragment() == null) { list.add(new
			 * CommentsTimeLineFragment(getAccount(), getUser(), getToken())); }
			 * else { list.add(getCommentFragment()); }
			 */

			/*
			 * if (getResources().getBoolean(R.bool.blackmagic)) { if
			 * (getDMFragment() == null) { list.add(new DMUserListFragment()); }
			 * else { list.add(getDMFragment()); } } else { if (getMyFragment()
			 * == null) { list.add(new MyStatussTimeLineFragment(getUser(),
			 * getToken())); } else { list.add(getMyFragment()); }
			 * 
			 * }
			 */

		}

		public Fragment getItem(int position) {
			return list.get(position);
		}

		@Override
		protected String getTag(int position) {
			List<String> tagList = new ArrayList<String>();
			tagList.add(FriendsTimeLineFragment.class.getName());
			tagList.add(MentionsTimeLineFragment.class.getName());
			tagList.add(CommentsTimeLineFragment.class.getName());
			if (getResources().getBoolean(R.bool.blackmagic)) {
				tagList.add(DMUserListFragment.class.getName());
			} else {
				tagList.add(MyStatussTimeLineFragment.class.getName());
			}
			return tagList.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

	}

	private class GetUnreadCountTask extends
			MyAsyncTask<Void, Void, UnreadBean> {

		@Override
		protected UnreadBean doInBackground(Void... params) {
			UnreadDao unreadDao = new UnreadDao(getToken(),
					accountBean.getUid());
			try {
				return unreadDao.getCount();
			} catch (WeiboException e) {
				AppLogger.e(e.getError());
			}
			return null;
		}

		@Override
		protected void onPostExecute(UnreadBean unreadBean) {
			super.onPostExecute(unreadBean);
			if (unreadBean != null) {
				buildUnreadTabTxt(unreadBean);

			}
		}
	}

	private class NewMsgBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			AccountBean newMsgAccountBean = (AccountBean) intent
					.getSerializableExtra("account");
			if (newMsgAccountBean.getUid().equals(
					MainTimeLineActivity.this.accountBean.getUid())) {
				abortBroadcast();
				UnreadBean unreadBean = (UnreadBean) intent
						.getSerializableExtra("unread");
				buildUnreadTabTxt(unreadBean);

			}

		}
	}

	private void buildUnreadTabTxt(UnreadBean unreadBean) {
		int unreadMentionsCount = unreadBean.getMention_status();
		int unreadCommentsCount = unreadBean.getMention_cmt()
				+ unreadBean.getCmt();

		if (unreadMentionsCount > 0 && getMentionFragment() != null)
			getMentionFragment().refreshUnread(unreadBean);

		if (unreadCommentsCount > 0 && getCommentFragment() != null)
			getCommentFragment().refreshUnread(unreadBean);
	}

	/**
	 * 单击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTitleBtnLeft:
			// 点击标题左边按钮弹出左侧菜单
			mSlidingMenu.showMenu(true);
			break;

		default:
			mSlidingMenu.showSecondaryMenu(true);
			break;
		}

	}



	@Override
	public void connectionStatusChanged(int connectedState, String reason) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(com.engc.smartedu.bean.Message message) {
		Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
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
		if (!isNetConnected) {
			Utility.ToastMessage(this, R.string.network_not_connected);
			mNetErrorView.setVisibility(View.VISIBLE);
		} else {
			mNetErrorView.setVisibility(View.GONE);
		}
		
	}

	@Override
	public void onNewFriend(User u) {
		Message handlerMsg = handler.obtainMessage(NEW_FRIEND);
		handlerMsg.obj = u;
		handler.sendMessage(handlerMsg);
		
	}

	@Override
	public void onHomePressed() {
		// 先判断应用是否在运行，
				global.showNotification();
		
	}

	@Override
	public void onHomeLongPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(View view, int position) {
		// TODO Auto-generated method stub
		
	}

}