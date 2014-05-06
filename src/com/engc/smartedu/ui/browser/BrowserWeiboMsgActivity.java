package com.engc.smartedu.ui.browser;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.ShareActionProvider;
import android.widget.Toast;
import com.engc.smartedu.R;

import com.engc.smartedu.bean.MessageBean;
import com.engc.smartedu.dao.destroy.DestroyStatusDao;
import com.engc.smartedu.support.error.WeiboException;
import com.engc.smartedu.support.lib.AppFragmentPagerAdapter;
import com.engc.smartedu.support.lib.MyAsyncTask;
import com.engc.smartedu.support.utils.AppConfig;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.basefragment.AbstractTimeLineFragment;
import com.engc.smartedu.ui.fragment.LeftMenuFragment;
import com.engc.smartedu.ui.fragment.RightMenuFragment;
import com.engc.smartedu.ui.home.HomeFragment;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.interfaces.MainTitmeLineAppActivity;
import com.engc.smartedu.ui.main.MainTimeLineActivity;
import com.engc.smartedu.ui.send.WriteCommentActivity;
import com.engc.smartedu.ui.send.WriteRepostActivity;
import com.engc.smartedu.ui.task.FavAsyncTask;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Jiang Qi
 * Date: 12-8-1
 */
@SuppressLint("NewApi")
public class BrowserWeiboMsgActivity extends MainTitmeLineAppActivity implements RemoveWeiboMsgDialog.IRemove {

    private MessageBean msg;
    private String token;


    private String comment_sum = "";
    private String retweet_sum = "";

    private ViewPager mViewPager = null;

    private FavAsyncTask favTask = null;

    private ShareActionProvider mShareActionProvider;

    private GestureDetector gestureDetector;

