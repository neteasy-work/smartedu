package com.engc.smartedu.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;

public class FriendsFragment extends BaseSlidingFragment {

	private TextView txtTitle;
	private View view;
	private RelativeLayout friendHeaderLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.main_right_fragment, container, false);
		friendHeaderLayout=(RelativeLayout) view.findViewById(R.id.relayout_chat_fragment);
		friendHeaderLayout.setVisibility(RelativeLayout.GONE);
		return view;
	}
	
	
	
	
}
