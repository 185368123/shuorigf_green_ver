package com.wcsmobile.dialog;




import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.wcsmobile.R;
import com.wcsmobile.widget.wheelview.OnWheelScrollListener;
import com.wcsmobile.widget.wheelview.WheelView;
import com.wcsmobile.widget.wheelview.adapter.NumericWheelAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class SetDateDialog extends AlertDialog {
	private String TAG="SetDateDialog";
	private Context context;
	private WheelView yearView;
	private WheelView monthView;
	private int curYear,curMonth;
	private int startyear = 1990;
	private ClickListenerInterface clickListenerInterface;
	private boolean showMonth = true;
	public SetDateDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public SetDateDialog(Context context,boolean showMonth) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.showMonth = showMonth;
	}
	public SetDateDialog(Context context,boolean showMonth,int curYear,int curMonth) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.showMonth = showMonth;
		this.curYear = curYear;
		this.curMonth = curMonth;
	}
	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			int n_year = yearView.getCurrentItem() + startyear;
			int n_month = monthView.getCurrentItem() + 1;

		}
	};
	public void setClicklistener(ClickListenerInterface clickListenerInterface) {
		this.clickListenerInterface = clickListenerInterface;
	}
	public interface ClickListenerInterface {
		public void doConfirm(Date str);
		public void doCancel();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_set_date, null);
		setContentView(view);
		yearView = (WheelView)view.findViewById(R.id.wvYear);
		monthView = (WheelView)view.findViewById(R.id.wvMonth);
		if(!showMonth)
		{
			monthView.setVisibility(View.GONE);
		}
		((Button)(view.findViewById(R.id.btnCancel))).setOnClickListener(clickListener);
		((Button)(view.findViewById(R.id.btnConfirm))).setOnClickListener(clickListener);

		initWheel();


		/*	Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = context.getResources().getDisplayMetrics();
		lp.width = (int) (d.widthPixels * 1.0); 
		lp.alpha = 1.0f;        
		lp.dimAmount = 0.5f;      
		dialogWindow.setAttributes(lp);*/
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = context.getResources().getDisplayMetrics(); 
		lp.width = (int) (d.widthPixels * 0.9); 

		dialogWindow.setAttributes(lp);
	}
	private View.OnClickListener clickListener =  new View.OnClickListener(){


		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.btnCancel:
				clickListenerInterface.doCancel();
				dismiss();
				break;
			case R.id.btnConfirm:
				String strDate = String.format("%04d-%02d-%02d", 
						yearView.getCurrentItem() + startyear
						,monthView.getCurrentItem() + 1,1);
						
				Log.d(TAG, "strDate:"+strDate);
				Date date = stringToDate(strDate);
				clickListenerInterface.doConfirm(date);
				dismiss();
				break;
			default:
				break;
			}
		}

	};
	public static Date stringToDate(String str) {  
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
		Date date = null;  
		try {  
			// Fri Feb 24 00:00:00 CST 2012  
			date = format.parse(str);   
		} catch (ParseException e) {  
			e.printStackTrace();  
		}  

		date = java.sql.Date.valueOf(str);  

		return date;  
	}  
	private void initWheel() {
		// TODO Auto-generated method stub
		/*Calendar calendar=Calendar.getInstance();
		curYear=calendar.get(Calendar.YEAR);
		curMonth=calendar.get(Calendar.MONTH)+1;
*/

		NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(context,startyear, startyear+100); 
		numericWheelAdapter1.setLabel(context.getString(R.string.year));
		yearView.setViewAdapter(numericWheelAdapter1);
		yearView.setCyclic(true);
		yearView.addScrollingListener(scrollListener);

		NumericWheelAdapter numericWheelAdapter2=new NumericWheelAdapter(context,1, 12, "%02d"); 
		numericWheelAdapter2.setLabel(context.getString(R.string.month));
		monthView.setViewAdapter(numericWheelAdapter2);
		monthView.setCyclic(true);
		monthView.addScrollingListener(scrollListener);





		yearView.setCurrentItem(curYear-startyear);
		monthView.setCurrentItem(curMonth-1);

		yearView.setVisibleItems(6);
		monthView.setVisibleItems(6);


	}


	public static int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
		case 0:
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 2:
			day = flag ? 29 : 28;
			break;
		default:
			day = 30;
			break;
		}
		return day;
	}

}
