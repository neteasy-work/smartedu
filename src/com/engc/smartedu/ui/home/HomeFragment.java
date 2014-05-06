package com.engc.smartedu.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.engc.smartedu.R;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;

public class HomeFragment extends BaseSlidingFragment {
	
	  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.viewpager_layout, container, false);
    	return view;
    }

	

}
