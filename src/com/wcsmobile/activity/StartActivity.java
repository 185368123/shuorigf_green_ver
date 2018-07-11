package com.wcsmobile.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;



import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.wcsmobile.R;
import com.wcsmobile.data.BitmapRenderscript;
import com.wcsmobile.data.CMDExecute;
import com.wcsmobile.data.DisplayUtil;
import com.wcsmobile.data.ModbusData;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class StartActivity extends BaseActivity implements OnSeekBarChangeListener,
OnChartGestureListener, OnChartValueSelectedListener{
	public final String tag="StartActivity";
	private LineChart mChart;
	public  String startLogcat(){
		boolean logcatRuning = false;
		String result = "";
		CMDExecute cmdexe = new CMDExecute();
		String path = Environment.getExternalStorageDirectory().getPath()+"/WCS.log";
		File f= new File(path);  
		if (f.exists() && f.isFile()){  
			if(f.length()>2*1024*1024)
			{
				f.delete();
			}
		}
		File find= new File(path);  
		long len = -1,newlen=-1;
		if (find.exists() && find.isFile()){  
			len =find.length();
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		Log.i(tag,"Start Log==============================="+df.format(new Date()));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (find.exists() && find.isFile()){  
			newlen = find.length();
		}
		if(newlen > len)
		{
			logcatRuning = true;
		}
		try{
			String[] args = {"logcat",/*"-t","200000",*/"-f",path};
			//Process process = Runtime.getRuntime().exec(args);
			if(logcatRuning)
			{
				Log.i(tag,"Logcat early runing");
			}else{
				Log.i(tag,"Logcat enter");
				result = cmdexe.run(args,"/system/bin");
				Log.i(tag,"Logcat exit");
			}
		}catch (IOException ex){
			ex.printStackTrace();
		}

		return result;
	}
	private class GetLogThread implements Runnable{  

		public void run() { 
			startLogcat();
		}
	}
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.arg1) {
            case 0:
            //TODO
        		Intent intent = new Intent();
        		intent.setClass(StartActivity.this, MainActivity.class);
        		StartActivity.this.startActivity(intent);
        		StartActivity.this.finish();
            case 1:
            //TODO
            }

        }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		setContentView(R.layout.activity_start);
		new Thread(new GetLogThread()).start(); 
		//new Thread(new LogThread()).start();
		int size=DisplayUtil.px2sp(StartActivity.this, (int)this.getResources().getDimension(R.dimen.btnTextSize));
		Configuration config = getResources().getConfiguration();

		/*	DisplayMetrics dm =  getResources().getDisplayMetrics();
		config.locale = Locale.ENGLISH; 
		 getResources().updateConfiguration(config, dm); 
		 */
		int  smallestScreenWidth = config.smallestScreenWidthDp;
		Log.d(tag, "size:"+size +",smallestScreenWidth:"+smallestScreenWidth+","+config.screenWidthDp);
		//byte [] bytes = {0x01, 0x06,  (byte)0xE0, 0x1D, 0x00,0x08};

		//Log.d(tag, "CRC:"+String.format("0x%04x", ChecksumCRC.calcCrc16(bytes)));
		byte [] bytes = null;
		//bytes = ModbusData.BuildReadRegsCmd(0x01, 0x000A, 0x0001);
		//bytes = ModbusData.BuildWriteRegCmd(0x01, 0xE001, 0x0064);//01 06 E001 0064 EE21
		/*int [] data = {0x00AA , 0x009B , 0x0092 , 0x0090 , 0x008A , 0x0084 , 0x007E , 0x0078
						, 0x006E , 0x0069 , 0x6432 , 0x0005 , 0x003C , 0x003C , 0x001E , 0x0005};//new int [0x10];
		bytes = ModbusData.BuildWriteRegsCmd(0x01, 0xE005, 0x10, data);
		/*����01 10 E005 0010 
		00AA 009B 0092 0090 008A 0084 007E 0078 006E 0069 6432 0005 003C 003C 001E 0005 
		C140 */
		//bytes = ModbusData.BuildClearHistoryCmd(0x01);
		bytes = ModbusData.BuildRestoreFactoryCmd(0x01);
		/*for(int i=0;i<bytes.length;i++)
		{
			Log.d(tag, String.format("bytes[%d]=0x%02x", i,bytes[i]));
		}*/
		byte [] bs={0x01, 0x03, 0x02, 0x00,0x7B ,(byte)0xF8,0x67};
		//	Log.d(tag,"crc:"+ModbusData.DataCrcCorrect( bs));
		byte [] bad_bs={0x01, 0x03, 0x02, 0x00,0x7B ,(byte)0xF8,0x68};
		//	Log.d(tag,"bad crc:"+ModbusData.DataCrcCorrect( bad_bs));		
		new Thread(new Runnable(){   

            public void run(){   
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.arg1 = 0;
                handler.sendMessage(msg); 
            }   

        }).start();

		//LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
		//linearLayout.getBackground().setAlpha(0);
		//	 Bitmap bitmap = BitmapFactory.decodeResource(getResources(),     
		//              R.drawable.test); 
		//linearLayout.setBackground(BitmapRenderscript.OpacityCcaleZoom(bitmap));
		// linearLayout.setBackground(BitmapRenderscript.Blur(bitmap,StartActivity.this));
		// linearLayout.getBackground().setAlpha(50);


		mChart = (LineChart) findViewById(R.id.chart1);
		mChart.setOnChartGestureListener(this);
		mChart.setOnChartValueSelectedListener(this);
		mChart.setDrawGridBackground(false);
		mChart.setDescription("");
		mChart.setNoDataTextDescription("You need to provide data for the chart.");
		// enable value highlighting
		mChart.setHighlightEnabled(true);

		// enable touch gestures
		mChart.setTouchEnabled(true);

		// enable scaling and dragging
		mChart.setDragEnabled(true);
		mChart.setScaleEnabled(true);
		// mChart.setScaleXEnabled(true);
		// mChart.setScaleYEnabled(true);

		// if disabled, scaling can be done on x- and y-axis separately
		mChart.setPinchZoom(true);



		LimitLine ll1 = new LimitLine(180f, "15ah");
		ll1.setLineWidth(1f);
		ll1.setLineColor(Color.BLACK);
		ll1.enableDashedLine(10f, 0f, 0f);
		ll1.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
		ll1.setTextSize(15.0f);
		ll1.setTextColor(Color.BLACK);

		LimitLine ll2 = new LimitLine(0f, "5.0ah");
		ll2.setLineWidth(1f);
		ll2.setLineColor(Color.BLACK);
		ll2.enableDashedLine(10f, 0f, 0f);
		ll2.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
		ll2.setTextSize(15.0f);
		ll2.setTextColor(Color.BLACK);       


		YAxis leftAxis = mChart.getAxisLeft();
		leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
		leftAxis.addLimitLine(ll1);
		leftAxis.addLimitLine(ll2);
		leftAxis.setAxisMaxValue(220f);
		leftAxis.setAxisMinValue(-50f);
		leftAxis.setStartAtZero(false);
		//leftAxis.setYOffset(20f);
		leftAxis.enableGridDashedLine(10f, 10f, 0f);

		// limit lines are drawn behind data (and not on top)
		leftAxis.setDrawLimitLinesBehindData(true);
		XAxis xAxis = mChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		// mChart.getXAxis().setEnabled(false);
		leftAxis.disableGridDashedLine();
		leftAxis.setDrawAxisLine(false);
		leftAxis.setDrawLabels(false);
		//  leftAxis.setDrawGridLines(false);
		xAxis.setDrawGridLines(false);

		mChart.getAxisRight().setEnabled(false);
		// add data
		setData(45, 100);



		mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
		//      mChart.invalidate();

		// get the legend (only possible after setting data)
		Legend l = mChart.getLegend();

		// modify the legend ...
		// l.setPosition(LegendPosition.LEFT_OF_CHART);
		l.setForm(LegendForm.LINE);
	}
	private void setData(int count, float range) {

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			xVals.add((i) + "");
		}

		ArrayList<Entry> yVals = new ArrayList<Entry>();

		for (int i = 0; i < count; i++) {

			float mult = (range + 1);
			float val = (float) (Math.random() * mult) + 3;// + (float)
			// ((mult *
			// 0.1) / 10);
			yVals.add(new Entry(val, i));
		}

		// create a dataset and give it a type
		LineDataSet set1 = new LineDataSet(yVals, "8");
		// set1.setFillAlpha(110);
		// set1.setFillColor(Color.RED);

		// set the line to be drawn like this "- - - - - -"
		set1.enableDashedLine(10f, 0f, 0f);

		set1.setColor(Color.RED);
		set1.setCircleColor(Color.GRAY);
		set1.setLineWidth(1f);
		set1.setCircleSize(3f);

		set1.setDrawCircleHole(true);
		set1.setValueTextSize(9f);
		set1.setFillAlpha(65);
		set1.setFillColor(Color.BLACK);
		//	        set1.setDrawFilled(true);
		// set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
		// Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		dataSets.add(set1); // add the datasets

		// create a data object with the datasets
		LineData data = new LineData(xVals, dataSets);

		// set data
		mChart.setData(data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);


		return true;
	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub
		Log.d(tag, "onNothingSelected:");	
	}

	@Override
	public void onValueSelected(Entry arg0, int arg1, Highlight arg2) {
		// TODO Auto-generated method stub
		Log.d(tag, "onValueSelected:"+arg0.toString());	
	}

	@Override
	public void onChartDoubleTapped(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.d(tag, "onChartDoubleTapped:");	
	}

	@Override
	public void onChartFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		Log.d(tag, "onChartFling:");	
	}

	@Override
	public void onChartLongPressed(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.d(tag, "onChartLongPressed:");	
	}

	@Override
	public void onChartScale(MotionEvent arg0, float arg1, float arg2) {
		// TODO Auto-generated method stub
		Log.d(tag, "onChartScale:");		
	}

	@Override
	public void onChartSingleTapped(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.d(tag, "onChartSingleTapped:");		

	}

	@Override
	public void onChartTranslate(MotionEvent arg0, float arg1, float arg2) {
		// TODO Auto-generated method stub
		Log.d(tag, "onChartTranslate:"+arg1+":"+arg2);		

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		Log.d(tag, "onProgressChanged");		

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.d(tag, "onStartTrackingTouch");		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.d(tag, "onStopTrackingTouch");
	}

}
