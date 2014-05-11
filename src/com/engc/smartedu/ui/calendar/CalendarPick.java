package com.engc.smartedu.ui.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

import com.engc.smartedu.R;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.ui.leave.LeaveActivity;
import com.engc.smartedu.widget.times.calendar.CalendarPickerView;
import com.engc.smartedu.widget.times.calendar.CalendarPickerView.SelectionMode;

/**
 * 日历
 * 
 * @ClassName: CalendarPick
 * @Description: TODO
 * @author wutao
 * @date 2013-10-16 下午4:50:28
 * 
 */
public class CalendarPick extends AbstractAppActivity {
	private static final String TAG = "CalendarPick";
	private CalendarPickerView calendar;
	private Button imgBack;
	private String timeType;
	private RadioButton rdoDay, rdoAm, rdoPm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_picker);
		timeType = getIntent().getStringExtra("timeType");


		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.MONTH, 1);

		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.MONTH, -1);
		final Calendar nextMonth = Calendar.getInstance();

		/*
		 * nextMonth.add(Calendar.MONTH, 2); final Calendar
		 * lastMonth=Calendar.getInstance(); lastMonth.add(Calendar.MONTH, -2);
		 */

		calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
		calendar.init(lastYear.getTime(), nextYear.getTime()) //
				.inMode(SelectionMode.SINGLE) //
				.withSelectedDate(new Date());

		calendar.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(CalendarPick.this, LeaveActivity.class);
				intent.putExtra("startTime", calendar.getSelectedDate()
						.getTime());
				setResult(0, intent);
				finish();

			}
		});
		calendar.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Intent intent = new Intent(CalendarPick.this, LeaveActivity.class);
				intent.putExtra("startTime", calendar.getSelectedDate()
						.getTime());
				setResult(0, intent);
				finish();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		final Button single = (Button) findViewById(R.id.button_single);
		final Button multi = (Button) findViewById(R.id.button_multi);
		final Button range = (Button) findViewById(R.id.button_range);
		final Button dialog = (Button) findViewById(R.id.button_dialog);
		single.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				single.setEnabled(false);
				multi.setEnabled(true);
				range.setEnabled(true);

				calendar.init(new Date(), nextYear.getTime()) //
						.inMode(SelectionMode.SINGLE) //
						.withSelectedDate(new Date());
			}
		});

		multi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				single.setEnabled(true);
				multi.setEnabled(false);
				range.setEnabled(true);

				Calendar today = Calendar.getInstance();
				ArrayList<Date> dates = new ArrayList<Date>();
				for (int i = 0; i < 5; i++) {
					today.add(Calendar.DAY_OF_MONTH, 3);
					dates.add(today.getTime());
				}
				calendar.init(new Date(), nextYear.getTime()) //
						.inMode(SelectionMode.MULTIPLE) //
						.withSelectedDates(dates);
			}
		});

		range.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				single.setEnabled(true);
				multi.setEnabled(true);
				range.setEnabled(false);

				Calendar today = Calendar.getInstance();
				ArrayList<Date> dates = new ArrayList<Date>();
				today.add(Calendar.DATE, 3);
				dates.add(today.getTime());
				today.add(Calendar.DATE, 5);
				dates.add(today.getTime());
				calendar.init(new Date(), nextYear.getTime()) //
						.inMode(SelectionMode.RANGE) //
						.withSelectedDates(dates);
			}
		});

		dialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CalendarPickerView dialogView = (CalendarPickerView) getLayoutInflater()
						.inflate(R.layout.calendar_dialog, null, false);
				dialogView.init(new Date(), nextYear.getTime());
				new AlertDialog.Builder(CalendarPick.this)
						.setTitle("I'm a dialog!")
						.setView(dialogView)
						.setNeutralButton("Dismiss",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialogInterface,
											int i) {
										dialogInterface.dismiss();
									}
								}).create().show();
			}
		});

		findViewById(R.id.done_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {

						showChooseDayorTime();
						/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						Date date = new Date(calendar.getSelectedDate().getTime());
						if (timeType.equals("start")) {
							Intent intent = new Intent(CalendarPick.this,
									AskForLeave.class);
							intent.putExtra("startTime", sdf.format(date).toString()
									+ " 12:00");
							setResult(0, intent);
							finish();

						} else {
							Intent intent = new Intent(CalendarPick.this,
									AskForLeave.class);
							intent.putExtra("endTime", sdf.format(date).toString()
									+ " 18:00");
							setResult(1, intent);
							finish();
						}*/
					}
				});
	}

	/**
	 * 弹出选择 上午、下午、全天选择框
	 */
	private void showChooseDayorTime() {
		final AlertDialog alg = new AlertDialog.Builder(CalendarPick.this)
				.create();
		alg.show();
		Window window = alg.getWindow();
		window.setContentView(R.layout.choose_calendar_dialog);
		rdoAm = (RadioButton) window.findViewById(R.id.rdo_am);
		rdoPm = (RadioButton) window.findViewById(R.id.rdo_pm);
		rdoDay = (RadioButton) window.findViewById(R.id.rdoallday);
		// 上午选中事件
		rdoAm.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date(calendar.getSelectedDate().getTime());
				if (timeType.equals("start")) {
					Intent intent = new Intent(CalendarPick.this,
							LeaveActivity.class);
					intent.putExtra("startTime", sdf.format(date).toString()
							+ " 06:00");
					intent.putExtra("isHalf", "AM");
					intent.putExtra("DisplaystartTime", sdf.format(date).toString()+" 上午");

					setResult(0, intent);
					alg.cancel();
					finish();

				} else {
					Intent intent = new Intent(CalendarPick.this,
							LeaveActivity.class);
					intent.putExtra("endTime", sdf.format(date).toString()
							+ " 12:00");
					intent.putExtra("isHalf", "AM");
					intent.putExtra("DisplayendTime", sdf.format(date).toString()+" 上午");

					setResult(1, intent);
					alg.cancel();
					finish();
				}

			}
		});
		// 下午选中事件
		rdoPm.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date(calendar.getSelectedDate().getTime());
				if (timeType.equals("start")) {
					Intent intent = new Intent(CalendarPick.this,
							LeaveActivity.class);
					intent.putExtra("startTime", sdf.format(date).toString()
							+ " 12:00");
					intent.putExtra("isHalf", "PM");
					intent.putExtra("DisplaystartTime", sdf.format(date).toString()+" 下午");
					setResult(0, intent);
					alg.cancel();
					finish();

				} else {
					Intent intent = new Intent(CalendarPick.this,
							LeaveActivity.class);
					intent.putExtra("endTime", sdf.format(date).toString()
							+ " 18:00");
					intent.putExtra("isHalf", "PM");
					intent.putExtra("DisplayendTime", sdf.format(date).toString()+" 下午");
					setResult(1, intent);
					alg.cancel();
					finish();
				}

			}
		});

		// 全天选中事件
		rdoDay.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date(calendar.getSelectedDate().getTime());
				if (timeType.equals("start")) {
					Intent intent = new Intent(CalendarPick.this,
							LeaveActivity.class);
					intent.putExtra("startTime", sdf.format(date).toString()
							+ " 00:00");
					intent.putExtra("isHalf", "None");
					intent.putExtra("DisplaystartTime", sdf.format(date).toString());

					setResult(0, intent);
					alg.cancel();
					finish();

				} else {
					Intent intent = new Intent(CalendarPick.this,
							LeaveActivity.class);
					intent.putExtra("endTime", sdf.format(date).toString()
							+ " 00:00");
					intent.putExtra("isHalf", "None");
					intent.putExtra("DisplayendTime", sdf.format(date).toString());
					setResult(1, intent);
					alg.cancel();
					finish();
				}

			}
		});

	}

}
