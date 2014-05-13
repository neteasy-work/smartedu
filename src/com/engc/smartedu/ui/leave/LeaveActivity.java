package com.engc.smartedu.ui.leave;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.dao.leave.LeaveDao;
import com.engc.smartedu.dao.login.LoginDao;
import com.engc.smartedu.support.exception.AppException;
import com.engc.smartedu.support.utils.DialogUtil;
import com.engc.smartedu.support.utils.SIMCardInfo;
import com.engc.smartedu.support.utils.TimeUtil;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.adapter.LeaveTypeAdapter;
import com.engc.smartedu.ui.calendar.CalendarPick;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.main.MainTimeLineActivity;

@SuppressLint("NewApi")
public class LeaveActivity extends AbstractAppActivity{
	private TableRow tbrLeaveStartDate, tbrLeaveEndDate, tbrLeaveType;
	private AutoCompleteTextView actLeaveRemark, actLeaveDays;
	private TextView txtLeaveMan, txtLeaveType, txtLeaveStartTime,
			txtDisplayLeaveMan, txtLeaveEndTime;
	private Button btnSubmit, imgBack;
	private ImageView imChooseLeaveMan;
	private ListView listLeaveType;
	private static final List<String> listLeaveTypeResource = new ArrayList<String>();
	private InputMethodManager imm;
	private LinearLayout mLayout;
	private int holidaysTypeCode;
	private String leaveBeginTime, leaveEndTime;
	private Dialog requestDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ask_for_leave);
		ActionBar actionBar=getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("请假");
		listLeaveTypeResource.clear();
		listLeaveTypeResource.add("病假");
		listLeaveTypeResource.add("事假");
		initView();

	}
	
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
	/**
	 * 初始化视图
	 */
	private void initView() {

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		actLeaveDays = (AutoCompleteTextView) findViewById(R.id.actleavedays);
		actLeaveRemark = (AutoCompleteTextView) findViewById(R.id.actleaveremark);
		tbrLeaveStartDate = (TableRow) findViewById(R.id.ask_for_leavestartdate_row);
		tbrLeaveEndDate = (TableRow) findViewById(R.id.ask_for_leaveenddate_row);
		tbrLeaveType = (TableRow) findViewById(R.id.ask_for_leavetype_row);
	
		txtLeaveMan = (TextView) findViewById(R.id.txt_ask_for_leaveman);
		imChooseLeaveMan = (ImageView) findViewById(R.id.imgchooseleaveman);
		btnSubmit = (Button) findViewById(R.id.btnentryleave);
		txtLeaveType = (TextView) findViewById(R.id.txtleavetype);
		txtLeaveStartTime = (TextView) findViewById(R.id.txtleavestartdate);
		txtLeaveEndTime = (TextView) findViewById(R.id.txtleaveenddate);
		txtLeaveMan = (TextView) findViewById(R.id.txt_ask_for_leaveman);
		txtDisplayLeaveMan = (TextView) findViewById(R.id.txtdisplayleavename);
		txtLeaveMan.setText(!LoginDao.getLoginInfo(LeaveActivity.this).getSonname().equals("") ? LoginDao
				.getLoginInfo(LeaveActivity.this).getSonname() : LoginDao.getLoginInfo(LeaveActivity.this).getUsername());
		txtDisplayLeaveMan.setText("请假人:" + txtLeaveMan.getText());
		actLeaveRemark = (AutoCompleteTextView) findViewById(R.id.actleaveremark);

		// 选择请假人
		imChooseLeaveMan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		// 结束时间
		tbrLeaveEndDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LeaveActivity.this, CalendarPick.class);
				intent.putExtra("timeType", "end");
				startActivityForResult(intent, 1);

			}
		});

		// 开始时间
		tbrLeaveStartDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LeaveActivity.this, CalendarPick.class);
				intent.putExtra("timeType", "start");
				startActivityForResult(intent, 1);
			}
		});
		// 请假类型
		tbrLeaveType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showChooseLeaveType();

			}
		});

		// 提交请假事件
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				if (TextUtils.isEmpty(txtLeaveType.getText().toString())) {
					Utility.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidaytype_null));
					return;
				}
				if (TextUtils.isEmpty(txtLeaveStartTime.getText().toString())) {
					Utility.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidaystartdate_null));
					return;
				}
				if (TextUtils.isEmpty(txtLeaveEndTime.getText().toString())) {
					Utility.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidayenddate_null));
					return;
				}
				if (!TextUtils
						.isEmpty(txtLeaveStartTime.getText().toString())
						&& !TextUtils.isEmpty(txtLeaveEndTime.getText()
								.toString())) {
					String startTime = txtLeaveStartTime.getText().toString();
					String endTime = txtLeaveEndTime.getText().toString();
					if (TimeUtil.toDate(startTime).getTime() > TimeUtil
							.toDate(endTime).getTime()) {
						Utility.ToastMessage(
								v.getContext(),
								getString(R.string.message_validate_holidays_startdate_enddate_normal));
						return;
					}

				}
				if (TextUtils.isEmpty(actLeaveDays.getText().toString())) {
					Utility.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidaydays_null));
					return;
				}
				if (TextUtils.isEmpty(actLeaveRemark.getText().toString())) {
					Utility.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidaysremark_null));
					return;
				}
				if (!Utility.isOnlyDigital(actLeaveDays.getText()
						.toString())) {
					Utility.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidays_isdigital));
					return;
				}
				if (actLeaveDays.getText().toString().equals("0")) {
					Utility.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidays_more_zero));
					return;
				}

				String PhoneNum = new SIMCardInfo(LeaveActivity.this)
						.getNativePhoneNumber();
				if (!PhoneNum.equals(""))
					PhoneNum = PhoneNum;
				/*
				 * else if (ac.getLoginInfo().getParentphone() != null) PhoneNum
				 * = ac.getLoginInfo().getParentphone();
				 */
				else
					PhoneNum = "13245678765";

				String days = actLeaveDays.getText().toString();

				/*
				 * if (days.indexOf("天") != -1) days = days.substring(0,
				 * days.length() - 1);
				 */
				requestDialog = DialogUtil.getRequestDialog(LeaveActivity.this,
						"正在登陆中");
				requestDialog.show();
				Utility.initAnim(v.getContext(), (ImageView) requestDialog
						.findViewById(R.id.auth_loading_icon), R.anim.rotate);
				ApplyHoliday(leaveBeginTime, leaveEndTime, Double
						.parseDouble(days), PhoneNum, actLeaveRemark.getText()
						.toString(), holidaysTypeCode);

			}
		});
	}
	
	
	/**
	 * 回调函数
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String name = "";
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 0:
			if (data != null) {
				leaveBeginTime = data.getExtras().getString("startTime");
				txtLeaveStartTime.setText(data.getExtras().getString(
						"DisplaystartTime"));

			}
			break;
		default:
			if (data != null) {
				leaveEndTime = data.getExtras().getString("endTime");
				String isHalf=data.getExtras().getString("isHalf");
				txtLeaveEndTime.setText(data.getExtras().getString(
						"DisplayendTime"));
				if (txtLeaveStartTime.getText() == null)
					Utility.ToastMessage(LeaveActivity.this, "请选择开始时间");

				//else if(isHalf.equals("AM")||isHalf.equals("PM"))
					/*actLeaveDays.setText(StringUtils
							.ConvertDateToStringForTimeResult(txtLeaveStartTime
									.getText().toString(), data.getExtras()
									.getString("endTime"))
							+ "天");
*/
			}
			break;

		}

	}

	/**
	 * 选择请假类型
	 */
	private void showChooseLeaveType() {
		final AlertDialog alg = new AlertDialog.Builder(LeaveActivity.this)
				.create();
		alg.show();
		Window window = alg.getWindow();
		window.setContentView(R.layout.ask_for_leave_type_list);
		listLeaveType = (ListView) window.findViewById(R.id.listleavetype);
		LeaveTypeAdapter adapter = new LeaveTypeAdapter(this,
				listLeaveTypeResource);
		listLeaveType.setAdapter(adapter);

		listLeaveType.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				txtLeaveType.setText(listLeaveTypeResource.get(arg2));
				if (arg2 == 0)
					holidaysTypeCode = 2;
				else
					holidaysTypeCode = 1;
				alg.cancel();
			}
		});

	}
	/**
	 * 申请假期
	 * 
	 * @param userCode
	 * @param startDate
	 * @param endDate
	 * @param days
	 * @param remark
	 * @param holidayType
	 */
	private void ApplyHoliday(final String startDate, final String endDate,
			final double days, final String telNo, final String remark,
			final int holidayType) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					Utility.ToastMessage(LeaveActivity.this,
							String.valueOf(msg.obj));
					//UIHelper.showMain(AskForLeave.this);
					requestDialog.cancel();
					finish();
				} else if (msg.what == 0) {
					requestDialog.cancel();
					Utility.ToastMessage(LeaveActivity.this,
							String.valueOf(msg.obj));
				} else if (msg.what == -1) {
					requestDialog.cancel();
					((AppException) msg.obj).makeToast(LeaveActivity.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					User user = LeaveDao.ApplyHolidays(LoginDao.getLoginInfo(LeaveActivity.this)
							.getUsercode(), LoginDao.getLoginInfo(LeaveActivity.this).getOrgid(),telNo, startDate, endDate, days,
							remark, holidaysTypeCode,LoginDao.getLoginInfo(LeaveActivity.this).getUsertype());
					if (user.getIsError().equals("true")) {
						msg.what = 1;// 成功
						msg.obj = user.getMessage();

					} else {
						msg.what = 0;
						msg.obj = user.getMessage();
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}

				handler.sendMessage(msg);
			}
		}.start();

	}

	
}
