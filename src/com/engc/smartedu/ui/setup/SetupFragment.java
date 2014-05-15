package com.engc.smartedu.ui.setup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.support.utils.PreferenceUtils;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;

@SuppressLint("NewApi")
public class SetupFragment extends BaseSlidingFragment implements
		OnClickListener {

	private TextView txtCurrentVersion;
	private View view;

	LinearLayout clearCacheLayout, feedBackLayout, aboutLayout, updateLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.preference, container, false);
		// initView();
		// getActivity().getActionBar().hide();
		/*
		 * myViewLB = (MenuItemView) view.findViewById(R.id.myViewLB);
		 * myViewLB.setPosition(MenuItemView.POSITION_LEFT_BOTTOM);
		 * myViewLB.setRadius(80);
		 */
		initView();
		return view;
	}

	private void initView() {
		txtCurrentVersion=(TextView) view.findViewById(R.id.txt_display_versioncode);
		txtCurrentVersion.setText("检查更新                   当前版本:"+PreferenceUtils.getPrefString(getActivity(), "APP_Version_Code", "1.0"));
		clearCacheLayout = (LinearLayout) view
				.findViewById(R.id.clearcachelayout);
		clearCacheLayout.setOnClickListener(this);
		feedBackLayout = (LinearLayout) view.findViewById(R.id.feedbacklayout);
		feedBackLayout.setOnClickListener(this);
		aboutLayout = (LinearLayout) view.findViewById(R.id.about_us_layout);
		aboutLayout.setOnClickListener(this);
		updateLayout = (LinearLayout) view
				.findViewById(R.id.check_update_layout);
		updateLayout.setOnClickListener(this);

	}
	

	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.clearcachelayout:

			break;
		case R.id.feedbacklayout:
			Intent intent=new Intent(getActivity(),FeedBack.class);
			startActivity(intent);
			break;
		case R.id.about_us_layout:
			Intent it=new Intent(getActivity(),AboutActivity.class);
			startActivity(it);
			break;
		case R.id.check_update_layout:
			break;

		default:
			break;
		}

	}

	/*
	 * @Override public void onCreateOptionsMenu(Menu menu, MenuInflater
	 * inflater) { inflater.inflate(R.menu.actionbar_menu_infofragment, menu);
	 * super.onCreateOptionsMenu(menu, inflater);
	 * 
	 * }
	 */

}
