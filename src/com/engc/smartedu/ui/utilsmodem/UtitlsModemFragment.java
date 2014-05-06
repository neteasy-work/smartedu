package com.engc.smartedu.ui.utilsmodem;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;

public class UtitlsModemFragment extends BaseSlidingFragment {

	private TextView txtTitle;
	private View view;
	private GridView gvUtilsModem;

	public List<String> imgtitleList; // 存放应用标题list
	public List<Integer> imgList; // 存放应用图片list
	public View[] itemViews;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_uitlsmodem, container, false);
		initView();

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initGridData(0);

	}

	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.actionbar_menu_commentnewactivity, menu);
		super.onCreateOptionsMenu(menu, inflater);
		ActionBar ab=getActivity().getActionBar();
		ab.hide();

	}

	private void initView() {
		gvUtilsModem = (GridView) view.findViewById(R.id.gvutilsmodem);

	}

	/**
	 * 根据当前 状态 初始化 gridview
	 * 
	 * @param cardStatus
	 */
	private void initGridData(int cardStatus) {
		imgtitleList = new ArrayList<String>();
		imgList = new ArrayList<Integer>();

		imgtitleList.clear();
		imgList.clear();
		imgtitleList.add("充值");
		imgtitleList.add("挂失");
		imgtitleList.add("请假");
		imgtitleList.add("更多");
		imgList.add(R.drawable.icon_prepaid);
		imgList.add(R.drawable.icon_lost);
		imgList.add(R.drawable.icon_holdday);
		imgList.add(R.drawable.icon_more);
		gvUtilsModem
				.setAdapter(new GridViewModemAdapter(imgtitleList, imgList));

	}

	/**
	 * 
	 * @ClassName: GridViewModemAdapter
	 * @Description: APPs 九宫格 数据适配源
	 * @author wutao
	 * @date 2013-10-10 上午11:23:54
	 * 
	 */
	public class GridViewModemAdapter extends BaseAdapter {

		public GridViewModemAdapter(List<String> imgTitles, List<Integer> images) {
			itemViews = new View[images.size()];
			for (int i = 0; i < itemViews.length; i++) {
				itemViews[i] = makeItemView(imgTitles.get(i), images.get(i));
			}
		}

		public View makeItemView(String imageTitilsId, int imageId) {
			// try {
			// LayoutInflater inflater = (LayoutInflater)
			// UtitlsModemFragment.this
			// .getSystemService(LAYOUT_INFLATER_SERVICE);
			// View
			// view=LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.grid_apps_item,
			// null);
			LayoutInflater inflater = LayoutInflater.from(getActivity()
					.getApplicationContext());
			View itemView = inflater.inflate(R.layout.grid_apps_item, null);
			TextView title = (TextView) itemView.findViewById(R.id.TextItemId);
			title.setText(imageTitilsId);
			ImageView image = (ImageView) itemView
					.findViewById(R.id.ImageItemId);
			image.setImageResource(imageId);
			// image.setScaleType(ImageView.ScaleType.FIT_CENTER);
			return itemView;
			/*
			 * } catch (Exception e) {
			 * System.out.println("makeItemView Exception error" +
			 * e.getMessage()); return null; }
			 */

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemViews.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemViews[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				return itemViews[position];
			}
			return convertView;
		}

	}
}
