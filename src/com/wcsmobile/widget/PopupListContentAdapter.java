package com.wcsmobile.widget;

import java.util.ArrayList;
import java.util.List;

import com.wcsmobile.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PopupListContentAdapter extends BaseAdapter {
	private Context context;
	private List<String> mData;
	private LayoutInflater mInflater;


	public PopupListContentAdapter(Context context)
	{
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		mData = new ArrayList<String>();
		mData.add(context.getResources().getString(R.string.added_device));
		mData.add(context.getResources().getString(R.string.check_update));
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {

			holder=new ViewHolder();  


			convertView = mInflater.inflate(R.layout.item_popup_listview, null);
			holder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);


			convertView.setTag(holder);

		}else {

			holder = (ViewHolder)convertView.getTag();
		}
		holder.tvTitle.setText((String)mData.get(position).toString());
		return convertView; 
	}
	private final class ViewHolder{
		public TextView tvTitle;

	}
}
