package com.engc.smartedu.ui.leave;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.LeaveBean;
import com.engc.smartedu.bean.LeaveRecordList;
import com.engc.smartedu.dao.leave.LeaveDao;
import com.engc.smartedu.dao.login.LoginDao;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.lib.pulltorefresh.PullToRefreshBase;
import com.engc.smartedu.support.lib.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.engc.smartedu.support.lib.pulltorefresh.PullToRefreshListView;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.ui.adapter.ListViewHoildayRecordAdapter;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;

/**
 * 请假记录 activity
 * 
 * @author Admin
 * 
 */
@SuppressLint("NewApi")
public class LeaveRecordActivity extends AbstractAppActivity {

	private ListViewHoildayRecordAdapter lvHolidayRecordAdapter; // 适配器

	private List<LeaveBean> lvHolidayRecordData = new ArrayList<LeaveBean>();
	// 消息类型
	public final static int AUDIT_ED_HOLIDAY = 3;// 记录 已审核审核状态
	public final static int UN_AUDIT_HOLIDAY = 1; // 记录未审核状态

	private TextView txtHeadTitle;// activity title
	private View lvHolidayRecord_footer; // 记录底部
	private TextView lvHolidayRecord_foot_more; // 记录更多

	private PullToRefreshListView lvHolidayRecord; // 记录listview

	private Handler lvHolidayRecordHandler; // 记录handler

	private int lvHolidayRecordSumData;

	private int curHolidayRecodCatalog = LeaveRecordList.CATALOG_ALL;

	private ProgressBar lvHolidayRecord_foot_progress; // 新闻 progressbar
	private Button imBack; // 返回按钮

	private ViewPager viewPager;// 页卡内容
	private ImageView imageView, imgItemIcon;// 动画图片
	private TextView txtReadedMsg, txtUnReadMsg;
	private List<View> views;// Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private View view1, view2;
	private int listviewStatusCode = 0;
	private Resources rs = null;
	private PullToRefreshListView ptlListview;

	private LeaveRecordList list;
	 ListViewHoildayRecordAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.holiday_record);
		initActionBar();
		rs = this.getResources();
		InitImageView();
		InitTextView();
		InitViewPager();
		initFrameListView(view1, UN_AUDIT_HOLIDAY);
	}

	/**
	 * 初始化Actionbar
	 */
	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("请假记录");
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 初始化viewpage
	 * 
	 */
	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();

		view1 = inflater.inflate(R.layout.un_audit_holiday_item, null);
		view2 = inflater.inflate(R.layout.audited_holiday_list, null);

		views.add(view1);
		views.add(view2);

		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	/**
	 * 初始化头标
	 */

	private void InitTextView() {

		txtReadedMsg = (TextView) findViewById(R.id.txtunauditholiday);
		txtUnReadMsg = (TextView) findViewById(R.id.txtauditedholiday);

		txtReadedMsg.setOnClickListener(new MyOnClickListener(0));
		txtUnReadMsg.setOnClickListener(new MyOnClickListener(1));
	}

	/**
	 * 初始化所有ListView
	 */
	private void initFrameListView(View view, final int newsType) {
		intitPullToRefreshListView(view, newsType);
		

	}

	private void intitPullToRefreshListView(View view, int record) {
		ptlListview = (PullToRefreshListView) view
				.findViewById(R.id.audit_holiday_list);
		ptlListview.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				new GetDataTask().execute();
			}
		});

	}

	private class GetDataTask extends AsyncTask<Void, Void, LeaveRecordList> {

		@Override
		protected LeaveRecordList doInBackground(Void... params) {
			// Simulates a background job.
			try {

				list = LeaveDao.getHolidayRecordList(0, 1, LoginDao.getLoginInfo(LeaveRecordActivity.this).getUsercode(), String.valueOf(AUDIT_ED_HOLIDAY));

			} catch (AppException e) {
			}
			return list;
		}

		@Override
		protected void onPostExecute(LeaveRecordList result) {

			// mListItems.addFirst("Added after refresh...");
			// mAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
		    adapter=new ListViewHoildayRecordAdapter(getApplicationContext(),list.getHolidayslist(),R.layout.list_holiday_record_item);
			ptlListview.setAdapter(adapter);
		    adapter.notifyDataSetChanged();
		    ptlListview.onRefreshComplete();

			super.onPostExecute(result);
		}

	
	}

	/**
	 * 2 * 初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据 3
	 */

	private void InitImageView() {
		imageView = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.album_box_5).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * 
	 * 头标点击监听 3
	 */
	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}

	}

	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}
		

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@SuppressLint("ResourceAsColor")
		public void onPageSelected(int arg0) {
			/*
			 * 两种方法，这个是一种，下面还有一种，显然这个比较麻烦 Animation animation = null;
			 */

			switch (arg0) {
			case 0:

				txtReadedMsg.setTextColor(rs
						.getColor(R.color.detail_node_title));
				txtUnReadMsg.setTextColor(rs.getColor(R.color.audit_leave_menu_title_unselected));
				txtReadedMsg.setTextSize(16);
				txtUnReadMsg.setTextSize(16);
				initFrameListView(view1, UN_AUDIT_HOLIDAY);

				break;
			case 1:
				txtUnReadMsg.setTextColor(rs
						.getColor(R.color.detail_node_title));
				txtReadedMsg.setTextColor(rs.getColor(R.color.audit_leave_menu_title_unselected));
				txtUnReadMsg.setTextSize(16);
				txtReadedMsg.setTextSize(16);
				initFrameListView(view2, AUDIT_ED_HOLIDAY);
				break;

			}

			Animation animation = new TranslateAnimation(one * currIndex, one
					* arg0, 0, 0);// 显然这个比较简洁，只有一行代码。
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			imageView.startAnimation(animation);
			// Toast.makeText(WeiBoActivity.this, "您选择了"+
			// viewPager.getCurrentItem()+"页卡", Toast.LENGTH_SHORT).show();
		}

	}

}
