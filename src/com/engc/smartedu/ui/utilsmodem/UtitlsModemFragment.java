package com.engc.smartedu.ui.utilsmodem;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.dao.login.LoginDao;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.utils.CardConstants;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;
import com.engc.smartedu.ui.leave.LeaveActivity;
import com.engc.smartedu.ui.leave.LeaveRecordActivity;

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
		ActionBar ab = getActivity().getActionBar();
		ab.hide();

	}

	private void initView() {
		gvUtilsModem = (GridView) view.findViewById(R.id.gvutilsmodem);
		gvUtilsModem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0: // 解挂
					final Handler handler1 = new Handler() {
						public void handleMessage(Message msg) {
							if (msg.what == 1) {
								Utility.ToastMessage(getActivity()
										.getApplicationContext(),
										(String) msg.obj);
								User user = LoginDao.getLoginInfo(getActivity()
										.getApplicationContext());
								user.setCardstatus(CardConstants.NORMAL_CARD);
								LoginDao.saveLoginInfo(getActivity()
										.getApplicationContext(), user);
								// initGridData(CardStatus.NORMAL_CARD);
							} else {
								Utility.ToastMessage(getActivity()
										.getApplicationContext(),
										(String) msg.obj);

							}
						}
					};
					new Thread() {
						public void run() {
							Message msg = new Message();
							try {
								User user = LoginDao
										.ChangeCardStatus(
												LoginDao.getLoginInfo(
														getActivity()
																.getApplicationContext())
														.getUsercode(),
												LoginDao.getLoginInfo(
														getActivity()
																.getApplicationContext())
														.getCardstatus(), 5);
								if (user.getIsError().equals("false")) {
									msg.what = 1;
									msg.obj = user.getMessage();

								} else {
									msg.what = 0;
									msg.obj = user.getMessage();
								}

							} catch (AppException e) {
								e.printStackTrace();
								msg.what = -1;
								msg.obj = e;
							}
							handler1.sendMessage(msg);
						}
					}.start();

					break;

				case 1:// 挂失
					final Handler handler = new Handler() {
						public void handleMessage(Message msg) {
							if (msg.what == 1) {
								Utility.ToastMessage(getActivity()
										.getApplicationContext(),
										(String) msg.obj);
								User user = LoginDao.getLoginInfo(getActivity()
										.getApplicationContext());
								user.setCardstatus(CardConstants.REPORT_LOSS_ED_CARD);
								LoginDao.saveLoginInfo(getActivity()
										.getApplicationContext(), user);
								initGridData(CardConstants.REPORT_LOSS_ED_CARD);
							} else {
								Utility.ToastMessage(getActivity()
										.getApplicationContext(),
										(String) msg.obj);

							}

						}
					};
					new Thread() {
						public void run() {
							Message msg = new Message();
							try {
								User user = LoginDao
										.ChangeCardStatus(
												LoginDao.getLoginInfo(
														getActivity()
																.getApplicationContext())
														.getUsercode(),
												LoginDao.getLoginInfo(
														getActivity()
																.getApplicationContext())
														.getCardstatus(), 1);
								if (user.getIsError().equals("false")) {
									msg.what = 1;
									msg.obj = user.getMessage();

								} else {
									msg.what = 0;
									msg.obj = user.getMessage();
								}

							} catch (AppException e) {
								e.printStackTrace();
								msg.what = -1;
								msg.obj = e;
							}
							handler.sendMessage(msg);
						}
					}.start();

					break;
				case 2: // 请假
					Intent intent = new Intent(getActivity()
							.getApplicationContext(), LeaveActivity.class);
					startActivity(intent);
					break;

				case 3: // 请假记录
					//Intent inte = new Intent(getActivity()
						//	.getApplicationContext(), LeaveRecordActivity.class);
					//startActivity(inte);
					break;
				default: //更多
					Utility.ToastMessage(getActivity().getApplicationContext(), "暂未开放，敬请期待");
					break;
				}

			}
		});

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
		imgtitleList.add("解挂");
		imgtitleList.add("挂失");
		imgtitleList.add("请假");
		imgtitleList.add("请假记录");

		imgtitleList.add("更多");
		imgList.add(R.drawable.icon_prepaid);
		imgList.add(R.drawable.icon_lost);
		imgList.add(R.drawable.icon_holdday);
		imgList.add(R.drawable.icon_leave_record);
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
