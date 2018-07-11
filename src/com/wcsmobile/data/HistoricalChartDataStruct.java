package com.wcsmobile.data;

public class HistoricalChartDataStruct {

	public HistoricalChartDataStruct(String mXVal, float mUpYVal, int mUpXIndex,
			Object mUpObject, float mDownYVal, int mDownXIndex,
			Object mDownObject) {
		super();
		this.mXVal = mXVal;
		this.mUpYVal = mUpYVal;
		this.mUpXIndex = mUpXIndex;
		this.mUpObject = mUpObject;
		this.mDownYVal = mDownYVal;
		this.mDownXIndex = mDownXIndex;
		this.mDownObject = mDownObject;
	}
	public String mXVal;
	public float mUpYVal;
	public int mUpXIndex;
	public Object mUpObject;
	public float mDownYVal;
	public int mDownXIndex;
	public Object mDownObject;
}
