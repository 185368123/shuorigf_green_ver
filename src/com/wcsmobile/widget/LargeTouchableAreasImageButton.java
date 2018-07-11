package com.wcsmobile.widget;

import com.wcsmobile.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.ImageButton;

public class LargeTouchableAreasImageButton extends ImageButton {
    private final int TOUCH_ADDITION = 0;  
    private int mTouchAdditionBottom = 0;  
    private int mTouchAdditionLeft = 0;  
    private int mTouchAdditionRight = 0;  
    private int mTouchAdditionTop = 0;  
    private int mPreviousLeft = -1;  
    private int mPreviousRight = -1;  
    private int mPreviousBottom = -1;  
    private int mPreviousTop = -1;  
	public LargeTouchableAreasImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public LargeTouchableAreasImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context, attrs);  
	}

	public LargeTouchableAreasImageButton(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context, attrs);  
	}
	 private void init(Context context, AttributeSet attrs) {  
	        TypedArray a = context.obtainStyledAttributes(attrs,  
	                R.styleable.LargeTouchableAreaView);  
	        int addition = (int) a.getDimension(  
	                R.styleable.LargeTouchableAreaView_addition, TOUCH_ADDITION);  
	        mTouchAdditionBottom = addition;  
	        mTouchAdditionLeft = addition;  
	        mTouchAdditionRight = addition;  
	        mTouchAdditionTop = addition;  
	        mTouchAdditionBottom = (int) a.getDimension(  
	                R.styleable.LargeTouchableAreaView_additionBottom,  
	                mTouchAdditionBottom);  
	        mTouchAdditionLeft = (int) a.getDimension(  
	                R.styleable.LargeTouchableAreaView_additionLeft,  
	                mTouchAdditionLeft);  
	        mTouchAdditionRight = (int) a.getDimension(  
	                R.styleable.LargeTouchableAreaView_additionRight,  
	                mTouchAdditionRight);  
	        mTouchAdditionTop = (int) a.getDimension(  
	                R.styleable.LargeTouchableAreaView_additionTop,  
	                mTouchAdditionTop);  
	        a.recycle();  
	    }  
	   @Override  
	    protected void onLayout(boolean changed, int left, int top, int right,  
	            int bottom) {  
	        super.onLayout(changed, left, top, right, bottom);  
	        if (left != mPreviousLeft || top != mPreviousTop  
	                || right != mPreviousRight || bottom != mPreviousBottom) {  
	            mPreviousLeft = left;  
	            mPreviousTop = top;  
	            mPreviousRight = right;  
	            mPreviousBottom = bottom;  
	            final View parent = (View) this.getParent();  
	            parent.setTouchDelegate(new TouchDelegate(new Rect(left  
	                    - mTouchAdditionLeft, top - mTouchAdditionTop, right  
	                    + mTouchAdditionRight, bottom + mTouchAdditionBottom), this));  
	        }  
	    }
}
