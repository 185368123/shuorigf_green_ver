package com.wcsmobile.data;

import java.io.Serializable;

import android.util.Log;

public class ShourigfData {
	public static int Bytes2Int(byte []bs,int offset,int len)
	{ 
		int ret = 0;
		if(bs.length < offset+len )//error
			return ret;

		for(int i=0;i<len;i++)
		{
			ret |= (bs[offset+i] & 0xff) << (8*(len-i-1));
		}
		return ret;
	}
	public static long  Bytes2Long(byte []bs,int offset,int len)
	{ 
		long ret = 0;
		if(bs.length < offset+len )//error
			return ret;

		for(int i=0;i<len;i++)
		{
			ret |= (bs[offset+i] & 0xff) << (8*(len-i-1));
		}
		return ret;
	}
	public static String Bytes2String(byte []bs,int offset,int len)
	{ 
		byte [] temp_data = new byte[len];
		String ret = "";
		if(bs.length < offset+len )//error
			return ret;
		System.arraycopy(bs,offset,temp_data,0,len);
		ret = new String(temp_data);
		return ret;
	}
	static public class SystemlVoltageApm{
		public final static int REG_ADDR=0x000A;
		public final static int READ_WORD=0x0001;
		public int mVoltage=0;//V
		public int mElectricity=0;//A		
		private boolean mDataIsCorrect=false;
		public SystemlVoltageApm(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			if(mDataIsCorrect)
			{
				mVoltage = Bytes2Int(bs,3,1);
				mElectricity =Bytes2Int(bs,4,1);
			}
		}
	};

	static public class SolarPanelInfo{
		public final static int REG_ADDR=0x0107;
		public final static int READ_WORD=0x0004;
		public float mVoltage=0;//0.1V
		public float mElectricity=0;//0.1A		
		public int mChargingPower=0;//W
		public int mSwitch=1;
		private boolean mDataIsCorrect=false;
		public SolarPanelInfo(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			if(mDataIsCorrect)
			{
				mVoltage = Bytes2Int(bs,3,2)*0.1f;
				mElectricity = Bytes2Int(bs,5,2)*0.01f;
				mChargingPower =  Bytes2Int(bs,7,2);
				mSwitch = Bytes2Int(bs,9,2);
			}
		}
	};	

	/*	static public class BatteryEsidualCapacity{
		public final static int REG_ADDR=0x0100;
		public final static int READ_WORD=0x0001;
		public int mCapacity=0;//%

		private boolean mDataIsCorrect=false;
		public BatteryEsidualCapacity(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			if(mDataIsCorrect)
			{
				mCapacity = Bytes2Int(bs,3,2);
			}
		}
	};	

	static public class BatteryVoltage{
		public final static int REG_ADDR=0x0101;
		public final static int READ_WORD=0x0001;
		public float mVoltage=0;//0.1V

		private boolean mDataIsCorrect=false;
		public BatteryVoltage(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			if(mDataIsCorrect)
			{
				mVoltage = Bytes2Int(bs,3,2)*0.1f;
			}
		}
	};	
	 */
	static public class BatteryParamInfo{
		public final static int REG_ADDR=0x0100;
		public final static int READ_WORD=0x0007;
		public int mCapacity=0;//%
		public float mVoltage=0;//0.1V
		public float mElectricity=0;
		public int mDeviceTemperature = -10;
		public int mBatteryTemperature = 20;
		public float mLoadVoltage=0;
		public float mLoadElectricity=0;
		public int mLoadPower=0;
		private boolean mDataIsCorrect=false;

		public BatteryParamInfo(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			if(mDataIsCorrect)
			{
				mCapacity = Bytes2Int(bs,3,2);
				mVoltage = Bytes2Int(bs,5,2)*0.1f;
				mElectricity = Bytes2Int(bs,7,2)*0.01f;
				mDeviceTemperature = bs[9]&0x7f;
				mBatteryTemperature = bs[10]&0x7f;
				if( (bs[9]&0x80) != 0)
					mDeviceTemperature = -mDeviceTemperature;
				if( (bs[10]&0x80) != 0)
					mBatteryTemperature = -mBatteryTemperature;
				
				mLoadVoltage = Bytes2Int(bs,11,2)*0.1f;
				mLoadElectricity = Bytes2Int(bs,13,2)*0.01f;
				mLoadPower  = Bytes2Int(bs,15,2);
				
			}
		}
	};	
	static public class SolarPanelAndBatteryState{
		public final static int REG_ADDR=0x0120;
		public final static int READ_WORD=0x0003;
		public int mSolarPanelState=0;
		public int mBatteryState=0;
		public int mControllerInfo =0;
		private boolean mDataIsCorrect=false;
		public SolarPanelAndBatteryState(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			if(mDataIsCorrect)
			{
				mSolarPanelState = Bytes2Int(bs,3,1)>>7;
				mBatteryState = Bytes2Int(bs,4,1);
				if(mBatteryState>6)
						mBatteryState =0;
				mControllerInfo = Bytes2Int(bs,5,4);
			}
		}
	};	


	/*about device info*/
	static public class DeviceProductType{
		public final static int REG_ADDR=0x000C;
		public final static int READ_WORD=0x0008;
		public String mProductTypeStr="";
		private boolean mDataIsCorrect=false;
		public DeviceProductType(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			if(mDataIsCorrect)
			{
				mProductTypeStr = Bytes2String(bs,3,16).replaceAll(" ", "");

			}
		}
	};	
	static public class DeviceVersionInfo{
		public final static int REG_ADDR=0x0014;
		public final static int READ_WORD=0x0004;
		public String mSoftVersion="V1.0.0";
		public String mHardwareVersion="V1.0.0";
		private boolean mDataIsCorrect=false;
		public DeviceVersionInfo(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			if(mDataIsCorrect)
			{
				mSoftVersion = "V"+bs[4]+"."+bs[5]+"."+bs[6];
				mHardwareVersion = "V"+bs[8]+"."+bs[9]+"."+bs[10];
			}
		}
	};	
	static public class DeviceSerialNumber{
		public final static int REG_ADDR=0x0018;
		public final static int READ_WORD=0x0003;
		public String mSN="150100011";
		public int mDeviceAddr = 0;

