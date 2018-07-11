package com.wcsmobile.widget;



import com.wcsmobile.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 
 * @author xiaanming
 *
 */
public class WiperSwitch extends View implements OnTouchListener{
	private Bitmap bg_on, bg_off, slipper_btn;

	private float downX, nowX;


	private boolean onSlip = false;

	
	private boolean nowStatus = false;

	
	private OnChangedListener listener;


	public WiperSwitch(Context context) {
		super(context);
		init();
	}

	public WiperSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	private static Bitmap small(Bitmap bitmap) {
		Matrix matrix = new Matrix(); 
		matrix.postScale(0.6f,0.6f); 
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
		return resizeBmp;
	}
	public void init(){
		//锟斤拷锟斤拷图片锟斤拷源
		bg_on = small(BitmapFactory.decodeResource(getResources(), R.drawable.switch_track_on));
		bg_off = small(BitmapFactory.decodeResource(getResources(), R.drawable.switch_track_off));
		slipper_btn = small(BitmapFactory.decodeResource(getResources(), R.drawable.switch_thumb));

		setOnTouchListener(this);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x = 0;
       /* paint.setColor(Color.BLUE);  
        paint.setStrokeWidth(5);  
        
        canvas.drawLine(0, 0, 0, getHeight(), paint);  
        canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paint);  */

		if (nowX < (bg_on.getWidth()/2)){
			canvas.drawBitmap(bg_off, matrix, paint);
		}else{
			canvas.drawBitmap(bg_on, matrix, paint);
		}

		if (onSlip) {
			if(nowX >= bg_on.getWidth())//
				x = bg_on.getWidth() - slipper_btn.getWidth()/2;
			else
				x = nowX - slipper_btn.getWidth()/2;
		}else {
			if(nowStatus){
				x = bg_on.getWidth() - slipper_btn.getWidth();
			}else{
				x = 0;
			}
		}
		if (x < 0 ){
			x = 0;
		}
		else if(x > bg_on.getWidth() - slipper_btn.getWidth()){
			x = bg_on.getWidth() - slipper_btn.getWidth();
		}

	
		canvas.drawBitmap(slipper_btn, x , 0, paint); 
	//	canvas.drawBitmap(slipper_btn, x , 0, paint); 
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:{
			if (event.getX() > bg_off.getWidth() || event.getY() > bg_off.getHeight()){
				return false;
			}else{
				onSlip = true;
				downX = event.getX();
				nowX = downX;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE:{
			nowX = event.getX();
			break;
		}
		case MotionEvent.ACTION_UP:{
			onSlip = false;
			if(event.getX() >= (bg_on.getWidth()/2)){
				nowStatus = true;
				nowX = bg_on.getWidth() - slipper_btn.getWidth();
			}else{
				nowStatus = false;
				nowX = 0;
			}

			if(listener != null){
				listener.OnChanged(WiperSwitch.this, nowStatus);
			}
			break;
		}
		}
		//刷锟铰斤拷锟斤拷
		invalidate();
		return true;
	}



	/**
	 * 
	 * @param listener
	 */
	public void setOnChangedListener(OnChangedListener listener){
		this.listener = listener;
	}


	/**
	 * 
	 * @param checked
	 */
	public void setChecked(boolean checked){
		if(checked){
			nowX = bg_off.getWidth();
		}else{
			nowX = 0;
		}
		nowStatus = checked;
	}
	public boolean getChecked()
	{
		return nowStatus;
	}


	/**
	 * 
	 * @author len
	 *
	 */
	public interface OnChangedListener {
		public void OnChanged(WiperSwitch wiperSwitch, boolean checkState);
	}


}
