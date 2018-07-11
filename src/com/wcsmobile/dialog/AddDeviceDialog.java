package com.wcsmobile.dialog;

import java.util.ArrayList;
import java.util.Date;

import com.wcsmobile.R;
import com.wcsmobile.adapter.DeviceInfoAdapter;
import com.wcsmobile.ble.BleDeviceInfo;
import com.wcsmobile.data.GlobalStaticData;
import com.wcsmobile.dialog.SetDateDialog.ClickListenerInterface;
import com.wcsmobile.widget.wheelview.WheelView;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class AddDeviceDialog extends AlertDialog {
	private String TAG="AddDeviceDialog";
	private Context context;
	private ArrayList <BleDeviceInfo> mBleDevices;
	private BleDeviceInfo mCurrentInfo;
	private OnConfirmInterface mOnConfirmInterface;
	private ListView mDeviceListView;
	private DeviceInfoAdapter mDeviceInfoAdapter;
	private boolean mAutoSel = true;
	public AddDeviceDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public AddDeviceDialog(Context context,ArrayList <BleDeviceInfo> deviceinfo) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		mBleDevices = new  ArrayList <BleDeviceInfo>();
		if(deviceinfo !=null)
			mBleDevices.addAll(deviceinfo);
		if(mBleDevices!= null && mBleDevices.size()>0)
			mCurrentInfo=deviceinfo.get(0);
	}
	public void SetOnConfirmInterface(OnConfirmInterface onConfirmInterface) {
		this.mOnConfirmInterface = onConfirmInterface;
	}
	public interface OnConfirmInterface {
		public void doConfirm(BleDeviceInfo info);

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Create...");
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_add_device, null);
		setContentView(view);
		mDeviceInfoAdapter = new DeviceInfoAdapter(context,mBleDevices);
		mDeviceListView = (ListView)view.findViewById(R.id.lvDeviceInfo);
		mDeviceListView.setAdapter(mDeviceInfoAdapter);
		mDeviceListView.setOnItemClickListener(listOnItemClickListener);

		((Button)(view.findViewById(R.id.btnCancel))).setOnClickListener(clickListener);
		((Button)(view.findViewById(R.id.btnConfirm))).setOnClickListener(clickListener);


		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = context.getResources().getDisplayMetrics(); 
		lp.width = (int) (d.widthPixels * 0.9); 

		dialogWindow.setAttributes(lp);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(TAG, "OnStart");
		mAutoSel = true;
		UpdateDevicesList(mBleDevices);
	}
	public void UpdateDevicesList(ArrayList <BleDeviceInfo> deviceinfo)
	{

		if(mAutoSel)
		{
			if(mBleDevices == null)
			{
				mBleDevices = new  ArrayList <BleDeviceInfo>();
			}
			else
			{
				mBleDevices.clear();
			}
			if(deviceinfo !=null)
				mBleDevices.addAll(deviceinfo);
			int index  = -1;
			if(GlobalStaticData.getInstance().getCurrentConnectDevInfo() != null)
			{
				for(int i=0;i<mBleDevices.size();i++)
				{
					if(mBleDevices.get(i).GetMacAddr().equals(
							GlobalStaticData.getInstance().getCurrentConnectDevInfo().GetMacAddr()))
					{

						index = i;
						break;
					}
				}
				if(index == -1)
				{
					mBleDevices.add(0, GlobalStaticData.getInstance().getCurrentConnectDevInfo());
					index= 0;
				}
			}

			if(mBleDevices!= null && mBleDevices.size()>0)
			{
				if(index == -1)
				{
					mCurrentInfo=mBleDevices.get(0);
					if(mDeviceInfoAdapter!=null)
						mDeviceInfoAdapter.setSelectIndex(0);
				}
				else
				{
					mCurrentInfo=mBleDevices.get(index);
					if(mDeviceInfoAdapter!=null)
						mDeviceInfoAdapter.setSelectIndex(index);
				}
			}
		}
		else
		{
			if(mBleDevices == null)
			{
				mBleDevices = new  ArrayList <BleDeviceInfo>();
			}
			if(deviceinfo !=null)
			{
				for(int i = 0; i< deviceinfo.size();i++)
				{
					boolean find = false;
					for(int j=0;j<mBleDevices.size();j++)
					{
						if(mBleDevices.get(j).GetMacAddr().equals(
								deviceinfo.get(i).GetMacAddr()))
						{
							find = true;
							break;
						}
					}
					if(!find)
						mBleDevices.add(deviceinfo.get(i));
				}

			}
		}
		if(mDeviceInfoAdapter!=null)
			mDeviceInfoAdapter.notifyDataSetChanged();
	}
	private OnItemClickListener listOnItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			mAutoSel = false;

			mDeviceInfoAdapter.setSelectIndex(arg2);
			mCurrentInfo = (BleDeviceInfo)mDeviceInfoAdapter.getItem(arg2);
			mDeviceInfoAdapter.notifyDataSetChanged();

		}};
		private View.OnClickListener clickListener =  new View.OnClickListener(){


			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(v.getId())
				{
				case R.id.btnCancel:
					dismiss();
					break;
				case R.id.btnConfirm:
					mOnConfirmInterface.doConfirm(mCurrentInfo);
					dismiss();
					break;
				default:
					break;
				}
			}

		};
}
