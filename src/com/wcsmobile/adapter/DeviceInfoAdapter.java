package com.wcsmobile.adapter;

import java.util.ArrayList;

import com.wcsmobile.DBHelp;
import com.wcsmobile.R;
import com.wcsmobile.ble.BleDeviceInfo;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceInfoAdapter extends BaseAdapter {
	private static final String TAG="DeviceInfoAdapter";
	private ArrayList <BleDeviceInfo> mBleDevices;
	private Context context;
	private LayoutInflater inflater;
	private int mCurrentIndex= 0;
	public DeviceInfoAdapter(Context context,ArrayList <BleDeviceInfo> devices)
	{
		this.inflater  =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		mBleDevices = devices;
	}
	public int getSelectIndex()
	{
		return mCurrentIndex;
	}
	public void setSelectIndex(int index)
	{
		mCurrentIndex = index;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mBleDevices!=null)
			return mBleDevices.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mBleDevices.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.item_device_info, null);
			holder =  new ViewHolder();
			holder.deviceName = (TextView) convertView.findViewById(R.id.tv_device_name);

			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.deviceName.setText(DBHelp.getInstance().getDeviceName(mBleDevices.get(position).GetDeviceName()));
		if( position == mCurrentIndex) {
			convertView.setBackgroundColor(Color.argb(0xff,0xd2,0xd2,0xd2));
			holder.deviceName.setTextColor(Color.rgb(0x1c,0x1c,0x1c));
		}else {
			convertView.setBackgroundColor(Color.argb(0xff,0xf1,0xf1,0xf1));
			holder.deviceName.setTextColor(Color.rgb(0x77,0x77,0x77));
		}
		return convertView;
	}
	private class ViewHolder{
		public TextView deviceName;
	}


}
