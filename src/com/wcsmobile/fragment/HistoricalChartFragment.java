package com.wcsmobile.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.wcsmobile.R;
import com.wcsmobile.activity.HistoricalDataActivity;
import com.wcsmobile.data.AllChartsStruct;
import com.wcsmobile.data.GlobalStaticFun;
import com.wcsmobile.data.ShourigfData;
import com.wcsmobile.view.MyMarkerView;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class HistoricalChartFragment extends BaseV4Fragment {
	private static final String TAG = "HistoricalChartFragment";

	private int fragmentType;
	private LineChart mUpChart;
	private LineChart mDownChart;
	private String curUpChartName;
	private String curDownChartName;
	private String curXUintStr;
	private String curYuintStr;
	private String upCurrentVal;
	private String downCurrentVal;	
	private float upMaxVal=100;
	private float downMaxVal=100;
	private float upMinVal=1;
	private float downMinVal=1;
	private AllChartsStruct mChartsData;
	private ShourigfData.HistoricalData mHistoricalData;



	private View mView;


	public HistoricalChartFragment(int type,ShourigfData.HistoricalData historialData)
	{
		fragmentType = type;
		mHistoricalData = historialData;

	}
	/*	
	private OnCheckedChangeListener CheckedChangeListener = new OnCheckedChangeListener()
	{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			Log.d(TAG,"checkedId:"+checkedId);
			switch(checkedId)
			{
			case R.id.rdbtn_month:
				tv_historicalchart_title.setText(R.string.base_month_chart_summary);
				btn_set_date.setVisibility(View.VISIBLE);
				tv_historicalchart_date.setVisibility(View.VISIBLE);
				tv_historicalchart_date.setText(curYear+getString(R.string.year)+curMonth+getString(R.string.month));
				disMonth = true;
				BuildMonthData();
				break;
			case R.id.rdbtn_all:
				tv_historicalchart_title.setText(R.string.all_chart_summary);
				btn_set_date.setVisibility(View.INVISIBLE);
				tv_historicalchart_date.setVisibility(View.INVISIBLE);
				BuildAllData();
				break;
			case R.id.rdbtn_year:
				BuildYearData();
				tv_historicalchart_title.setText(R.string.base_year_chart_summary);
				btn_set_date.setVisibility(View.VISIBLE);
				tv_historicalchart_date.setVisibility(View.VISIBLE);
				disMonth = false;
				tv_historicalchart_date.setText(curYear+getString(R.string.year));

				break;
						case R.id.rdbtn_day:

				break;
			default:
				break;
			}
			UpDateAllCharts();

		}

	};*/

	/*
	private float GetValue(int range)
	{
		float mult = (range + 1);
		float val = (float) (Math.random() * mult) + 3;// + (float)
		return val;
	}


	private void BuildAllData() {
		// TODO Auto-generated method stub
		ClearAllVals();
		curXUintStr=getString(R.string.year);
		for(int i=2015,j=0;i<2017;i++,j++)
		{
			xVals.add(i+ "");
			yUpVals.add(new Entry(GetValue(5), j,i+getString(R.string.year)));
			yDownVals.add(new Entry(GetValue(5), j,i+getString(R.string.year)));

		}
		upMinVal = downMinVal = 2015;
		upMaxVal = downMaxVal = 2016;
		curUpTagString = "" +tagStringArray[1];
		curDownTagString = "" + tagStringArray[0];


	}
	private void BuildYearData() {
		// TODO Auto-generated method stub
		ClearAllVals();
		curXUintStr=getString(R.string.month);
		for(int i=1,j=0;i<13;i++,j++)
		{
			xVals.add(i+ getString(R.string.month));
			yUpVals.add(new Entry(GetValue(15), j,i+getString(R.string.month)));
			yDownVals.add(new Entry(GetValue(15), j,i+getString(R.string.month)));

		}
		upMinVal = downMinVal = 0;
		upMaxVal = downMaxVal = 19;

		curUpTagString = curYear+getString(R.string.year) + tagStringArray[1];
		curDownTagString = curYear+getString(R.string.year)+ tagStringArray[0];
	}

	private void BuildMonthData() {
		// TODO Auto-generated method stub
		int cur_month_total_days = SetDateDialog.getDay(curYear, curMonth);
		int cur_month= curMonth;
		ClearAllVals();
		curXUintStr=getString(R.string.day);

		for(int i=1,j=0;i<cur_month_total_days;i++,j++)
		{
			xVals.add(i+ "");
			yUpVals.add(new Entry(GetValue(20), j,curYear+getString(R.string.year)+curMonth+getString(R.string.month)));
			yDownVals.add(new Entry(GetValue(20), j,curYear+getString(R.string.year)+curMonth+getString(R.string.month)));

		}
		upMinVal = downMinVal = 0;
		upMaxVal = downMaxVal = cur_month_total_days;
		curUpTagString = curYear+getString(R.string.year) + cur_month + getString(R.string.month)+ tagStringArray[1];
		curDownTagString =curYear+getString(R.string.year) + cur_month + getString(R.string.month)+ tagStringArray[0];

	}*/
	public void UpDateAllCharts(AllChartsStruct data)
	{
		mChartsData = data;
		if(mView != null)
		{
			DisplayCharts(data);
		}

	}
	private void DisplayCharts(AllChartsStruct data)
	{		
		curXUintStr = data.getCurXUintStr();
		InitChart(mUpChart,upMaxVal,upMinVal);
		setData(mUpChart,data.getxVals(),data.getyUpVals(),data.getCurUpTagString());
		UpdateUpChartsOtherData();
		mUpChart.invalidate();
		InitChart(mDownChart,downMaxVal,downMinVal);
		setData(mDownChart,data.getxVals(),data.getyDownVals(),data.getCurDownTagString());
		UpdateDownChartsOtherData();
		mDownChart.invalidate();

	}
	private void UpdateUpChartsOtherData()
	{
		if( HistoricalDataActivity.CHART_ELECTRICITY == fragmentType)
		{
/*			((TextView)mView.findViewById(R.id.tv_up_historicalchart_min_value_value)).setText(Math.round(mUpChart.getYMin()*1000)/1000.f+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_ave_value_value)).setText(Math.round(mUpChart.getAverage()*1000)/1000.f+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_max_value_value)).setText(Math.round(mUpChart.getYMax()*1000)/1000.f+curYuintStr);*/
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_min_value_value)).setText(GlobalStaticFun.buildBigDecimal(mUpChart.getYMin())+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_ave_value_value)).setText(GlobalStaticFun.buildBigDecimal(mUpChart.getAverage())+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_max_value_value)).setText(GlobalStaticFun.buildBigDecimal(mUpChart.getYMax())+curYuintStr);
			
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_max_value)).setText(Math.round(mUpChart.getYMax()*1000)/1000.f+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_current_value)).setText(/*Math.round(mUpChart.getYMax()*100)/100+curYuintStr*/upCurrentVal);
			mUpChart.setDescription(Math.round(mUpChart.getYMin()*1000)/1000.f+curYuintStr);
			mUpChart.setDescription("");

		}
		else
		{
		/*	((TextView)mView.findViewById(R.id.tv_up_historicalchart_min_value_value)).setText(Math.round(mUpChart.getYMin()*100)/100+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_ave_value_value)).setText(Math.round(mUpChart.getAverage()*100)/100+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_max_value_value)).setText(Math.round(mUpChart.getYMax()*100)/100+curYuintStr);
**/
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_min_value_value)).setText(GlobalStaticFun.buildBigDecimal(mUpChart.getYMin())+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_ave_value_value)).setText(GlobalStaticFun.buildBigDecimal(mUpChart.getAverage())+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_max_value_value)).setText(GlobalStaticFun.buildBigDecimal(mUpChart.getYMax())+curYuintStr);
			
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_max_value)).setText(Math.round(mUpChart.getYMax()*100)/100+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_up_historicalchart_current_value)).setText(/*Math.round(mUpChart.getYMax()*100)/100+curYuintStr*/upCurrentVal);
			mUpChart.setDescription(Math.round(mUpChart.getYMin()*100)/100+curYuintStr);
			mUpChart.setDescription("");
		}
	}
	private void UpdateDownChartsOtherData()
	{
		if( HistoricalDataActivity.CHART_ELECTRICITY == fragmentType)
		{
	/*		((TextView)mView.findViewById(R.id.tv_down_historicalchart_min_value_value)).setText(Math.round(mDownChart.getYMin()*1000)/1000.f+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_ave_value_value)).setText(Math.round(mDownChart.getAverage()*1000)/1000.f+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_max_value_value)).setText(Math.round(mDownChart.getYMax()*1000)/1000.f+curYuintStr);
*/
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_min_value_value)).setText(GlobalStaticFun.buildBigDecimal(mDownChart.getYMin())+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_ave_value_value)).setText(GlobalStaticFun.buildBigDecimal(mDownChart.getAverage())+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_max_value_value)).setText(GlobalStaticFun.buildBigDecimal(mDownChart.getYMax())+curYuintStr);

			((TextView)mView.findViewById(R.id.tv_down_historicalchart_max_value)).setText(Math.round(mDownChart.getYMax()*1000)/1000.f+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_current_value)).setText(/*Math.round(mDownChart.getYMax()*100)/100+curYuintStr*/downCurrentVal);
			mDownChart.setDescription(Math.round(mDownChart.getYMin()*1000)/1000.f+curYuintStr);
			mDownChart.setDescription("");
		}
		else
		{
/*			((TextView)mView.findViewById(R.id.tv_down_historicalchart_min_value_value)).setText(Math.round(mDownChart.getYMin()*100)/100+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_ave_value_value)).setText(Math.round(mDownChart.getAverage()*100)/100+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_max_value_value)).setText(Math.round(mDownChart.getYMax()*100)/100+curYuintStr);
*/
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_min_value_value)).setText(GlobalStaticFun.buildBigDecimal(mDownChart.getYMin())+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_ave_value_value)).setText(GlobalStaticFun.buildBigDecimal(mDownChart.getAverage())+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_max_value_value)).setText(GlobalStaticFun.buildBigDecimal(mDownChart.getYMax())+curYuintStr);

			((TextView)mView.findViewById(R.id.tv_down_historicalchart_max_value)).setText(Math.round(mDownChart.getYMax()*100)/100+curYuintStr);
			((TextView)mView.findViewById(R.id.tv_down_historicalchart_current_value)).setText(/*Math.round(mDownChart.getYMax()*100)/100+curYuintStr*/downCurrentVal);
			mDownChart.setDescription(Math.round(mDownChart.getYMin()*100)/100+curYuintStr);
			mDownChart.setDescription("");
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_historical_chart, container,false);
		InitView(view); 
		return view;//return super.onCreateView(inflater, container, savedInstanceState);
	}
	private void InitView(View view) {
		// TODO Auto-generated method stub
		mView = view;



		mUpChart = (LineChart) view.findViewById(R.id.chart1);
		mDownChart = (LineChart) view.findViewById(R.id.chart2);
		mUpChart.setOnChartGestureListener(upChartGestureListener);
		mUpChart.setOnChartValueSelectedListener(upChartValueSelectedListener);
		mDownChart.setOnChartGestureListener(downChartGestureListener);
		mDownChart.setOnChartValueSelectedListener(downChartValueSelectedListener);
		mUpChart.setNoDataText("");
		mUpChart.setNoDataTextDescription("");
		mDownChart.setNoDataText("");
		mDownChart.setNoDataTextDescription("");

		curXUintStr = getString(R.string.day);
		curYuintStr = "V";
		int chartNameId[] = {				
				R.string.day_production_power,R.string.day_consumption_power,
				R.string.day_charge_amp_hour,R.string.day_discharge_amp_hour,
				R.string.day_charge_max_power,R.string.day_discharge_max_power,
				R.string.day_battery_min_voltage,R.string.day_battery_max_voltage,
		};

		curUpChartName = getString(chartNameId[fragmentType*2]);
		curDownChartName =  getString(chartNameId[fragmentType*2+1]);
		((TextView)view.findViewById(R.id.tv_up_historicalchart_title)).setText(curUpChartName);
		((TextView)view.findViewById(R.id.tv_down_historicalchart_title)).setText(curDownChartName);

		switch(fragmentType)
		{
		case HistoricalDataActivity.CHART_VOLTAGE:
			upCurrentVal = GlobalStaticFun.buildBigDecimal(mHistoricalData.mDayBatteryMinVoltage)+"V";
			downCurrentVal = GlobalStaticFun.buildBigDecimal(mHistoricalData.mDayBatteryMaxVoltage)+"V";
			curYuintStr = "V";
			break;
		case HistoricalDataActivity.CHART_VPOWER:

			upCurrentVal =GlobalStaticFun.buildUnitTransformationToString(mHistoricalData.mDayChargeMaxPower,1000,"W","KW");
			downCurrentVal = GlobalStaticFun.buildUnitTransformationToString(mHistoricalData.mDayDischargeMaxPower,1000,"W","KW");
			((LinearLayout)view.findViewById(R.id.tv_down_historicalchart_layout)).setVisibility(View.GONE);
			
			curYuintStr = "W";
			break;
		case HistoricalDataActivity.CHART_APM_HOUR:


			upCurrentVal =GlobalStaticFun.buildUnitTransformationToString(mHistoricalData.mDayChargeAmpHour,1000,"ah","kAh");
			downCurrentVal =GlobalStaticFun.buildUnitTransformationToString(mHistoricalData.mDayDischargeAmpHour,1000,"ah","kAh");
			((LinearLayout)view.findViewById(R.id.tv_down_historicalchart_layout)).setVisibility(View.GONE);
			
			curYuintStr = "ah";
			break;
		case HistoricalDataActivity.CHART_ELECTRICITY:
			upCurrentVal = GlobalStaticFun.buildUnitTransformationToString(mHistoricalData.mDayProductionPower,1000,"Wh","kWh");
			downCurrentVal =GlobalStaticFun.buildUnitTransformationToString(mHistoricalData.mDayConsumptionPower,1000,"Wh","kWh");
			curYuintStr="kWh";

			break;
		default:break;

		}


		if(mChartsData != null)
			DisplayCharts(mChartsData);

	}


	private OnChartValueSelectedListener downChartValueSelectedListener = new OnChartValueSelectedListener()
	{

		@Override
		public void onNothingSelected() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onValueSelected(Entry arg0, int arg1, Highlight arg2) {
			// TODO Auto-generated method stub

		}

	};
	private OnChartValueSelectedListener upChartValueSelectedListener = new OnChartValueSelectedListener()
	{

		@Override
		public void onNothingSelected() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onValueSelected(Entry arg0, int arg1, Highlight arg2) {
			// TODO Auto-generated method stub

		}

	};
	private OnChartGestureListener downChartGestureListener = new OnChartGestureListener(){

		@Override
		public void onChartDoubleTapped(MotionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChartFling(MotionEvent arg0, MotionEvent arg1,
				float arg2, float arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChartLongPressed(MotionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChartScale(MotionEvent arg0, float arg1, float arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChartSingleTapped(MotionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChartTranslate(MotionEvent arg0, float arg1, float arg2) {
			// TODO Auto-generated method stub

		}

	};
	private OnChartGestureListener upChartGestureListener = new OnChartGestureListener(){

		@Override
		public void onChartDoubleTapped(MotionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChartFling(MotionEvent arg0, MotionEvent arg1,
				float arg2, float arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChartLongPressed(MotionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChartScale(MotionEvent arg0, float arg1, float arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChartSingleTapped(MotionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChartTranslate(MotionEvent arg0, float arg1, float arg2) {
			// TODO Auto-generated method stub

		}

	};


	private void InitChart(LineChart chart,float max,float min) {
		// TODO Auto-generated method stub

		chart.setDrawGridBackground(false);
		chart.setDescription("3.0V");

		chart.setNoDataText("");
		chart.setNoDataTextDescription("");
		// enable value highlighting
		chart.setHighlightEnabled(true);

		// enable touch gestures
		chart.setTouchEnabled(true);

		// enable scaling and dragging
		chart.setDragEnabled(false);
		chart.setScaleEnabled(false);
		chart.setDescriptionColor(Color.WHITE);
		// chart.setScaleXEnabled(true);
		// chart.setScaleYEnabled(true);

		// if disabled, scaling can be done on x- and y-axis separately
		chart.setPinchZoom(false);

		MyMarkerView mv = new MyMarkerView(this.getActivity(), R.layout.custom_marker_view);

		chart.setMarkerView(mv);
		chart.setDrawMarkerViews(true);
		/*
        LimitLine ll1 = new LimitLine(180f, "15ah");
        ll1.setLineWidth(1f);
        ll1.setLineColor(Color.BLACK);
        ll1.enableDashedLine(10f, 0f, 0f);
        ll1.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        ll1.setTextSize(15.0f);
        ll1.setTextColor(Color.BLACK);

        LimitLine ll2 = new LimitLine(0f, "5.0ah");
        ll2.setLineWidth(1f);
        ll2.setLineColor(Color.BLACK);
        ll2.enableDashedLine(10f, 0f, 0f);
        ll2.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
        ll2.setTextSize(15.0f);
        ll2.setTextColor(Color.BLACK);       
		 */

		YAxis leftAxis = chart.getAxisLeft();
		leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
		//  leftAxis.addLimitLine(ll1);
		//   leftAxis.addLimitLine(ll2);
		/* leftAxis.setAxisMaxValue(max);
        leftAxis.setAxisMinValue(min);*/
		leftAxis.setStartAtZero(false);

		//leftAxis.setYOffset(20f);
		leftAxis.enableGridDashedLine(10f, 10f, 0f);
		leftAxis.setGridColor(Color.WHITE);
		// limit lines are drawn behind data (and not on top)
		leftAxis.setDrawLimitLinesBehindData(true);
		XAxis xAxis = chart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		// chart.getXAxis().setEnabled(false);
		// leftAxis.disableGridDashedLine();
		leftAxis.setDrawAxisLine(false);
		leftAxis.setDrawLabels(false);
		leftAxis.setAxisLineColor(Color.WHITE);

		//  leftAxis.setDrawGridLines(false);
		xAxis.setDrawGridLines(false);
		xAxis.setAxisLineColor(Color.WHITE);
		xAxis.setTextColor(Color.WHITE);
		xAxis.setAxisLineWidth(/*BitmapHelper.dip2px(getActivity(),1)*/1);
		xAxis.setTextSize(12);
		xAxis.setAvoidFirstLastClipping(true);

		//chart.getAxisRight().setDrawAxisLine(false);
		chart.getAxisRight().disableGridDashedLine();
		chart.getAxisRight().setDrawGridLines(false);
		chart.getAxisRight().setEnabled(false);


		Paint DescPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		DescPaint.setColor(Color.WHITE);
		DescPaint.setTextAlign(Align.RIGHT);
		DescPaint.setTextSize(Utils.convertDpToPixel(12f));
		chart.setPaint(DescPaint, LineChart.PAINT_DESCRIPTION);


		chart.animateX(25, Easing.EasingOption.EaseInOutQuart);
		//      Chart.invalidate();

		// get the legend (only possible after setting data)
		Legend l = chart.getLegend();
		l.setEnabled(false);//barack

		// modify the legend ...
		// l.setPosition(LegendPosition.LEFT_OF_CHART);
		l.setForm(LegendForm.LINE);
	}
	private void setData(LineChart chart, ArrayList<String> xVals, ArrayList<Entry> yVals,String tagString) {
		// TODO Auto-generated method stub


		// create a dataset and give it a type
		LineDataSet set1 = new LineDataSet(yVals, tagString);
		// set1.setFillAlpha(110);
		// set1.setFillColor(Color.RED);

		// set the line to be drawn like this "- - - - - -"
		set1.enableDashedLine(10f, 0f, 0f);

		set1.setColor(Color.WHITE);
		set1.setCircleColor(Color.WHITE);
		set1.setLineWidth(1f);
		set1.setCircleSize(3f);

		set1.setDrawCircleHole(true);
		set1.setValueTextSize(9f);
		set1.setFillAlpha(65);
		set1.setFillColor(Color.WHITE);
		set1.setDrawValues(false);

		set1.setDrawHorizontalHighlightIndicator(false);
		set1.setDrawVerticalHighlightIndicator(false);
		set1.setHighLightColor(Color.WHITE);
		set1.setHighlightLineWidth(1f);
		set1.SetXUintStr(curXUintStr);
		set1.SetYUintStr(curYuintStr);

		chart.highlightValues(null);//clear all hight

		//        set1.setDrawFilled(true);
		// set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
		// Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		dataSets.add(set1); // add the datasets

		// create a data object with the datasets
		LineData data = new LineData(xVals, dataSets);

		// set data
		chart.setData(data);
	}



}
