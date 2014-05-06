package com.engc.smartedu.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.engc.smartedu.bean.SearchStatusListBean;
import com.engc.smartedu.dao.search.SearchDao;
import com.engc.smartedu.support.error.WeiboException;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.ui.basefragment.AbstractMessageTimeLineFragment;
import com.engc.smartedu.ui.browser.BrowserWeiboMsgActivity;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;

/**
 * User: qii
 * Date: 12-11-10
 */
public class SearchStatusFragment extends AbstractMessageTimeLineFragment<SearchStatusListBean> {

    private int page = 1;

    private SearchStatusListBean bean = new SearchStatusListBean();

    @Override
    public SearchStatusListBean getList() {
        return bean;
    }

    public SearchStatusFragment() {

    }

    public void search() {
        pullToRefreshListView.startRefreshNow();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("bean", bean);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        commander = ((AbstractAppActivity) getActivity()).getBitmapDownloader();
        if (savedInstanceState != null && bean.getItemList().size() == 0) {
            clearAndReplaceValue((SearchStatusListBean) savedInstanceState.getSerializable("bean"));
            timeLineAdapter.notifyDataSetChanged();

        }

        refreshLayout(bean);


    }


    protected void listViewItemClick(AdapterView parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), BrowserWeiboMsgActivity.class);
        intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
        intent.putExtra("msg", bean.getItem(position));
        startActivity(intent);
    }


    @Override
    protected SearchStatusListBean getDoInBackgroundMiddleData(String beginId, String endId) throws WeiboException {
        return null;
    }


    @Override
    protected SearchStatusListBean getDoInBackgroundNewData() throws WeiboException {
        page = 1;
        SearchDao dao = new SearchDao(GlobalContext.getInstance().getSpecialToken(), ((SearchMainActivity) getActivity()).getSearchWord());
        SearchStatusListBean result = dao.getStatusList();

        return result;
    }

    @Override
    protected SearchStatusListBean getDoInBackgroundOldData() throws WeiboException {

        SearchDao dao = new SearchDao(GlobalContext.getInstance().getSpecialToken(), ((SearchMainActivity) getActivity()).getSearchWord());
        dao.setPage(String.valueOf(page + 1));

        SearchStatusListBean result = dao.getStatusList();

        return result;
    }

    @Override
    protected void newMsgOnPostExecute(SearchStatusListBean newValue) {
        if (newValue != null && getActivity() != null && newValue.getSize() > 0) {
            getList().addNewData(newValue);
            getAdapter().notifyDataSetChanged();
            getListView().setSelectionAfterHeaderView();
            getActivity().invalidateOptionsMenu();
        }

    }

    @Override
    protected void oldMsgOnPostExecute(SearchStatusListBean newValue) {

        if (newValue != null && newValue.getSize() > 0) {
            getList().addOldData(newValue);
            getAdapter().notifyDataSetChanged();
            getActivity().invalidateOptionsMenu();
            page++;
        }
    }
}
