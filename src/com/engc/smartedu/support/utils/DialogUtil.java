package com.engc.smartedu.support.utils;



import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.engc.smartedu.R;

public class DialogUtil {
	public static Dialog getMenuDialog(Activity context, View view) {

		final Dialog dialog = new Dialog(context, R.style.MenuDialogStyle);
		dialog.setContentView(view);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		int screenW = getScreenWidth(context);
		// int screenH = getScreenHeight(context);
		lp.width = screenW;
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		//window.setWindowAnimations(R.style.MenuDialogAnimation); // 添加动画
		return dialog;
	}
   
	public static Dialog getRequestDialog(Activity context,String displayMessage) {

		final Dialog dialog = new Dialog(context, R.style.Dialog);
		dialog.setContentView(R.layout.wait_dialog_view);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		int screenW = getScreenWidth(context);
		lp.width = (int) (0.6 * screenW);

		TextView titleTxtv = (TextView) dialog.findViewById(R.id.tvLoad);
		titleTxtv.setText(displayMessage);
		return dialog;
	}

	public static Dialog getCustomDialog(Activity context) {
		final Dialog dialog = new Dialog(context, R.style.Dialog);
		return dialog;
	}

	public static int getScreenWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getScreenHeight(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
}
