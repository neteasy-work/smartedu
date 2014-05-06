package com.engc.smartedu.ui.appcenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.ui.interfaces.AbstractAppFragment;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;
import com.engc.smartedu.ui.interfaces.FixedOnActivityResultBugFragment;

public class AppsFragment extends BaseSlidingFragment {

	private TextView txtTitle;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_appcenter, container, false);
		//initView();
		return view;
	}

	private void initView() {
		txtTitle = (TextView) view.findViewById(R.id.ivTitleName);
		txtTitle.setText("应用中心");

	}

}