		private boolean mDataIsCorrect=false;
		public DeviceSerialNumber(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			if(mDataIsCorrect)
			{
				mSN = String.format("%02d%02d%04d", Bytes2Int(bs,3,1),Bytes2Int(bs,4,1),Bytes2Int(bs,5,2));
				mDeviceAddr =  Bytes2Int(bs,0,1);
			}
		}
	};	
	
	
	/**historicaldata*/
	static public class HistoricalData implements Serializable {
		private static final long serialVersionUID = -7060210544600464481L; 
		public final static int REG_ADDR=0x010B;
		public final static int READ_WORD=0x0015;
		public float mDayBatteryMinVoltage=0;
		public float mDayBatteryMaxVoltage=0;
		public int mDayChargeMaxPower=0;
		public int mDayDischargeMaxPower=0;
		public int mDayChargeAmpHour=0;
		public int mDayDischargeAmpHour=0;
		public int mDayProductionPower=0;
		public int mDayConsumptionPower=0;
		public int mAllRunDays=0;
		public int mBatteryAllDischargeTimes = 0;
		public int mbatteryChargeFullTimes=0;
		public long mBatteryChargeAllApmHour=0;
		public long mBatteryDischargeAllApmHour=0;
		public long mAllProductionPower=0;
		public long mAllConsumptionPower=0;

		private boolean mDataIsCorrect=false;
		public HistoricalData(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			if(mDataIsCorrect)
			{
				mDayBatteryMinVoltage =  Bytes2Int(bs,3,2)*0.1f;
				mDayBatteryMaxVoltage =  Bytes2Int(bs,5,2)*0.1f;
				mDayChargeMaxPower = Bytes2Int(bs,11,2);
				mDayDischargeMaxPower = Bytes2Int(bs,13,2);
				mDayChargeAmpHour = Bytes2Int(bs,15,2);
				mDayDischargeAmpHour = Bytes2Int(bs,17,2);
				mDayProductionPower = Bytes2Int(bs,19,2);
				mDayConsumptionPower = Bytes2Int(bs,21,2);
				mAllRunDays = Bytes2Int(bs,23,2); 
				mBatteryAllDischargeTimes = Bytes2Int(bs,25,2); 
				mbatteryChargeFullTimes = Bytes2Int(bs,27,2); 
				mBatteryChargeAllApmHour = Bytes2Long(bs,29,4); 
				mBatteryDischargeAllApmHour = Bytes2Long(bs,33,4); 
				mAllProductionPower = Bytes2Long(bs,37,4); 
				mAllConsumptionPower =  Bytes2Long(bs,41,4); 
			}
		}
		public void  SyncHistoricalChartData(ShourigfData.HistoricalChartData data)
		{
			mDayBatteryMinVoltage = data.mDayBatteryMinVoltage;
			mDayBatteryMaxVoltage = data.mDayBatteryMaxVoltage;
			mDayChargeMaxPower = data.mDayChargeMaxPower;
			mDayDischargeMaxPower = data.mDayDischargeMaxPower;
			mDayChargeAmpHour = data.mDayChargeAmpHour;
			mDayDischargeAmpHour = data.mDayDischargeAmpHour;
			mDayProductionPower = data.mDayProductionPower;
			mDayConsumptionPower = data.mDayConsumptionPower;
			
		}
	};	
	/**historicalChartData*/
	static public class HistoricalChartData{
		public final static int REG_ADDR=0xF000;
		public final static int READ_WORD=0x000A;
		public float mDayBatteryMinVoltage=0;
		public float mDayBatteryMaxVoltage=0;
		public int mDayChargeMaxPower=0;
		public int mDayDischargeMaxPower=0;
		public int mDayChargeAmpHour=0;
		public int mDayDischargeAmpHour=0;
		public int mDayProductionPower=0;
		public int mDayConsumptionPower=0;
		private boolean mDataIsCorrect=false;
		public HistoricalChartData(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			//Log.d("mDataIsCorrect", "mDataIsCorrect:"+mDataIsCorrect);
			if(mDataIsCorrect)
			{
				mDayBatteryMinVoltage =  Bytes2Int(bs,3,2)*0.1f;
				mDayBatteryMaxVoltage =  Bytes2Int(bs,5,2)*0.1f;
				mDayChargeMaxPower = Bytes2Int(bs,11,2);
				mDayDischargeMaxPower = Bytes2Int(bs,13,2);
				mDayChargeAmpHour = Bytes2Int(bs,15,2);
				mDayDischargeAmpHour = Bytes2Int(bs,17,2);
				mDayProductionPower = Bytes2Int(bs,19,2);
				mDayConsumptionPower = Bytes2Int(bs,21,2);
			}
		}
	};	
	
	/*param settings*/
	
	static public class ParamSettingData{
		public final static int REG_ADDR=0xE001;
		public final static int READ_WORD=0x0021;
		public int mData[] = new int [READ_WORD];

		private boolean mDataIsCorrect=false;
		public ParamSettingData(byte []bs)
		{
			mDataIsCorrect = ModbusData.DataCorrect(bs,READ_WORD);
			if(mDataIsCorrect)
			{
				for(int i=0;i<mData.length;i++)
				{
					mData[i] = Bytes2Int(bs,3+i*2,2); 
				}
			}
		}
	};	
	
}
