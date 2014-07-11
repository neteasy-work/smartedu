package com.engc.smartedu.ui.search;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.engc.smartedu.bean.UserBean;
import com.engc.smartedu.bean.UserListBean;
import com.engc.smartedu.dao.search.SearchDao;
import com.engc.smartedu.support.error.WeiboException;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.ui.basefragment.AbstractUserListFragment;

import java.util.List;

/**
 * User: qii
 * Date: 12-11-10
 */
public class SearchUserFragment extends AbstractUserListFragment {

    private int page = 1;


    public SearchUserFragment() {
        super();
    }

    public void search() {
        pullToRefreshListView.startRefreshNow();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            clearAndReplaceValue((UserListBean) savedInstanceState.getSerializable("bean"));
            getAdapter().notifyDataSetChanged();
        }
        refreshLayout(bean);
    }


    @SuppressLint("NewApi")
	@Override
    protected UserListBean getDoInBackgroundNewData() throws WeiboException {
        page = 1;
        SearchDao dao = new SearchDao(GlobalContext.getInstance().getSpecialToken(), ((SearchMainActivity) getActivity()).getSearchWord());
        UserListBean result = dao.getUserList();

        return result;
    }

    @SuppressLint("NewApi")
	@Override
    protected UserListBean getDoInBackgroundOldData() throws WeiboException {
        SearchDao dao = new SearchDao(GlobalContext.getInstance().getSpecialToken(), ((SearchMainActivity) getActivity()).getSearchWord());
        dao.setPage(String.valueOf(page + 1));

        UserListBean result = dao.getUserList();

        return result;
    }

    @Override
    protected void oldUserOnPostExecute(UserListBean newValue) {
        if (newValue != null && newValue.getUsers().size() > 0) {
            List<UserBean> list = newValue.getUsers();
            getList().getUsers().addAll(list);
            page++;
        }
    }

    @Override
    protected void newUserOnPostExecute() {

    }

}

