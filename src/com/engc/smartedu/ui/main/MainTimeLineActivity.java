package com.engc.smartedu.ui.main;

import java.util.ArrayList;
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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.AccountBean;
import com.engc.smartedu.bean.UnreadBean;
import com.engc.smartedu.bean.UserBean;
import com.engc.smartedu.dao.unread.UnreadDao;
import com.engc.smartedu.othercomponent.ClearCacheTask;
import com.engc.smartedu.othercomponent.chat.AppBroadcastReceiver.EventHandler;
import com.engc.smartedu.othercomponent.chat.AppService;
import com.engc.smartedu.othercomponent.chat.IConnectionStatusCallback;
import com.engc.smartedu.othercomponent.notification.UnreadMsgReceiver;
import com.engc.smartedu.support.error.WeiboException;
import com.engc.smartedu.support.lib.AppFragmentPagerAdapter;
import com.engc.smartedu.support.lib.MyAsyncTask;
import com.engc.smartedu.support.settinghelper.SettingUtility;
import com.engc.smartedu.support.utils.AppLogger;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.PreferenceConstants;
import com.engc.smartedu.support.utils.PreferenceUtils;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.support.utils.XMPPHelper;
import com.engc.smartedu.ui.basefragment.AbstractTimeLineFragment;
import com.engc.smartedu.ui.dm.DMUserListFragment;
import com.engc.smartedu.ui.fragment.LeftMenuFragment;
import com.engc.smartedu.ui.fragment.RightMenuFragment;
import com.engc.smartedu.ui.home.HomeFragment;
import com.engc.smartedu.ui.interfaces.IAccountInfo;
import com.engc.smartedu.ui.interfaces.IUserInfo;
import com.engc.smartedu.ui.interfaces.MainTitmeLineAppActivity;
import com.engc.smartedu.ui.login.AccountActivity;
import com.engc.smartedu.ui.login.LoginActivity;
import com.engc.smartedu.ui.maintimeline.CommentsTimeLineFragment;
import com.engc.smartedu.ui.maintimeline.FriendsTimeLineFragment;
import com.engc.smartedu.ui.maintimeline.MentionsTimeLineFragment;
import com.engc.smartedu.ui.maintimeline.MyStatussTimeLineFragment;
import com.engc.smartedu.ui.preference.SettingActivity;
import com.engc.smartedu.ui.search.SearchMainActivity;
import com.engc.smartedu.ui.userinfo.MyInfoActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: MainTimeLineActivity.java
 * @Package: com.engc.smartedu.ui.main
 * @Description: 首页时间轴
 * @author: wutao  
 * @date: 2014-5-4 下午4:11:04
 */
