package com.engc.smartedu.ui.actionmenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.engc.smartedu.R;

import com.engc.smartedu.bean.UserBean;
import com.engc.smartedu.dao.relationship.FriendshipsDao;
import com.engc.smartedu.support.error.WeiboException;
import com.engc.smartedu.support.lib.MyAsyncTask;
import com.engc.smartedu.support.utils.AppLogger;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.ui.adapter.UserListAdapter;
import com.engc.smartedu.ui.basefragment.AbstractUserListFragment;
import com.engc.smartedu.ui.send.WriteWeiboActivity;

/**
 * User: qii
 * Date: 12-10-11
 */
@SuppressLint("NewApi")
public class MyFriendSingleChoiceModeListener implements ActionMode.Callback {
    private ListView listView;
    private UserListAdapter adapter;
    private Fragment fragment;
    private ActionMode mode;
    private UserBean bean;

    private UnFollowTask unfollowTask;


    public void finish() {
        if (mode != null)
            mode.finish();

        if (unfollowTask != null)
            unfollowTask.cancel(true);
    }

    public MyFriendSingleChoiceModeListener(ListView listView, UserListAdapter adapter, Fragment fragment, UserBean bean) {
        this.listView = listView;
        this.fragment = fragment;
        this.adapter = adapter;
        this.bean = bean;
    }

    private Activity getActivity() {
        return fragment.getActivity();
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (this.mode == null)
            this.mode = mode;

        return true;

    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        menu.clear();

        inflater.inflate(R.menu.contextual_menu_myfriendlistfragment, menu);

        mode.setTitle(bean.getScreen_name());


        return true;


    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_at:
                Intent intent = new Intent(getActivity(), WriteWeiboActivity.class);
                intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
                intent.putExtra("content", "@" + bean.getScreen_name());
                intent.putExtra("account", GlobalContext.getInstance().getAccountBean());
                getActivity().startActivity(intent);
                listView.clearChoices();
                mode.finish();
                break;

            case R.id.menu_unfollow:
                if (unfollowTask == null || unfollowTask.getStatus() == MyAsyncTask.Status.FINISHED) {
                    unfollowTask = new UnFollowTask();
                    unfollowTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }
                listView.clearChoices();
                mode.finish();
                break;
        }


        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.mode = null;
        listView.clearChoices();
        adapter.notifyDataSetChanged();
        ((AbstractUserListFragment) fragment).setmActionMode(null);

    }


    private class UnFollowTask extends MyAsyncTask<Void, UserBean, UserBean> {
        WeiboException e;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected UserBean doInBackground(Void... params) {

            FriendshipsDao dao = new FriendshipsDao(GlobalContext.getInstance().getSpecialToken());
            if (!TextUtils.isEmpty(bean.getId())) {
                dao.setUid(bean.getId());
            } else {
                dao.setScreen_name(bean.getScreen_name());
            }

            try {
                return dao.unFollowIt();
            } catch (WeiboException e) {
                AppLogger.e(e.getError());
                this.e = e;
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onCancelled(UserBean userBean) {
            super.onCancelled(userBean);
        }

        @Override
        protected void onPostExecute(UserBean o) {
            super.onPostExecute(o);
            Toast.makeText(getActivity(), getActivity().getString(R.string.unfollow_successfully), Toast.LENGTH_SHORT).show();
            adapter.removeItem(bean);
        }
    }

}
