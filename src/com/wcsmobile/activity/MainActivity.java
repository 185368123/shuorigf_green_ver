package com.wcsmobile.activity;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import com.wcsmobile.DBHelp;
import com.wcsmobile.R;
import com.wcsmobile.R.layout;
import com.wcsmobile.R.menu;
import com.wcsmobile.ble.BleDeviceInfo;
import com.wcsmobile.ble.BluetoothLeService;
import com.wcsmobile.ble.GattAttributes;
import com.wcsmobile.data.ChecksumCRC;
import com.wcsmobile.data.GlobalStaticData;
import com.wcsmobile.data.ModbusData;
import com.wcsmobile.data.SharePreferenceUtils;
import com.wcsmobile.data.ShourigfData;
import com.wcsmobile.dialog.AddDeviceDialog;
import com.wcsmobile.dialog.ShowSoftVersionInfoDialog;
import com.wcsmobile.dialog.AddDeviceDialog.OnConfirmInterface;
import com.wcsmobile.fragment.BaseFragment;
import com.wcsmobile.fragment.DeviceInfoFragment;
import com.wcsmobile.fragment.HistoricalDataFragment;
import com.wcsmobile.fragment.ParamSettingFragment;
import com.wcsmobile.fragment.RealTimeMonitoringFragment;
import com.wcsmobile.view.SystemBarTintManager;
import com.wcsmobile.widget.PopupListContentAdapter;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener{
	public final static String TAG="MainActivity";
	private TextView tvTitle;
	private ImageButton btnAdd;
	private FragmentManager fragmentManager;
	public final static int SEND_UART_DATA = 0x1001;
	public final static int SEND_UART_STRING = 0x1002;
	public final static int START_SCAN = 0x1003;
	public final static int CONNECT_DEVICE = 0x1004;
	public final static int DISCONNECT_DEVICE = 0x1005;
	public static final int DISCOVE_DEVICE_ID = 0x1006;

	private Date mToastDisconectDate;

	private RealTimeMonitoringFragment rtmFragment = null;
	private HistoricalDataFragment hisDataFragment =null;
	private ParamSettingFragment paramSetFragment = null;
	private DeviceInfoFragment devInfoFragment = null;
	private PopupWindow popupWinow = null;
	private View popupView = null;
	private AddDeviceDialog mAddDeviceDialog;
	private ShowSoftVersionInfoDialog mShowSoftVersionInfoDialog;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 10000;
	private ArrayList <BleDeviceInfo> mBleDevices;
	private BluetoothLeService mBluetoothLeService;
	private BluetoothGattCharacteristic mUartRecvCharacteristic;
	private BluetoothGattCharacteristic mUartSendCharacteristic;
	public  boolean mConnected = false;
	private SharedPreferences mSharedPreferences;
	private String SHAREDPERNAME ="devices";
	private BleDeviceInfo mLastConectDevInfo;
	private int mDeviceIdOld=-1;
	private int receiveDeviceId=-1;
	private boolean isFirst = true;
	private Timer mtimer;
	private boolean mAdmin=false;
	private int mCurrentFrag=-1;//BaseFragment.REALTIMEMONITORING_FRAGM;
	private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

	//private PopupListContentAdapter popupListContentAdapter;
	//	private ListView popupListView;
	private BleDeviceInfo GetLastDeviceInfo()
	{
		String name = mSharedPreferences.getString("LastDeviceName", "");
		String mac = mSharedPreferences.getString("LastDeviceMacAddr", "");
		return new BleDeviceInfo(name,mac);
	}
	private void SaveLastDeviceInfo(BleDeviceInfo info)
	{
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString("LastDeviceName", info.GetDeviceName());
		editor.putString("LastDeviceMacAddr", info.GetMacAddr());
		editor.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSharedPreferences = getSharedPreferences(SHAREDPERNAME, Activity.MODE_PRIVATE); 
		mLastConectDevInfo = GetLastDeviceInfo();

		fragmentManager = getFragmentManager();
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
		radioGroup.setOnCheckedChangeListener(this);
		tvTitle=(TextView)findViewById(R.id.tvTitle);
		btnAdd=(ImageButton)findViewById(R.id.btnAdd);
		popupView = LayoutInflater.from(MainActivity.this).inflate(
				R.layout.view_popup, null);
		//  popupListContentAdapter = new PopupListContentAdapter(MainActivity.this);
		//  popupListView =(ListView) popupView.findViewById(R.id.lsv_content);
		//   popupListView.setAdapter(popupListContentAdapter);
		mToastDisconectDate = new Date();
		GlobalStaticData.getInstance().setCurrentConnectDevInfo(null);

		((TextView) popupView.findViewById(R.id.tvAddDevice)).setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "AddDevice Clicked!");
				AddDevice();
				popupWinow.dismiss();
			}

		});

		((TextView) popupView.findViewById(R.id.tvCheckUpdate)).setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "check update Clicked!");
				CheckUpdate();
				popupWinow.dismiss();

			}


		});
		((TextView) popupView.findViewById(R.id.tvGetAdmin)).setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, " admin Clicked!");
				CheckAdmin();
				popupWinow.dismiss();

			}


		});
		((TextView) popupView.findViewById(R.id.tvModifyPwd)).setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, " update pwd Clicked!");
				UpdatePassword();
				popupWinow.dismiss();

			}


		});
		btnAdd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popupWinow == null) {
					popupWinow = new PopupWindow(popupView,
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, true);
					popupWinow.setBackgroundDrawable(new ColorDrawable(
							0x00000000));
				}
				popupWinow.showAsDropDown(btnAdd, 100, 1);
			}

		});

		
	/*	if (Build.VERSION.SDK_INT >=23) {
	        // Android M Permission check
	        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
	            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
	        }
	    }*/
		
		//default
		findViewById(R.id.rb_monitoring).performClick();
		InitBle();
		//test
		//mDeviceId = 1;
		//UpdateDeviceId(mDeviceId);
		
		
		if(Build.VERSION.SDK_INT>=23){
		    //判断是否有权限
		    if (ContextCompat.checkSelfPermission(MainActivity.this,
		Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
		    //请求权限
		 ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_REQUEST_COARSE_LOCATION);
		//向用户解释，为什么要申请该权限
		if(ActivityCompat.shouldShowRequestPermissionRationale(this,
		Manifest.permission.READ_CONTACTS)) {
		Toast.makeText(MainActivity.this,"shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
		      }
		    }
		}
		

	}
	
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
	    switch (requestCode) {
	        case PERMISSION_REQUEST_COARSE_LOCATION:
	            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
	                // TODO request success
	            }
	        break;
	    }
	}
	
	private void DisConnectDevice()
	{
		Log.d(TAG, "DisConnectDevice:"+mConnected+","+mBluetoothLeService);
		if(mBluetoothLeService != null && mConnected)
		{
			
			mBluetoothLeService.disconnect();
			mLastConectDevInfo = null;
			//mBluetoothLeService.close();
		}
	}
	private void ConnectDevice(BleDeviceInfo info)
	{
		if(info != null && mBluetoothLeService != null)
		{
			if(mLastConectDevInfo != null && mLastConectDevInfo.GetMacAddr().equals(info.GetMacAddr()) && mConnected)
			{
				String strinfo = String.format(getString(R.string.connect_device_device_success), mLastConectDevInfo.GetDeviceName());
				Toast.makeText(MainActivity.this,strinfo , Toast.LENGTH_SHORT).show();
				return;
			}
			Log.d(TAG, "mBluetoothLeService.connect to "+info.GetDeviceName()+",mac:"+info.GetMacAddr());
			/*unregisterReceiver(mGattUpdateReceiver);
			Log.d(TAG, "mBluetoothLeService.connect to "+info.GetDeviceName()+",mac:"+info.GetMacAddr());

			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
			intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
			intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
			intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
			registerReceiver(mGattUpdateReceiver, intentFilter);*/
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
			mLastConectDevInfo = info;
			mBluetoothLeService.connect(info.GetMacAddr());

			SaveLastDeviceInfo(info);
		}
	}
	private void InitBle() {
		// TODO Auto-generated method stub
		mHandler = new Handler();
		// Use this check to determine whether BLE is supported on the device.  Then you can
		// selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.your_device_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}
		// Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
		// BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.your_device_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		Log.i(TAG, "bindService");
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
			if(!mConnected)
			{
				if(!mLastConectDevInfo.GetMacAddr().equals(""))
				{
					mBluetoothLeService.close();
					//mBluetoothLeService.connect(mLastConectDevInfo.GetMacAddr());
					ConnectDevice(mLastConectDevInfo);
				}
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		/*if(mConnected)
		{
			mBluetoothLeService.disconnect();
		}*/
		if(mBluetoothLeService != null && mConnected)
		{
			
			mBluetoothLeService.disconnect();
		}
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
	}
	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub

			Log.d(TAG, "mTask on:mConnected:"+mConnected );
			if(mLastConectDevInfo != null)
				Log.d(TAG,  "mLastConectDevInfo:"+mLastConectDevInfo.GetDeviceName());
			if(mBleDevices != null)
			{
				Log.d(TAG,  "mBleDevices:"+mBleDevices.size());
			}

			if(!mConnected)
			{

				if(mLastConectDevInfo != null && mBleDevices != null &&mBleDevices.size()> 0 && !mLastConectDevInfo.GetDeviceName().equals(""))
				{
					Log.d(TAG,  "ConnectDevice:"+mLastConectDevInfo.GetDeviceName());
					ConnectDevice(mLastConectDevInfo);
				}
				scanLeDevice(true);
			}

		}

	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Configuration config = getResources().getConfiguration();	
		DisplayMetrics dm =  getResources().getDisplayMetrics();
		 getResources().updateConfiguration(config, dm); 
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
		// fire an intent to display a dialog asking the user to grant permission to enable it.
		if (!mBluetoothAdapter.isEnabled()) {

			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
		mBleDevices = new ArrayList <BleDeviceInfo>();
		mAddDeviceDialog = new AddDeviceDialog(MainActivity.this,mBleDevices);
		mShowSoftVersionInfoDialog = new ShowSoftVersionInfoDialog(MainActivity.this);
		mAddDeviceDialog.SetOnConfirmInterface(new OnConfirmInterface(){
			@Override
			public void doConfirm(BleDeviceInfo info) {
				// TODO Auto-generated method stub
				//Log.d(TAG, "connect:"+info.GetDeviceName());
				ConnectDevice(info);
			}});
		scanLeDevice(true);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		registerReceiver(mGattUpdateReceiver, intentFilter);
		mtimer = new Timer();
		mtimer.schedule(new MyTimerTask(), 20000,20000);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		scanLeDevice(false);
		mBleDevices.clear();
		unregisterReceiver(mGattUpdateReceiver);
		mtimer.cancel();
		mtimer = null;
	}
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Log.i(TAG, "ACTION:" + action);
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mDeviceIdOld = -1;
				isFirst = true;
				mConnected = true;
				UpdateDeviceConnectStateDis(mConnected);
				//scanLeDevice(false);
				//GetGattServices(mBluetoothLeService.getSupportedGattServices());
				//	String info = String.format(getString(R.string.connect_device_device_success), mLastConectDevInfo.GetDeviceName());
				//	Toast.makeText(MainActivity.this,info , Toast.LENGTH_SHORT).show();
				//	new Thread(new GetDeviceIdThread()).start(); 

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Toast.makeText(MainActivity.this, getString(R.string.disconnect_device_info), Toast.LENGTH_SHORT).show();
				mDeviceIdOld = -1;
				isFirst = true;
				mConnected = false;
				UpdateDeviceConnectStateDis(mConnected);
				GlobalStaticData.getInstance().setCurrentConnectDevInfo(null);

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the user interface.
				GetGattServices(mBluetoothLeService.getSupportedGattServices());
				if(!isFirst)
				{
					RefreshData();
				}
				else
				{
					new Thread(new GetDeviceIdThread()).start(); 
				}
				if (mLastConectDevInfo.GetDeviceName()!=null){
                    String info = String.format(getString(R.string.connect_device_device_success), DBHelp.getInstance().getDeviceName(mLastConectDevInfo.GetDeviceName()));
                    Toast.makeText(MainActivity.this,info , Toast.LENGTH_SHORT).show();
                }
				mConnected = true;
				UpdateDeviceConnectStateDis(mConnected);
				GlobalStaticData.getInstance().setCurrentConnectDevInfo(mLastConectDevInfo);

			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				RecvData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
			}
		}

		private void GetGattServices(
				List<BluetoothGattService> supportedGattServices) {
			// TODO Auto-generated method stub
			Log.d(TAG, "supportedGattServices:"+supportedGattServices);

			if (supportedGattServices == null) return;
			for (BluetoothGattService gattService : supportedGattServices) {
				//uuid = supportedGattServices.getUuid().toString();

				Log.d(TAG, "gattService:"+gattService);

				List<BluetoothGattCharacteristic> gattCharacteristics =
						gattService.getCharacteristics();
				Log.d(TAG, "gattCharacteristics:"+gattCharacteristics);
				// Loops through available Characteristics.
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					Log.d(TAG, "id:"+gattCharacteristic.getUuid().toString());
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
		/**
		 * 接收数据
		 * @param bs
		 */
		private void RecvData(byte[] bs) {
			// TODO Auto-generated method stub
			if(bs == null)
				return;
			Log.e("DDDD","接收到的数据11-->:"+Arrays.toString(bs));
			if(mDeviceIdOld <= 0 && bs.length > 0)
			{
				mDeviceIdOld = 1;
				receiveDeviceId = bs[0];
				isFirst = false;
				Log.d(TAG, "GetDeviceID:"+bs[0]);
				UpdateDeviceId(bs[0]);
				RefreshData();
			}
			SendMessageToFragment(bs);


			///test it			
			String msg="main recv data";
			for(int i=0;i<bs.length;i++)
			{
				msg+=String.format("[%02x] ", bs[i]);
			}
		//	SendUartString(msg+"\r\n");   //test
			Log.d(TAG, msg);
		}
	};
	/**
	 * 发送数据
	 * @param bs
	 */
	private void SendUartData(byte [] bs)
	{
		sendByteData(bs);
//		int timerout = 300;
//		timerout = timerout/10;
//		while(!mBluetoothLeService.isReceiveData && timerout-- !=0)
//		{
//			Sleep(10);
//		}
//		Log.d(TAG, this+"timerout:"+timerout);
//		if(timerout <= 0)
//		{
//			Log.d("DDDD", "重新发送数据==="+Arrays.toString(bs));
//			sendByteData(bs);
//		}
	}
	private void  sendByteData(byte[] bs){
		Log.d("DDDD","发送数据00-->:"+Arrays.toString(bs));
		//if(bs.length > 0)
		//	return;
		
		//Log.v(TAG, "mConnected:"+mConnected);
		Log.v(TAG, "mConnected:"+mConnected + ",USC:"+mUartSendCharacteristic+",BLES:"+mBluetoothLeService);
		if(mUartSendCharacteristic != null && mBluetoothLeService!=null && mConnected)
		{
			mUartSendCharacteristic.setValue(bs);
			mBluetoothLeService.writeCharacteristic(mUartSendCharacteristic);
			String msg="send data";
			for(int i=0;i<bs.length;i++)
			{
				msg+=String.format("[%02x] ", bs[i]);
			}
			Log.d(TAG, msg);
		}
		else if(mBluetoothLeService != null)
		{
			long diff = new Date().getTime() - mToastDisconectDate.getTime();
			if(!mScanning && !mConnected && diff > 35*1000)
			{
				Toast.makeText(this, getString(R.string.can_not_connect_device_info), Toast.LENGTH_SHORT).show();
				mToastDisconectDate = new Date();
			}

		}
	}
	
	/**
	 * 发送响应数据
	 * @param bs
	 */
	private void SendUartString(String str)
	{
		sendStrData(str);
//		int timerout = 2000;
//		timerout = timerout/10;
//		while(!mBluetoothLeService.isReceiveData && timerout-- !=0)
//		{
//			Sleep(10);
//		}
//		Log.d(TAG, this+"timerout:"+timerout);
//		if(timerout <= 0)
//		{
//			Log.d("DDDD", "重新发送数据==="+str);
//			sendStrData(str);
//		}
	}
	private void  sendStrData(String str){
		Log.e("DDDD","发送响应数据22-->:"+str);
		if(mUartSendCharacteristic != null && mBluetoothLeService!=null && mConnected)
		{
			mUartSendCharacteristic.setValue(str);
			mBluetoothLeService.writeCharacteristic(mUartSendCharacteristic);
		}
	}
	private void scanLeDevice(final boolean enable) {//开始或停止扫描设备
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					//invalidateOptionsMenu();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBleDevices.clear();
			mBluetoothAdapter.startLeScan(mLeScanCallback);//开始扫描设备完成后回调callback
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止扫描设备完成后回调callback
		}
		//invalidateOptionsMenu();
	}
	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(device == null)
						return;
					boolean exsit = false;
					if(device != null && device.getName() != null && !device.getName().equals(""))
					{
						for(BleDeviceInfo info :mBleDevices)
						{
							Log.d(TAG, device.getName()+"----"+info.GetDeviceName());
							if(info.GetDeviceName().equals(device.getName())
									&& info.GetMacAddr().equals(device.getAddress()))
							{
								exsit = true;
							}
						}
						Log.d(TAG, device.getName()+",addr:"+device.getAddress());
						if(!exsit)
						{
							mBleDevices.add(new BleDeviceInfo(device.getName(),device.getAddress()));
									
							if(mLastConectDevInfo != null)
							{
							Log.d(TAG, mLastConectDevInfo.GetDeviceName()+
									",mLastConectDevInfo.GetMacAddr():"+mLastConectDevInfo.GetMacAddr());
							}

						}
						if(((RadioGroup) findViewById(R.id.rg_tab)).getCheckedRadioButtonId() == R.id.rb_devinfo)
						{
							devInfoFragment.UpdateDeviecs(mBleDevices);
						}
						if(mAddDeviceDialog != null)
							mAddDeviceDialog.UpdateDevicesList(mBleDevices);
					}
				}
			});
		}
	};

	private void AddDevice() {
		// TODO Auto-generated method stub
		// mBleDevices.clear();
		scanLeDevice(true);
		//test
		/*	mBleDevices.add(new BleDeviceInfo("SR-xxxx-1","6c:f0:49:89:12:fa"));
		mBleDevices.add(new BleDeviceInfo("SR-xxxxxx-21","6c:f0:49:89:12:fa"));
		mBleDevices.add(new BleDeviceInfo("SR-xxxxxx-3","6c:f0:49:89:12:fa"));
		mBleDevices.add(new BleDeviceInfo("SR-xxxxxx-5","6c:f0:49:89:12:fa"));
		 */

		mAddDeviceDialog.show();

	}
	private void CheckUpdate() {
		// TODO Auto-generated method stub
		mShowSoftVersionInfoDialog.show();
	}
	private void  CheckAdmin()
	{
		final EditText  pwdEditText = new EditText(this);
		pwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		new AlertDialog.Builder(this).setTitle(R.string.please_input_the_password).setIcon(
			     android.R.drawable.ic_dialog_info).setView(
			    		 pwdEditText).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
											@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								String pwd= pwdEditText.getText().toString();
								Log.d(TAG, "pwd:"+pwd);
							//	if(pwd.equals("135790123"))
								if(pwd.equals(SharePreferenceUtils.getString(MainActivity.this, SharePreferenceUtils.PREF_PUTTED_PWD, "135790123")))
								{
									mAdmin = true;
									Toast.makeText(MainActivity.this,getString(R.string.correct_password) , Toast.LENGTH_SHORT).show();
									if(paramSetFragment != null)
									{
										paramSetFragment.setAdmin(mAdmin);
									}
								}
								else
								{
									mAdmin = false;
									Toast.makeText(MainActivity.this,getString(R.string.incorrect_password) , Toast.LENGTH_SHORT).show();
									if(paramSetFragment != null)
									{
										paramSetFragment.setAdmin(mAdmin);
									}
								}
								
							}
						})
			     .setNegativeButton(R.string.cancel, null).show();
	}
	private void UpdatePassword()
	{

		LayoutInflater inflater = getLayoutInflater();
		   View layout = inflater.inflate(R.layout.update_password_dialog, (ViewGroup) findViewById(R.id.update_pwd_dialog));

			final EditText  pwdEditText = (EditText) layout.findViewById(R.id.et_oldpwd);//new EditText(this);
			pwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			final EditText  newPwdEditText =  (EditText) layout.findViewById(R.id.et_newpwd);//new EditText(this);
			newPwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			final EditText  affirmpwdEditText = (EditText) layout.findViewById(R.id.et_affirmpwd);//new EditText(this);
			affirmpwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			
			pwdEditText.setHint(R.string.old_password);
			newPwdEditText.setHint(R.string.new_password);
			affirmpwdEditText.setHint(R.string.reaffirm_new_password);
			
		   
		   
		   
		new AlertDialog.Builder(this).setTitle(R.string.update_password).setIcon(
			     android.R.drawable.ic_dialog_info).setView(layout).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
											@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								String pwd= pwdEditText.getText().toString();
								Log.d(TAG, "pwd:"+pwd);
							//	if(pwd.equals("135790123"))
								if(pwd.equals(SharePreferenceUtils.getString(MainActivity.this, SharePreferenceUtils.PREF_PUTTED_PWD, "135790123")))
								{
									mAdmin = true;
									//Toast.makeText(MainActivity.this,getString(R.string.correct_password) , Toast.LENGTH_SHORT).show();
									String newpwd= newPwdEditText.getText().toString();
									String affirmpwd= affirmpwdEditText.getText().toString();
									if(newpwd.equals(affirmpwd) && !affirmpwd.isEmpty())
									{
										SharePreferenceUtils.putString(MainActivity.this, SharePreferenceUtils.PREF_PUTTED_PWD,newpwd );
										Toast.makeText(MainActivity.this,getString(R.string.update_password_success) , Toast.LENGTH_SHORT).show();
									}
									else
									{
										Toast.makeText(MainActivity.this,getString(R.string.update_password_fail) , Toast.LENGTH_SHORT).show();
									}
								}
								else
								{
									mAdmin = false;
									Toast.makeText(MainActivity.this,getString(R.string.incorrect_password) , Toast.LENGTH_SHORT).show();
									if(paramSetFragment != null)
									{
										paramSetFragment.setAdmin(mAdmin);
									}
								}
								
							}
						})
			     .setNegativeButton(R.string.cancel, null).show();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
		case R.id.item_add_device:
			AddDevice();
			break;
		case R.id.item_check_update:
			CheckUpdate();
			break;

		default:
			break;
		}
		return true;

		//return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);


		return true;
	}

	private Handler mFragmentHandler = new Handler()
	{
		public void handleMessage(Message msg){ 
			//Log.d(TAG, "recv msg:"+msg.arg1);
			switch (msg.arg1) {  
			case DISCOVE_DEVICE_ID:
				SendUartData((byte [])msg.obj);
				break;
			case SEND_UART_DATA:
				if(mDeviceIdOld>0)
					SendUartData((byte [])msg.obj);
				break;
			case SEND_UART_STRING:
				SendUartString((String)msg.obj);
				break;
			case START_SCAN:
				scanLeDevice(true);
				break;
			case CONNECT_DEVICE:
				ConnectDevice((BleDeviceInfo)msg.obj);
				break;
			case DISCONNECT_DEVICE:
				DisConnectDevice();
				break;
			default:

				break;

			}  
		}
	};
	private void SendMessageToFragment(byte[] bs)
	{
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
		switch(radioGroup.getCheckedRadioButtonId())
		{
		case R.id.rb_monitoring:
			rtmFragment.OnRecvMessage(bs);
			break;
		case R.id.rb_historical_data:
			hisDataFragment.OnRecvMessage(bs);
			break;
		case R.id.rb_param_set:
			paramSetFragment.OnRecvMessage(bs);
			break;
		case R.id.rb_devinfo:
			devInfoFragment.OnRecvMessage(bs);
			break;
		default :break;
		}
	}

	private void RefreshData()
	{
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
		switch(radioGroup.getCheckedRadioButtonId())
		{
		case R.id.rb_monitoring:

			rtmFragment.RefreshData();
			break;
		case R.id.rb_historical_data:

			hisDataFragment.RefreshData();
			break;
		case R.id.rb_param_set:

			//paramSetFragment.RefreshData();
			break;
		case R.id.rb_devinfo:
			devInfoFragment.RefreshData();
			break;
		default :break;
		}
	}
	private void UpdateDeviceId(int id)
	{
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
		switch(radioGroup.getCheckedRadioButtonId())
		{
		case R.id.rb_monitoring:
			rtmFragment.setDeviceId(id);
			//rtmFragment.RefreshData();
			break;
		case R.id.rb_historical_data:
			hisDataFragment.setDeviceId(id);
			//hisDataFragment.RefreshData();
			break;
		case R.id.rb_param_set:
			paramSetFragment.setDeviceId(id);
			//paramSetFragment.RefreshData();
			break;
		case R.id.rb_devinfo:
			devInfoFragment.setDeviceId(id);
			//devInfoFragment.RefreshData();
			break;
		default :break;
		}
	}

	private class GetDeviceIdThread implements Runnable{  

		public void run() { 
			byte [] cmd=null;
			/*for(int id= 0x7f;id>=1;id--)
			{


				byte []bs = ModbusData.BuildReadRegsCmd(id, 0x001A,0x2);
				//if(id == 50)//test
				//	bs = ModbusData.BuildReadRegsCmd(1, 0x001A,0x2);
				Message msg = new Message();
				msg.arg1 = MainActivity.DISCOVE_DEVICE_ID;
				msg.obj = bs;
				mFragmentHandler.sendMessage(msg);
				Sleep(30);
				if(id%16==0)
					Sleep(1000);
				if(mDeviceId>0)
					return;
			}*/

			while(mDeviceIdOld<0 && mConnected)
			{
				//for(int id= 1;id<=0x7f;id++)
				{

 
//					byte []bs = ModbusData.BuildReadRegsCmd(0xfe, /*0x001A*/0x000C,2);
					byte []bs = ModbusData.BuildReadRegsCmd(0xff, /*0x001A*/0x000C,0x2);
					
					//if(id == 50)//test
//					byte []bs = ModbusData.BuildReadRegsCmd(1, 0x001A,0x2);
					Sleep(510);
					Message msg = new Message();
					msg.arg1 = MainActivity.DISCOVE_DEVICE_ID;
					msg.obj = bs;
					mFragmentHandler.sendMessage(msg);
					//Sleep(100);
					//if(id%16==0)
					Sleep(1000);
					if(mDeviceIdOld>0 || !mConnected)
					{
						return;
					}
				}
			}
		}

	};
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
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if(checkedId == R.id.rb_devinfo)
		{
			btnAdd.setVisibility(View.INVISIBLE);
		}
		else
		{
			btnAdd.setVisibility(View.VISIBLE);
		}
		switch (checkedId) {
		case R.id.rb_monitoring:
			if(tvTitle!=null)
				tvTitle.setText(R.string.real_time_monitoring);
			if(rtmFragment == null)
			{
				rtmFragment = new RealTimeMonitoringFragment();
				rtmFragment.SetHandler(mFragmentHandler);
			}
			if(mCurrentFrag != BaseFragment.REALTIMEMONITORING_FRAGM && !rtmFragment.isAdded())
			{
				transaction.replace(R.id.content, rtmFragment);
				transaction.commit();
			}
			mCurrentFrag = BaseFragment.REALTIMEMONITORING_FRAGM;
			break;
		case R.id.rb_historical_data:
			if(tvTitle!=null)
				tvTitle.setText(R.string.historical_data);
			if(hisDataFragment == null)
			{
				hisDataFragment = new HistoricalDataFragment();
				hisDataFragment.SetHandler(mFragmentHandler);
			}
			if(mCurrentFrag != BaseFragment.HISTORICAL_DATA_FRAGM && !hisDataFragment.isAdded())
			{
				transaction.replace(R.id.content, hisDataFragment);
				transaction.commit();
			}
			mCurrentFrag = BaseFragment.HISTORICAL_DATA_FRAGM;
			break;
		case R.id.rb_param_set:
			if(tvTitle!=null)
				tvTitle.setText(R.string.param_setting);
			if(paramSetFragment == null)
			{
				paramSetFragment = new ParamSettingFragment();
				paramSetFragment.SetHandler(mFragmentHandler);
			}
			if(mCurrentFrag != BaseFragment.PARAMSETTIN_FRAGM && !paramSetFragment.isAdded())
			{
				transaction.replace(R.id.content, paramSetFragment);
				transaction.commit();
			}
			paramSetFragment.setAdmin(mAdmin);
			mCurrentFrag = BaseFragment.PARAMSETTIN_FRAGM;

			break;
		case R.id.rb_devinfo:
			if(tvTitle!=null)
				tvTitle.setText(R.string.device_info);
			if(devInfoFragment == null)
			{
				devInfoFragment = new DeviceInfoFragment(mBleDevices);
				devInfoFragment.SetHandler(mFragmentHandler);
			}
			if(mCurrentFrag != BaseFragment.DEVICEINFO_FRAGM && !devInfoFragment.isAdded())
			{
				transaction.replace(R.id.content, devInfoFragment);
				transaction.commit();
			}
			mCurrentFrag = BaseFragment.DEVICEINFO_FRAGM;
			devInfoFragment.UpdateDeviecs(mBleDevices);
			break;

		default:
			break;

		}




		UpdateDeviceId(receiveDeviceId);

	}
	private void UpdateDeviceConnectStateDis(boolean connected)
	{
		if(devInfoFragment == null)
		{
			devInfoFragment = new DeviceInfoFragment(mBleDevices);
			devInfoFragment.SetHandler(mFragmentHandler);
		}
		devInfoFragment.UpdateDeviceConnectStateDis(connected);
	}

}
