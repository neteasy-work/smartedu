package com.engc.smartedu.ui.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.DynamicBean;
import com.engc.smartedu.bean.LeaveBean;
import com.engc.smartedu.support.asyncdrawable.TimeLineBitmapDownloader;
import com.engc.smartedu.support.lib.TimeLineAvatarImageView;
import com.engc.smartedu.support.lib.TimeLineImageView;
import com.engc.smartedu.support.utils.BitmapManager;
import com.engc.smartedu.support.utils.ImageUtils;
import com.engc.smartedu.support.utils.TimeTool;
import com.engc.smartedu.support.utils.TimeUtil;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.adapter.ListViewHoildayRecordAdapter.ListItemView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * 
 * @Title: DynamicAdapter.java
 * @Package: com.engc.smartedu.ui.adapter
 * @Description:动态适配器
 * @author: Administrator
 * @date: 2014-6-17 上午9:31:22
 */
public class DynamicAdapter extends BaseAdapter {
	private Context context;// 运行上下文s
	private List<DynamicBean> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();

	static class ListItemView { // 自定义控件集合
		public TextView userName;
		public TextView content;
		public TextView date;
		public TimeLineAvatarImageView face;
		private TimeLineImageView contentPic;
		private FrameLayout contentPicLayout;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public DynamicAdapter(Context context, List<DynamicBean> data, int resource) {

		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
	}

	@Override
	public int getCount() {

		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 自定义视图
		ListItemView listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.userName = (TextView) convertView
					.findViewById(R.id.username);

			listItemView.date = (TextView) convertView.findViewById(R.id.time);
			listItemView.content = (TextView) convertView
					.findViewById(R.id.content);
			listItemView.face = (TimeLineAvatarImageView) convertView
					.findViewById(R.id.avatar);
			listItemView.contentPic = (TimeLineImageView) convertView
					.findViewById(R.id.content_pic);
			listItemView.contentPicLayout = (FrameLayout) convertView
					.findViewById(R.id.repost_and_pic);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		// 设置文字和图片
		// Messages message = listItems.get(position);

		DynamicBean dynamic = listItems.get(position);

		listItemView.userName.setTag(dynamic);// 设置隐藏参数(实体类)
		listItemView.userName.setText(dynamic.getUserName());

		listItemView.content.setText(dynamic.getContent());
		// listItemView.author.setText(action.getArea());
		listItemView.date.setText(TimeTool.getListTime(Long.valueOf(dynamic
				.getCreateDate())));
		// listItemView.contentPic.setba
		if (!dynamic.getThumbnail().equals("")) {
			new BitmapManager().loadBitmap(dynamic.getThumbnail(),
					listItemView.contentPic);

		} else {
			listItemView.contentPicLayout.setVisibility(View.GONE);
		}
		// listItemView.contentPic.setBackgroundResource(R.drawable.young);

		// listItemView.count.setText(holidays.getLeavetype());

		// if(StringUtils.isToday(action.getApplytime()))
		// if(listItemView.flag.VISIBLE==ImageView.VISIBLE)
		// listItemView.flag.setVisibility(ImageView.);
		// if(message.getStatus()==2)
		listItemView.face.setBackgroundResource(R.drawable.honey_face);
		// else if(message.getStatus()==1)
		// listItemView.flag.setImageResource(R.drawable.message_unread);

		return convertView;
	}

}
