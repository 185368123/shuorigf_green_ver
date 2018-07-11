package com.wcsmobile.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.wcsmobile.R;
import com.wcsmobile.activity.HistoricalDataActivity;
import com.wcsmobile.activity.MainActivity;
import com.wcsmobile.data.GlobalStaticFun;
import com.wcsmobile.data.ModbusData;
import com.wcsmobile.data.ShourigfData;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HistoricalDataFragment extends BaseFragment {
	public String TAG="HistoricalDataFragment";
	private TextView tvDateTime;
	private Date curDateTime;
	private int mRunDays=0;

	private ShourigfData.HistoricalData mData = new ShourigfData.HistoricalData(null); ;
	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd");  
	private TextView tv_day_consumption_power_value;
	private TextView tv_day_production_power_value;
	private TextView tv_day_discharge_amp_hour_value;
	private TextView tv_day_charge_amp_hour_value;
	private TextView tv_day_discharge_max_power_value;
	private TextView tv_day_charge_max_power_value;
	private TextView tv_day_battery_max_voltage_value;
	private TextView tv_day_battery_min_voltage_value;
	private TextView tv_all_run_days_value;
	private TextView tv_battery_all_discharge_times_value;
	private TextView tv_battery_charge_full_times_value;
	private TextView tv_battery_charge_all_apm_hour_value;
	private TextView tv_battery_discharge_all_apm_hour_value;
	private TextView tv_all_production_power_value;
	private TextView tv_all_consumption_power_value;
	private View mView = null;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mFragmentType = HISTORICAL_DATA_FRAGM;
		if(mView != null)
			return mView;
		View view = inflater.inflate(R.layout.fragment_historical_data, container,false);
		InitView(view);
		mView = view;
		return view;//return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		long time=System.currentTimeMillis();
		Date cur_data =new Date(time); 
	    long diff = cur_data.getTime() - curDateTime.getTime();
	    long days = diff / (1000 * 60 * 60 * 24);
	    if(days > 0)
	    {
	    	DoUpdateHistoricalData();
	    }
	    else
	    {
	    	new Thread(new RefreshThread()).start();  
	    }

	}

	private void InitView(View view) {
		// TODO Auto-generated method stub
		tvDateTime = (TextView)view.findViewById(R.id.tvDateTime);
		long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();  
		simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd");  
		curDateTime=new Date(time); 

		tvDateTime.setText(simpleDateFormat.format(curDateTime));

		((ImageButton)view.findViewById(R.id.btnLastDay)).setOnClickListener(clickListener);
		((ImageButton)view.findViewById(R.id.btnNextDay)).setOnClickListener(clickListener);
		((RelativeLayout)view.findViewById(R.id.current_power_layout)).setOnClickListener(clickListener);
		((RelativeLayout)view.findViewById(R.id.current_amp_hour_layout)).setOnClickListener(clickListener);
		((RelativeLayout)view.findViewById(R.id.current_max_power_layout)).setOnClickListener(clickListener);
		((RelativeLayout)view.findViewById(R.id.current_voltage_layout)).setOnClickListener(clickListener);


		tv_day_consumption_power_value = (TextView)view.findViewById(R.id.tv_day_consumption_power_value);
		tv_day_production_power_value = (TextView)view.findViewById(R.id.tv_day_production_power_value);
		tv_day_discharge_amp_hour_value = (TextView)view.findViewById(R.id.tv_day_discharge_amp_hour_value);
		tv_day_charge_amp_hour_value = (TextView)view.findViewById(R.id.tv_day_charge_amp_hour_value);
		tv_day_discharge_max_power_value = (TextView)view.findViewById(R.id.tv_day_discharge_max_power_value);
		tv_day_charge_max_power_value = (TextView)view.findViewById(R.id.tv_day_charge_max_power_value);
		tv_day_battery_max_voltage_value = (TextView)view.findViewById(R.id.tv_day_battery_max_voltage_value);
		tv_day_battery_min_voltage_value = (TextView)view.findViewById(R.id.tv_day_battery_min_voltage_value);
		tv_all_run_days_value = (TextView)view.findViewById(R.id.tv_all_run_days_value);
		tv_battery_all_discharge_times_value = (TextView)view.findViewById(R.id.tv_battery_all_discharge_times_value);
		tv_battery_charge_full_times_value = (TextView)view.findViewById(R.id.tv_battery_charge_full_times_value);
		tv_battery_charge_all_apm_hour_value = (TextView)view.findViewById(R.id.tv_battery_charge_all_apm_hour_value);
		tv_battery_discharge_all_apm_hour_value = (TextView)view.findViewById(R.id.tv_battery_discharge_all_apm_hour_value);
		tv_all_production_power_value = (TextView)view.findViewById(R.id.tv_all_production_power_value);
		tv_all_consumption_power_value = (TextView)view.findViewById(R.id.tv_all_consumption_power_value);
		int tv_group[] = {R.id.tv_day_production_power_value,R.id.tv_day_consumption_power_value,
				R.id.tv_day_charge_amp_hour_value,R.id.tv_day_discharge_amp_hour_value,
				R.id.tv_day_charge_max_power_value,R.id.tv_day_discharge_max_power_value,
				R.id.tv_day_battery_min_voltage_value,R.id.tv_day_battery_max_voltage_value,
				R.id.tv_all_run_days_value,
				R.id.tv_battery_all_discharge_times_value,R.id.tv_battery_charge_full_times_value,
				R.id.tv_battery_charge_all_apm_hour_value,R.id.tv_battery_discharge_all_apm_hour_value,
				R.id.tv_all_production_power_value,R.id.tv_all_consumption_power_value};
		String tv_init[] ={getString(R.string.init_null_charter),getString(R.string.init_null_charter),
				getString(R.string.init_null_charter),getString(R.string.init_null_charter),getString(R.string.init_null_charter)
				,getString(R.string.init_null_charter),getString(R.string.init_null_charter),getString(R.string.init_null_charter),
				getString(R.string.init_null_charter),getString(R.string.init_null_charter),getString(R.string.init_null_charter),
				getString(R.string.init_null_charter),getString(R.string.init_null_charter)/*GlobalStaticFun.buildUnitTransformationToString(0xffffffffl,1000,"Wh","kWh")*/,getString(R.string.init_null_charter),
				getString(R.string.init_null_charter)};
		for(int ti=0;ti< (tv_group.length>tv_init.length?tv_init.length:tv_group.length);ti++)
		{
			((TextView)view.findViewById(tv_group[ti])).setText(tv_init[ti]);
		}
	}
	private OnClickListener clickListener = new OnClickListener()
	{
		@Override
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d(TAG, String.format("onClick id:0x08%x", v.getId()));
			switch(v.getId())
			{
			case R.id.btnLastDay:
				DoLastDay();
				break;
			case R.id.btnNextDay:
				DoNextDay();
				break;
			case R.id.current_power_layout:
				StartHistoricalDataActivity(HistoricalDataActivity.CHART_ELECTRICITY);
				break;
			case R.id.current_amp_hour_layout:
				StartHistoricalDataActivity(HistoricalDataActivity.CHART_APM_HOUR);
				break;
			case R.id.current_max_power_layout:
				StartHistoricalDataActivity(HistoricalDataActivity.CHART_VPOWER);
				break;
			case R.id.current_voltage_layout:
				StartHistoricalDataActivity(HistoricalDataActivity.CHART_VOLTAGE);
				break;
			default:break;
			}
		}

		private void StartHistoricalDataActivity(int i) {
			// TODO Auto-generated method stub
			Intent data = new Intent();
			data.putExtra("defalutView", i);
			if(mData != null)
			{
				data.putExtra("runDays", /*mRunDays*/mData.mAllRunDays);
				data.putExtra("HistoricalData", mData);
			}
			data.setClass(getActivity(), HistoricalDataActivity.class);
			getActivity().startActivity(data);
		}
	};
	
	public void  DoUpdateHistoricalData()
	{

		long time=System.currentTimeMillis();
		Date cur_data =new Date(time); 
	    long diff = cur_data.getTime() - curDateTime.getTime();
	    long days = diff / (1000 * 60 * 60 * 24);
	    Log.d(TAG, "days:"+days);
		ClearRecvFifo();
		mReadingRegId = ShourigfData.HistoricalChartData.REG_ADDR+(int)days;
		mReadingCount = ShourigfData.HistoricalChartData.READ_WORD;
		SendUartData(ModbusData.BuildReadRegsCmd(mDeviceId, mReadingRegId, mReadingCount));
		StartLoopRecvOneMessage(2000);
	}

	private void DoNextDay() {
		// TODO Auto-generated method stub]
		//curDateTime.setDate(curDateTime.getDay()+1);
		Log.d(TAG, "doNextDay:"+simpleDateFormat.format(curDateTime));
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(curDateTime);
		rightNow.add(Calendar.DAY_OF_YEAR,1);
		
		long time=System.currentTimeMillis();
		Date cur_date =new Date(time); 
		Date historDate = rightNow.getTime();
	    long diff = cur_date.getTime() - historDate.getTime();
	    long days = diff / (1000 * 60 * 60 * 24);
	    Log.d(TAG, "days:"+days+",diff:"+diff);
		if(diff < 0)
		{
			return;
		}
		curDateTime=rightNow.getTime();
		tvDateTime.setText(simpleDateFormat.format(curDateTime));
		DoUpdateHistoricalData();
	}

	private void DoLastDay() {
		// TODO Auto-generated method stub
		//curDateTime.setDate(curDateTime.getDay()-1);
		Log.d(TAG, "DoLastDay:"+simpleDateFormat.format(curDateTime));
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(curDateTime);
		rightNow.add(Calendar.DAY_OF_YEAR,-1);
		curDateTime=rightNow.getTime();
		tvDateTime.setText(simpleDateFormat.format(curDateTime));
		DoUpdateHistoricalData();
	}
	@Override
	protected boolean PostRecvMessage()
	{
		if(super.PostRecvMessage() == false)
			return false;
		byte[] bs = mUartRecvData;
		
		Log.d(TAG, "PostRecvMessage");

		String msg="recv data";
		if(bs==null){
			return false;
		}
		for(int i=0;i<bs.length;i++)
		{
			msg+=String.format("[%02x] ", bs[i]);
		}
		Log.d(TAG, msg);
		if(bs.length<3)
			return false;
		int b=(0xff)&bs[2];
		if(mReadingCount*2 != b)
			return false;
	
		Message message = new Message();
		message.arg1 = mReadingRegId;
		message.obj = bs;
		mMessageHandler.sendMessage(message);
		//mReadingRegId = 0;
		
		return true;

	}
	private Handler mMessageHandler = new Handler()
	{
		public void handleMessage(Message msg){ 
			if(!mIsActive)
				return;
			byte[] bs=(byte[])msg.obj;
			switch(msg.arg1)
			{
			case ShourigfData.HistoricalData.REG_ADDR:
				UpdateHistoricalData(bs);
				break;
			default:break;
			}
			if(msg.arg1 >=  ShourigfData.HistoricalChartData.REG_ADDR)
			{
				UpdateHistoricalChartData(bs);
			}
		}


	};
	public void RefreshData()
	{
	
		new Thread(new RefreshThread()).start();  
	}

	private class RefreshThread implements Runnable{  

		public void run() {  
			if(mRefreshThreadRuning)
				return;
			mRefreshThreadRuning = true;
			int retry = 3;
			while(true)
			{
				Sleep(1000);
				ClearRecvFifo();
				mReadingRegId = ShourigfData.HistoricalData.REG_ADDR;
				mReadingCount = ShourigfData.HistoricalData.READ_WORD;
				SendUartData(ModbusData.BuildReadRegsCmd(mDeviceId, mReadingRegId, mReadingCount));
				Sleep(100);
				if(WaitPostMessage(2000) || !mIsActive || retry-- <= 0)
					break;
				//modify 2018 06 03
			/*	if(!mIsActive)
					break;*/
				if(WaitPostMessage(2000) )
					Sleep(1000);
			}
			Log.d(TAG, "exit thread");
			mRefreshThreadRuning = false;

		}
	}



	protected void UpdateHistoricalData(byte[] bs) {
		int tv_group[] = {R.id.tv_day_production_power_value,R.id.tv_day_consumption_power_value,
				R.id.tv_day_charge_amp_hour_value,R.id.tv_day_discharge_amp_hour_value,
				R.id.tv_day_charge_max_power_value,R.id.tv_day_discharge_max_power_value,
				R.id.tv_day_battery_min_voltage_value,R.id.tv_day_battery_max_voltage_value,
				R.id.tv_all_run_days_value,
				R.id.tv_battery_all_discharge_times_value,R.id.tv_battery_charge_full_times_value,
				R.id.tv_battery_charge_all_apm_hour_value,R.id.tv_battery_discharge_all_apm_hour_value,
				R.id.tv_all_production_power_value,R.id.tv_all_consumption_power_value};
		// TODO Auto-generated method stub
		ShourigfData.HistoricalData data = new ShourigfData.HistoricalData(bs);
		mRunDays = data.mAllRunDays;
		mData = data;
		String tv_init[] ={/*data.mDayProductionPower+"W",data.mDayConsumptionPower+"W",*/
				GlobalStaticFun.buildUnitTransformationToString(data.mDayProductionPower,1000,"Wh","kWh"),
				GlobalStaticFun.buildUnitTransformationToString(data.mDayConsumptionPower,1000,"Wh","kWh"),
			/*	data.mDayChargeAmpHour+"ah",data.mDayDischargeAmpHour+"ah",*/
				GlobalStaticFun.buildUnitTransformationToString(data.mDayChargeAmpHour,1000,"ah","kAh"),
				GlobalStaticFun.buildUnitTransformationToString(data.mDayDischargeAmpHour,1000,"ah","kAh"),
				/*data.mDayChargeMaxPower+"W",data.mDayDischargeMaxPower+"W",*/
				GlobalStaticFun.buildUnitTransformationToString(data.mDayChargeMaxPower,1000,"W","KW"),
				GlobalStaticFun.buildUnitTransformationToString(data.mDayDischargeMaxPower,1000,"W","KW"),
				Math.round(data.mDayBatteryMinVoltage*100)/100.f+"V",Math.round(data.mDayBatteryMaxVoltage*100)/100.f+"V",
				data.mAllRunDays+getString(R.string.day),
				data.mBatteryAllDischargeTimes+getString(R.string.times),data.mbatteryChargeFullTimes+getString(R.string.times),
				GlobalStaticFun.buildUnitTransformationToString(data.mBatteryChargeAllApmHour,1000,"ah","kAh"),
				GlobalStaticFun.buildUnitTransformationToString(data.mBatteryDischargeAllApmHour,1000,"ah","kAh"),
				GlobalStaticFun.buildUnitTransformationToString(data.mAllProductionPower,1000,"Wh","kWh"),
				GlobalStaticFun.buildUnitTransformationToString(data.mAllConsumptionPower,1000,"Wh","kWh")/*,
				data.mBatteryChargeAllApmHour+"ah",data.mBatteryDischargeAllApmHour+"ah",
				data.mAllProductionPower+"W",data.mAllConsumptionPower+"W"*/};
		for(int ti=0;ti< (tv_group.length>tv_init.length?tv_init.length:tv_group.length);ti++)
		{
			((TextView)getView().findViewById(tv_group[ti])).setText(tv_init[ti]);
		}
	}
	private void UpdateHistoricalChartData(byte[] bs) {
		// TODO Auto-generated method stub
		int tv_group[] = {R.id.tv_day_production_power_value,R.id.tv_day_consumption_power_value,
				R.id.tv_day_charge_amp_hour_value,R.id.tv_day_discharge_amp_hour_value,
				R.id.tv_day_charge_max_power_value,R.id.tv_day_discharge_max_power_value,
				R.id.tv_day_battery_min_voltage_value,R.id.tv_day_battery_max_voltage_value,
		};
		// TODO Auto-generated method stub
		ShourigfData.HistoricalChartData data = new ShourigfData.HistoricalChartData(bs);
		mData = new ShourigfData.HistoricalData(bs);
		mData.SyncHistoricalChartData(data);
		mData.mAllRunDays = mRunDays;
		String tv_init[] ={/*data.mDayProductionPower/1000+"kWh",data.mDayConsumptionPower/1000+"kWh",
				data.mDayChargeAmpHour+"ah",data.mDayDischargeAmpHour+"ah",
				data.mDayChargeMaxPower+"W",data.mDayDischargeMaxPower+"W",*/
				GlobalStaticFun.buildUnitTransformationToString(data.mDayProductionPower,1000,"Wh","kWh"),
				GlobalStaticFun.buildUnitTransformationToString(data.mDayConsumptionPower,1000,"Wh","kWh"),
			/*	data.mDayChargeAmpHour+"ah",data.mDayDischargeAmpHour+"ah",*/
				GlobalStaticFun.buildUnitTransformationToString(data.mDayChargeAmpHour,1000,"ah","kAh"),
				GlobalStaticFun.buildUnitTransformationToString(data.mDayDischargeAmpHour,1000,"ah","kAh"),
				/*data.mDayChargeMaxPower+"W",data.mDayDischargeMaxPower+"W",*/
				GlobalStaticFun.buildUnitTransformationToString(data.mDayChargeMaxPower,1000,"W","KW"),
				GlobalStaticFun.buildUnitTransformationToString(data.mDayDischargeMaxPower,1000,"W","KW"),
				
				Math.round(data.mDayBatteryMinVoltage*100)/100.f+"V",Math.round(data.mDayBatteryMaxVoltage*100)/100.f+"V",
		};
		for(int ti=0;ti< (tv_group.length>tv_init.length?tv_init.length:tv_group.length);ti++)
		{
			((TextView)getView().findViewById(tv_group[ti])).setText(tv_init[ti]);
		}
	}
}





