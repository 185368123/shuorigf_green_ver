package com.wcsmobile.fragment;



import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.wcsmobile.R;
import com.wcsmobile.activity.MainActivity;
import com.wcsmobile.data.ModbusData;
import com.wcsmobile.data.ParamSettingDataStruct;
import com.wcsmobile.data.ShourigfData;
import com.wcsmobile.dialog.ListItemSelectDialog;
import com.wcsmobile.dialog.ListItemSelectDialog.OnConfirmInterface;
import com.wcsmobile.widget.SegmentedGroup;
import com.wcsmobile.widget.SegmentedRadioGroup;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ParamSettingFragment extends BaseFragment {
	public final static String TAG="ParamSettingFragment";
	public static final int VIEW_BATTERYPARAM = 0;
	public static final int VIEW_LOADPARAM = 1;
	protected static final int DEVICE_PARAM_SET_SUCCESS = 0x1100;
	protected static final int DEVICE_PARAM_SET_FAILED = 0x1101;
	protected static final int DEVICE_PARAM_SET_NOTHING =0x1102;
	protected static final int DEVICE_PARAM_GET_SUCCESS = 0x1103;
	protected static final int DEVICE_PARAM_GET_FAILED = 0x1104;
	private ViewFlipper viewFliper = null;
	private View viewBatteryParam;
	private View viewLoadParam;
	private int [] mParamData = null;
	private Map<String,String> mModifyIndex;
	private TextWatcher mTextWatcher[]=null;
	private boolean mDoRead = true;
	private boolean mFromReadButton=false;
	private String [] mArrayBatteryType=null;
	private String [] mArrayLoadOperatingMode=null;
	private View mView =null;
	private Toast mSuccessToast =null;
	private Toast mFailedToast = null;
	private Toast mNothingToast = null;
	private Toast mParamRaedSuccessToast= null;
	private Toast mParamRaedFailedToast= null;
	private String mAutomaticRecognitionStr="";
	private boolean mAdmin=false;
	private SharedPreferences mSharedPreferences;
	private String SHAREDPERNAME ="param";
	public void setAdmin(boolean en)
	{
		mAdmin = en;
		onSetAdmin();
	}
	private void onSetAdmin()
	{
		if(mView == null)
		{
			return;
		}
		if(mAdmin == true)
		{
			//	((Button)mView.findViewById(R.id.btn_setting)).setEnabled(true);
		}
		else
		{
			//	((Button)mView.findViewById(R.id.btn_setting)).setEnabled(false);
		}
		enableParamSet(mAdmin);

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onCreateView:"+this+mView);
		mFragmentType = PARAMSETTIN_FRAGM;
		if(mView != null)
			return mView;
		mAutomaticRecognitionStr = getString(R.string.automatic_recognition);
		View view = inflater.inflate(R.layout.fragment_param_setting, container,false);
		viewBatteryParam =  inflater.inflate(R.layout.view_battery_parameters, null);
		viewLoadParam = inflater.inflate(R.layout.view_load_parameters, null);
		mArrayBatteryType = new String[]{getString(R.string.battery_type_val00h),
				getString(R.string.battery_type_val01h),getString(R.string.battery_type_val02h),
				getString(R.string.battery_type_val03h),getString(R.string.battery_type_val04h),};
		mArrayLoadOperatingMode = new String[]{getString(R.string.load_operating_mode_val00h),
				getString(R.string.load_operating_mode_val01h),getString(R.string.load_operating_mode_val02h),
				getString(R.string.load_operating_mode_val03h),getString(R.string.load_operating_mode_val04h),
				getString(R.string.load_operating_mode_val05h),getString(R.string.load_operating_mode_val06h),
				getString(R.string.load_operating_mode_val07h),getString(R.string.load_operating_mode_val08h),
				getString(R.string.load_operating_mode_val09h),getString(R.string.load_operating_mode_val0ah),
				getString(R.string.load_operating_mode_val0bh),getString(R.string.load_operating_mode_val0ch),
				getString(R.string.load_operating_mode_val0dh),getString(R.string.load_operating_mode_val0eh),
				getString(R.string.load_operating_mode_val0fh),getString(R.string.load_operating_mode_val10h),
				getString(R.string.load_operating_mode_val11h),};
		InitView(view);
		mView = view;
		mFailedToast = Toast.makeText(getActivity(), getString(R.string.device_param_setting_failed), Toast.LENGTH_SHORT);
		mSuccessToast = Toast.makeText(getActivity(), getString(R.string.device_param_setting_success), Toast.LENGTH_SHORT);
		mNothingToast = Toast.makeText(getActivity(), getString(R.string.device_param_setting_nothing), Toast.LENGTH_SHORT);
		mParamRaedSuccessToast = Toast.makeText(getActivity(), getString(R.string.device_param_getting_success), Toast.LENGTH_SHORT);
		mParamRaedFailedToast = Toast.makeText(getActivity(), getString(R.string.device_param_getting_failed), Toast.LENGTH_SHORT);
		onSetAdmin();
		mSharedPreferences = getActivity().getSharedPreferences(SHAREDPERNAME, Activity.MODE_PRIVATE); 
		readParam();
		return view;//return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//mDoRead = true;
		//new Thread(new RefreshThread()).start();  

	}

	private void InitView(View view) {
		// TODO Auto-generated method stub
		viewFliper = (ViewFlipper) view.findViewById(R.id.viewFlipper);
		viewFliper.addView(viewBatteryParam);
		viewFliper.addView(viewLoadParam);



		SegmentedGroup RadioGroup = (SegmentedGroup)view.findViewById(R.id.segment_Group);
		RadioGroup.setOnCheckedChangeListener(CheckedChangeListener);
		view.findViewById(R.id.rdbtn_battery_parameters).performClick();

		((Button)view.findViewById(R.id.btn_read)).setOnClickListener(clickListener);
		((Button)view.findViewById(R.id.btn_setting)).setOnClickListener(clickListener);

		mTextWatcher = new TextWatcher[ParamSettingDataStruct.ID.length];
		mModifyIndex = new  HashMap <String,String>();
		for(int i=0;i<ParamSettingDataStruct.ID.length;i++)
		{
			EditText editText = null;
			if(ParamSettingDataStruct.ID[i] < 0)
				continue;
			editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[i]);
			if(editText == null)
			{
				editText = (EditText)viewLoadParam.findViewById(ParamSettingDataStruct.ID[i]);

			}
			if(editText == null)
			{
				continue;
			}
			mTextWatcher[i] = CreateTextWatcher(i);
			editText.addTextChangedListener(mTextWatcher[i]);
			editText.setOnFocusChangeListener(editOnFocusChangeListener);

			if(i== ParamSettingDataStruct.BATTERY_TYPE_OFFSET ||  i== ParamSettingDataStruct.LOAD_OPERATIN_MODE_OFFSET
					||  i== ParamSettingDataStruct.SYSTEM_VOLTAGE_OFFSET)
			{
				editText.setFocusable(false);
				editText.setOnClickListener(clickListener);

			}
			/*editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View arg0, boolean arg1) {
					// TODO Auto-generated method stub
					 if(arg1) {

						} else {
							EditText edit = (EditText)arg0;
							edit.setText(float_str(""+Float.parseFloat(edit.getText().toString())));
						}
				}
			});*/
		}

	}
	private void ClearAllEditTextFocus()
	{
		for(int i=0;i<ParamSettingDataStruct.ID.length;i++)
		{
			EditText editText = null;
			if(ParamSettingDataStruct.ID[i] < 0)
				continue;
			editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[i]);
			if(editText == null)
			{
				editText = (EditText)viewLoadParam.findViewById(ParamSettingDataStruct.ID[i]);

			}
			if(editText == null)
			{
				continue;
			}
			editText.clearFocus();
			//editText.setFocusable(false);
		}
	}

	private void EditTextLimitFloatCheck(EditText editText,float min,float max)
	{
		float f = 0.0f;  
		try {  
			f = Float.parseFloat(editText.getText().toString());  
		} catch (NumberFormatException e) {  
			// TODO Auto-generated catch block  
			f = 0.0f;  
		}
		int fint = (int)(f*1000.f);
		f = (float)fint /1000.f;
		if (f > max)  
		{
			editText.setText(max+"");						

		}
		else if(f< min )
		{
			editText.setText(min+"");					

		}
	}

	private void EditTextLimitIntegerCheck(EditText editText,int min,int max)
	{
		int i = 0;
		try {  
			i = Integer.parseInt(editText.getText().toString());  
		} catch (NumberFormatException e) {  
			// TODO Auto-generated catch block  
			i = 0;  
		}  
		if (i > max)  
		{
			editText.setText(max+"");					

		}
		else if(i < min )
		{
			editText.setText(min+"");					
		}
	}
	private OnFocusChangeListener editOnFocusChangeListener = new OnFocusChangeListener()
	{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			Log.d(TAG,"id:"+v.getId()+",focus:"+hasFocus);
			EditText editText = (EditText)v;
			for(int i=ParamSettingDataStruct.OVER_VOLTAGE_VOLTAGE_OFFSET;
					i<=ParamSettingDataStruct.DISCHARGE_LIMIT_VOLTAGE_OFFSET;i++)
			{//
				if(v.getId() == ParamSettingDataStruct.ID[i])
				{
					EditTextLimitFloatCheck(editText,7.0f,17.0f);
					return;
				}
			}
			switch(v.getId())
			{
			case R.id.tv_parameter_over_discharge_delay_time_set_value:
				EditTextLimitIntegerCheck(editText,0,120);
				break;
			case R.id.tv_parameter_equalization_charging_time_set_value:
				EditTextLimitIntegerCheck(editText,0,300);
				break;
			case R.id.tv_parameter_promote_charging_time_set_value:
				EditTextLimitIntegerCheck(editText,10,300);
				break;
			case R.id.tv_parameter_equilibrium_charge_interval_set_value:
				EditTextLimitIntegerCheck(editText,0,255);
				break;
			case R.id.tv_parameter_temperature_compensation_coefficient_set_value:
				EditTextLimitIntegerCheck(editText,0,5);
				break;

			case R.id.tv_parameter_first_working_time_set_value:
			case R.id.tv_parameter_second_working_time_set_value:
			case R.id.tv_parameter_third_working_time_set_value:
			case R.id.tv_parameter_morning_working_time_set_value:
				EditTextLimitIntegerCheck(editText,0,0x15);
				break;	
			case R.id.tv_parameter_first_working_power_set_value:
			case R.id.tv_parameter_second_working_power_set_value:
			case R.id.tv_parameter_third_working_power_set_value:
			case R.id.tv_parameter_morning_working_power_set_value:
				EditTextLimitIntegerCheck(editText,0,100);
				break;	
			case R.id.tv_parameter_optical_control_delay_time_set_value:
				EditTextLimitIntegerCheck(editText,0,60);
				break;	
			case R.id.tv_parameter_optical_control_voltage_set_value:
				EditTextLimitIntegerCheck(editText,1,40);
				break;	
			default :
				break;
			}
		}

	};
	
	private String float_str(String str )
	{

		if (str.contains(".")) {
			int index = str.indexOf(".");
			String ret = str;
			if (index + 4 < str.length()) {
				 ret = str.substring(0, index + 4);

			}
			if( ret.length() >2 && ret.substring(ret.length()-1).equals("0") && !ret.substring(ret.length()-2,ret.length()-1).equals(".") )
			{
				return float_str(ret.substring(0, ret.length()-1));
			}


		}
		return str;
	}
	
	private void updateDefaulteValue(int index,String str)
	{
		int decindex =0;
		String bat_type = ((EditText)viewBatteryParam.findViewById(R.id.tv_parameter_battery_type_set_value))
				.getText().toString();

		if(bat_type.toString().equals(getString(R.string.battery_type_val04h)))//LI
		{

			if(ParamSettingDataStruct.LEFT_CHARGE_VOLTAGE_OFFSET == ParamSettingDataStruct.DATA_OFFSET[index])
			{

				
				EditText editText1 = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[index-1]);

				if(editText1 == null)
				{
					return;
				}
				editText1.setText(str);
				decindex = index-1;
				mModifyIndex.put(decindex+"", str);
				editText1 = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[index+1]);

				if(editText1 == null)
				{
					return;
				}
				editText1.setText(str);
				decindex = index+1;
				mModifyIndex.put(decindex+"", str);
				editText1 = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[index+2]);

				if(editText1 == null)
				{
					return;
				}
				if(str.length() >1)
				{
					editText1.setText(float_str(String.valueOf(Float.parseFloat(str) -1.2f)));
					decindex = index+2;
					mModifyIndex.put(decindex+"", editText1.getText().toString());
				}
				
			//	mModifyIndex.put(index+1+"", str);
			//	mModifyIndex.put(index-1+"", str);
			}
			else if(ParamSettingDataStruct.OVER_DISCHARGE_VOLTAGE_OFFSET == ParamSettingDataStruct.DATA_OFFSET[index])
			{
				if(str.length() >1)
				{
					((EditText)viewBatteryParam.findViewById(R.id.tv_parameter_warning_under_voltage_set_value)).setText(float_str(String.valueOf(Float.parseFloat(str) +1.0f)));
				}
				if(str.length() >1)
				{
					((EditText)viewBatteryParam.findViewById(R.id.tv_parameter_discharge_limit_voltage_set_value)).setText(float_str(String.valueOf(Float.parseFloat(str) -0.5f)));
				}
			}
			
		}
	}

	private TextWatcher CreateTextWatcher(int index)
	{
		final int index_i = index;
		return new TextWatcher()
		{

			private int mIndex = index_i;
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				//Log.d(TAG, "mIndex:"+mIndex);

				//mModifyIndex.remove(mIndex+"");

				//		Log.d(TAG,"afterTextChanged");
				mModifyIndex.put(mIndex+"", s.toString());
				updateDefaulteValue(mIndex,s.toString());
		/*		EditText editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[index_i]);
				if(editText == null)
				{
					editText = (EditText)viewLoadParam.findViewById(ParamSettingDataStruct.ID[index_i]);

				}
				if(editText == null)
				{
					return;
				}
				String text = s.toString();
				if(text.substring(text.length()-1).equals("."))
					editText.setText(float_str(text));*/
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				//	Log.d(TAG,"beforeTextChanged");
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				//Log.d(TAG,"onTextChanged");
				EditText editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[index_i]);
				if(editText == null)
				{
					editText = (EditText)viewLoadParam.findViewById(ParamSettingDataStruct.ID[index_i]);

				}
				if(editText == null)
				{
					return;
				}
				String text = s.toString();
				updateDefaulteValue(index_i,s.toString());
				if (text.contains(".")) {
					int index = text.indexOf(".");
					if (index + 4 < text.length()) {
						text = text.substring(0, index + 4);
						editText.setText(text);
						editText.setSelection(text.length());
					}
				}
			}

		};
	}

	private OnClickListener clickListener =  new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ClearAllEditTextFocus();


			if(v.getId() == R.id.btn_read)
			{
				mDoRead = true;
				mFromReadButton = true;
				new Thread(new RefreshThread()).start();  
			}else if(!mAdmin)
			{
				ShowDialog(getString(R.string.please_get_admin));
			}
			else if(v.getId() == R.id.btn_setting)
			{//do setting
				if(mAdmin)
				{

					if(saveParam())
					{
						mDoRead = false;
						new Thread(new RefreshThread()).start();  
					}
				}
				else
				{
					ShowDialog(getString(R.string.please_get_admin));
				}

			}
			else if(v.getId() == R.id.tv_parameter_battery_type_set_value)
			{
				ParameterBatteryTypeSet();
			}
			else if(v.getId() == R.id.tv_parameter_load_operating_mode_set_value)
			{
				ParameterLoadOperatingModeSet();
			}
			else if(v.getId() ==  R.id.tv_parameter_set_system_voltage_set_value)
			{
				ParameterSystemVoltageSet();
			}
		}

	};

	private OnCheckedChangeListener CheckedChangeListener = new OnCheckedChangeListener()
	{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			switch(checkedId)
			{
			case R.id.rdbtn_battery_parameters:
				viewFliper.setDisplayedChild(VIEW_BATTERYPARAM);
				break;
			case R.id.rdbtn_load_parameters:
				viewFliper.setDisplayedChild(VIEW_LOADPARAM);
				break;
			default:
				break;
			}
		}

	};
	
	private int doSettingDataEx()//add 20180603
	{
		boolean ok=true;
		boolean seted = false;
		for(int i=ParamSettingDataStruct.OVER_VOLTAGE_VOLTAGE_OFFSET;
				i<=ParamSettingDataStruct.TIMEPERATURE_COMPENSTATION_COEFFICIENT_OFFSET;i++)
		{
			seted = true; 
			EditText editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[i]);
			if(editText == null )
			{
				continue;
			}

			String str = editText.getText().toString();
			
			ClearRecvFifo();
			int offset = i;
			int val = 0;
			if(offset == ParamSettingDataStruct.BATTERY_TYPE_OFFSET)
			{

				val = findStringFromStringArray(mArrayBatteryType,str);
				if(val < 0)
				{
					val = 0;
				}
			}
			else if(offset == ParamSettingDataStruct.LOAD_OPERATIN_MODE_OFFSET)
			{
				val = findStringFromStringArray(mArrayLoadOperatingMode,str);
				if(val < 0)
				{
					val = 0;
				}
			}
			else if(offset>= ParamSettingDataStruct.OVER_VOLTAGE_VOLTAGE_OFFSET && offset<=ParamSettingDataStruct.DISCHARGE_LIMIT_VOLTAGE_OFFSET)
			{
				val =(int) (Float.parseFloat(str)*10); 
			}
			else if(offset == ParamSettingDataStruct.SYSTEM_VOLTAGE_OFFSET)
			{
				if(mAutomaticRecognitionStr.equals(str))
					val = 0xff00;
				else
					val = Integer.parseInt(str) << 8;
			}
			else
			{
				if(!str.equals("-"))
				{
					val = Integer.parseInt(str);
				}
			}
			mReadingRegId = -1;
			SendUartData(ModbusData.BuildWriteRegCmd(mDeviceId, ShourigfData.ParamSettingData.REG_ADDR+
					offset,val));

			Sleep(100);
			boolean bret = WaitPostMessage(1000);
			ok = ok && bret;
			Log.d(TAG, "ok:"+ok+",bret:"+bret);

		}
		Message message = new Message();

		if(ok)
		{
			message.arg1 = DEVICE_PARAM_SET_SUCCESS;
			mModifyIndex.clear();
		}
		else
		{
			message.arg1 = DEVICE_PARAM_SET_FAILED;			
		}
		if(!seted)
		{
			message.arg1 = DEVICE_PARAM_SET_NOTHING;
		}
		mUiHandler.sendMessage(message);
		return message.arg1;

	}
	
	private int doSettingData()
	{
		boolean ok=true;
		boolean seted = false;
	/*	if(mModifyIndex.size() > 0)//add 2018 0603
		{
			return doSettingDataEx();
			
		}*/
		for(int i= 0;i<ParamSettingDataStruct.OPTICAL_CONTROL_VOLTAGE_OFFSET;i++)
		{
		
			for (Map.Entry<String, String> entry : mModifyIndex.entrySet()) {  //fixed me bug
				Log.d(TAG,"key= " + entry.getKey() + " and value= " + entry.getValue());  
				seted = true;
				ClearRecvFifo();
				int offset = Integer.parseInt(entry.getKey());
				int val = 0;
				if(i  != offset)
					continue;
				if(offset == ParamSettingDataStruct.BATTERY_TYPE_OFFSET)
				{

					val = findStringFromStringArray(mArrayBatteryType,entry.getValue());
					if(val < 0)
					{
						val = 0;
					}
				}
				else if(offset == ParamSettingDataStruct.LOAD_OPERATIN_MODE_OFFSET)
				{
					val = findStringFromStringArray(mArrayLoadOperatingMode,entry.getValue());
					if(val < 0)
					{
						val = 0;
					}
				}
				else if(offset>= ParamSettingDataStruct.OVER_VOLTAGE_VOLTAGE_OFFSET && offset<=ParamSettingDataStruct.DISCHARGE_LIMIT_VOLTAGE_OFFSET)
				{
					val =(int) (Float.parseFloat(entry.getValue())*10); 
				}
				else if(offset == ParamSettingDataStruct.SYSTEM_VOLTAGE_OFFSET)
				{
					if(mAutomaticRecognitionStr.equals(entry.getValue()))
						val = 0xff00;
					else
						val = Integer.parseInt(entry.getValue()) << 8;
				}
				else
				{
					if(!entry.getValue().equals("-"))
					{
						val = Integer.parseInt(entry.getValue());
					}
				}
				mReadingRegId = -1;
				SendUartData(ModbusData.BuildWriteRegCmd(mDeviceId, ShourigfData.ParamSettingData.REG_ADDR+
						offset,val));

				Sleep(300);
				boolean bret = WaitPostMessage(1000);
				ok = ok && bret;
				Log.d(TAG, "ok:"+ok+",bret:"+bret);
			}  
		}
		Message message = new Message();

		if(ok)
		{
			message.arg1 = DEVICE_PARAM_SET_SUCCESS;
			mModifyIndex.clear();
		}
		else
		{
			message.arg1 = DEVICE_PARAM_SET_FAILED;			
		}
		if(!seted)
		{
			message.arg1 = DEVICE_PARAM_SET_NOTHING;
		}
		mUiHandler.sendMessage(message);
		return message.arg1;

	}

	private int findStringFromStringArray(String[] array,
			String value) {
		// TODO Auto-generated method stub
		int ret = -1;
		for(int i=0;i<array.length;i++)
		{
			if(value.equals(array[i]))
			{
				ret = i;
				break;
			}
		}
		return ret;
	}
	private boolean getIsWrite(String cur_batty_tyep,int param)
	{
		int battery_type_val04h_li_battery_iswrite[] =new int[]{ParamSettingDataStruct.LEFT_CHARGE_VOLTAGE_OFFSET,
				ParamSettingDataStruct.OVER_DISCHARGE_RETURN_VOLTAGE_OFFSET,
				ParamSettingDataStruct.OVER_DISCHARGE_VOLTAGE_OFFSET};
		if(cur_batty_tyep.equals(getString(R.string.battery_type_val04h)))
		{
			for(int i=0;i<battery_type_val04h_li_battery_iswrite.length;i++ )
			{
				if(param ==battery_type_val04h_li_battery_iswrite[i] )
					return true;
			}
		}
		else
		{
			if(param ==ParamSettingDataStruct.TIMEPERATURE_COMPENSTATION_COEFFICIENT_OFFSET)
				return true;
		}

		return false;
	}
	private void changedDefaultValue(String cur_batty_tyep/*,int param*/)
	{

		/*
		 * 
		 		if(cur_batty_tyep.equals(getString(R.string.battery_type_val04h)))//LI
		{
			defVal = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","0","20","21","22"};
		}
		else if(cur_batty_tyep.equals(getString(R.string.battery_type_val01h)))//FLD
		{
			defVal = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","30","20","21","22"};

		}
		else if(cur_batty_tyep.equals(getString(R.string.battery_type_val03h)))//GEL
		{
			defVal = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","0","20","21","22"};

		}
		else if(cur_batty_tyep.equals(getString(R.string.battery_type_val02h)))//SLD
		{
			defVal = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","30","20","21","22"};

		}

		 * */
		String defVal[] = null;
		if(cur_batty_tyep.equals(getString(R.string.battery_type_val04h)))//LI
		{
			defVal = new String[]{"1","2","3","4","16.0","15.5","14.4","14.4","14.4","13.2","12.6","12.0","11.1","10.6","15","5","0","-","-","0","21","22"};
		}
		else if(cur_batty_tyep.equals(getString(R.string.battery_type_val01h)))//FLD
		{
			defVal = new String[]{"1","2","3","4","16.0","15.5","14.8","14.6","13.8","13.2","12.6","12.0","11.1","10.6","15","5","120","120","30","3","21","22"};

		}
		else if(cur_batty_tyep.equals(getString(R.string.battery_type_val03h)))//GEL
		{
			defVal = new String[]{"1","2","3","4","16.0","15.5","14.2","14.2","13.8","13.2","12.6","12.0","11.1","10.6","15","5","0","120","0","3","21","22"};

		}
		else if(cur_batty_tyep.equals(getString(R.string.battery_type_val02h)))//SLD
		{
			defVal = new String[]{"1","2","3","4","16.0","15.5","14.6","14.4","13.8","13.2","12.6","12.0","11.1","10.6","15","5","120","120","30","3","21","22"};

		}

		if(defVal != null )
		{
			for(int i=ParamSettingDataStruct.OVER_VOLTAGE_VOLTAGE_OFFSET;
					i<=ParamSettingDataStruct.TIMEPERATURE_COMPENSTATION_COEFFICIENT_OFFSET;i++)
			{
				if(defVal.length <= i)
					break;
				EditText editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[i]);
				if(editText == null )
				{
					continue;
				}
				editText.setText(defVal[i]);
			}
		}
	}
	@Override  
	public void onDetach() {  
		super.onDetach();  
		try {  
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");  
			childFragmentManager.setAccessible(true);  
			childFragmentManager.set(this, null);  

		} catch (NoSuchFieldException e) {  
			throw new RuntimeException(e);  
		} catch (IllegalAccessException e) {  
			throw new RuntimeException(e);  
		}  

	}
	private void UpdateUiEnable()
	{
		String str = ((EditText)viewBatteryParam.findViewById(R.id.tv_parameter_battery_type_set_value))
				.getText().toString();
		boolean en = true;
		if(str.equals(getString(R.string.battery_type_val00h)))
		{
			en = true;
		}
		else
		{
			en = false;
		}
		for(int i=ParamSettingDataStruct.OVER_VOLTAGE_VOLTAGE_OFFSET;
				i<=ParamSettingDataStruct.TIMEPERATURE_COMPENSTATION_COEFFICIENT_OFFSET;i++)
		{
			EditText editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[i]);
			if(editText == null)
			{
				continue;
			}
			boolean isWrite = en | getIsWrite(str,i);
			editText.setEnabled(isWrite);
			if(isWrite)
				editText.setBackgroundColor(0x00ffffff);
			else
				editText.setBackgroundColor(0xffefeff4);//barack add 17/0812
		}
		EditText editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[1]);
		if(editText != null)
		{
			editText.setText("200");//barack add 17/0814  //barack del0827
		}
		changedDefaultValue(str);
	}

	protected void ParameterBatteryTypeSet() {
		// TODO Auto-generated method stub
		ListItemSelectDialog alertDialog = new ListItemSelectDialog(getActivity(),mArrayBatteryType);
		alertDialog.SetOnConfirmInterface(new OnConfirmInterface() {

			@Override
			public void doConfirm(String selStr) {
				// TODO Auto-generated method stub
				((EditText)viewBatteryParam.findViewById(R.id.tv_parameter_battery_type_set_value)).setText(selStr);
				UpdateUiEnable();

			}
		});
		alertDialog.show();
	}
	private void ParameterLoadOperatingModeSet() {
		// TODO Auto-generated method stub

		ListItemSelectDialog alertDialog = new ListItemSelectDialog(getActivity(),mArrayLoadOperatingMode);
		alertDialog.SetOnConfirmInterface(new OnConfirmInterface() {

			@Override
			public void doConfirm(String selStr) {
				// TODO Auto-generated method stub
				((EditText)viewLoadParam.findViewById(R.id.tv_parameter_load_operating_mode_set_value)).setText(selStr);

			}
		});
		alertDialog.show();

	}
	private void ParameterSystemVoltageSet() {
		// TODO Auto-generated method stub
		String str[] = {"12","24","36","48","96",mAutomaticRecognitionStr};
		String batteryParam = ((EditText)viewBatteryParam.findViewById(R.id.tv_parameter_battery_type_set_value))
				.getText().toString();

		ListItemSelectDialog alertDialog = new ListItemSelectDialog(getActivity(),str);
		if(batteryParam.equals(/*getString(R.string.battery_type_val04h)*/mArrayBatteryType[4]))
		{
			alertDialog = null;
			String str1[] ={"12","24","36","48","96"};
			alertDialog = new ListItemSelectDialog(getActivity(),str1);
		}
		alertDialog.SetOnConfirmInterface(new OnConfirmInterface() {

			@Override
			public void doConfirm(String selStr) {
				// TODO Auto-generated method stub
				((EditText)viewBatteryParam.findViewById(R.id.tv_parameter_set_system_voltage_set_value)).setText(selStr);

			}
		});
		alertDialog.show();

	}

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
		if(mReadingRegId<0)
			return true;

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
	private void ShowDialog(String info)
	{
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage(info/*getString(R.string.device_param_setting_success)*/);
		builder.setTitle(getString(R.string.prompt));
		builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		});
		builder.setNegativeButton(getString(R.string.confirm), new  DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}


		});
		builder.create().show();
	}
	private Handler mUiHandler = new Handler()
	{
		public void handleMessage(Message msg){ 
			if(!mIsActive)
			{
				return;
			}
			byte[] bs=(byte[])msg.obj;
			switch(msg.arg1)
			{
			case DEVICE_PARAM_SET_SUCCESS:
				//mSuccessToast.show();
				ShowDialog(getString(R.string.device_param_setting_success));
				break;
			case DEVICE_PARAM_SET_FAILED:
				//mFailedToast.show();
				ShowDialog(getString(R.string.device_param_setting_failed)); 
				break;
			case DEVICE_PARAM_SET_NOTHING:
				//mNothingToast.show();

				ShowDialog(getString(R.string.device_param_setting_nothing)); 
				break;
			case DEVICE_PARAM_GET_SUCCESS:
				//	ShowDialog(getString(R.string.device_param_getting_success)); //del 170827
				mParamRaedSuccessToast.show();
				mFromReadButton = false;
				break;
			case DEVICE_PARAM_GET_FAILED:
				//ShowDialog(getString(R.string.device_param_getting_failed));
				mParamRaedFailedToast.show();
				mFromReadButton = false;
				break;
			default:break;
			}
		}
	};

	private Handler mMessageHandler = new Handler()
	{
		public void handleMessage(Message msg){ 
			if(!mIsActive)
				return;
			byte[] bs=(byte[])msg.obj;
			switch(msg.arg1)
			{
			case ShourigfData.ParamSettingData.REG_ADDR:
				UpdateParamSettingData(bs);
				break;

			default:break;
			}
		}



	};
	public void RefreshData()
	{
		mDoRead = true;
		new Thread(new RefreshThread()).start();  
	}

	private class RefreshThread implements Runnable{  

		public void run() {  
			if(mRefreshThreadRuning)
				return;
			mRefreshThreadRuning = true;
			if(!mDoRead)
			{
				int ret = doSettingData();
				if(ret != DEVICE_PARAM_SET_SUCCESS)
				{
					mRefreshThreadRuning = false;
					return;
				}
				else
				{//add 2018 06 03
					Sleep(1000);
				}
				//	return;
			}
			while(true)
			{
				ClearRecvFifo();
				Sleep(100);
				mReadingRegId = ShourigfData.ParamSettingData.REG_ADDR;
				mReadingCount = ShourigfData.ParamSettingData.READ_WORD;
				SendUartData(ModbusData.BuildReadRegsCmd(mDeviceId, mReadingRegId, mReadingCount));
				Sleep(100);
				boolean ret = WaitPostMessage(5000);
				if(mDoRead && mFromReadButton)
				{
					Message message = new Message();
					if(ret)
					{
						message.arg1 = DEVICE_PARAM_GET_SUCCESS;
					}
					else
					{
						message.arg1 = DEVICE_PARAM_GET_FAILED;
					}

					mUiHandler.sendMessage(message);
				}
				break;
			}
			Log.d(TAG, "exit thread");
			Sleep(1000);
			mRefreshThreadRuning = false;

		}
	}


	private boolean saveParam()
	{

		String batteryParam = ((EditText)viewBatteryParam.findViewById(R.id.tv_parameter_battery_type_set_value))
				.getText().toString();
		String system_vol =  ((EditText)viewBatteryParam.findViewById(R.id.tv_parameter_set_system_voltage_set_value)).getText().toString();

		if(batteryParam.equals(/*getString(R.string.battery_type_val04h)*/mArrayBatteryType[4]) && system_vol.equals(mAutomaticRecognitionStr))
		{
			ShowDialog(getString(R.string.please_select_fixed_sys_vol));
			return false;
		}

		SharedPreferences.Editor editor = mSharedPreferences.edit();

		EditText editText = null;
		for(int i=0;i<ParamSettingDataStruct.ID.length;i++)
		{

			if(ParamSettingDataStruct.ID[i] < 0)
				continue;
			editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[i]);
			if(editText == null)
			{
				editText = (EditText)viewLoadParam.findViewById(ParamSettingDataStruct.ID[i]);

			}
			if(editText == null)
			{
				continue;
			}
			String str = editText.getText().toString();
			//Log.d(TAG, "mSharedPreferences write"+str);
			editor.putString("DATA"+i, editText.getText().toString());

		}
		editor.commit();
		return true;
	}
	private void readParam()
	{
		EditText editText = null;
		for(int i=0;i<ParamSettingDataStruct.ID.length;i++)
		{

			if(ParamSettingDataStruct.ID[i] < 0)
				continue;
			editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[i]);
			if(editText == null)
			{
				editText = (EditText)viewLoadParam.findViewById(ParamSettingDataStruct.ID[i]);

			}
			if(editText == null)
			{
				continue;
			}
			String str= mSharedPreferences.getString("DATA"+i, "");
			if(str!= null && !str.equals(""))
			{
				editText.setText(str);
			}
		}
		UpdateUiEnable();//barack add 
	}

	private void enableParamSet(boolean en)
	{
		/*if(viewBatteryParam == null ||viewLoadParam==null )
		{
			return;
		}*/
		if(en)
		{
			EditText editText = null;
			for(int i=0;i<ParamSettingDataStruct.ID.length;i++)
			{

				if(ParamSettingDataStruct.ID[i] < 0)
					continue;
				editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[i]);
				if(editText == null)
				{
					editText = (EditText)viewLoadParam.findViewById(ParamSettingDataStruct.ID[i]);

				}
				if(editText == null)
				{
					continue;
				}
				//	editText.setEnabled(true);
				//editText.setClickable(false);
				editText.setFocusable(true);
				editText.setFocusableInTouchMode(true);
				editText.setOnClickListener(null);
				if(i== ParamSettingDataStruct.BATTERY_TYPE_OFFSET ||  i== ParamSettingDataStruct.LOAD_OPERATIN_MODE_OFFSET
						||  i== ParamSettingDataStruct.SYSTEM_VOLTAGE_OFFSET)
				{
					editText.setFocusable(false);
					editText.setOnClickListener(clickListener);

				}
			}
			//UpdateUiEnable();
		}
		else
		{
			EditText editText = null;
			for(int i=0;i<ParamSettingDataStruct.ID.length;i++)
			{

				if(ParamSettingDataStruct.ID[i] < 0)
					continue;
				editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[i]);
				if(editText == null)
				{
					editText = (EditText)viewLoadParam.findViewById(ParamSettingDataStruct.ID[i]);

				}
				if(editText == null)
				{
					continue;
				}
				//editText.setEnabled(false);
				//	editText.setClickable(true);
				editText.setFocusable(false);
				editText.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						ShowDialog(getString(R.string.please_get_admin));
					}
				} );
			}
		}
	}
	private boolean isLiBatteryType()
	{
		String bat_type = ((EditText)viewBatteryParam.findViewById(R.id.tv_parameter_battery_type_set_value))
				.getText().toString();

		if(bat_type.toString().equals(getString(R.string.battery_type_val04h)))//LI
		{
				return true;
		}
		return false;
	}

	private boolean isNotUpdateType(int i)
	{
		String bat_type = ((EditText)viewBatteryParam.findViewById(R.id.tv_parameter_battery_type_set_value))
				.getText().toString();

		if(bat_type.toString().equals(getString(R.string.battery_type_val04h)))//LI
		{
			if(ParamSettingDataStruct.ID[i] == R.id.tv_parameter_equilibrium_charge_interval_set_value|| ParamSettingDataStruct.ID[i] == R.id.tv_parameter_promote_charging_time_set_value )
				return true;
		}
		return false;
	}
	protected void UpdateParamSettingData(byte[] bs) {
		// TODO Auto-generated method stub
		ShourigfData.ParamSettingData data = new ShourigfData.ParamSettingData(bs);
		mParamData = data.mData;

		EditText editText = null;
		for(int i=0;i<ParamSettingDataStruct.ID.length;i++)
		{

			if(ParamSettingDataStruct.ID[i] < 0 || isNotUpdateType(i))
				continue;
			editText = (EditText)viewBatteryParam.findViewById(ParamSettingDataStruct.ID[i]);
			if(editText == null)
			{
				editText = (EditText)viewLoadParam.findViewById(ParamSettingDataStruct.ID[i]);

			}
			if(editText == null)
			{
				continue;
			}
			if(i== ParamSettingDataStruct.BATTERY_TYPE_OFFSET )
			{
				if(data.mData[i]<mArrayBatteryType.length)
					editText.setText(mArrayBatteryType[data.mData[i]]);
				continue;
			}
			else if(i== ParamSettingDataStruct.LOAD_OPERATIN_MODE_OFFSET)
			{
				if(data.mData[i]<mArrayLoadOperatingMode.length)
					editText.setText(mArrayLoadOperatingMode[data.mData[i]]);
				continue;

			}
			else if((isLiBatteryType())&&(i == ParamSettingDataStruct.BALANCED_CHARGE_VOLTAGE_OFFSET ||i == ParamSettingDataStruct.FLOAT_CHARGE_VOLTAGE_OFFSET ))
			{//barack add 2017/1027 
				editText.setText((float)data.mData[ParamSettingDataStruct.LEFT_CHARGE_VOLTAGE_OFFSET]/10.f+"");
				continue;
			}
			else if(i>= ParamSettingDataStruct.OVER_VOLTAGE_VOLTAGE_OFFSET && i<=ParamSettingDataStruct.DISCHARGE_LIMIT_VOLTAGE_OFFSET)
			{
				editText.setText((float)data.mData[i]/10.f+"");
				continue;
			}
			else if(i == ParamSettingDataStruct.SYSTEM_VOLTAGE_OFFSET )
			{
				//if((data.mData[i] &0xff00) == 0xff00)
				if(((data.mData[i]>>8) &0xff) != 12
						&& (((data.mData[i]>>8) &0xff) != 24)
						&& (((data.mData[i]>>8) &0xff) != 36)
						&& (((data.mData[i]>>8) &0xff) != 96)
						&& (((data.mData[i]>>8) &0xff) != 48))
				{
					editText.setText(mAutomaticRecognitionStr);
				}
				else
				{
					editText.setText(/*(data.mData[i]&0xff)*/((data.mData[i]>>8) &0xff) +"");
				}
				continue;
			}

			editText.setText(data.mData[i]+"");

		}
		mModifyIndex.clear();
		//UpdateUiEnable(); //barack del 0827
	}

}
