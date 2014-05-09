package com.engc.smartedu.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.User;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: FriendsAdapter.java
 * @Package: com.engc.smartedu.ui.adapter
 * @Description: 好友适配器
 * @author: Administrator  
 * @date: 2014-5-8 下午2:01:57
 */
public class FriendsAdapter extends BaseAdapter  {

	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<User> usersList;
	

	public FriendsAdapter(Context context,List<User> data,int resource) {
		// super(context, android.R.layout.simple_list_item_1, cursor, from,
		// to);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		usersList=data;
	}
	public int getCount() {
		return usersList.size();
		

	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.firends_listview_item, null);
			holder = new ViewHolder();
			holder.alpha = (TextView) convertView
					.findViewById(R.id.friends_item_alpha);
			holder.alpha_line = (ImageView) convertView
					.findViewById(R.id.friends_item_alpha_line);
			holder.avatar = (ImageView) convertView
					.findViewById(R.id.friends_item_avatar);
			holder.name = (TextView) convertView
					.findViewById(R.id.friends_item_name);
			holder.arrow = (ImageView) convertView
					.findViewById(R.id.friends_item_arrow);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		User user=usersList.get(position);
		
		//if (mIsAll) {
			//int section = getSectionForPosition(position);
			
			/*if (getPositionForSection(section) == position) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha_line.setVisibility(View.VISIBLE);
				//holder.alpha.setText(user.getNick().get(section));
			} else {
				holder.alpha.setVisibility(View.GONE);
				holder.alpha_line.setVisibility(View.GONE);
			}*/
			holder.name.setText(user.getUsername());
			holder.avatar.setImageResource(R.drawable.honey_face);
			holder.arrow.setVisibility(View.GONE);
		//} else {
			/*PublicPageResult result = mMyPublicPageResults.get(position);
			holder.alpha.setVisibility(View.GONE);
			holder.alpha_line.setVisibility(View.GONE);
			holder.arrow.setVisibility(View.VISIBLE);
			holder.avatar.setImageBitmap(mKXApplication
					.getPublicPageAvatar(result.getAvatar()));
			holder.name.setText(result.getName());*/
		//}
		return convertView;
	}

	class ViewHolder {
		TextView alpha;
		ImageView alpha_line;
		ImageView avatar;
		TextView name;
		ImageView arrow;
	}

	
}
