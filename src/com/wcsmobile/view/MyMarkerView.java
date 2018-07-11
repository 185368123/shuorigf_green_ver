
package com.wcsmobile.view;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.wcsmobile.R;


/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

	private TextView tvContent;
	private static final int HIDE_TVCONTENT=0x1001;
	protected static final String TAG = "MyMarkerView";
	// private String formatStr;
	Timer mTimer = new Timer(true); 
	private int mCurrintIndex =0;
	private Handler mHandler = new Handler()
	{
		@Override  
		public void handleMessage(Message msg) {  
			super.handleMessage(msg);  
			int msgId = msg.arg1;  
			switch (msgId) {  
			case HIDE_TVCONTENT:  
				// do some action  
				Log.d(TAG, "handleMessage on");
				MyMarkerView.this.setVisibility(View.GONE);
				break;  
			default:  
				break;  
			}  
		}  
	};
	public MyMarkerView(Context context, int layoutResource) {
		super(context, layoutResource);

		tvContent = (TextView) findViewById(R.id.tvContent);
		// formatStr = "%d"+context.getString(R.string.year)+"%f";
	}

	// callbacks everytime the MarkerView is redrawn, can be used to update the
	// content (user-interface)
	@Override
	public void refreshContent(Entry e, Highlight highlight) {
		mCurrintIndex = e.getXIndex();
		if(mCurrintIndex <= 1)
		{
			((RelativeLayout)findViewById(R.id.layoutContent)).setBackgroundResource(R.drawable.marker);
		}
		else
		{
			((RelativeLayout)findViewById(R.id.layoutContent)).setBackgroundResource(R.drawable.marker1);
		}

		Log.d(TAG, "refreshContent.mCurrintIndex:"+mCurrintIndex);
		if (e instanceof CandleEntry) {

			CandleEntry ce = (CandleEntry) e;

			tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
		} else {
			//	String str=String.format(formatStr,e.getXIndex(), e.getVal());//e.getData();
			// tvContent.setText((String)e.getData()/*str/*""+Utils.formatNumber(e.getVal(), 0, true)*/);
			//tvContent.setText(e.getVal()+"");//barack modify 201608526
			tvContent.setText( new DecimalFormat("###,###,###.#").format(e.getVal() ));//barack modify 201608526
			/*if(mTimer == null)
				mTimer = new Timer();
			mTimer.schedule(new MyTimerTask(), 1000); */ 
		//	e.getXIndex();
		//	highlight.getXIndex();
			
		}
	}

	@Override
	public int getXOffset() {
		// this will center the marker-view horizontally
		Log.d(TAG, "getXOffset");
		if(mCurrintIndex <=1)
			return -5;// -(getWidth() / 2);
		else
		{
			return  -(getWidth()-10);
		}
	}

	@Override
	public int getYOffset() {
		// this will cause the marker-view to be above the selected value
		Log.d(TAG, "getYOffset");
		return -getHeight()-10;
	}


	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message message = new Message();  
			message.arg1 = HIDE_TVCONTENT;  
			Log.d(TAG, "mTask on");
			mHandler.sendMessage(message);  
			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}

	}
}
