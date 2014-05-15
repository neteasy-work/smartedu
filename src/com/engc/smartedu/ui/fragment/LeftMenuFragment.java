package com.engc.smartedu.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.SharePreferenceUtil;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.appcenter.AppsFragment;
import com.engc.smartedu.ui.friends.FriendsFragment;
import com.engc.smartedu.ui.home.HomeFragment;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;
import com.engc.smartedu.ui.interfaces.FixedOnActivityResultBugFragment;
import com.engc.smartedu.ui.main.MainTimeLineActivity;
import com.engc.smartedu.ui.setup.SetupFragment;
import com.engc.smartedu.ui.utilsmodem.UtitlsModemFragment;


/**
 * 左侧滑动 栏
 */
public class LeftMenuFragment extends BaseSlidingFragment implements
		OnClickListener {
	private View homeBtnLayout; // 左侧首页菜单
	private View friendsBtnLayout; // 左侧好友菜单
	//private View appCenterBtnLayout; // 左侧应用中心菜单
	private View setupBtnLayout;
	private View utilsModemLayout; //
	private TextView titleText; //头部标题
	private TextView txtUserName;
	private SharePreferenceUtil spUtil;
	private View exitLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_left_fragment, container,
				false);
		spUtil = GlobalContext.getInstance().getSpUtil();
		homeBtnLayout = view.findViewById(R.id.homeBtnLayout);
		homeBtnLayout.setOnClickListener(this);
		friendsBtnLayout = view.findViewById(R.id.friendBtnLayout);
		friendsBtnLayout.setOnClickListener(this);
		txtUserName=(TextView) view.findViewById(R.id.nickNameTextView);
		txtUserName.setText(spUtil.getUserName());
		
		//appCenterBtnLayout = view.findViewById(R.id.appcenterBtnLayout);
		//appCenterBtnLayout.setOnClickListener(this);
		setupBtnLayout=view.findViewById(R.id.setupBtnLayout);
		setupBtnLayout.setOnClickListener(this);
		utilsModemLayout=view.findViewById(R.id.uitlsmodemBtnLayout);
		utilsModemLayout.setOnClickListener(this);
		titleText=MainTimeLineActivity.titleText;
		exitLayout=view.findViewById(R.id.exitBtnLayout);
		exitLayout.setOnClickListener(this);
		
		homeBtnLayout.setSelected(true);
		friendsBtnLayout.setSelected(false);
		//appCenterBtnLayout.setSelected(false);
		setupBtnLayout.setSelected(false);
		utilsModemLayout.setSelected(false);

		System.out.println();
		return view;
	}

	@Override
	public void onClick(View v) {
		Fragment newContent = null;
		switch (v.getId()) {

		case R.id.homeBtnLayout:
			newContent = new HomeFragment();
			homeBtnLayout.setSelected(true);
			friendsBtnLayout.setSelected(false);
			//appCenterBtnLayout.setSelected(false);
			setupBtnLayout.setSelected(false);
			utilsModemLayout.setSelected(false);

			break;
		case R.id.friendBtnLayout:
			newContent = new FriendsFragment();
			homeBtnLayout.setSelected(false);
			friendsBtnLayout.setSelected(true);
			//appCenterBtnLayout.setSelected(false);
			setupBtnLayout.setSelected(false);
			utilsModemLayout.setSelected(false);
			//titleText.setText(getString(R.string.leftslidingmenu_friedns));
			break;
		/*case R.id.appcenterBtnLayout:
			newContent = new AppsFragment();
			homeBtnLayout.setSelected(false);
			friendsBtnLayout.setSelected(false);
			appCenterBtnLayout.setSelected(true);
			setupBtnLayout.setSelected(false);
			utilsModemLayout.setSelected(false);
			//titleText.setText(getString(R.string.leftslidingmenu_appcenter));

			break;*/
		case R.id.uitlsmodemBtnLayout:
			newContent = new UtitlsModemFragment();
			homeBtnLayout.setSelected(false);
			friendsBtnLayout.setSelected(false);
			//appCenterBtnLayout.setSelected(false);
			setupBtnLayout.setSelected(false);
			utilsModemLayout.setSelected(true);
			//titleText.setText(getString(R.string.leftslidingmenu_uitlsmodem));
			break;
		case R.id.setupBtnLayout:
			newContent = new SetupFragment();
			homeBtnLayout.setSelected(false);
			friendsBtnLayout.setSelected(false);
			//appCenterBtnLayout.setSelected(false);
			setupBtnLayout.setSelected(true);
			utilsModemLayout.setSelected(false);
			//titleText.setText(getString(R.string.leftslidingmenu_setup));
			break;

		case R.id.exitBtnLayout:
			
			Utility.Logout(getActivity());
			//getActivity().finish();
			break;
		default:
			
			
			break;
		}

		if (newContent != null)
			switchFragment(newContent);

	}

	// 切换到不同的功能内容

	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		MainTimeLineActivity ra = (MainTimeLineActivity) getActivity();
		ra.switchContent(fragment);

	}
	
	

}
