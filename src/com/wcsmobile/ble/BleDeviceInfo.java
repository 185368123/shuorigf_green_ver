package com.wcsmobile.ble;

import java.io.Serializable;

public class BleDeviceInfo implements Serializable {
	private static final long serialVersionUID = -7060210544600464481L;
	private String mDeviceName;
	private String mMacAddr;
	
	public BleDeviceInfo(String name,String addr)
	{
		mDeviceName = name;
		mMacAddr = addr;
	}
	public String GetDeviceName()
	{
		return mDeviceName;
	}
	public String GetMacAddr()
	{
		return mMacAddr;
	}

}
