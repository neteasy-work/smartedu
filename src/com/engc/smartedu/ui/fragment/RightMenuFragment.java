package com.engc.smartedu.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.othercomponent.chat.AppBroadcastReceiver.EventHandler;
import com.engc.smartedu.othercomponent.chat.AppService;
import com.engc.smartedu.othercomponent.chat.IConnectionStatusCallback;
import com.engc.smartedu.support.database.MessageDB;
import com.engc.smartedu.support.database.RecentDB;
import com.engc.smartedu.support.database.UserDB;
import com.engc.smartedu.support.utils.AppLogger;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.SharePreferenceUtil;
import com.engc.smartedu.ui.adapter.FriendsAdapter;
import com.engc.smartedu.ui.chat.ChatActivity;
import com.engc.smartedu.ui.interfaces.FixedOnActivityResultBugFragment;
import com.engc.smartedu.ui.login.AccountActivity;

/**
 * 右侧侧滑菜单
 * 
 * @author Administrator
 * 
 */
@SuppressLint("NewApi")
public class RightMenuFragment extends FixedOnActivityResultBugFragment
		implements OnClickListener, IConnectionStatusCallback, EventHandler {

	private Handler mainHandler = new Handler();
	private AppService appService;
	private RelativeLayout fragementHeaderLayout;

    private GlobalContext global;
	private UserDB mUserDB;
	private RecentDB mRecentDB;
	private MessageDB mMsgDB;
	private SharePreferenceUtil mSpUtil;
	private ListView firendsList;

	/*
	 * ServiceConnection mServiceConnection = new ServiceConnection() {
	 * 
	 * @Override public void onServiceConnected(ComponentName name, IBinder
	 * service) { appService = ((AppService.AppBinder) service).getService();
	 * appService.registerConnectionStatusCallback(RightMenuFragment.this); //
	 * 开始连接xmpp服务器 if (!appService.isAuthenticated()) { String usr =
	 * PreferenceUtils.getPrefString(getActivity() .getApplicationContext(),
	 * PreferenceConstants.ACCOUNT, ""); String password =
	 * PreferenceUtils.getPrefString(getActivity() .getApplicationContext(),
	 * PreferenceConstants.PASSWORD, ""); appService.Login(usr, password); //
	 * mTitleNameView.setText(R.string.login_prompt_msg); //
	 * setStatusImage(false); // mTitleProgressBar.setVisibility(View.VISIBLE);
	 * } else { mTitleNameView.setText(XMPPHelper
	 * .splitJidAndServer(PreferenceUtils.getPrefString(
	 * getActivity().getApplicationContext(), PreferenceConstants.ACCOUNT,
	 * ""))); setStatusImage(true); } }
	 * 
	 * @Override public void onServiceDisconnected(ComponentName name) {
	 * appService.unRegisterConnectionStatusCallback(); appService = null; }
	 * 
	 * };
	 */

	public void updateAdapter() {
		if (firendsList != null) {
			AppLogger.i("update friend...");
			initUserData();
		}
		
	}
	@Override
	public void onClick(View v) {

	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_right_fragment, container,
				false);
		int height = AccountActivity.actionBarHeight;
		/*
		 * fragementHeaderLayout = (RelativeLayout) view
		 * .findViewById(R.id.relayout_chat_fragment);
		 * RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
		 * fragementHeaderLayout.getLayoutParams().width, getActivity()
		 * .getActionBar().getHeight());
		 * fragementHeaderLayout.setLayoutParams(params);
		 */

		firendsList = (ListView) view.findViewById(R.id.friends_display);

		firendsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(getActivity()
						.getApplicationContext(), ChatActivity.class);
				startActivity(intent);

			}
		});

		/*
		 * EditText ediChat=(EditText) view.findViewById(R.id.friends_search);
		 * ediChat.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent=new
		 * Intent(v.getContext(),ChatActivity.class); startActivity(intent);
		 * 
		 * 
		 * } });
		 * 
		 * System.out.println();
		 */
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		global=GlobalContext.getInstance();
		mUserDB = global.getUserDB();
		mMsgDB = global.getMessageDB();
		mRecentDB = global.getRecentDB();
		mSpUtil = global.getSpUtil();

	}

	@Override
	public void onResume() {
		super.onResume();
		initUserData();
	}

	/**
	 * 初始化好友数据
	 */
	private void initUserData() {
		// TODO Auto-generated method stub
		List<User> dbUsers = mUserDB.getUser();// 查询本地数据库所有好友
		FriendsAdapter adapter = new FriendsAdapter(getActivity()
				.getApplicationContext(), dbUsers, 0);
		firendsList.setAdapter(adapter);

		/*
		 * // 初始化组名和child for (int i = 0; i < groups.length; ++i) {
		 * mGroup.add(groups[i]);// 组名 List<User> childUsers = new
		 * ArrayList<User>();// 每一组的child mChildren.put(i, childUsers); } //
		 * 给每一组添加数据 for (User u : dbUsers) { for (int i = 0; i < mGroup.size();
		 * ++i) { if (u.getGroup() == i) { mChildren.get(i).add(u); } } }
		 */

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ActionBar actionBar = getActivity().getActionBar();
		int height = AccountActivity.actionBarHeight;

	}

	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater infalter) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(null, menu);
		/*
		 * RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
		 * fragementHeaderLayout.getLayoutParams().width, getActivity()
		 * .getActionBar().getHeight());
		 * fragementHeaderLayout.setLayoutParams(params);
		 */

	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "小甜甜");
		map.put("info", "I love sweety");
		map.put("img", R.drawable.honey_face);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "QQ君");
		map.put("info", "I love QQ");
		map.put("img", R.drawable.login_default_avatar);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "coffee君");
		map.put("info", "I love coffee");
		map.put("img", R.drawable.login_default_avatar);
		list.add(map);

		return list;
	}

	private void setStatusImage(boolean isConnected) {
		if (!isConnected) {
			// mTitleStatusView.setVisibility(View.GONE);
			return;
		}
		/*
		 * String statusMode =
		 * PreferenceUtils.getPrefString(getActivity().getApplicationContext(),
		 * PreferenceConstants.STATUS_MODE, PreferenceConstants.AVAILABLE); int
		 * statusId = mStatusMap.get(statusMode); if (statusId == -1) {
		 * //mTitleStatusView.setVisibility(View.GONE); } else {
		 * //mTitleStatusView.setVisibility(View.VISIBLE);
		 * //mTitleStatusView.setImageResource(statusId); }
		 */
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