@SuppressLint("NewApi")
public class MainTimeLineActivity extends MainTitmeLineAppActivity implements
		IUserInfo, IAccountInfo, OnClickListener,IConnectionStatusCallback,EventHandler {

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
	private AppService appService;
	
	
	
	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			appService = ((AppService.AppBinder) service).getService();
			appService.registerConnectionStatusCallback(MainTimeLineActivity.this);
			// 开始连接xmpp服务器
			if (!appService.isAuthenticated()) {
				String usr = PreferenceUtils.getPrefString(MainTimeLineActivity.this,
						PreferenceConstants.ACCOUNT, "");
				String password = PreferenceUtils.getPrefString(
						MainTimeLineActivity.this, PreferenceConstants.PASSWORD, "");
				appService.Login(usr, password);
				
				AppLogger.i("当前的链接状态为"+"未连接");
			} else {
				AppLogger.i("当前的链接状态为"+XMPPHelper
						.splitJidAndServer(PreferenceUtils.getPrefString(
								MainTimeLineActivity.this, PreferenceConstants.ACCOUNT,
								"")));
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			appService.unRegisterConnectionStatusCallback();
			appService = null;
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
		startService(new Intent(MainTimeLineActivity.this, AppService.class)); //开启服务
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

		buildPhoneInterface();

		Executors.newSingleThreadScheduledExecutor().schedule(
				new ClearCacheTask(), 8000, TimeUnit.SECONDS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(null, menu);
		ActionBar actionBar = getActionBar();
		actionBarHeight=actionBar.getHeight();
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
		titleText=(TextView) this.findViewById(R.id.ivTitleName);
		ivTitleBtnLeft.setOnClickListener(this);
		ivTitleBtnRight.setOnClickListener(this);

		return true;
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
		mSlidingMenu.setSecondaryShadowDrawable(R.drawable.right_fragment_shadow);
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
		//mSlidingMenu.setShadowDrawable(R.drawable.shadow);// 设置左菜单阴影图片
		mSlidingMenu.setFadeEnabled(true);// 设置滑动时菜单的是否淡入淡出
		mSlidingMenu.setBehindScrollScale(0.333f);// 设置滑动时拖拽效果

	}

	private void buildPhoneInterface() {
		buildViewPager();
		 buildActionBarAndViewPagerTitles();
//		/ buildTabTitle(getIntent());
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
		// actionBar.setCustomView(resId)
		// actionBar.setTitle("智慧教育");
		if (SettingUtility.getAppTheme() == R.style.AppTheme_Four
				&& getResources().getBoolean(R.bool.is_phone))
			// actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.ab_solid_custom_blue_inverse_holo));
			if (getResources().getBoolean(R.bool.is_phone)) {
				actionBar.setDisplayShowTitleEnabled(false);
				actionBar.setDisplayShowHomeEnabled(false);
			}

		 MainTabListener tabListener = new MainTabListener();

		 actionBar.addTab(actionBar.newTab()
		 .setText(getString(R.string.home))
		 .setTabListener(tabListener));

		
		  actionBar.addTab(actionBar.newTab()
		  .setText(getString(R.string.mentions)) .setTabListener(tabListener));
		  
		  actionBar.addTab(actionBar.newTab()
		  .setText(getString(R.string.comments)) .setTabListener(tabListener));
		 

		
		 if (getResources().getBoolean(R.bool.blackmagic)) {
		  actionBar.addTab(actionBar.newTab() .setText(getString(R.string.dm))
		  .setTabListener(tabListener)); } else {
		  actionBar.addTab(actionBar.newTab() .setText(getString(R.string.me))
		  .setTabListener(tabListener)); }
		 
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
		
		bindXMPPService();
		/*getContentResolver().registerContentObserver(
			RosterProvider.CONTENT_URI, true, mRosterObserver);
		setStatusImage(isConnected());
		if (!isConnected())
			mTitleNameView.setText(R.string.login_prompt_no);
		mRosterAdapter.requery();
		XXBroadcastReceiver.mListeners.add(this);
		if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE)
			mNetErrorView.setVisibility(View.VISIBLE);
		else
			mNetErrorView.setVisibility(View.GONE);
		ChangeLog cl = new ChangeLog(this);
		if (cl != null && cl.firstRun()) {
			cl.getFullLogDialog().show();
		}*/

	}
	
	private void unbindXMPPService() {
		try {
			unbindService(mServiceConnection);
			AppLogger.i(LoginActivity.class, "[SERVICE] Unbind*****************服务绑定");
		} catch (IllegalArgumentException e) {
			AppLogger.e(LoginActivity.class, "Service wasn't bound**************服务未能绑定!");
		}
	}

	private void bindXMPPService() {
		AppLogger.i(LoginActivity.class, "[SERVICE] Unbind");
		bindService(new Intent(MainTimeLineActivity.this, AppService.class),
				mServiceConnection, Context.BIND_AUTO_CREATE
						+ Context.BIND_DEBUG_UNBIND);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(newMsgBroadcastReceiver);
		newMsgScheduledExecutorService.shutdownNow();
		if (getUnreadCountTask != null)
			getUnreadCountTask.cancel(true);
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
	public void onNetChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionStatusChanged(int connectedState, String reason) {
		// TODO Auto-generated method stub
		
	}

}