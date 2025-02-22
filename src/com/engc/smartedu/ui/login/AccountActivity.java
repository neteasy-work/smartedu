package com.engc.smartedu.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.engc.smartedu.R;

import com.engc.smartedu.bean.AccountBean;
import com.engc.smartedu.dao.login.LoginDao;
import com.engc.smartedu.support.database.DatabaseManager;
import com.engc.smartedu.support.lib.MyAsyncTask;
import com.engc.smartedu.support.lib.changelogdialog.ChangeLogDialog;
import com.engc.smartedu.support.settinghelper.SettingUtility;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.SharePreferenceUtil;
import com.engc.smartedu.ui.blackmagic.BlackMagicActivity;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.main.MainTimeLineActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressLint("NewApi")
public class AccountActivity extends AbstractAppActivity {

	private ListView listView = null;

	private AccountAdapter listAdapter = null;

	private List<AccountBean> accountList = new ArrayList<AccountBean>();

	private GetAccountListDBTask getTask = null;
	private RemoveAccountDBTask removeTask = null;  

	private final int ADD_ACCOUNT_REQUEST_CODE = 0;
	public static int actionBarHeight;
	private SharePreferenceUtil spUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		GlobalContext.getInstance().startedApp = true;
		spUtil = GlobalContext.getInstance().getSpUtil();
		jumpToHomeActivity();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.accountactivity_layout);

		listAdapter = new AccountAdapter();
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new AccountListItemClickListener());
		listView.setAdapter(listAdapter);
		listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new AccountMultiChoiceModeListener());

		getTask = new GetAccountListDBTask();
		getTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);

		if (SettingUtility.firstStart())
			showChangeLogDialog();
	}

	@Override
	public void onBackPressed() {
		GlobalContext.getInstance().startedApp = false;
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelAllTask();
	}

	private void cancelAllTask() {
		if (getTask != null)
			getTask.cancel(true);

		if (removeTask != null)
			removeTask.cancel(true);
	}

	private void showChangeLogDialog() {
		ChangeLogDialog changeLogDialog = new ChangeLogDialog(this);
		changeLogDialog.show();
	}

	private void jumpToHomeActivity() {
		Intent intent = getIntent();
		if (intent != null) {
			boolean launcher = intent.getBooleanExtra("launcher", true);
			if (launcher) {
				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(this);
				String id = sharedPref.getString("id", "");
				if (!TextUtils.isEmpty(id)) {
					AccountBean bean = DatabaseManager.getInstance()
							.getAccount(id);
					String userCode=spUtil.getUserCode();
					if (bean != null) {
						if (!TextUtils.isEmpty(userCode)) {
							Intent start = new Intent(AccountActivity.this,
									MainTimeLineActivity.class);
							start.putExtra("account", bean);
							startActivity(start);
							finish();
						} else {
							Intent start = new Intent(AccountActivity.this,
									LoginActivity.class);
							start.putExtra("account", bean);
							startActivity(start);
							finish();
						}
					}
				}
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menu_accountactivity, menu);
		actionBarHeight = getActionBar().getHeight();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_account:
			addAccount();
			break;
		case R.id.menu_hack_login:
			Intent intent = new Intent(this, BlackMagicActivity.class);
			startActivityForResult(intent, ADD_ACCOUNT_REQUEST_CODE);
			break;
		}
		return true;
	}

	public void addAccount() {

		Intent intent = new Intent(this, OAuthActivity.class);
		startActivityForResult(intent, ADD_ACCOUNT_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_ACCOUNT_REQUEST_CODE && resultCode == RESULT_OK) {
			refresh();
		}
	}

	private void refresh() {
		if (getTask == null
				|| getTask.getStatus() == MyAsyncTask.Status.FINISHED) {
			getTask = new GetAccountListDBTask();
			getTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	private class AccountListItemClickListener implements
			AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i,
				long l) {

			Intent intent = new Intent(AccountActivity.this,
					MainTimeLineActivity.class);
			intent.putExtra("account", accountList.get(i));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
	}

	private class AccountMultiChoiceModeListener implements
			AbsListView.MultiChoiceModeListener {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(
					R.menu.contextual_menu_accountactivity, menu);
			mode.setTitle(getString(R.string.account_management));
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_remove_account:
				if (removeTask == null
						|| removeTask.getStatus() == MyAsyncTask.Status.FINISHED) {
					removeTask = new RemoveAccountDBTask();
					removeTask
							.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
				}
				mode.finish();
				return true;
			}
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			listAdapter.notifyDataSetChanged();
		}
	}

	private class AccountAdapter extends BaseAdapter {

		int checkedBG;
		int defaultBG;

		public AccountAdapter() {
			defaultBG = getResources().getColor(R.color.transparent);

			int[] attrs = new int[] { R.attr.listview_checked_color };
			TypedArray ta = obtainStyledAttributes(attrs);
			checkedBG = ta.getColor(0, 430);
		}

		@Override
		public int getCount() {
			return accountList.size();
		}

		@Override
		public Object getItem(int i) {
			return accountList.get(i);
		}

		@Override
		public long getItemId(int i) {
			return Long.valueOf(accountList.get(i).getUid());
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getView(final int i, View view, ViewGroup viewGroup) {

			LayoutInflater layoutInflater = getLayoutInflater();

			View mView = layoutInflater.inflate(
					R.layout.accountactivity_listview_item_layout, viewGroup,
					false);
			mView.findViewById(R.id.listview_root)
					.setBackgroundColor(defaultBG);

			if (listView.getCheckedItemPositions().get(i)) {
				mView.findViewById(R.id.listview_root).setBackgroundColor(
						checkedBG);
			}

			TextView textView = (TextView) mView
					.findViewById(R.id.account_name);
			if (accountList.get(i).getInfo() != null)
				textView.setText(accountList.get(i).getInfo().getScreen_name());
			else
				textView.setText(accountList.get(i).getUsernick());
			ImageView imageView = (ImageView) mView
					.findViewById(R.id.imageView_avatar);

			if (!TextUtils.isEmpty(accountList.get(i).getAvatar_url())) {
				commander.downloadAvatar(imageView, accountList.get(i)
						.getInfo(), false);
			}

			return mView;
		}
	}

	private class GetAccountListDBTask extends
			MyAsyncTask<Void, List<AccountBean>, List<AccountBean>> {

		@Override
		protected List<AccountBean> doInBackground(Void... params) {
			return DatabaseManager.getInstance().getAccountList();
		}

		@Override
		protected void onPostExecute(List<AccountBean> accounts) {
			accountList = accounts;
			listAdapter.notifyDataSetChanged();

		}
	}

	private class RemoveAccountDBTask extends
			MyAsyncTask<Void, List<AccountBean>, List<AccountBean>> {

		Set<String> set = new HashSet<String>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			long[] ids = listView.getCheckedItemIds();
			for (long id : ids) {
				set.add(String.valueOf(id));
			}
		}

		@Override
		protected List<AccountBean> doInBackground(Void... params) {
			return DatabaseManager.getInstance()
					.removeAndGetNewAccountList(set);
		}

		@Override
		protected void onPostExecute(List<AccountBean> accounts) {
			accountList = accounts;
			listAdapter.notifyDataSetChanged();
		}
	}
}
