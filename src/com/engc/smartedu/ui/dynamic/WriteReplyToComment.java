package com.engc.smartedu.ui.dynamic;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jivesoftware.smack.util.StringUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.engc.smartedu.R;
import com.engc.smartedu.dao.login.LoginDao;
import com.engc.smartedu.support.utils.GlobalContext;
import com.engc.smartedu.support.utils.ImageUtils;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.adapter.FaceAdapter;
import com.engc.smartedu.ui.adapter.FacePageAdapter;
import com.engc.smartedu.ui.interfaces.AbstractAppActivity;
import com.engc.smartedu.widget.CirclePageIndicator;
import com.engc.smartedu.widget.JazzyViewPager;

/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * 
 * @Title: WriteReplyToComment.java
 * @Package: com.engc.smartedu.ui.dynamic
 * @Description: 回复评论
 * @author: Administrator
 * @date: 2014-6-18 下午3:44:21
 */
@SuppressLint("NewApi")
public class WriteReplyToComment extends AbstractAppActivity implements
		OnClickListener, OnTouchListener {

	private JazzyViewPager mFaceViewPager;// 表情选择ViewPager
	private int mCurrentPage = 0;// 当前表情页
	private boolean mIsFaceShow = false;// 是否显示表情
	private int currentPage = 0;
	private boolean isFaceShow = false;
	private LinearLayout faceLinearLayout;
	private List<String> keys;
	private EditText msgEt;
	private WindowManager.LayoutParams params;
	private WindowManager.LayoutParams mWindowNanagerParams;
	private InputMethodManager mInputMethodManager;

	private GlobalContext global;
	private ImageButton imageEmoticon, imgeCamera;
	private TextView txtHeadTitle, txtChooicePic, txtCamera;
	private String dynamicId, commentContent, userName;

	private String theLarge;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_dynamic_content);
		initView();
		initData();

		initFacePage();

		/*
		 * token = getIntent().getStringExtra("token"); if
		 * (TextUtils.isEmpty(token)) token =
		 * GlobalContext.getInstance().getSpecialToken();
		 * 
		 * bean = (CommentBean) getIntent().getSerializableExtra("msg"); if
		 * (bean == null) { replyDraftBean = (ReplyDraftBean)
		 * getIntent().getSerializableExtra("draft");
		 * getEditTextView().setText(replyDraftBean.getContent()); bean =
		 * replyDraftBean.getCommentBean(); }
		 * 
		 * getEditTextView().setHint("@" + bean.getUser().getScreen_name() + "："
		 * + bean.getText());
		 * 
		 * //this time menu item is null...omg fuck android if
		 * (savedInstanceState != null) { savedEnableRepost =
		 * savedInstanceState.getBoolean("repost"); }
		 */
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mInputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
		faceLinearLayout.setVisibility(View.GONE);
		isFaceShow = false;
		super.onPause();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

		}
		return super.onOptionsItemSelected(item);
	}

	private void initView() {
		global = GlobalContext.getInstance();
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mWindowNanagerParams = getWindow().getAttributes();
		dynamicId = getIntent().getStringExtra("dynamicId");
		commentContent = getIntent().getStringExtra("content");
		userName = getIntent().getStringExtra("userName");
		getActionBar().setTitle(getString(R.string.reply_to_comment));
		getActionBar().setSubtitle(
				LoginDao.getLoginInfo(WriteReplyToComment.this).getUsername());
		getActionBar().setDisplayHomeAsUpEnabled(true);

		msgEt = (EditText) findViewById(R.id.status_new_content);
		msgEt.setHint("@" + userName + ":" + commentContent);
		imageEmoticon = (ImageButton) findViewById(R.id.menu_emoticon);
		imgeCamera = (ImageButton) findViewById(R.id.menu_topic);
		faceLinearLayout = (LinearLayout) findViewById(R.id.face_ll);
		mFaceViewPager = (JazzyViewPager) findViewById(R.id.face_pager);
		imageEmoticon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mIsFaceShow) {
					mInputMethodManager.hideSoftInputFromWindow(
							msgEt.getWindowToken(), 0);
					try {
						Thread.sleep(80);// 解决此时会黑一下屏幕的问题
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					faceLinearLayout.setVisibility(View.VISIBLE);
					mIsFaceShow = true;
				} else {
					faceLinearLayout.setVisibility(View.GONE);
					mInputMethodManager.showSoftInput(msgEt, 0);
					mIsFaceShow = false;
				}

			}
		});

		msgEt.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mInputMethodManager.showSoftInput(msgEt, 0);
				faceLinearLayout.setVisibility(View.GONE);
				mIsFaceShow = false;
				return false;
			}
		});
		imgeCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSendPicDialog("选择", "拍照", "相册");

			}
		});
		msgEt.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (params.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
							|| isFaceShow) {
						faceLinearLayout.setVisibility(View.GONE);
						isFaceShow = false;
						// imm.showSoftInput(msgEt, 0);
						return true;
					}
				}
				return false;
			}
		});
		msgEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				/*
				 * if (s.length() > 0) { mSendMsgBtn.setEnabled(true); } else {
				 * mSendMsgBtn.setEnabled(false); }
				 */
			}
		});
	}

	private void initData() {

		Set<String> keySet = global.getFaceMap().keySet();
		keys = new ArrayList<String>();
		keys.addAll(keySet);
	}

	/**
	 * 初始化表情page
	 */
	private void initFacePage() {
		// TODO Auto-generated method stub
		List<View> lv = new ArrayList<View>();
		for (int i = 0; i < global.NUM_PAGE; ++i)
			lv.add(getGridView(i));
		FacePageAdapter adapter = new FacePageAdapter(lv, mFaceViewPager);
		mFaceViewPager.setAdapter(adapter);
		mFaceViewPager.setCurrentItem(currentPage);
		// mFaceViewPager.setTransitionEffect(mEffects[mSpUtil.getFaceEffect()]);
		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mFaceViewPager);
		adapter.notifyDataSetChanged();
		faceLinearLayout.setVisibility(View.GONE);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentPage = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// do nothing
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// do nothing
			}
		});

	}

	/**
	 * 显示发送对话框
	 * 
	 * @param headTitle
	 */
	private void showSendPicDialog(String headTitle, String subTitle,
			String otherSubTitle) {
		final AlertDialog alg = new AlertDialog.Builder(
				WriteReplyToComment.this).create();
		alg.show();
		Window window = alg.getWindow();
		window.setContentView(R.layout.comment_dialog);
		txtHeadTitle = (TextView) window
				.findViewById(R.id.txt_info_header_title);
		txtHeadTitle.setText(headTitle);
		txtChooicePic = (TextView) window.findViewById(R.id.txtreplycomment);
		txtChooicePic.setText(subTitle);
		txtCamera = (TextView) window.findViewById(R.id.txtdisplaycomment);
		txtCamera.setText(otherSubTitle);
		// 拍照操作，打开照相
		txtCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String savePath = "";
				// 判断是否挂载了SD卡
				String storageState = Environment.getExternalStorageState();
				if (storageState.equals(Environment.MEDIA_MOUNTED)) {
					savePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/szzyz/Camera/";// 存放照片的文件夹
					File savedir = new File(savePath);
					if (!savedir.exists()) {
						savedir.mkdirs();
					}
				}

				// 没有挂载SD卡，无法保存文件
				if (TextUtils.isEmpty(savePath)) {
					Utility.ToastMessage(WriteReplyToComment.this,
							"无法保存照片，请检查SD卡是否挂载");
					return;
				}

				String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
						.format(new Date());
				String fileName = "smartedu" + timeStamp + ".jpg";// 照片命名
				File out = new File(savePath, fileName);
				Uri uri = Uri.fromFile(out);

				theLarge = savePath + fileName;// 该照片的绝对路径

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent,
						ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);

			}
		});

		txtChooicePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "选择图片"),
						ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);

			}
		});
	}

	/**
	 * 获得表情 gridview
	 * 
	 * @param i
	 * @return
	 */
	private GridView getGridView(int i) {
		// TODO Auto-generated method stub
		GridView gv = new GridView(this);
		gv.setNumColumns(7);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
		gv.setBackgroundColor(Color.TRANSPARENT);
		gv.setCacheColorHint(Color.TRANSPARENT);
		gv.setHorizontalSpacing(1);
		gv.setVerticalSpacing(1);
		gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		gv.setGravity(Gravity.CENTER);
		gv.setAdapter(new FaceAdapter(this, i));
		gv.setOnTouchListener(forbidenScroll());
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == global.NUM) {// 删除键的位置
					int selection = msgEt.getSelectionStart();
					String text = msgEt.getText().toString();
					if (selection > 0) {
						String text2 = text.substring(selection - 1);
						if ("]".equals(text2)) {
							int start = text.lastIndexOf("[");
							int end = selection;
							msgEt.getText().delete(start, end);
							return;
						}
						msgEt.getText().delete(selection - 1, selection);
					}
				} else {
					int count = currentPage * global.NUM + arg2;
					// 注释的部分，在EditText中显示字符串
					// String ori = msgEt.getText().toString();
					// int index = msgEt.getSelectionStart();
					// StringBuilder stringBuilder = new StringBuilder(ori);
					// stringBuilder.insert(index, keys.get(count));
					// msgEt.setText(stringBuilder.toString());
					// msgEt.setSelection(index + keys.get(count).length());

					// 下面这部分，在EditText中显示表情
					Bitmap bitmap = BitmapFactory.decodeResource(
							getResources(), (Integer) global.getFaceMap()
									.values().toArray()[count]);
					if (bitmap != null) {
						int rawHeigh = bitmap.getHeight();
						int rawWidth = bitmap.getHeight();
						int newHeight = 40;
						int newWidth = 40;
						// 计算缩放因子
						float heightScale = ((float) newHeight) / rawHeigh;
						float widthScale = ((float) newWidth) / rawWidth;
						// 新建立矩阵
						Matrix matrix = new Matrix();
						matrix.postScale(heightScale, widthScale);
						// 设置图片的旋转角度
						// matrix.postRotate(-30);
						// 设置图片的倾斜
						// matrix.postSkew(0.1f, 0.1f);
						// 将图片大小压缩
						// 压缩后图片的宽和高以及kB大小均会变化
						Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
								rawWidth, rawHeigh, matrix, true);
						ImageSpan imageSpan = new ImageSpan(
								WriteReplyToComment.this, newBitmap);
						String emojiStr = keys.get(count);
						SpannableString spannableString = new SpannableString(
								emojiStr);
						spannableString.setSpan(imageSpan,
								emojiStr.indexOf('['),
								emojiStr.indexOf(']') + 1,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						msgEt.append(spannableString);
					} else {
						String ori = msgEt.getText().toString();
						int index = msgEt.getSelectionStart();
						StringBuilder stringBuilder = new StringBuilder(ori);
						stringBuilder.insert(index, keys.get(count));
						msgEt.setText(stringBuilder.toString());
						msgEt.setSelection(index + keys.get(count).length());
					}
				}
			}
		});
		return gv;
	}

	// 防止乱pageview乱滚动
	private OnTouchListener forbidenScroll() {
		return new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_emoticon:
			if (!mIsFaceShow) {
				mInputMethodManager.hideSoftInputFromWindow(
						msgEt.getWindowToken(), 0);
				try {
					Thread.sleep(80);// 解决此时会黑一下屏幕的问题
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				faceLinearLayout.setVisibility(View.VISIBLE);
				mIsFaceShow = true;
			} else {
				faceLinearLayout.setVisibility(View.GONE);
				mInputMethodManager.showSoftInput(msgEt, 0);
				mIsFaceShow = false;
			}

			break;
		case R.id.menu_topic:
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.status_new_content:
			mInputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(),
					0);
			faceLinearLayout.setVisibility(View.GONE);
			mIsFaceShow = false;
			break;
		case R.id.input:
			mInputMethodManager.showSoftInput(msgEt, 0);
			faceLinearLayout.setVisibility(View.GONE);
			mIsFaceShow = false;
			break;

		default:
			break;
		}
		return false;
	}

}
