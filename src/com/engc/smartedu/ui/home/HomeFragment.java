package com.engc.smartedu.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.engc.smartedu.R;
import com.engc.smartedu.baidupush.client.PushMessageReceiver;
import com.engc.smartedu.bean.AccountBean;
import com.engc.smartedu.bean.MessageItem;
import com.engc.smartedu.bean.RecentItem;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.bean.UserBean;
import com.engc.smartedu.support.database.MessageDB;
import com.engc.smartedu.support.database.RecentDB;
import com.engc.smartedu.support.database.UserDB;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.HomeWatcher.OnHomePressedListener;
import com.engc.smartedu.support.utils.SharePreferenceUtil;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.fragment.RightMenuFragment;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;
import com.engc.smartedu.ui.interfaces.IAccountInfo;
import com.engc.smartedu.ui.interfaces.IUserInfo;
import com.google.gson.Gson;


public class HomeFragment extends BaseSlidingFragment implements IUserInfo,
		IAccountInfo,PushMessageReceiver.EventHandler,OnHomePressedListener{

	public static final int NEW_MESSAGE = 0x000;// 有新消息
	public static final int NEW_FRIEND = 0x001;// 有好友加入
	private ViewPager mViewPager;
	private View view;
	private AccountBean accountBean;
	private GlobalContext global;
	private UserDB mUserDB;
	private MessageDB mMsgDB;
	private RecentDB mRecentDB;
	private RightMenuFragment rightFragment;
	private SharePreferenceUtil mSpUtil;
	private Gson mGson;

	public String getToken() {
		return accountBean.getAccess_token();
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.viewpager_layout, container, false);
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 initData();
	}
	
	private void initData() {
		global=GlobalContext.getInstance();
		mSpUtil=global.getSpUtil();
		
		mGson = global.getGson();
		mUserDB = global.getUserDB();
		mMsgDB = global.getMessageDB();
		mRecentDB = global.getRecentDB();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("account", accountBean);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NEW_FRIEND:
				User u = (User) msg.obj;
				// mUserDB.addUser(u);
				if (rightFragment == null)
					rightFragment = (RightMenuFragment) getFragmentManager()
							.findFragmentById(R.id.main_right_fragment);
				rightFragment.updateAdapter();// 更新
			    Utility.ToastMessage(global, "好友列表已更新");
				break;
			case NEW_MESSAGE:
				// String message = (String) msg.obj;
				com.engc.smartedu.bean.Message msgItem = (com.engc.smartedu.bean.Message) msg.obj;
				String userId = msgItem.getUser_id();
				String nick = msgItem.getNick();
				String content = msgItem.getMessage();
				int headId = msgItem.getHead_id();
				// try {
				// headId = Integer
				// .parseInt(JsonUtil.getFromUserHead(message));
				// } catch (Exception e) {
				// L.e("head is not integer  " + e);
				// }
				if (mUserDB.selectInfo(userId) == null) {// 如果不存在此好友，则添加到数据库
					User user = new User(userId, msgItem.getChannel_id(), nick,
							headId, 0);
					mUserDB.addUser(user);
					rightFragment = (RightMenuFragment) getFragmentManager()
							.findFragmentById(R.id.main_left_fragment);
					rightFragment.updateAdapter();// 更新一下好友列表
				}
				// TODO Auto-generated method stub
				MessageItem item = new MessageItem(
						MessageItem.MESSAGE_TYPE_TEXT, nick,
						System.currentTimeMillis(), content, headId, true, 1);
				mMsgDB.saveMsg(userId, item);
				// 保存到最近会话列表
				RecentItem recentItem = new RecentItem(userId, headId, nick,
						content, 0, System.currentTimeMillis());
				mRecentDB.saveRecent(recentItem);
				//mAdapter.addFirst(recentItem);
				Utility.ToastMessage(global, nick + ":" + content);
				break;
			default:
				break;
			}
		}
	};
	/*private void buildViewPager() {
		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
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
*/
	/*private MentionsTimeLineFragment getMentionFragment() {
		return ((MentionsTimeLineFragment) getFragmentManager()
				.findFragmentByTag(MentionsTimeLineFragment.class.getName()));
	}

	private CommentsTimeLineFragment getCommentFragment() {
		return ((CommentsTimeLineFragment) getFragmentManager()
				.findFragmentByTag(CommentsTimeLineFragment.class.getName()));
	}*/

	/*private class TimeLinePagerAdapter extends AppFragmentPagerAdapter {

		List<Fragment> list = new ArrayList<Fragment>();

		public TimeLinePagerAdapter(FragmentManager fm) {
			super(fm);
			if (getHomeFragment() == null) {
				list.add(new FriendsTimeLineFragment(getAccount(), getUser(),
						getToken()));
			} else {
				list.add(getHomeFragment());
			}

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
*/
	ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		@SuppressLint("NewApi")
		@Override
		public void onPageSelected(int position) {
			getActivity().getActionBar().setSelectedNavigationItem(position);
		}
	};

	@Override
	public UserBean getUser() {
		return accountBean.getInfo();

	}

	@Override
	public AccountBean getAccount() {
		return accountBean;
	}

	@Override
	public void onHomePressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHomeLongPressed() {
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
		if (!isNetConnected) 
			Utility.ToastMessage(getActivity().getApplicationContext(), "网络连接发生异常，请检查");
			
		
	}

	@Override
	public void onNewFriend(User u) {
		Message handlerMsg = handler.obtainMessage(NEW_FRIEND);
		handlerMsg.obj = u;
		handler.sendMessage(handlerMsg);
		
	}

}
