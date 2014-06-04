package com.engc.smartedu.ui.setup;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
/**
 * 关于 我们 activity
 * @author Admin
 *
 */
@SuppressLint("NewApi")
public class AboutActivity extends AbstractAppActivity{
    
	private TextView mVersion;	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("关于");
			
		//获取客户端版本信息
        try { 
        	PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
        	mVersion = (TextView)findViewById(R.id.about_version);
    		mVersion.setText("版本："+info.versionName);
        } catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
        
      
	}
}
