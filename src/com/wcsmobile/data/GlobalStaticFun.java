package com.wcsmobile.data;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class GlobalStaticFun {

	public static String buildUnitTransformationToString(int val,int multiple,String smallUint,String bigUint )
	{
/*		if(val < multiple && !bigUint.equals("kWh"))
		{
			return val+smallUint;
		}
		else
		{
			float bigVal = ((float)val)/((float)multiple);
			//float  bigVal_float = Math.round(bigVal*100)/100.f ;
			return String.format("%1$.3f",bigVal)  + bigUint;
		}*/
		if(!bigUint.equals("kWh"))
		{
			return buildBigDecimal(val)+smallUint;
		}
		else
		{
			if(val >= multiple)
			{
			float bigVal = ((float)val)/((float)multiple);
			
			//float  bigVal_float = Math.round(bigVal*100)/100.f ;
			return/* String.format("%1$.3f",bigVal)  + */buildBigDecimal(bigVal)  + bigUint;
			}
			else
			{
				return buildBigDecimal(val)+smallUint; 
			}
		}
	}
	//https://blog.csdn.net/xgyhh/article/details/78218490
	public static String buildUnitTransformationToString(long val,int multiple,String smallUint,String bigUint )
	{
/*		if(val < multiple && !bigUint.equals("kWh"))
		{
			return val+smallUint;
		}
		else
		{
			float bigVal = ((float)val)/((float)multiple);
			//float  bigVal_float = Math.round(bigVal*100)/100.f ;
			return String.format("%1$.3f",bigVal)  + bigUint;
		}*/
		if( !bigUint.equals("kWh"))
		{
			return /**buildBigDecimal(val)*/longTounsignedLongString(val)+smallUint;
		}
		else
		{
	//		float bigVal = ((float)val)/((float)multiple);
			//float  bigVal_float = Math.round(bigVal*100)/100.f ;
		///	return /*String.format("%1$.3f",bigVal)*/longToKUintDoubleString(val)  + bigUint;
			if(val >= multiple)
			{
				Long longval = val&0x0ffffffffl;
				Double double_mul = multiple*1.0d;
				Double doubleval = longval/double_mul;
				DecimalFormat df = new DecimalFormat("###,###.000");         
				DecimalFormatSymbols symbols=new	DecimalFormatSymbols();
				symbols.setDecimalSeparator('.');
				df.setDecimalFormatSymbols(symbols);
				
				return buildDot(df.format(doubleval)) + bigUint;//longval.toString();
			}
			else
			{
					return buildBigDecimal(val)+smallUint; 
			}
		}
	}
	public static String buildDot(String str)
	{
		String retStr = str;//.123 -> 0.123 
		if(str.length()>0 && str.getBytes()[0] == '.' )
		{
			retStr = "0"+retStr;
		}
/*		for(int i=0;i<2&&retStr.indexOf('.')< retStr.length()-2 &&retStr.indexOf('.')>0;i++)
		{//1.200 -> 1.2
			if(retStr.charAt(retStr.length()-1)=='0')
			{
				StringBuffer stb = new StringBuffer(retStr);				
				stb.delete(retStr.length()-1, retStr.length());
				retStr = stb.toString();
			}
			else
			{
				break;
			}
		}*/
		
		return retStr;
	}
	public static String buildBigDecimal(int val)
	{     
		String str  = val +"";
		DecimalFormat df = new DecimalFormat("###,###");         
		return buildDot(df.format(Double.parseDouble(str)));    
	}
	public static String buildBigDecimal(long val)
	{     
		String str  = val +"";
		DecimalFormat df = new DecimalFormat("###,###");         
		return buildDot(df.format(Double.parseDouble(str)));    
	}
	public static String buildBigDecimal(float val)
	{     
		DecimalFormat df = new DecimalFormat("###,###.000");         
		DecimalFormatSymbols symbols=new	DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(symbols);
		return buildDot(df.format(val));    
	}
	public static int getDaysByYearMonth(int year, int month) {  

		Calendar a = Calendar.getInstance();  
		a.set(Calendar.YEAR, year);  
		a.set(Calendar.MONTH, month - 1);  
		a.set(Calendar.DATE, 1);  
		a.roll(Calendar.DATE, -1);  
		int maxDate = a.get(Calendar.DATE);  
		return maxDate;  
	}  


	public static int getGapCount(Date startDate, Date endDate) {  
		/*      Calendar fromCalendar = Calendar.getInstance();    
	       fromCalendar.setTime(startDate);  
	       fromCalendar.set(Calendar.HOUR_OF_DAY, 0);    
	       fromCalendar.set(Calendar.MINUTE, 0);    
	       fromCalendar.set(Calendar.SECOND, 0);    
	       fromCalendar.set(Calendar.MILLISECOND, 0);   

	       Calendar toCalendar = Calendar.getInstance();    
       toCalendar.setTime(endDate);    

	      toCalendar.set(Calendar.HOUR_OF_DAY, 0);    
	       toCalendar.set(Calendar.MINUTE, 0);    
	       toCalendar.set(Calendar.SECOND, 0);    
	       toCalendar.set(Calendar.MILLISECOND, 0);  
	       return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));*/  
		long diff = endDate.getTime() - startDate.getTime();
		//	Log.d("TAG", "diff:"+diff+","+endDate.toString()+","+startDate.toString());
		return (int) (diff/(1000 * 60 * 60 * 24));
	}  
	public static int getCurDayDifft(Date startDate) {  

		return getGapCount(startDate,new Date());
	}        
	public static int getCurDayDifft(int startYear,int startMonth,int startDay) {  

		String strDate = String.format("%04d-%02d-%02d", 
				startYear,startMonth,startDay);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
		Date date = null;  
		try {  
			date = format.parse(strDate);   
		} catch (ParseException e) {  
			e.printStackTrace();  
		}  

		date = java.sql.Date.valueOf(strDate);  
		//Log.d("TAG", date.toString()+"---"+new Date().toString());
		return getGapCount(date,new Date());
	}        
	public static String longTounsignedLongString(long val)
	{

		Long longval = val&0x0ffffffffl;
		DecimalFormat df = new DecimalFormat("###,###");         
		return df.format(longval);//longval.toString();

	}
	public static String longToKUintDoubleString(long  val)
	{
		Long longval = val&0x0ffffffffl;
		Double doubleval = longval/1000.d;
		DecimalFormat df = new DecimalFormat("###,###.000");         
		DecimalFormatSymbols symbols=new	DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(symbols);
		return df.format(doubleval);//longval.toString();

	}
	//https://blog.csdn.net/aerchi/article/details/51258476
	public static String DeutschStringNumber(String str)
	{
		if(Locale.getDefault().toString().equals("de_DE"))
		{
			return str.replace(",", ".");
		}
		return str;

	}
	
}
