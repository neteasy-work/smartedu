package com.engc.smartedu.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.othercomponent.chat.AppBroadcastReceiver.EventHandler;
import com.engc.smartedu.othercomponent.chat.AppService;
import com.engc.smartedu.othercomponent.chat.IConnectionStatusCallback;
import com.engc.smartedu.support.utils.PreferenceConstants;
import com.engc.smartedu.support.utils.PreferenceUtils;
import com.engc.smartedu.support.utils.XMPPHelper;
import com.engc.smartedu.ui.chat.ChatActivity;
import com.engc.smartedu.ui.interfaces.FixedOnActivityResultBugFragment;

/**
 * 右侧侧滑菜单
 * 
 * @author Administrator
 * 
 */
public class RightMenuFragment extends FixedOnActivityResultBugFragment
		implements OnClickListener, IConnectionStatusCallback, EventHandler {

	private Handler mainHandler = new Handler();
	private AppService appService;
	private TextView mTitleNameView;

	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			appService = ((AppService.AppBinder) service).getService();
			appService.registerConnectionStatusCallback(RightMenuFragment.this);
			// 开始连接xmpp服务器
			if (!appService.isAuthenticated()) {
				String usr = PreferenceUtils.getPrefString(getActivity()
						.getApplicationContext(), PreferenceConstants.ACCOUNT,
						"");
				String password = PreferenceUtils.getPrefString(getActivity()
						.getApplicationContext(), PreferenceConstants.PASSWORD,
						"");
				appService.Login(usr, password);
				// mTitleNameView.setText(R.string.login_prompt_msg);
				// setStatusImage(false);
				// mTitleProgressBar.setVisibility(View.VISIBLE);
			} else {
				mTitleNameView.setText(XMPPHelper
						.splitJidAndServer(PreferenceUtils.getPrefString(
								getActivity().getApplicationContext(),
								PreferenceConstants.ACCOUNT, "")));
				setStatusImage(true);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			appService.unRegisterConnectionStatusCallback();
			appService = null;
		}

	};

	@Override
	public void onClick(View v) {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_right_fragment, container,
				false);

		ListView firendsList = (ListView) view
				.findViewById(R.id.friends_display);
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), getData(),
				R.layout.firends_listview_item, new String[] { "title", "info",
						"img" }, new int[] { R.id.txtfriendname,
						R.id.txtfriendcontent, R.id.img_friends_face });
		firendsList.setAdapter(adapter);

		
		firendsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				 Intent intent=new Intent(getActivity().getApplicationContext(),ChatActivity.class);
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
