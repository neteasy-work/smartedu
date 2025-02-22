package com.engc.smartedu.ui.basefragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.AbsListView;

import com.engc.smartedu.bean.UserBean;
import com.engc.smartedu.bean.UserListBean;
import com.engc.smartedu.ui.adapter.ListViewHoildayRecordAdapter;
import com.engc.smartedu.ui.interfaces.IUserInfo;

import java.util.List;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: AbstractFriendsFanListFragment.java
 * @Package: com.engc.smartedu.ui.basefragment
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-7-7 下午4:27:18
 */
@SuppressLint("NewApi")
public abstract class AbstractFriendsFanListFragment extends AbstractUserListFragment {

    protected UserBean currentUser;
    protected String uid;


    public AbstractFriendsFanListFragment() {

    }

    public AbstractFriendsFanListFragment(String uid) {
        this.uid = uid;
    }

    @Override
    protected void oldUserOnPostExecute(UserListBean newValue) {
        if (newValue != null && newValue.getUsers().size() > 0 && newValue.getPrevious_cursor() != bean.getPrevious_cursor()) {
            List<UserBean> list = newValue.getUsers();
            getList().getUsers().addAll(list);
            bean.setNext_cursor(newValue.getNext_cursor());
            buildActionBarSubtitle();
        }

    }

    @Override
    protected void newUserOnPostExecute() {
        buildActionBarSubtitle();
    }

    private void buildActionBarSubtitle() {
        if (!TextUtils.isEmpty(currentUser.getFriends_count())) {

            int size = Integer.valueOf(currentUser.getFriends_count());
            int newSize = bean.getTotal_number();
            String number = "";
            if (size >= newSize) {
                number = bean.getUsers().size() + "/" + size;
            } else {
                number = bean.getUsers().size() + "/" + newSize;
            }
            getActivity().getActionBar().setSubtitle(number);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                pullToRefreshListView.startRefreshNow();
                break;
            case SCREEN_ROTATE:
                //nothing
                refreshLayout(bean);
                break;
            case ACTIVITY_DESTROY_AND_CREATE:
                currentUser = (UserBean) savedInstanceState.getSerializable("currentUser");
                uid = savedInstanceState.getString("uid");
                clearAndReplaceValue((UserListBean) savedInstanceState.getSerializable("bean"));
                getAdapter().notifyDataSetChanged();
                break;
        }

        refreshLayout(bean);

        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = ((IUserInfo) getActivity()).getUser();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentUser", currentUser);
        outState.putString("uid", uid);
    }
}
