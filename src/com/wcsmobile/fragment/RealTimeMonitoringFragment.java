package com.wcsmobile.fragment;



import java.util.Locale;


import com.wcsmobile.R;
import com.wcsmobile.activity.MainActivity;
import com.wcsmobile.data.GlobalStaticFun;
import com.wcsmobile.data.ModbusData;
import com.wcsmobile.data.ParamSettingDataStruct;
import com.wcsmobile.data.ShourigfData;
import com.wcsmobile.widget.switchbutton.SwitchButton;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RealTimeMonitoringFragment extends BaseFragment {
	public String TAG="RealTimeMonitoringFragment";
	private TextView tv_solar_panel_voltage_value;
	private TextView tv_solar_panel_electricity_value;
	private TextView tv_charging_power_value;
	private TextView tv_battery_plate_state_value;
	private TextView tv_battery_esidual_capacity_value;
	private TextView tv_battery_voltage_value;
	private TextView tv_charging_electricity_value;
	private TextView tv_battery_temperature_value;
	private TextView tv_battery_state_value;
	private TextView tv_charging_state_value;
	private TextView tv_load_voltage_value;
	private TextView tv_load_electricity_value;
	private TextView tv_load_power_value;
	private TextView tv_load_state_value;

	private SwitchButton mLoadSwitchStatusSB;
	private View mView = null;
	private boolean mExit=false;
	private int mLoadOptMod = 0;
	private int mDelayTimes = 10;

	@Override
	protected boolean PostRecvMessage()
	{
		if(super.PostRecvMessage() == false)
			return false;
		byte[] bs = mUartRecvData;

		Log.d(TAG, "PostRecvMessage");

		String msg="recv data";
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
		mReadingRegId = 0;

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
			case ShourigfData.SolarPanelInfo.REG_ADDR:
				UpdateSolarPanelInfo(bs);
				break;
			case ShourigfData.BatteryParamInfo.REG_ADDR:
				UpdateBatteryParamInfo(bs);
				break;
			case ShourigfData.SolarPanelAndBatteryState.REG_ADDR:
				UpdateSolarPanelAndBatteryState(bs);
				break;
			case ShourigfData.ParamSettingData.REG_ADDR:
				UpdateParamSettingData(bs);
				break;
			default:break;
			}
		}
	};

	private void UpdateSolarPanelAndBatteryState(byte[] bs)
	{
		ShourigfData.SolarPanelAndBatteryState state = new ShourigfData.SolarPanelAndBatteryState(bs);
		String batter_state_str[] = {getString(R.string.battery_state_val00h),getString(R.string.battery_state_val01h),
				getString(R.string.battery_state_val02h),getString(R.string.battery_state_val03h),
				getString(R.string.battery_state_val04h),getString(R.string.battery_state_val05h)
				,getString(R.string.battery_state_val06h)};
//		tv_battery_state_value.setText(batter_state_str[state.mBatteryState]);
	tv_charging_state_value.setText(/*""+state.mBatteryState*/batter_state_str[state.mBatteryState]);
		String battery_plate_sate_str[] ={
				getString(R.string.controller_info_byte0),getString(R.string.controller_info_byte1),
				getString(R.string.controller_info_byte2),getString(R.string.controller_info_byte3),
				getString(R.string.controller_info_byte4),getString(R.string.controller_info_byte5),
				getString(R.string.controller_info_byte6),getString(R.string.controller_info_byte7),
				getString(R.string.controller_info_byte8),getString(R.string.controller_info_byte9),
				getString(R.string.controller_info_byte10),getString(R.string.controller_info_byte11),
				getString(R.string.controller_info_byte12),getString(R.string.controller_info_byte13),
				getString(R.string.controller_info_byte14),getString(R.string.controller_info_none)				
		};
		String controllerInfoStr = getString(R.string.controller_info_none);
		String batteryStateInfoStr = getString(R.string.controller_info_none);
		String loadState= getString(R.string.opened);

		if(state.mSolarPanelState == 0)
			loadState= getString(R.string.closed);
		
		
		//state.mControllerInfo |= 0x3<<3;//test 
		//Log.d(TAG, "state.mControllerInfo:"+state.mControllerInfo);
		if(state.mControllerInfo != 0)
		{
			for(int i=0;i<battery_plate_sate_str.length;i++)
			{
				if(i>=7 && i<=14)
				{
					if((state.mControllerInfo & (1<<i)) != 0)
					{
						controllerInfoStr = battery_plate_sate_str[i];
						continue;
					}
				}
				else if(i>=0 && i<=2)
				{
					if((state.mControllerInfo & (1<<2)) != 0)
					{
						batteryStateInfoStr = battery_plate_sate_str[2];
					}
					if((state.mControllerInfo & (1<<1)) != 0)
					{
						batteryStateInfoStr = battery_plate_sate_str[1];
					}
					if((state.mControllerInfo & (1<<0)) != 0)
					{
						batteryStateInfoStr = battery_plate_sate_str[0];
					}
					continue;
				}
				else if(i==3 || i==4)
				{
					//Log.d(TAG, "state.mControllerInfo:"+state.mControllerInfo);
					if((state.mControllerInfo & (1<<3)) != 0)
					{
						loadState = battery_plate_sate_str[3];
						continue;
					}
					else  if((state.mControllerInfo & (1<<i)) != 0)
					{
						loadState = battery_plate_sate_str[i];
						continue;
					}
				}
			}
		}
		tv_battery_state_value.setText(batteryStateInfoStr);
		tv_battery_plate_state_value.setText(controllerInfoStr);


		tv_load_state_value.setText(loadState);


		mLoadSwitchStatusSB.setChecked(state.mSolarPanelState == 1);
		if(mView !=null)
		{
	//		((TextView)mView.findViewById(R.id.tv_load_switch_status_value)).setVisibility(View.GONE);
//			((RelativeLayout)mView.findViewById(R.id.relayout_load_switch_status_value)).setVisibility(View.VISIBLE);
		}
		
	}
	protected void UpdateParamSettingData(byte[] bs) {
		// TODO Auto-generated method stub
		ShourigfData.ParamSettingData data = new ShourigfData.ParamSettingData(bs);
		mLoadOptMod = 	data.mData[ParamSettingDataStruct.LOAD_OPERATIN_MODE_OFFSET];
		Log.d(TAG, "mLoadOptMod:"+mLoadOptMod);
		if(mLoadOptMod  == 0xf)
		{
			mLoadSwitchStatusSB.setEnabled(true);
		}
		else
		{
			mLoadSwitchStatusSB.setEnabled(false);
		}
	
	}
	private void UpdateSolarPanelInfo(byte[] bs) {
		// TODO Auto-generated method stub
		ShourigfData.SolarPanelInfo spinfo = new ShourigfData.SolarPanelInfo(bs);
		tv_solar_panel_voltage_value.setText(GlobalStaticFun.DeutschStringNumber(String.format("%1$.1fV",spinfo.mVoltage)));
		tv_solar_panel_electricity_value.setText(GlobalStaticFun.DeutschStringNumber(String.format("%1$.2fA",spinfo.mElectricity)));
		tv_charging_power_value.setText(spinfo.mChargingPower+"W");
		//		cb_load_switch_status.setChecked(spinfo.mSwitch == 1);
	}
	private void UpdateBatteryParamInfo(byte[] bs) {
		// TODO Auto-generated method stub
		ShourigfData.BatteryParamInfo bec = new ShourigfData.BatteryParamInfo(bs);
		tv_battery_esidual_capacity_value.setText(bec.mCapacity+"%");
		tv_battery_voltage_value.setText(Math.round(bec.mVoltage*100)/100.f+"V");
		tv_charging_electricity_value.setText(/*Math.round(bec.mElectricity*100)/100.f+"A"*/
				GlobalStaticFun.DeutschStringNumber(String.format("%1$.2fA",bec.mElectricity)));
		tv_battery_temperature_value.setText(bec.mBatteryTemperature+getString(R.string.centigrade));
		tv_load_voltage_value.setText(Math.round(bec.mLoadVoltage*100)/100.f+"V");
		tv_load_electricity_value.setText(/*Math.round(bec.mLoadElectricity*100)/100.f+"A"*/
				GlobalStaticFun.DeutschStringNumber(String.format("%1$.2fA",bec.mLoadElectricity)));
		tv_load_power_value.setText(bec.mLoadPower+"W");

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(mView != null)
			return mView;
		View view = inflater.inflate(R.layout.fragment_realtime_monitor, container,false);
		InitView(view);
		


		mView = view;
		return view;//super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	private void InitView(View view) {
		// TODO Auto-generated method stub
		tv_solar_panel_voltage_value = (TextView)view.findViewById(R.id.tv_solar_panel_voltage_value);
		tv_solar_panel_voltage_value.setText(getString(R.string.init_null_charter));
		tv_solar_panel_electricity_value = (TextView)view.findViewById(R.id.tv_solar_panel_electricity_value);
		tv_solar_panel_electricity_value.setText(getString(R.string.init_null_charter));
		tv_charging_power_value = (TextView)view.findViewById(R.id.tv_solar_panel_charging_power_value);
		tv_charging_power_value.setText(getString(R.string.init_null_charter));
		tv_battery_plate_state_value = (TextView)view.findViewById(R.id.tv_solar_panel_state_value);
		tv_battery_plate_state_value.setText(getString(R.string.init_null_charter));
		tv_battery_esidual_capacity_value = (TextView)view.findViewById(R.id.tv_battery_esidual_capacity_value);
		tv_battery_esidual_capacity_value.setText(getString(R.string.init_null_charter));
		tv_battery_voltage_value = (TextView)view.findViewById(R.id.tv_battery_voltage_value);
		tv_battery_voltage_value.setText(getString(R.string.init_null_charter));
		tv_charging_electricity_value = (TextView)view.findViewById(R.id.tv_battery_electricity_value);
		tv_charging_electricity_value.setText(getString(R.string.init_null_charter));
		tv_battery_temperature_value = (TextView)view.findViewById(R.id.tv_battery_temperature_value);
		tv_battery_temperature_value.setText(getString(R.string.init_null_charter));
		tv_battery_state_value = (TextView)view.findViewById(R.id.tv_battery_state_value);
		tv_battery_state_value.setText(getString(R.string.init_null_charter));
		tv_charging_state_value = (TextView)view.findViewById(R.id.tv_battery_charging_state_value);
		tv_charging_state_value.setText(getString(R.string.init_null_charter));
		tv_load_voltage_value = (TextView)view.findViewById(R.id.tv_load_voltage_value);
		tv_load_voltage_value.setText(getString(R.string.init_null_charter));
		tv_load_electricity_value = (TextView)view.findViewById(R.id.tv_load_electricity_value);
		tv_load_electricity_value.setText(getString(R.string.init_null_charter));
		tv_load_power_value = (TextView)view.findViewById(R.id.tv_load_power_value);
		tv_load_power_value.setText(getString(R.string.init_null_charter));
		tv_load_state_value = (TextView)view.findViewById(R.id.tv_load_state_value);
		tv_load_state_value.setText(getString(R.string.init_null_charter));
//		cb_load_switch_status = (CheckBox)view.findViewById(R.id.load_switch_status);
		//cb_load_switch_status.setVisibility(View.GONE);
		mLoadSwitchStatusSB = (SwitchButton)view.findViewById(R.id.sb_load_switch_status);
		mLoadSwitchStatusSB.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDelayTimes = 10;
				if(mLoadSwitchStatusSB.isChecked())
				{
					tv_load_state_value.setText(getString(R.string.opened));
					Log.d(TAG, "isChecked");
					SendUartData(ModbusData.BuildWriteRegCmd(mDeviceId, 0x010A, 1));
				}
				else
				{
					tv_load_state_value.setText(getString(R.string.closed));
					Log.d(TAG, "unChecked");
					SendUartData(ModbusData.BuildWriteRegCmd(mDeviceId, 0x010A, 0));
				}
			}

		});

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mExit =true;
		Log.d(TAG, "onPause");
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mExit = false;
		new Thread(new RefreshThread()).start();  

	}
	public void RefreshData()
	{
		mExit = false;
		new Thread(new RefreshThread()).start();  
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mExit = true;
		Log.d(TAG, "onStop");
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	private class RefreshThread implements Runnable{  

		public void run() {  
			if(mRefreshThreadRuning)
				return;
			mRefreshThreadRuning = true;
			int times = 0;
			while(!mExit)
			{
				ClearRecvFifo();
				Sleep(100);
				mReadingRegId = ShourigfData.BatteryParamInfo.REG_ADDR;
				mReadingCount = ShourigfData.BatteryParamInfo.READ_WORD;
				SendUartData(ModbusData.BuildReadRegsCmd(mDeviceId, mReadingRegId, mReadingCount));
				Sleep(200);
				WaitPostMessage(1000);
				if(mExit)break;

				ClearRecvFifo();
				mReadingRegId = ShourigfData.SolarPanelInfo.REG_ADDR;
				mReadingCount = ShourigfData.SolarPanelInfo.READ_WORD;
				SendUartData(ModbusData.BuildReadRegsCmd(mDeviceId, mReadingRegId, mReadingCount));
				Sleep(200);
				WaitPostMessage(1000);
				if(mExit)break;

				ClearRecvFifo();
				mReadingRegId = ShourigfData.SolarPanelAndBatteryState.REG_ADDR;
				mReadingCount = ShourigfData.SolarPanelAndBatteryState.READ_WORD;
				SendUartData(ModbusData.BuildReadRegsCmd(mDeviceId, mReadingRegId, mReadingCount));
				Sleep(200);
				WaitPostMessage(1000);	
				
				ClearRecvFifo();
				mReadingRegId = ShourigfData.ParamSettingData.REG_ADDR;
				mReadingCount = ShourigfData.ParamSettingData.READ_WORD;
				SendUartData(ModbusData.BuildReadRegsCmd(mDeviceId, mReadingRegId, mReadingCount));
				Sleep(200);
				WaitPostMessage(5000);
				
				if(mExit)break;

			/*	times++;
				if(times%2 != 0)
					continue;

				while(mDelayTimes-- > 0)
					Sleep(1000);
				mDelayTimes= 10;*/
				//modify 2018 06 03
				Sleep(2000);
				

			}
			Log.d(TAG, "exit thread");
			mExit = true;
			mRefreshThreadRuning = false;
		}
	}

}
