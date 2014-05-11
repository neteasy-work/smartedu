package com.engc.smartedu.ui.adapter;

import java.util.List;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.LeaveBean;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * 请假记录 适配器 
 */
public class ListViewHoildayRecordAdapter extends BaseAdapter {
	private Context context;// 运行上下文
	private List<LeaveBean> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private int[] images = { R.drawable.app_icon };

	static class ListItemView { // 自定义控件集合
		public TextView title;
		public TextView author;
		public TextView date;
		public TextView count;
		public ImageView flag;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewHoildayRecordAdapter(Context context, List<LeaveBean> data,
			int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
	}

	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// Log.d("method", "getView");

		// 自定义视图
		ListItemView listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.title = (TextView) convertView
					.findViewById(R.id.news_listitem_title);

			listItemView.author = (TextView) convertView
					.findViewById(R.id.news_listitem_author);
			listItemView.count = (TextView) convertView
					.findViewById(R.id.news_listitem_commentCount);
			listItemView.date = (TextView) convertView
					.findViewById(R.id.news_listitem_date);
			listItemView.flag = (ImageView) convertView
					.findViewById(R.id.news_listitem_flag);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		// 设置文字和图片
		// Messages message = listItems.get(position);
		LeaveBean holidays = listItems.get(position);

		listItemView.title.setText(holidays.getUsername());

		listItemView.title.setTag(holidays);// 设置隐藏参数(实体类)
		// listItemView.author.setText(action.getArea());
		listItemView.author.setText("申请时间:"
				+ holidays.getLeavetime().substring(0, 16));

		listItemView.count.setText(holidays.getLeavetype());

		// if(StringUtils.isToday(action.getApplytime()))
		// if(listItemView.flag.VISIBLE==ImageView.VISIBLE)
		// listItemView.flag.setVisibility(ImageView.);
		// if(message.getStatus()==2)
		listItemView.flag.setImageResource(R.drawable.app_icon);
		// else if(message.getStatus()==1)
		// listItemView.flag.setImageResource(R.drawable.message_unread);

		return convertView;
	}
}
