package com.engc.smartedu.ui.adapter;

import java.util.LinkedList;
import java.util.regex.Matcher;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.RecentItem;
import com.engc.smartedu.support.database.MessageDB;
import com.engc.smartedu.support.database.RecentDB;
import com.engc.smartedu.support.utils.AppLogger;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.TimeUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;



public class RecentAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private LinkedList<RecentItem> mData;
	private MessageDB mMessageDB;
	private RecentDB mRecentDB;
	private Context mContext;
	private ListView mListView;

	public RecentAdapter(Context context, LinkedList<RecentItem> data,
			ListView listview) {
		mContext = context;
		this.mInflater = LayoutInflater.from(context);
		mData = data;
		this.mListView = listview;
		mMessageDB = GlobalContext.getInstance().getMessageDB();
		mRecentDB = GlobalContext.getInstance().getRecentDB();
	}

	public void remove(int position) {
		if (position < mData.size()) {
			mData.remove(position);
			notifyDataSetChanged();
		}
	}

	public void remove(RecentItem item) {
		if (mData.contains(item)) {
			mData.remove(item);
			notifyDataSetChanged();
		}
	}

	public void addFirst(RecentItem item) {
		if (mData.contains(item)) {
			mData.remove(item);
		}
		mData.addFirst(item);
		AppLogger.i("addFirst: " + item);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final RecentItem item = mData.get(position);
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.recent_listview_item, null);
		}
		TextView nickTV = (TextView) convertView
				.findViewById(R.id.recent_list_item_name);
		TextView msgTV = (TextView) convertView
				.findViewById(R.id.recent_list_item_msg);
		TextView numTV = (TextView) convertView.findViewById(R.id.unreadmsg);
		TextView timeTV = (TextView) convertView
				.findViewById(R.id.recent_list_item_time);
		ImageView headIV = (ImageView) convertView.findViewById(R.id.icon);
		
		nickTV.setText(item.getName());
		//msgTV.setText(convertNormalStringToSpannableString(item.getMessage()),
				//BufferType.SPANNABLE);
		timeTV.setText(TimeUtil.getChatTime(item.getTime()));
		//headIV.setImageResource(PushApplication.heads[item.getHeadImg()]);
		int num = mMessageDB.getNewCount(item.getUserId());
		if (num > 0) {
			numTV.setVisibility(View.VISIBLE);
			numTV.setText(num + "");
		} else {
			numTV.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	
}