    private RemoveTask removeTask;
    private int mScreenWidth; // 当前设备屏幕宽度
    protected SlidingMenu mSlidingMenu;
    private android.support.v4.app.Fragment mContent;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("msg", msg);
        outState.putString("token", token);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            msg = (MessageBean) savedInstanceState.getSerializable("msg");
            token = savedInstanceState.getString("token");
        } else {
            Intent intent = getIntent();
            token = intent.getStringExtra("token");
            msg = (MessageBean) intent.getSerializableExtra("msg");
        }
        initSlidingMenu(); //初始化 slidingmenu  ps: why add init slidingmenu？ Shit！
        //setBehindContentView(R.layout.main_left_layout);
        setContentView(R.layout.viewpager_layout);
        initRightSlidingMenuView(); //初始化右侧slidingmenu

        buildViewPager();
        buildActionBarAndViewPagerTitles();
    }
    

	/**
	 * 初始化右侧slidingmenu
	 */
	private void initRightSlidingMenuView() {
		// /初始化Rightmenu
		android.support.v4.app.FragmentTransaction FragementTransaction = getSupportFragmentManager()
				.beginTransaction();
		mSlidingMenu.setSecondaryMenu(R.layout.main_right_layout);
		android.support.v4.app.Fragment mFragRight = new RightMenuFragment();
		FragementTransaction.replace(R.id.main_right_fragment, mFragRight);
		FragementTransaction.commit();
		mSlidingMenu.setRightMenuOffset(mScreenWidth / 6);
		mSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadow);
	}
    
	/**
	 * 初始化slidingmenu
	 */
    private void initSlidingMenu() {
		// int mScreenWidth=Utility.getScreenWidth();//获取屏幕宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		mContent = new HomeFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();
		setBehindContentView(R.layout.main_left_layout);
		android.support.v4.app.FragmentTransaction mFragementTransaction = getSupportFragmentManager()
				.beginTransaction();
		android.support.v4.app.Fragment mFrag = new LeftMenuFragment();
		mFragementTransaction.replace(R.id.main_left_fragment, mFrag);
		mFragementTransaction.commit();
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);// 设置是左滑还是右滑，还是左右都可以滑，
		// mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// 设置菜单宽度
		mSlidingMenu.setBehindOffset(mScreenWidth / 2);

		mSlidingMenu.setFadeDegree(0.35f);// 设置淡入淡出的比例
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置手势模式
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);// 设置左菜单阴影图片
		mSlidingMenu.setFadeEnabled(true);// 设置滑动时菜单的是否淡入淡出
		mSlidingMenu.setBehindScrollScale(0.333f);// 设置滑动时拖拽效果

	}


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.cancelTasks(removeTask);
    }

    private void buildViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        TimeLinePagerAdapter adapter = new TimeLinePagerAdapter(getFragmentManager());
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(onPageChangeListener);
        gestureDetector = new GestureDetector(BrowserWeiboMsgActivity.this, new MyOnGestureListener());
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void buildActionBarAndViewPagerTitles() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.detail));

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.weibo))
                .setTabListener(tabListener));

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.comments))
                .setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.repost))
                .setTabListener(tabListener));

    }

    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            getActionBar().setSelectedNavigationItem(position);
            switch (position) {

                case 2:
                    ((RepostsByIdTimeLineFragment) getRepostFragment()).load();
                    break;
            }

        }
    };


    private AbstractTimeLineFragment getRepostFragment() {
        return ((AbstractTimeLineFragment) getFragmentManager().findFragmentByTag(
                RepostsByIdTimeLineFragment.class.getName()));
    }

    private AbstractTimeLineFragment getCommentFragment() {
        return ((AbstractTimeLineFragment) getFragmentManager().findFragmentByTag(
                CommentsByIdTimeLineFragment.class.getName()));
    }

    private Fragment getBrowserWeiboMsgFragment() {
        return getFragmentManager().findFragmentByTag(BrowserWeiboMsgFragment.class.getName());
    }

    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
        boolean comment = false;
        boolean repost = false;

        public void onTabSelected(ActionBar.Tab tab,
                                  FragmentTransaction ft) {

            if (mViewPager.getCurrentItem() != tab.getPosition())
                mViewPager.setCurrentItem(tab.getPosition());
            if (getCommentFragment() != null)
                getCommentFragment().clearActionMode();
            if (getRepostFragment() != null)
                getRepostFragment().clearActionMode();

            switch (tab.getPosition()) {

                case 1:
                    comment = true;
                    break;
                case 2:
                    repost = true;
                    break;
                case 3:
                    break;
            }
        }

        public void onTabUnselected(ActionBar.Tab tab,
                                    FragmentTransaction ft) {
            switch (tab.getPosition()) {

                case 1:
                    comment = false;
                    break;
                case 2:
                    repost = false;
                    break;

            }
        }

        public void onTabReselected(ActionBar.Tab tab,
                                    FragmentTransaction ft) {
            switch (tab.getPosition()) {

                case 1:
                    if (comment) {
                        Utility.stopListViewScrollingAndScrollToTop(getCommentFragment().getListView());
                    }
                    break;
                case 2:
                    if (repost) {
                        Utility.stopListViewScrollingAndScrollToTop(getRepostFragment().getListView());
                    }
                    break;
                case 3:
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu_browserweibomsgactivity, menu);

        if (msg.getUser() != null && msg.getUser().getId().equals(GlobalContext.getInstance().getCurrentAccountId())) {
            menu.findItem(R.id.menu_delete).setVisible(true);
        }

        MenuItem item = menu.findItem(R.id.menu_share);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        buildShareActionMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainTimeLineActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.menu_repost:
                intent = new Intent(this, WriteRepostActivity.class);
                intent.putExtra("token", getToken());
                intent.putExtra("id", getMsg().getId());
                intent.putExtra("msg", getMsg());
                startActivity(intent);
                return true;
            case R.id.menu_comment:

                intent = new Intent(this, WriteCommentActivity.class);
                intent.putExtra("token", getToken());
                intent.putExtra("id", getMsg().getId());
                intent.putExtra("msg", getMsg());
                startActivity(intent);

                return true;

            case R.id.menu_share:

                buildShareActionMenu();
                return true;
            case R.id.menu_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("sinaweibo", getMsg().getText()));
                Toast.makeText(this, getString(R.string.copy_successfully), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_fav:
                if (favTask == null || favTask.getStatus() == MyAsyncTask.Status.FINISHED) {
                    favTask = new FavAsyncTask(getToken(), msg.getId());
                    favTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }

                return true;
            case R.id.menu_delete:
                RemoveWeiboMsgDialog dialog = new RemoveWeiboMsgDialog(msg.getId());
                dialog.show(getFragmentManager(), "");
                return true;
        }
        return false;
    }

    private void buildShareActionMenu() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        if (msg != null) {
            sharingIntent.putExtra(Intent.EXTRA_TEXT, msg.getText());
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(sharingIntent, 0);
            boolean isIntentSafe = activities.size() > 0;
            if (isIntentSafe && mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(sharingIntent);
            }
        }
    }

    @Override
    public void removeMsg(String id) {
        if (Utility.isTaskStopped(removeTask)) {
            removeTask = new RemoveTask(id);
            removeTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    class TimeLinePagerAdapter extends
            AppFragmentPagerAdapter {

        List<Fragment> list = new ArrayList<Fragment>();


        public TimeLinePagerAdapter(FragmentManager fm) {
            super(fm);
            if (getBrowserWeiboMsgFragment() == null) {
                list.add(new BrowserWeiboMsgFragment(msg));
            } else {
                list.add(getBrowserWeiboMsgFragment());
            }
            if (getCommentFragment() == null) {
                list.add(new CommentsByIdTimeLineFragment(token, msg.getId()));
            } else {
                list.add(getCommentFragment());
            }
            if (getRepostFragment() == null) {
                list.add(new RepostsByIdTimeLineFragment(token, msg.getId(), msg));
            } else {
                list.add(getRepostFragment());
            }
        }

        @Override
        public Fragment getItem(int i) {
            return list.get(i);
        }

        @Override
        protected String getTag(int position) {
            List<String> tagList = new ArrayList<String>();
            tagList.add(BrowserWeiboMsgFragment.class.getName());
            tagList.add(CommentsByIdTimeLineFragment.class.getName());
            tagList.add(RepostsByIdTimeLineFragment.class.getName());
            return tagList.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }


    public String getToken() {
        return token;
    }

    public MessageBean getMsg() {
        return msg;
    }


    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (velocityX > AppConfig.SWIPE_MIN_DISTANCE && mViewPager.getCurrentItem() == 0) {
                finish();
                return true;
            }
            return false;
        }
    }

    class RemoveTask extends MyAsyncTask<Void, Void, Boolean> {

        String id;
        WeiboException e;

        public RemoveTask(String id) {
            this.id = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            DestroyStatusDao dao = new DestroyStatusDao(token, id);
            try {
                return dao.destroy();
            } catch (WeiboException e) {
                this.e = e;
                cancel(true);
                return false;
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            if (this.e != null) {
                Toast.makeText(BrowserWeiboMsgActivity.this, e.getError(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                finish();
            }
        }
    }
}
