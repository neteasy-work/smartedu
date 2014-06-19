package com.engc.smartedu.ui.dynamic;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.Comment;
import com.engc.smartedu.bean.DynamicBean;
import com.engc.smartedu.bean.DynamicListBean;
import com.engc.smartedu.dao.dynamic.DynamicDao;
import com.engc.smartedu.dao.login.LoginDao;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.lib.pulltorefresh.PullToRefreshBase;
import com.engc.smartedu.support.lib.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.engc.smartedu.support.lib.pulltorefresh.PullToRefreshListView;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.adapter.DynamicAdapter;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * 
 * @Title: Dynamic.java
 * @Package: com.engc.smartedu.ui.dynamic
 * @Description: 动态
 * @author: Administrator
 * @date: 2014-6-17 上午9:23:30
 */
public class Dynamic extends AbstractAppActivity {
	private PullToRefreshListView prlDynamic;
	private DynamicListBean list;
	private DynamicAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dynamic);
		initView();
		new GetDataTask().execute();

	}

	private void initView() {
		prlDynamic = (PullToRefreshListView) findViewById(R.id.prlDynamic);
		prlDynamic.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				new GetDataTask().execute();
			}
		});

		prlDynamic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// 点击头部、底部栏无效
				if (position == 0)
					return;

				DynamicBean dynamic = null;
				// 判断是否是TextView
				if (view instanceof TextView) {
					dynamic = (DynamicBean) view.getTag();
				} else {
					TextView tv = (TextView) view.findViewById(R.id.username);
					dynamic = (DynamicBean) tv.getTag();
				}
				if (dynamic == null)
					return;
				Utility.ToastMessage(view.getContext(), dynamic.getTid());

				// 跳转到活动详情
				// UIHelper.showNewsRedirect(view.getContext(), news);
				/*
				 * if (!appContext.isNetworkConnected())
				 * UIHelper.ToastMessage(view.getContext(),
				 * R.string.network_not_connected); else
				 */
				// UIHelper.showActionDetail(view.getContext(), news.getId(),
				// "");
				Intent intent = new Intent(Dynamic.this, DynamicDetail.class);
				intent.putExtra("dynamicId",dynamic.getTid());
				intent.putExtra("userName", dynamic.getUserName());
				intent.putExtra("content", dynamic.getContent());
				intent.putExtra("date", dynamic.getCreateDate());
				List<Comment> list=dynamic.getCommentList();
				if(list!=null)
				
					intent.putExtra("commentList", (Serializable) list);
					
				
				startActivity(intent);

			}
		});
		
	}

	private class GetDataTask extends AsyncTask<Void, Void, DynamicListBean> {

		@Override
		protected DynamicListBean doInBackground(Void... params) {
			// Simulates a background job.
			try {

				list = DynamicDao.getDynamicList("1",
						LoginDao.getLoginInfo(Dynamic.this).getOrgid()
								.substring(0, 6));

			} catch (AppException e) {
			}
			return list;
		}

		@Override
		protected void onPostExecute(DynamicListBean result) {

			// mListItems.addFirst("Added after refresh...");
			// mAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			if (list.getDynamicList().size() > 0) {

				adapter = new DynamicAdapter(Dynamic.this,
						list.getDynamicList(),
						R.layout.timeline_listview_item_layout);
				prlDynamic.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				prlDynamic.onRefreshComplete();
			} else {
				prlDynamic.onRefreshComplete();
			}

			super.onPostExecute(result);
		}

	}
}
