package com.wcsmobile.fragment;

import java.util.ArrayList;

import com.wcsmobile.DBHelp;
import com.wcsmobile.R;
import com.wcsmobile.activity.MainActivity;
import com.wcsmobile.ble.BleDeviceInfo;
import com.wcsmobile.data.ModbusData;
import com.wcsmobile.data.ShourigfData;
import com.wcsmobile.dialog.AddDeviceDialog;
import com.wcsmobile.dialog.AddDeviceDialog.OnConfirmInterface;
import com.wcsmobile.dialog.InputDialog;
import com.wcsmobile.widget.PengButton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class DeviceInfoFragment extends BaseFragment {

    private static final String TAG = "DeviceInfoFragment";
    private TextView tv_device_id_value;
    private TextView tv_device_type_value;
    private TextView tv_device_version_value;
    private TextView tv_device_sn_code_value;
    private TextView tv_device_connect_state_value;
    private TextView tv_device_name_value;
    private TextView tv_device_name_change;
    private CheckBox switch_device_status;
    private PengButton btn_scan_device;
    private PengButton btn_disconnected_device;
    private PengButton btn_recovery_device;
    private AddDeviceDialog mAddDeviceDialog;
    private ArrayList<BleDeviceInfo> mBleDevices;
    private boolean mConnected = false;
    private LinearLayout disconnect_device_btn_layout;
    private LinearLayout recovery_device_btn_layout;
    private View mView = null;
    private final static int RESTORE_FACTORY_SUCCESS = 0x10001;
    private final static int RESTORE_FACTORY_FAILED = 0x10002;
    private String SHAREDPERNAME = "devices";
    //实时影藏ll_recovery_device按钮
    private LinearLayout ll_recovery_device;
    private InputDialog inputDialog;

    private void updateRecoveryDeviceClick(boolean connected) {
        if (connected) {
            if (ll_recovery_device != null && getActivity() != null && btn_recovery_device != null) {
                btn_recovery_device.setClickable(true);
                ll_recovery_device.setClickable(true);
                tv_device_name_value.setText(DBHelp.getInstance().getDeviceName(getActivity().getSharedPreferences(SHAREDPERNAME, Activity.MODE_PRIVATE).getString("LastDeviceName", "")));
                ll_recovery_device.setBackgroundColor(getActivity().getResources().getColor(R.color.app_background));
            }
        } else {
            if (ll_recovery_device != null && getActivity() != null && btn_recovery_device != null) {
                btn_recovery_device.setClickable(false);
                ll_recovery_device.setClickable(false);
                tv_device_name_value.setText(R.string.init_null_charter);
                ll_recovery_device.setBackgroundColor(getActivity().getResources().getColor(R.color.app_background_no));
            }
        }
    }

    public DeviceInfoFragment(ArrayList<BleDeviceInfo> BleDevices) {
        super();
        this.mBleDevices = BleDevices;
        mFragmentType = BaseFragment.DEVICEINFO_FRAGM;
    }

    public void UpdateDeviceConnectStateDis(boolean connected) {
        mConnected = connected;
        if (tv_device_connect_state_value == null) {
            return;
        }
        Log.d(TAG, "connected:" + connected);

        if (connected) {
            tv_device_connect_state_value.setText(R.string.device_connected);
            disconnect_device_btn_layout.setVisibility(View.VISIBLE);
        } else {
            tv_device_connect_state_value.setText(R.string.device_disconnected);
            disconnect_device_btn_layout.setVisibility(View.GONE);
        }
        updateRecoveryDeviceClick(connected);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (mView != null)
            return mView;
        View view = inflater.inflate(R.layout.fragment_device_info, container, false);
        InitView(view);
        mView = view;
        return view;//return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        new Thread(new RefreshThread()).start();
        UpdateDeviceConnectStateDis(mConnected);

    }

    private void InitView(View view) {
        ll_recovery_device = (LinearLayout) view.findViewById(R.id.recovery_device_btn_layout);
        // TODO Auto-generated method stub
        tv_device_id_value = (TextView) view.findViewById(R.id.tv_device_id_value);
        tv_device_type_value = (TextView) view.findViewById(R.id.tv_device_type_value);
        tv_device_version_value = (TextView) view.findViewById(R.id.tv_device_version_value);
        tv_device_sn_code_value = (TextView) view.findViewById(R.id.tv_device_sn_code_value);
        tv_device_connect_state_value = (TextView) view.findViewById(R.id.tv_device_connect_state_value);
        tv_device_name_value = (TextView) view.findViewById(R.id.tv_device_name_value);
        tv_device_name_change = (TextView) view.findViewById(R.id.tv_device_name_change);
        switch_device_status = (CheckBox) view.findViewById(R.id.switch_device_status);
        btn_scan_device = (PengButton) view.findViewById(R.id.btn_scan_device);
        btn_disconnected_device = (PengButton) view.findViewById(R.id.btn_disconnected_device);
        disconnect_device_btn_layout = (LinearLayout) view.findViewById(R.id.disconnect_device_btn_layout);
        recovery_device_btn_layout = (LinearLayout) view.findViewById(R.id.recovery_device_btn_layout);
        btn_recovery_device = (PengButton) view.findViewById(R.id.btn_recovery_device);
//		tv_device_id_value.setText(String.format("%03d", mDeviceId));
        tv_device_id_value.setText("FF");

        mAddDeviceDialog = new AddDeviceDialog(getActivity(), mBleDevices);
        inputDialog = new InputDialog(getActivity(), getActivity().getSharedPreferences(SHAREDPERNAME, Activity.MODE_PRIVATE).getString("LastDeviceName", ""), new InputDialog.OnSetName() {
            @Override
            public void onSucess() {
                tv_device_name_value.setText(DBHelp.getInstance().getDeviceName(getActivity().getSharedPreferences(SHAREDPERNAME, Activity.MODE_PRIVATE).getString("LastDeviceName", "")));

            }

            @Override
            public void onFaile() {
                tv_device_name_value.setText(DBHelp.getInstance().getDeviceName(getActivity().getSharedPreferences(SHAREDPERNAME, Activity.MODE_PRIVATE).getString("LastDeviceName", "")));
            }
        });

        mAddDeviceDialog.SetOnConfirmInterface(new OnConfirmInterface() {

            @Override
            public void doConfirm(BleDeviceInfo info) {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.arg1 = MainActivity.CONNECT_DEVICE;
                msg.obj = info;
                mHandler.sendMessage(msg);
            }

        });
        btn_disconnected_device.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG, "disconnect current device!");
                Message msg = new Message();
                msg.arg1 = MainActivity.DISCONNECT_DEVICE;
                mHandler.sendMessage(msg);
            }

        });
        btn_recovery_device.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG, "restore factory current device!");
                ShowRestoreDialog();
            }

        });
        btn_scan_device.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.arg1 = MainActivity.START_SCAN;
                mHandler.sendMessage(msg);
                mAddDeviceDialog.show();
            }

        });
        tv_device_name_change.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mConnected) {
                   inputDialog.setName(getActivity().getSharedPreferences(SHAREDPERNAME, Activity.MODE_PRIVATE).getString("LastDeviceName", ""));
                    inputDialog.show();
                } else {
                    Toast.makeText(getActivity(), "设备未连接", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void UpdateDeviecs(ArrayList<BleDeviceInfo> BleDevices) {
        // TODO Auto-generated method stub
        mBleDevices = BleDevices;
        if (mAddDeviceDialog != null && mAddDeviceDialog.isShowing())
            mAddDeviceDialog.UpdateDevicesList(mBleDevices);
    }

    private void ShowRestoreDialog() {

        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setMessage(getString(R.string.recovery_device));
        builder.setTitle(getString(R.string.prompt));
        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        builder.setNegativeButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new RestoreFactoryThread()).start();
                dialog.dismiss();
            }


        });
        builder.create().show();
    }

    @Override
    protected boolean PostRecvMessage() {
        //Log.d(TAG, "super.PostRecvMessage");
        if (super.PostRecvMessage() == false)
            return false;
        byte[] bs = mUartRecvData;

        Log.d(TAG, "PostRecvMessage");

        String msg = "recv data";
        if (bs == null) {
            return false;
        }
        for (int i = 0; i < bs.length; i++) {
            msg += String.format("[%02x] ", bs[i]);
        }
        Log.d(TAG, msg);

        if (bs.length < 3)
            return false;
        if (bs[1] == 0x78 && bs.length == 8)
            return true;//restore factory
        int b = (0xff) & bs[2];
        if (mReadingCount * 2 != b)
            return false;

        Message message = new Message();
        message.arg1 = mReadingRegId;
        message.obj = bs;
        mMessageHandler.sendMessage(message);
        //mReadingRegId = 0;

        return true;

    }

    private Handler mMessageHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(TAG, "mIsActive:" + mIsActive + ",msg.arg1=" + msg.arg1);
            if (!mIsActive)
                return;
            byte[] bs = (byte[]) msg.obj;
            switch (msg.arg1) {
                case ShourigfData.DeviceProductType.REG_ADDR:
                    UpdateDeviceProductType(bs);
                    break;
                case ShourigfData.DeviceVersionInfo.REG_ADDR:
                    UpdateDeviceVersionInfo(bs);
                    break;
                case ShourigfData.DeviceSerialNumber.REG_ADDR:
                    UpdateDeviceSerialNumber(bs);
                    break;
                case RESTORE_FACTORY_SUCCESS:
                    Toast.makeText(getActivity(), getString(R.string.restore_factory_success), Toast.LENGTH_SHORT).show();
                    break;
                case RESTORE_FACTORY_FAILED:
                    Toast.makeText(getActivity(), getString(R.string.restore_factory_failed), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }


    };

    public void RefreshData() {

        new Thread(new RefreshThread()).start();
    }

    private class RestoreFactoryThread implements Runnable {

        public void run() {
            while (mRefreshThreadRuning) ;
            ClearRecvFifo();
            Sleep(200);
            SendUartData(ModbusData.BuildRestoreFactoryCmd(mDeviceId));
            if (WaitPostMessage(1000) == true) {
                Message message = new Message();
                message.arg1 = RESTORE_FACTORY_SUCCESS;
                mMessageHandler.sendMessage(message);
            } else {
                Message message = new Message();
                message.arg1 = RESTORE_FACTORY_FAILED;
                mMessageHandler.sendMessage(message);
            }
        }
    }

    private class RefreshThread implements Runnable {

        public void run() {
            if (mRefreshThreadRuning)
                return;
            mRefreshThreadRuning = true;
            int times = 0;
            boolean ok = true;
            Log.d(TAG, "Enter thread!...");
            while (true) {
                ClearRecvFifo();
                Sleep(200);
                mReadingRegId = ShourigfData.DeviceProductType.REG_ADDR;
                mReadingCount = ShourigfData.DeviceProductType.READ_WORD;
                SendUartData(ModbusData.BuildReadRegsCmd(mDeviceId, mReadingRegId, mReadingCount));
                Sleep(200);
                ok = ok && WaitPostMessage(2000);

                ClearRecvFifo();
                Sleep(200);
                mReadingRegId = ShourigfData.DeviceVersionInfo.REG_ADDR;
                mReadingCount = ShourigfData.DeviceVersionInfo.READ_WORD;
                SendUartData(ModbusData.BuildReadRegsCmd(mDeviceId, mReadingRegId, mReadingCount));
                Sleep(200);
                ok = ok && WaitPostMessage(2000);

                ClearRecvFifo();
                Sleep(200);
                mReadingRegId = ShourigfData.DeviceSerialNumber.REG_ADDR;
                mReadingCount = ShourigfData.DeviceSerialNumber.READ_WORD;
                SendUartData(ModbusData.BuildReadRegsCmd(mDeviceId, mReadingRegId, mReadingCount));
                Sleep(200);
                ok = ok && WaitPostMessage(2000);
                Log.d(TAG, "ok=" + ok);
			/*	if(ok || (times>=2))
					break;
				Sleep(1000);
				times++;*/
                if (ok)
                    break;
                Sleep(1000);
                if (!mIsActive)
                    break;

            }
            Log.d(TAG, "--exit thread");
            mRefreshThreadRuning = false;

        }
    }

    protected void UpdateDeviceProductType(byte[] bs) {
        // TODO Auto-generated method stub
        ShourigfData.DeviceProductType type = new ShourigfData.DeviceProductType(bs);
        Log.d(TAG, "ProductTypeStr[" + type.mProductTypeStr + "]");
        tv_device_type_value.setText(type.mProductTypeStr);
    }

    protected void UpdateDeviceSerialNumber(byte[] bs) {
        // TODO Auto-generated method stub
        ShourigfData.DeviceSerialNumber sn = new ShourigfData.DeviceSerialNumber(bs);
        Log.d(TAG, "sn[" + sn.mSN + "]");
        tv_device_sn_code_value.setText(sn.mSN);
        tv_device_id_value.setText(String.format("%03d", sn.mDeviceAddr));
        //	tv_device_id_value.setText("FF");//barack del 170814
    }

    protected void UpdateDeviceVersionInfo(byte[] bs) {
        // TODO Auto-generated method stub

        ShourigfData.DeviceVersionInfo ver_info = new ShourigfData.DeviceVersionInfo(bs);
        Log.d(TAG, "SoftVersion[" + ver_info.mSoftVersion + "]");
        tv_device_version_value.setText(ver_info.mSoftVersion);
    }
}
