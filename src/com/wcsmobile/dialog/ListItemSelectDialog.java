package com.wcsmobile.dialog;



import com.wcsmobile.R;
import com.wcsmobile.adapter.ListItemInfoAdapter;
import com.wcsmobile.ble.BleDeviceInfo;


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
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ListItemSelectDialog extends AlertDialog {
	private String TAG="ListItemSelectDialog";
	private Context mContext;
	private String [] mData;
	private OnConfirmInterface mOnConfirmInterface;
    private ListView mListView;
    private ListItemInfoAdapter mInfoAdapter;
	public ListItemSelectDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public ListItemSelectDialog(Context context, String [] data) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mData = data;
	}
	public interface OnConfirmInterface {
		public void doConfirm(String selStr);

	}
	public void SetOnConfirmInterface(OnConfirmInterface onConfirmInterface) {
		this.mOnConfirmInterface = onConfirmInterface;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Create...");
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_list_item_select, null);
		setContentView(view);
		mInfoAdapter = new ListItemInfoAdapter(mContext,mData);
		mListView = (ListView)view.findViewById(R.id.lvInfo);
		mListView.setAdapter(mInfoAdapter);
		mListView.setOnItemClickListener(listOnItemClickListener);



		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = mContext.getResources().getDisplayMetrics(); 
		lp.width = (int) (d.widthPixels * 0.9); 

		dialogWindow.setAttributes(lp);
	}
	private OnItemClickListener listOnItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			mOnConfirmInterface.doConfirm(mData[arg2]);
			dismiss();
			
		}};

}
