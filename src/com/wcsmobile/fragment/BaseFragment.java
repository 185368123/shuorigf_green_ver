package com.wcsmobile.fragment;



import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.wcsmobile.activity.MainActivity;
import com.wcsmobile.ble.BluetoothLeService;
import com.wcsmobile.data.ModbusData;
import com.wcsmobile.data.ShourigfData;


import android.R;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class BaseFragment extends Fragment {
	private static final String TAG = "BaseFragment";
	protected Handler mHandler;	
	protected byte [] mUartRecvData;
	private byte []mUartRecvFifo;
	protected int mReadingRegId=0;
	protected int mReadingCount=1;
	protected int mDeviceId = -1;
	protected boolean mIsActive = false;
	protected boolean mRefreshThreadRuning=false;
	private ProgressDialog progressDialog = null;
	private static final int STOP_REFRESH= 0;
	private static final int START_REFRESH =1;
	private boolean mClearFifo=true;
	public final static int DEVICEINFO_FRAGM = 4;
	public final static int PARAMSETTIN_FRAGM=3;
	public final static int HISTORICAL_DATA_FRAGM=2;
	public final static int REALTIMEMONITORING_FRAGM=1;
	public int mFragmentType=-1;
	private ReadWriteLock myLock = null;

	public void setDeviceId(int id)
	{
		mDeviceId = id;
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onPause:"+this);
		mIsActive = false;
		super.onPause();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume:"+this);
		mIsActive = true;

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(TAG, "onStart:"+this);
		myLock =null;
		myLock = new ReentrantReadWriteLock(false);
		mIsActive = true;
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onStop:"+this);
		mIsActive = false;
		super.onStop();
	}
	
	public void SetHandler(Handler handler)
	{
		mHandler = handler;
	}
	
	protected void SendUartData(byte [] bs)
	{
		if(!mIsActive)
		{
			return;
		}
		Message msg = new Message();
		msg.arg1 = MainActivity.SEND_UART_DATA;
		msg.obj = bs;
		mHandler.sendMessage(msg);
	}
	protected void SendUartString(String str)
	{
		Message msg = new Message();
		msg.arg1 = MainActivity.SEND_UART_STRING;
		msg.obj = str;
		mHandler.sendMessage(msg);
	}
	public void OnRecvMessage(byte[] bs)
	{
		byte[] temp_data = null;
		if(bs == null || bs.length == 0 || !mIsActive)
		{
			return;
		}
		if(mClearFifo || mUartRecvFifo == null)
		{
			myLock.writeLock().lock();  
			mUartRecvFifo =bs;
			myLock.writeLock().unlock();  
			//temp_data = bs;
			mClearFifo = false;
		}
		else 
		{
			//temp_data =null;
			temp_data = new byte[bs.length + mUartRecvFifo.length];
			System.arraycopy(mUartRecvFifo,0,temp_data,0,mUartRecvFifo.length);
			System.arraycopy(bs,0,temp_data,mUartRecvFifo.length,bs.length);
			myLock.writeLock().lock();  
			mUartRecvFifo = temp_data;
			myLock.writeLock().unlock();  
		}

		String msg="recv data";
		for(int i=0;i<mUartRecvFifo.length;i++)
		{
			msg+=String.format("[%02x] ", mUartRecvFifo[i]);
		}
		Log.d(TAG, msg);
		boolean retb= false;
		switch(mFragmentType)
		{
		case DEVICEINFO_FRAGM:
		case PARAMSETTIN_FRAGM:
		case HISTORICAL_DATA_FRAGM:
			retb = PostRecvMessage();
			break;
			default:break;
		}
		
		if(retb)
		{
			mClearFifo = true;
		}

	}

	protected void ClearRecvFifo()
	{
		//mUartRecvFifo = null;
		mClearFifo = true;
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
	
	protected boolean PostRecvMessage()
	{
		//Log.d(TAG, this+"mIsActive:"+mIsActive+",mUartRecvFifo:"+mUartRecvFifo);
		if(mUartRecvFifo == null || !mIsActive )
			return false;
		myLock.readLock().lock();
		mUartRecvData = null;
		mUartRecvData = new byte[mUartRecvFifo.length];
		System.arraycopy(mUartRecvFifo,0,mUartRecvData,0,mUartRecvFifo.length);
		myLock.readLock().unlock();  
		//Log.d(TAG, this+"bs:"+bs);
		/*String msg="DataCrcCorrect:";
		for(int i=0;i<mUartRecvData.length;i++)
		{
			msg+=String.format("[%02x] ", mUartRecvData[i]);
		}
		Log.d(TAG, msg);*/
		if(mUartRecvData==null || !ModbusData.DataCrcCorrect(mUartRecvData))
		{

			Log.d(TAG, this+"DataCrcCorrect failed!");

			return false;
		}
		
		Log.d(TAG, this+"DataCrcCorrect success!");

		return true;
	}
	
	protected boolean WaitPostMessage(int timerout )
	{
//		timerout = timerout *2;
		timerout = timerout/10;
		while(!PostRecvMessage() && timerout-- !=0)
		{
			if(!mIsActive)
			{
				return false;
			}
			Sleep(10);
		}
		Log.d(TAG, this+"timerout:"+timerout);
		if(timerout <= 0)
		{
			return false;
		}
		return true;
		
	}
	protected void StartLoopRecvOneMessage(int timeout)
	{
		new Thread(new RecvOneMessageThread(timeout)).start();  
	}
	
	private class RecvOneMessageThread implements Runnable{  
		
		private int mTimerout = 2000;
		public RecvOneMessageThread(int timeout) {
			// TODO Auto-generated constructor stub
			super();
			mTimerout = timeout;
		}

		public void run() {  
            Message msgMessage=new Message();  
            msgMessage.arg1=START_REFRESH;  
            mRecvOneMessageThreadHandler.sendMessage(msgMessage);  
            
  
				WaitPostMessage(mTimerout);
			Log.d(TAG, "exit thread");

        	Message msgMessage1=new Message();  
        	msgMessage1.arg1=STOP_REFRESH;  
        	mRecvOneMessageThreadHandler.sendMessage(msgMessage1);
		}
	}
	private Handler mRecvOneMessageThreadHandler=new Handler()
	{
	      public void handleMessage(Message msg){  
	            switch (msg.arg1) {  
	            case STOP_REFRESH:
	        //    	progressDialog.dismiss(); 
	                break;  
	            case START_REFRESH:  
	         //   	progressDialog = ProgressDialog.show(getActivity(), "", "", true, false);
	    			break;

	            default:  
	                break;  
	            }  
	        }  
	};
}
