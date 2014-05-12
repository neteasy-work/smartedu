package com.engc.smartedu.ui.setup;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;
import com.engc.smartedu.widget.MenuItemView;

@SuppressLint("NewApi")
public class SetupFragment extends BaseSlidingFragment {

	private TextView txtTitle;
	private View view;

	SharedPreferences mPreferences;
	Preference infonotification;
	Preference cache;
	Preference feedback;
	Preference update;
	Preference about;
	CheckBoxPreference loadimage;
	MenuItemView myViewLB;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tweet_menu, container, false);
		// initView();
		//getActivity().getActionBar().hide();
		myViewLB = (MenuItemView) view.findViewById(R.id.myViewLB);
		myViewLB.setPosition(MenuItemView.POSITION_LEFT_BOTTOM);
		myViewLB.setRadius(80);
		return view;
	}

	/*@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.actionbar_menu_infofragment, menu);
		super.onCreateOptionsMenu(menu, inflater);

	}*/
	
	

	

}
