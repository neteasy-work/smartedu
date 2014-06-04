package com.engc.smartedu.widget;

import com.engc.smartedu.support.utils.GlobalContext;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
/**
 * 
 * Copyright © 2014ENGC. All rights reserved.
 * @Title: FloatView.java
 * @Package: com.engc.smartedu.widget
 * @Description: 悬浮按钮
 * @author: Administrator  
 * @date: 2014-5-26 下午4:04:03
 */
public class FloatView extends ImageView {
	
	private float x; 
    private float y; 
    
    private float mTouchX; 
    private float mTouchY; 
    
    private float mStartX; 
    private float mStartY; 
    
    private OnClickListener mClickListener; 
    
    private WindowManager windowManager = (WindowManager) getContext() 
            .getApplicationContext().getSystemService(Context.WINDOW_SERVICE); 
    private WindowManager.LayoutParams windowManagerParams = ((GlobalContext) getContext() 
            .getApplicationContext()).getWindowParams(); 

    public FloatView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    @Override 
    public boolean onTouchEvent(MotionEvent event){

        Rect frame = new Rect(); 
        getWindowVisibleDisplayFrame(frame); 
        int statusBarHeight = frame.top;
        System.out.println("statusBarHeight:"+statusBarHeight); 

        x = event.getRawX(); 
        y = event.getRawY() - statusBarHeight; 
        Log.i("tag", "currX" + x + "====currY" + y); 
        
        switch (event.getAction()) { 
        case MotionEvent.ACTION_DOWN: 
        mTouchX = event.getX(); 
        mTouchY = event.getY(); 
        mStartX = x; 
        mStartY = y; 
        Log.i("tag", "startX" + mTouchX + "====startY" 
        + mTouchY); 
        break; 
        case MotionEvent.ACTION_MOVE: 
        updateViewPosition(); 
        break; 
        case MotionEvent.ACTION_UP: 
        updateViewPosition(); 
        mTouchX = mTouchY = 0; 
        if ((x - mStartX) < 5 && (y - mStartY) < 5) { 
        if(mClickListener!=null) { 
        mClickListener.onClick(this); 
        } 
        } 
        break; 
        } 
        return false;
        
    }

    @Override 
    public void setOnClickListener(OnClickListener l) { 
        this.mClickListener = l; 
    }
    
    private void updateViewPosition() { 
        windowManagerParams.x = (int) (x - mTouchX); 
        windowManagerParams.y = (int) (y - mTouchY); 
        windowManager.updateViewLayout(this, windowManagerParams); 
        } 

}
