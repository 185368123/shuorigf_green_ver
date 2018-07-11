package com.wcsmobile.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.mikephil.charting.data.Entry;
import com.wcsmobile.R;
import com.wcsmobile.adapter.MyFragmentPagerAdapter;
import com.wcsmobile.ble.BleDeviceInfo;
import com.wcsmobile.ble.BluetoothLeService;
import com.wcsmobile.ble.GattAttributes;
import com.wcsmobile.data.AllChartsStruct;
import com.wcsmobile.data.GlobalStaticData;
import com.wcsmobile.data.GlobalStaticFun;
import com.wcsmobile.data.ModbusData;
import com.wcsmobile.data.ShourigfData;
import com.wcsmobile.dialog.SetDateDialog;
import com.wcsmobile.dialog.SetDateDialog.ClickListenerInterface;
import com.wcsmobile.fragment.HistoricalChartFragment;
import com.wcsmobile.widget.SegmentedGroup;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class HistoricalDataActivity extends BaseFragmentActivity  {

	protected static final String TAG = "HistoricalDataActivity";
	private static final int REBUILD_DONE = 0x1000;
	private HistoricalChartFragment ChartFragment[];
	private AllChartsStruct   mChartDataArray[];
	public static final int CHART_VOLTAGE=0x3;
	public static final int CHART_VPOWER=0x2;
	public static final int CHART_APM_HOUR=0x1;
	public static final int CHART_ELECTRICITY=0x0;
	private BluetoothLeService mBluetoothLeService;
	private BluetoothGattCharacteristic mUartRecvCharacteristic;
	private BluetoothGattCharacteristic mUartSendCharacteristic;
	private int mRunDays = 0;
	private ShourigfData.HistoricalData mHistoricalData;
	private ViewPager mPager;  
	private int mCurrIndex;
	private ArrayList<Fragment> fragmentList;  
	protected byte [] mUartRecvData;
	protected int mReadingRegId=0;
	protected int mReadingCount=1;
	protected int mDeviceId = 0x1;
	private int curYear=2015,curMonth=5;
	private Date mCurDate;
	private boolean disMonth = true;
	private SegmentedGroup mRadioGroup=null;
	private TextView tv_historicalchart_title;
	private TextView tv_historicalchart_date;
	private String tagStringArray[]=null;
	private boolean mStrarting =false;
	private ArrayList<Integer> mDays;
	private int mThreadId=0;
	private ArrayList<ShourigfData.HistoricalChartData>mData;
	private boolean mIsActive=false;
	private boolean mRebuilding = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_historical_data);
		Calendar calendar=Calendar.getInstance();
		curYear=calendar.get(Calendar.YEAR);
		curMonth=calendar.get(Calendar.MONTH)+1;
		String strDate = String.format("%04d-%02d-%02d", 
				curYear,curMonth,1);				
		mCurDate = SetDateDialog.stringToDate(strDate);

		ChartFragment = new HistoricalChartFragment [4];
		mChartDataArray = new AllChartsStruct[4];
		Intent data = this.getIntent();
		mCurrIndex = data.getIntExtra("defalutView", 0);
		mRunDays  = data.getIntExtra("runDays", 0);
		if(mRunDays==0)
			mRunDays =1;
		mHistoricalData = (ShourigfData.HistoricalData)data.getSerializableExtra("HistoricalData");
		mRadioGroup = (SegmentedGroup)findViewById(R.id.segment_Group);
		mRadioGroup.setOnCheckedChangeListener(CheckedChangeListener);
		tv_historicalchart_title = (TextView)findViewById(R.id.tv_historicalchart_title);
		tv_historicalchart_date = (TextView)findViewById(R.id.tv_historicalchart_date);
		tv_historicalchart_date.setText("2015"+getString(R.string.year));
		tagStringArray = new String[]{getString(R.string.more_high_voltage),getString(R.string.more_low_voltage),
				getString(R.string.discharge_max_power),getString(R.string.charge_max_power),
				getString(R.string.charge_apm_hour),getString(R.string.discharge_apm_hour),
				getString(R.string.consumption_power),getString(R.string.production_power)};
		InitViewPager(mCurrIndex);
		((ImageButton)findViewById(R.id.btnExit)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HistoricalDataActivity.this.finish();
			}

		});
		( (ImageButton)findViewById(R.id.btn_set_date)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(	((ImageButton)findViewById(R.id.btn_set_date)).getVisibility() == View.INVISIBLE)
				{
					return;
				}
				SetDate();
			}

		});
		findViewById(R.id.rdbtn_all).performClick();

		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		Log.i(TAG, "bindService");
	}
	public void InitViewPager(int defaultview){  
		mPager = (ViewPager)findViewById(R.id.content);  
		fragmentList = new ArrayList<Fragment>();  
		for(int i=0;i<ChartFragment.length;i++)
		{
			ChartFragment[i] = new HistoricalChartFragment(i,mHistoricalData);
			fragmentList.add(ChartFragment[i]);  
		}
		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));  
		mPager.setCurrentItem(defaultview);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener()); 
	}  
	private OnCheckedChangeListener CheckedChangeListener = new OnCheckedChangeListener()
	{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			Log.d(TAG,"checkedId:"+checkedId);
			switch(checkedId)
			{
			case R.id.rdbtn_month:
				tv_historicalchart_title.setText(R.string.base_month_chart_summary);
				( (ImageButton)findViewById(R.id.btn_set_date)).setVisibility(View.VISIBLE);
				tv_historicalchart_date.setVisibility(View.VISIBLE);
				tv_historicalchart_date.setText(curYear+getString(R.string.year)+curMonth+getString(R.string.month));
				disMonth = true;
				break;
			case R.id.rdbtn_all:
				tv_historicalchart_title.setText(R.string.all_chart_summary);
				( (ImageButton)findViewById(R.id.btn_set_date)).setVisibility(View.INVISIBLE);
				tv_historicalchart_date.setVisibility(View.INVISIBLE);

				break;
			case R.id.rdbtn_year:
				tv_historicalchart_title.setText(R.string.base_year_chart_summary);
				( (ImageButton)findViewById(R.id.btn_set_date)).setVisibility(View.VISIBLE);
				tv_historicalchart_date.setVisibility(View.VISIBLE);
				disMonth = false;
				tv_historicalchart_date.setText(curYear+getString(R.string.year));

				break;

			default:
				break;
			}
			//RebuildChartData();//add 0626
			startRefreshChart();

		}

	};
	public class MyOnPageChangeListener implements OnPageChangeListener{  
		private int one = 20;

		@Override  
		public void onPageScrolled(int arg0, float arg1, int arg2) {  
			// TODO Auto-generated method stub  

		}  

		@Override  
		public void onPageScrollStateChanged(int arg0) {  
			// TODO Auto-generated method stub  

		}  

		@Override  
		public void onPageSelected(int arg0) {  
			// TODO Auto-generated method stub  
			Animation animation = new TranslateAnimation(mCurrIndex*one,arg0*one,0,0);
			mCurrIndex = arg0;  
			animation.setFillAfter(true);
			animation.setDuration(200);
		}  
	}  
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
		mIsActive = false;

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		registerReceiver(mGattUpdateReceiver, intentFilter);
		mIsActive = true;
	}
	private void GetGattServices(
			List<BluetoothGattService> supportedGattServices) {
		// TODO Auto-generated method stub
		if (supportedGattServices == null) return;
		for (BluetoothGattService gattService : supportedGattServices) {
			//uuid = supportedGattServices.getUuid().toString();


			List<BluetoothGattCharacteristic> gattCharacteristics =
					gattService.getCharacteristics();

			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				if(gattCharacteristic.getUuid().toString().equals(GattAttributes.DEVICE_WRITE_DATA_DEVICE))
				{
					mUartSendCharacteristic = gattCharacteristic;
				}
				else if(gattCharacteristic.getUuid().toString().equals(GattAttributes.DEVICE_READ_DATA_DEVICE))
				{
					mUartRecvCharacteristic = gattCharacteristic;
					mBluetoothLeService.setCharacteristicNotification(
							mUartRecvCharacteristic, true);

				}
			}
		}

	}
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Log.i(TAG, "ACTION:" + action);
			if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the user interface.
				GetGattServices(mBluetoothLeService.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				if(mUartSendCharacteristic == null || mUartRecvCharacteristic == null)
				{
					GetGattServices(mBluetoothLeService.getSupportedGattServices());
				}
				RecvData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));

			}
		}



		private void RecvData(byte[] bs) {
			// TODO Auto-generated method stub
			if(bs == null)
				return;

			byte[] temp_data = null;

			if(mUartRecvData == null)
			{
				temp_data = bs;
			}
			else 
			{
				temp_data = new byte[bs.length + mUartRecvData.length];
				System.arraycopy(mUartRecvData,0,temp_data,0,mUartRecvData.length);
				System.arraycopy(bs,0,temp_data,mUartRecvData.length,bs.length);
			}
			mUartRecvData = temp_data;
			String msg="recv data";
			for(int i=0;i<bs.length;i++)
			{
				msg+=String.format("[%02x] ", bs[i]);
			}

			//	SendUartString(msg+"\r\n");//test
			Log.d(TAG, msg);
		}
	};
	private void SendUartData(byte [] bs)
	{
		if(mUartSendCharacteristic != null && mBluetoothLeService!=null)
		{
			mUartSendCharacteristic.setValue(bs);
			mBluetoothLeService.writeCharacteristic(mUartSendCharacteristic);
		}
	}
	private void SendUartString(String str)
	{
		if(mUartSendCharacteristic != null && mBluetoothLeService!=null)
		{
			mUartSendCharacteristic.setValue(str);
			mBluetoothLeService.writeCharacteristic(mUartSendCharacteristic);
		}
	}
	
	private void ConnectDevice(BleDeviceInfo info)
	{
		if(info != null && mBluetoothLeService != null)
		{
			Log.d(TAG, "mBluetoothLeService.connect to "+info.GetDeviceName()+",mac:"+info.GetMacAddr());
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
			mBluetoothLeService.connect(info.GetMacAddr());
		}
	}
	
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			Log.i(TAG, "onServiceConnected");
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
		//	ConnectDevice(GlobalStaticData.getInstance().getCurrentConnectDevInfo());//add  20180621
			GetGattServices(mBluetoothLeService.getSupportedGattServices());

			startRefreshChart();
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};
	private void SetDate() {
		// TODO Auto-generated method stub
		SetDateDialog dialog = new SetDateDialog(this,disMonth,curYear,curMonth);
		dialog.setClicklistener(new ClickListenerInterface(){

			@Override
			public void doConfirm(Date date) {
				// TODO Auto-generated method stub
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);

				curYear = calendar.get(Calendar.YEAR);
				curMonth = calendar.get(Calendar.MONTH)+1;
				Log.d(TAG,"curYear:"+ curYear +",curMonth:"+ curMonth);
				mCurDate = date;
				//RebuildChartData(); //add 0626
				switch(mRadioGroup.getCheckedRadioButtonId())
				{
				case R.id.rdbtn_month:
					tv_historicalchart_date.setText(curYear+getString(R.string.year)+curMonth+getString(R.string.month));
					break;
				case R.id.rdbtn_all:
					break;
				case R.id.rdbtn_year:
					tv_historicalchart_date.setText(curYear+getString(R.string.year));
					break;
					/*		case R.id.rdbtn_day:

					break;*/
				default:
					break;
				}
				
				
				startRefreshChart();
			}

			@Override
			public void doCancel() {
				// TODO Auto-generated method stub

			}

		});
		dialog.show();
	}
	protected void RebuildChartData() {
		// TODO Auto-generated method stub
		mRebuilding =true;
		switch(mRadioGroup.getCheckedRadioButtonId())
		{
		case R.id.rdbtn_month:
			BuildMonthData();
			tv_historicalchart_date.setText(curYear+getString(R.string.year)+curMonth+getString(R.string.month));
			break;
		case R.id.rdbtn_all:

			BuildAllData();
			break;
		case R.id.rdbtn_year:
			BuildYearData();
			tv_historicalchart_date.setText(curYear+getString(R.string.year));

			break;
			/*		case R.id.rdbtn_day:

			break;*/
		default:
			break;
		}
		mRebuilding =false;
	}
	private float GetValue(int range)
	{
		float mult = (range + 15);
		float val = (float) (Math.random() * mult) + 3;// + (float)
		return val;
	}


	private void BuildAllData() {
		// TODO Auto-generated method stub
		AllChartsStruct data[] = new AllChartsStruct[4];
		String curXUintStr;
		String curUpTagString;
		String curDownTagString;

		curXUintStr=getString(R.string.year);

		int startYear = curYear-mDays.size()+1;
		for(int x= 0;x<data.length;x++)
		{
			ArrayList<String> xVals = new ArrayList<String> ();
			ArrayList<Entry> yUpVals = new ArrayList<Entry>();
			ArrayList<Entry> yDownVals = new ArrayList<Entry>();

			xVals.clear();
			yUpVals.clear();
			yDownVals.clear();
			for(int i=startYear,j=0;i<(curYear+1) && (i-startYear) < mData.size()  ;i++,j++)
			{
				xVals.add(i+ "");
				//		yUpVals.add(new Entry(GetValue(x), j,i+getString(R.string.year)));
				//		yDownVals.add(new Entry(GetValue(x), j,i+getString(R.string.year)));
				ShourigfData.HistoricalChartData valdata = 	mData.get(i-startYear);//mDate cleared!...barck20160228 bugs
				float upval = 0;
				float down =0;

				if(x == CHART_VOLTAGE)
				{
					down = valdata.mDayBatteryMaxVoltage;
					upval = valdata.mDayBatteryMinVoltage;
				}
				else if(x == CHART_VPOWER)
				{
					down = valdata.mDayDischargeMaxPower;
					upval = valdata.mDayChargeMaxPower;
				}
				else if(x == CHART_APM_HOUR)
				{
					down = valdata.mDayDischargeAmpHour; 
					upval = valdata.mDayChargeAmpHour;
				}
				else if(x == CHART_ELECTRICITY)
				{
					down = valdata.mDayConsumptionPower/1000.f;
					upval = valdata.mDayProductionPower/1000.f;
				}
				Log.d(TAG, "down:"+down+",upval"+upval);

				yUpVals.add(new Entry(upval, j,i+getString(R.string.year)));
				yDownVals.add(new Entry(down, j,i+getString(R.string.year)));
			}
			curUpTagString =  "" + tagStringArray[x*2+1];
			curDownTagString =  "" +  tagStringArray[x+2+0];
			data[x] = new AllChartsStruct( xVals, yUpVals, yDownVals, curUpTagString,curDownTagString,curXUintStr);
		}
		mChartDataArray = data;
		UpdateChart();

	}
	private void BuildYearData() {
		// TODO Auto-generated method stub
		AllChartsStruct data[] = new AllChartsStruct[4];
		String curXUintStr;

		String curUpTagString;
		String curDownTagString;

		curXUintStr=getString(R.string.month);

		for(int x= 0;x<data.length;x++)
		{
			ArrayList<String> xVals = new ArrayList<String> ();
			ArrayList<Entry> yUpVals = new ArrayList<Entry>();
			ArrayList<Entry> yDownVals = new ArrayList<Entry>();
			xVals.clear();
			yUpVals.clear();
			yDownVals.clear();
			for(int i=1,j=0;i<mData.size();i++,j++)
			{
				xVals.add(i+ getString(R.string.month));
				ShourigfData.HistoricalChartData valdata = 	mData.get(i-1);
				float upval = 0;
				float down =0;
				if(x == CHART_VOLTAGE)
				{
					down = valdata.mDayBatteryMaxVoltage;
					upval = valdata.mDayBatteryMinVoltage;
				}
				else if(x == CHART_VPOWER)
				{
					down = valdata.mDayDischargeMaxPower;
					upval = valdata.mDayChargeMaxPower;
				}
				else if(x == CHART_APM_HOUR)
				{
					down = valdata.mDayDischargeAmpHour;
					upval = valdata.mDayChargeAmpHour;
				}
				else if(x == CHART_ELECTRICITY)
				{
					down = valdata.mDayConsumptionPower/1000.f;
					upval = valdata.mDayProductionPower/1000.f;
				}
				yUpVals.add(new Entry(upval, j,i+getString(R.string.month)));
				yDownVals.add(new Entry(down, j,i+getString(R.string.month)));
				Log.d(TAG, "down:"+down+",upval"+upval);

			}
			curUpTagString = curYear+getString(R.string.year) + tagStringArray[x*2+1];
			curDownTagString = curYear+getString(R.string.year)+ tagStringArray[x+2+0];
			data[x] = new AllChartsStruct( xVals, yUpVals, yDownVals, curUpTagString,curDownTagString,curXUintStr);
		}
		mChartDataArray = data;
		UpdateChart();
	}
	private void UpdateChart()
	{
		if(mChartDataArray == null)
			return;
		for(int i = 0 ;i< mChartDataArray.length ;i++)
		{
			if(mChartDataArray[i] == null)
				return;
			if(ChartFragment[i] == null)
				continue;
			//Log.d(TAG, "chartFragment:"+i);
			ChartFragment[i].UpDateAllCharts(mChartDataArray[i] );
		}
	}

	private void BuildMonthData() {
		// TODO Auto-generated method stub
		int cur_month_total_days = SetDateDialog.getDay(curYear, curMonth);
		int cur_month= curMonth;
		AllChartsStruct data[] = new AllChartsStruct[4];
		String curXUintStr;
		String curUpTagString;
		String curDownTagString;

		curXUintStr=getString(R.string.day);



		for(int x = 0;x<data.length;x++)
		{
			ArrayList<String> xVals = new ArrayList<String> ();
			ArrayList<Entry> yUpVals = new ArrayList<Entry>();
			ArrayList<Entry> yDownVals = new ArrayList<Entry>();

			xVals.clear();
			yUpVals.clear();
			yDownVals.clear();
			for(int i=1,j=0;i</*cur_month_total_days*/mData.size();i++,j++)
			{
				xVals.add(i+ "");
				ShourigfData.HistoricalChartData valdata = 	mData.get(i-1);
				float upval = 0;
				float down =0;
				if(x == CHART_VOLTAGE)
				{
					down = valdata.mDayBatteryMaxVoltage;
					upval = valdata.mDayBatteryMinVoltage;
				}
				else if(x == CHART_VPOWER)
				{
					down = valdata.mDayDischargeMaxPower;
					upval = valdata.mDayChargeMaxPower;
				}
				else if(x == CHART_APM_HOUR)
				{
					down = valdata.mDayDischargeAmpHour;
					upval = valdata.mDayChargeAmpHour;
				}
				else if(x == CHART_ELECTRICITY)
				{
					down = valdata.mDayConsumptionPower/1000.f;
					upval = valdata.mDayProductionPower/1000.f;
				}
				yUpVals.add(new Entry(upval, j,
						/*curYear+getString(R.string.year)+curMonth+getString(R.string.month)*/i+getString(R.string.day)));
				yDownVals.add(new Entry(down, j,
						/*curYear+getString(R.string.year)+curMonth+getString(R.string.month)*/i+getString(R.string.day)));

			}

			curUpTagString = curYear+getString(R.string.year) + cur_month + getString(R.string.month)+ tagStringArray[x*2+1];
			curDownTagString =curYear+getString(R.string.year) + cur_month + getString(R.string.month)+ tagStringArray[x*2+0];
			data[x] = new AllChartsStruct( xVals, yUpVals, yDownVals, curUpTagString,curDownTagString,curXUintStr);
		}
		mChartDataArray = data;

		UpdateChart();

	}
	protected void ClearRecvFifo()
	{
		mUartRecvData = null;
	}
	protected void Sleep(int ms)
	{
		try{
			Thread.sleep(ms);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	protected boolean WaitPostMessage(int timerout )
	{
		timerout = timerout/10;
		while(!PostRecvMessage() && timerout-- !=0)
		{
			Sleep(10);
		}
		if(timerout <= 0)
		{
			return false;
		}
		return true;

	}
	protected boolean PostRecvMessage()
	{
		if(mUartRecvData == null )
			return false;
		byte[] bs = mUartRecvData;

		if(!ModbusData.DataCrcCorrect(bs))
		{
			return false;
		}
		Log.d(TAG, "PostRecvMessage");

		/*String msg="recv data";
		for(int i=0;i<bs.length;i++)
		{
			msg+=String.format("[%02x] ", bs[i]);
		}
		Log.d(TAG, msg);*/

		Message message = new Message();
		message.arg1 = mReadingRegId;
		message.obj = bs;
		mMessageHandler.sendMessage(message);
		mReadingRegId = 0;

		return true;

	}
	private Handler mMessageHandler = new Handler()
	{
		public void handleMessage(Message msg){ 
			byte[] bs=(byte[])msg.obj;
			Log.d(TAG, "arg1="+msg.arg1 +","+ShourigfData.HistoricalChartData.REG_ADDR);
			if(msg.arg1 >=  ShourigfData.HistoricalChartData.REG_ADDR)
			{
				UpdateHistoricalChartData(bs);
			}
		}
	};

	private Handler mRefreshThreadHandler = new Handler()
	{
		public void handleMessage(Message msg){ 

			if(msg.arg1== REBUILD_DONE)
			{
				mRebuilding =true;
				//UpDateAllCharts();
				Log.d(TAG, "mDays.size:"+mDays.size()+"mData.size():"+mData.size());

				//if(mDays.size() == mData.size() && mDays.size()>0)
				RebuildChartData();
				mRebuilding =false;
			}
		}
	};
	private void buildDays()
	{
		mDays=new ArrayList<Integer>();
		mDays.clear();
		int i =0;
		switch(mRadioGroup.getCheckedRadioButtonId())
		{
		case R.id.rdbtn_month:
			for(i=0;i<GlobalStaticFun.getDaysByYearMonth(curYear, curMonth);i++)
			{
				int diff =  GlobalStaticFun.getCurDayDifft(mCurDate)-i;
				Log.d(TAG, "diff:"+diff);
				if(diff>=0)
					mDays.add(diff);
			}
			break;
		case R.id.rdbtn_all:
			for(i=0;i<mRunDays;i+=365)
			{
				mDays.add(0,i);
			}
			break;
		case R.id.rdbtn_year:
			for(i=0;i<12;i++)
			{
				int diff =  GlobalStaticFun.getCurDayDifft(curYear,i+1,1);
				Log.d(TAG, "diff:"+diff);
				if( diff>= 0)
					mDays.add(0,diff);
			}
			break;

		default:
			break;
		}
		Log.d(TAG,"mDays.size:"+mDays.size()+",mRunDays:"+mRunDays);
	}

	private void startRefreshChart()
	{
		new Thread(new RefreshThread()).start();  
	}
	private class RefreshThread implements Runnable{  


		public void run() {  
			Log.d(TAG, "enter thread ");
			boolean success = true;
			mThreadId++;
			int myId = mThreadId;
			while(mStrarting || mRebuilding);
			success = true;
			mStrarting = true;

			do
			{
				success = true;
				buildDays();
				/*if(mStrarting)
			{
				mStrarting = false;
				return;
			}*/
				mData = new  ArrayList<ShourigfData.HistoricalChartData>();
				mData.clear();
				Sleep(1000);
				if(mDays != null)
				{
					for(int i=0;i<mDays.size();i++)
					{
						//Log.d(TAG, "-----------i:"+i+",days:"+mDays.get(i));
						ClearRecvFifo();
						mReadingRegId = ShourigfData.HistoricalChartData.REG_ADDR+(int)mDays.get(i);
						mReadingCount = ShourigfData.HistoricalChartData.READ_WORD;
						SendUartData(ModbusData.BuildReadRegsCmd(mDeviceId, mReadingRegId, mReadingCount));
						Sleep(300);
						if(WaitPostMessage(2000) == false)
						{
							/*Message message = new Message();
						message.arg1 = REBUILD_DONE;
						mRefreshThreadHandler.sendMessage(message);*/
							//Log.d(TAG, "timerout exit thread");
							//mStrarting = false;
							//return;
							success = false;
							break;
						}
						//Sleep(1000);
						if(mThreadId != myId)
						{

							Log.d(TAG, "unlock:"+myId+",mThreadId:"+mThreadId);
							mStrarting = false;
							return ;
						}
					}
				}
				if(mThreadId != myId)
				{

					Log.d(TAG, "unlock:"+myId+",mThreadId:"+mThreadId);
					mStrarting = false;
					return ;
				}

				if(success)
				{
					Sleep(1000);
					if(mDays.size() == mData.size() )
					{
						Message message = new Message();
						message.arg1 = REBUILD_DONE;
						mRefreshThreadHandler.sendMessage(message);
						mRebuilding = true;
						while(mRebuilding);
						Log.d(TAG, "exit thread");
						mStrarting = false;
					}
					else
					{//need test
						success = false;
					}
				}

				Log.d(TAG, "unlock:"+myId);
			}while(!success && mIsActive);
		}
	}
	private void UpdateHistoricalChartData(byte[] bs) {
		// TODO Auto-generated method stub
		ShourigfData.HistoricalChartData data = new ShourigfData.HistoricalChartData(bs);
		mData.add(data);
		//Log.d(TAG,"addData+++++++++++++++++++++++++size:"+mData.size());
	}

}
