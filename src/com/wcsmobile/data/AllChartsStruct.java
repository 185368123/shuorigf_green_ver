package com.wcsmobile.data;

import java.util.ArrayList;

import com.github.mikephil.charting.data.Entry;

public class AllChartsStruct {
	private ArrayList<String> xVals;
	private ArrayList<Entry> yUpVals;
	private ArrayList<Entry> yDownVals;
	private String curUpTagString;
	private String curDownTagString;
	private String curXUintStr;
	
	public AllChartsStruct(ArrayList<String> xVals, ArrayList<Entry> yUpVals,
			ArrayList<Entry> yDownVals, String curUpTagString,
			String curDownTagString,String curXUintStr) {
		super();
		this.xVals = xVals;
		this.yUpVals = yUpVals;
		this.yDownVals = yDownVals;
		this.curUpTagString = curUpTagString;
		this.curDownTagString = curDownTagString;
		this.curXUintStr = curXUintStr;
	}
	public String getCurXUintStr() {
		return curXUintStr;
	}
	public void setCurXUintStr(String curXUintStr) {
		this.curXUintStr = curXUintStr;
	}
	public ArrayList<String> getxVals() {
		return xVals;
	}
	public void setxVals(ArrayList<String> xVals) {
		this.xVals = xVals;
	}
	public ArrayList<Entry> getyUpVals() {
		return yUpVals;
	}
	public void setyUpVals(ArrayList<Entry> yUpVals) {
		this.yUpVals = yUpVals;
	}
	public ArrayList<Entry> getyDownVals() {
		return yDownVals;
	}
	public void setyDownVals(ArrayList<Entry> yDownVals) {
		this.yDownVals = yDownVals;
	}
	public String getCurUpTagString() {
		return curUpTagString;
	}
	public void setCurUpTagString(String curUpTagString) {
		this.curUpTagString = curUpTagString;
	}
	public String getCurDownTagString() {
		return curDownTagString;
	}
	public void setCurDownTagString(String curDownTagString) {
		this.curDownTagString = curDownTagString;
	}
	
	
}
