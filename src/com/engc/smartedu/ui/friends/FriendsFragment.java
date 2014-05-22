package com.engc.smartedu.ui.friends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.SortModel;
import com.engc.smartedu.dao.friend.FriendDao;
import com.engc.smartedu.dao.login.LoginDao;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.utils.CharacterParser;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.adapter.SortAdapter;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;
import com.engc.smartedu.widget.ImageLoader;
import com.engc.smartedu.widget.PinyinComparator;
import com.engc.smartedu.widget.SideBar;
import com.engc.smartedu.widget.SideBar.OnTouchingLetterChangedListener;

public class FriendsFragment extends BaseSlidingFragment {

	private View view;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ProgressBar pgbLoading;


	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	//private  friendList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	public static FriendsFragment newInstance() {
		FriendsFragment friendsFragment = new FriendsFragment();
		return friendsFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_friend, container, false);
		/*
		 * friendHeaderLayout=(RelativeLayout)
		 * view.findViewById(R.id.relayout_chat_fragment);
		 * friendHeaderLayout.setVisibility(RelativeLayout.GONE);
		 */
		initViews();
		GetDataTask task=new GetDataTask();
		task.execute(SourceDateList);
		return view;
	}

	private void initViews() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) view.findViewById(R.id.sidrbar);
		dialog = (TextView) view.findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		pgbLoading=(ProgressBar) view.findViewById(R.id.prbloading);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) view.findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				Intent intent=new Intent(view.getContext(),FriendInfo.class);
				intent.putExtra("userName", SourceDateList.get(position).getName());
				intent.putExtra("userCode", SourceDateList.get(position).getUserCode());
				intent.putExtra("imgFace", SourceDateList.get(position).getImgSrc());
				startActivity(intent);
			}
		});
		
		
		
 
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(List<SortModel> list,String [] imgdata) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for(int i=0;i<list.size();i++){
			SortModel sortModel = new SortModel();
			sortModel.setImgSrc(i>=imgdata.length?imgdata[i-imgdata.length]:imgdata[i]);
			sortModel.setName(list.get(i).getName());
			sortModel.setUserCode(list.get(i).getUserCode());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(list.get(i).getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	private class GetDataTask extends AsyncTask<List<SortModel>, Void, List<SortModel>> {

		@Override
		protected List<SortModel> doInBackground(List<SortModel>... params) {

			try {
				SourceDateList = FriendDao.getFriendsByOrgId(LoginDao.getLoginInfo(
						getActivity()).getOrgid());
				
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return SourceDateList;
			
		}
		
		@Override
		protected void onPostExecute(List<SortModel> result){
			super.onPostExecute(result);
			SourceDateList=filledData(SourceDateList,getResources().getStringArray(R.array.img_src_data));
			Collections.sort(SourceDateList,pinyinComparator);
			adapter = new SortAdapter(getActivity(), SourceDateList);
			adapter.notifyDataSetChanged();
			sortListView.setAdapter(adapter);
			pgbLoading.setVisibility(View.GONE);
			
		}
		
	
		

	}

}
