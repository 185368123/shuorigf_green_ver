package com.wcsmobile.dialog;

import com.wcsmobile.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class ShowSoftVersionInfoDialog  extends AlertDialog {

	private String TAG="ShowSoftVersionInfoDialog";
	private Context context;
	public ShowSoftVersionInfoDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public ShowSoftVersionInfoDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public ShowSoftVersionInfoDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_show_soft_version_info, null);
		setContentView(view);
		
		((Button)(view.findViewById(R.id.btnCancel))).setOnClickListener(clickListener);
		((Button)(view.findViewById(R.id.btnConfirm))).setOnClickListener(clickListener);

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
				dismiss();
				break;
			case R.id.btnConfirm:
				dismiss();
				break;
			default:
				break;
			}
		}

	};
}
