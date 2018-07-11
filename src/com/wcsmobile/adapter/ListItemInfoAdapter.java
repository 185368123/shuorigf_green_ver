package com.wcsmobile.adapter;


import com.wcsmobile.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListItemInfoAdapter extends   BaseAdapter {
	private Context mContext;
	private String [] mData;
	private LayoutInflater mInflater;
	public ListItemInfoAdapter(Context context, String[] data) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mData = data;
		mInflater = 	  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list_info, null);
			holder =  new ViewHolder();
			holder.itemTitle = (TextView) convertView.findViewById(R.id.tv_item_title);

			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.itemTitle.setText(mData[position]);

		return convertView;
	}
	private class ViewHolder{
		public TextView itemTitle;
	}
}
