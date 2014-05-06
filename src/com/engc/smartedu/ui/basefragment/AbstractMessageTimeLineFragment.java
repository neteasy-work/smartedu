package com.engc.smartedu.ui.basefragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;
import com.engc.smartedu.R;

import com.engc.smartedu.bean.ListBean;
import com.engc.smartedu.bean.MessageBean;
import com.engc.smartedu.dao.destroy.DestroyStatusDao;
import com.engc.smartedu.support.error.WeiboException;
import com.engc.smartedu.support.lib.MyAsyncTask;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.actionmenu.StatusSingleChoiceModeListener;
import com.engc.smartedu.ui.adapter.StatusListAdapter;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.interfaces.IRemoveItem;
import com.engc.smartedu.ui.interfaces.MainTitmeLineAppActivity;

/**
 * User: qii
 * Date: 12-7-29
 */
@SuppressLint("NewApi")
public abstract class AbstractMessageTimeLineFragment<T extends ListBean<MessageBean, ?>> extends AbstractTimeLineFragment<T> implements IRemoveItem {

    private RemoveTask removeTask;

    protected void showNewMsgToastMessage(ListBean<MessageBean, ?> newValue) {
        if (newValue != null && getActivity() != null) {
            if (newValue.getSize() == 0) {
                Toast.makeText(getActivity(), getString(R.string.no_new_message), Toast.LENGTH_SHORT).show();
            } else if (newValue.getSize() > 0) {
                Toast.makeText(getActivity(), getString(R.string.total) + newValue.getSize() + getString(R.string.new_messages), Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void clearAndReplaceValue(ListBean<MessageBean, ?> value) {
        getList().getItemList().clear();
        getList().getItemList().addAll(value.getItemList());
        getList().setTotal_number(value.getTotal_number());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (position - 1 < getList().getSize() && position - 1 >= 0 && timeLineAdapter.getItem(position - 1) != null) {
                    MessageBean msg = getList().getItemList().get(position - 1);
                    StatusSingleChoiceModeListener choiceModeListener = new StatusSingleChoiceModeListener(getListView(), (StatusListAdapter) timeLineAdapter, AbstractMessageTimeLineFragment.this, msg);
                    if (mActionMode != null) {
                        mActionMode.finish();
                        mActionMode = null;
                    }

                    getListView().setItemChecked(position, true);
                    getAdapter().notifyDataSetChanged();
                    mActionMode = getActivity().startActionMode(choiceModeListener);
                    return true;

                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * fix android bug,long press a item in the first tab's listview, rotate screen, the item background is still blue(it is checked),
     * but if you test on other tabs' lstview, the item is not checked
     */
    @Override
    public void onResume() {
        super.onResume();
        clearActionMode();
    }

    @Override
    protected void buildListAdapter() {
        timeLineAdapter = new StatusListAdapter(this, ((MainTitmeLineAppActivity) getActivity()).getBitmapDownloader(), getList().getItemList(), getListView(), true);
        getListView().setAdapter(timeLineAdapter);
    }


    @Override
    public void removeItem(int position) {
        clearActionMode();
        if (removeTask == null || removeTask.getStatus() == MyAsyncTask.Status.FINISHED) {
            removeTask = new RemoveTask(GlobalContext.getInstance().getSpecialToken(), getList().getItemList().get(position).getId(), position);
            removeTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void removeCancel() {
        clearActionMode();
    }


    class RemoveTask extends MyAsyncTask<Void, Void, Boolean> {

        String token;
        String id;
        int positon;
        WeiboException e;

        public RemoveTask(String token, String id, int positon) {
            this.token = token;
            this.id = id;
            this.positon = positon;
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
            if (Utility.isAllNotNull(this.e, getActivity())) {
                Toast.makeText(getActivity(), e.getError(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                ((StatusListAdapter) timeLineAdapter).removeItem(positon);
            }
        }
    }
}
