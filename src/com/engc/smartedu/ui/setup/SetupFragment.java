package com.engc.smartedu.ui.setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.ui.interfaces.AbstractAppFragment;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;

public class SetupFragment extends BaseSlidingFragment {

	private TextView txtTitle;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setup, container, false);
		//initView();
		return view;
	}
	
	

	
}
