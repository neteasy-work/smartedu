package com.engc.smartedu.ui.topic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.main.MainTimeLineActivity;

/**
 * User: qii
 * Date: 12-9-8
 */
@SuppressLint("NewApi")
public class SearchTopicByNameActivity extends AbstractAppActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String q = getIntent().getStringExtra("q");
        if (TextUtils.isEmpty(q)) {
            Uri data = getIntent().getData();
            String d = data.toString();
            int index = d.lastIndexOf("/");
            q = d.substring(index + 1);
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("#" + q + "#");
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SearchTopicByNameFragment(q))
                    .commit();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(this, MainTimeLineActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;

        }
        return false;
    }
}
