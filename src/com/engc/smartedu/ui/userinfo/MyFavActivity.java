package com.engc.smartedu.ui.userinfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.engc.smartedu.R;

import com.engc.smartedu.bean.UserBean;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.interfaces.IUserInfo;
import com.engc.smartedu.ui.main.MainTimeLineActivity;

/**
 * User: qii
 * Date: 12-8-18
 */
@SuppressLint("NewApi")
public class MyFavActivity extends AbstractAppActivity implements IUserInfo {
    private UserBean bean;


    @Override
    public UserBean getUser() {
        return bean;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(getString(R.string.my_fav_list));
        String token = getIntent().getStringExtra("token");
        bean = (UserBean) getIntent().getSerializableExtra("user");
        if (getFragmentManager().findFragmentByTag(MyFavListFragment.class.getName()) == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new MyFavListFragment(), MyFavListFragment.class.getName())
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, MainTimeLineActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return false;
    }
}
