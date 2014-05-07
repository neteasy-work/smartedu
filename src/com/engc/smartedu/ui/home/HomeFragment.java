package com.engc.smartedu.ui.home;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.AccountBean;
import com.engc.smartedu.bean.UserBean;
import com.engc.smartedu.support.lib.AppFragmentPagerAdapter;
import com.engc.smartedu.ui.basefragment.AbstractTimeLineFragment;
import com.engc.smartedu.ui.dm.DMUserListFragment;
import com.engc.smartedu.ui.interfaces.BaseSlidingFragment;
import com.engc.smartedu.ui.interfaces.IAccountInfo;
import com.engc.smartedu.ui.interfaces.IUserInfo;
import com.engc.smartedu.ui.maintimeline.CommentsTimeLineFragment;
import com.engc.smartedu.ui.maintimeline.FriendsTimeLineFragment;
import com.engc.smartedu.ui.maintimeline.MentionsTimeLineFragment;
import com.engc.smartedu.ui.maintimeline.MyStatussTimeLineFragment;

public class HomeFragment extends BaseSlidingFragment implements IUserInfo,
		IAccountInfo {

	private ViewPager mViewPager;
	private View view;
	private AccountBean accountBean;

	public String getToken() {
		return accountBean.getAccess_token();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.viewpager_layout, container, false);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("account", accountBean);
	}

	/*private void buildViewPager() {
		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		TimeLinePagerAdapter adapter = new TimeLinePagerAdapter(
				getFragmentManager());
		// mViewPager.setOffscreenPageLimit(5);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(onPageChangeListener);
	}

	
	private AbstractTimeLineFragment getHomeFragment() {
		return ((AbstractTimeLineFragment) getFragmentManager()
				.findFragmentByTag(FriendsTimeLineFragment.class.getName()));
	}
*/
	/*private MentionsTimeLineFragment getMentionFragment() {
		return ((MentionsTimeLineFragment) getFragmentManager()
				.findFragmentByTag(MentionsTimeLineFragment.class.getName()));
	}

	private CommentsTimeLineFragment getCommentFragment() {
		return ((CommentsTimeLineFragment) getFragmentManager()
				.findFragmentByTag(CommentsTimeLineFragment.class.getName()));
	}*/

	/*private class TimeLinePagerAdapter extends AppFragmentPagerAdapter {

		List<Fragment> list = new ArrayList<Fragment>();

		public TimeLinePagerAdapter(FragmentManager fm) {
			super(fm);
			if (getHomeFragment() == null) {
				list.add(new FriendsTimeLineFragment(getAccount(), getUser(),
						getToken()));
			} else {
				list.add(getHomeFragment());
			}

		}

		public Fragment getItem(int position) {
			return list.get(position);
		}

		@Override
		protected String getTag(int position) {
			List<String> tagList = new ArrayList<String>();
			tagList.add(FriendsTimeLineFragment.class.getName());
			tagList.add(MentionsTimeLineFragment.class.getName());
			tagList.add(CommentsTimeLineFragment.class.getName());
			if (getResources().getBoolean(R.bool.blackmagic)) {
				tagList.add(DMUserListFragment.class.getName());
			} else {
				tagList.add(MyStatussTimeLineFragment.class.getName());
			}
			return tagList.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}
	}
*/
	ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		@SuppressLint("NewApi")
		@Override
		public void onPageSelected(int position) {
			getActivity().getActionBar().setSelectedNavigationItem(position);
		}
	};

	@Override
	public UserBean getUser() {
		return accountBean.getInfo();

	}

	@Override
	public AccountBean getAccount() {
		return accountBean;
	}

}
