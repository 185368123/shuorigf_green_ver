package com.wcsmobile.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wcsmobile.DBHelp;
import com.wcsmobile.R;
import com.wcsmobile.bean.NameSaveBean;

import org.litepal.crud.DataSupport;

import java.util.List;

public class InputDialog extends Dialog {
    private String TAG = "InputDialog";
    private Context context;
    private EditText et;
    private String name;
    private OnSetName setName;

    public interface OnSetName {
        void onSucess();

        void onFaile();
    }

    public InputDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        this.context = context;
    }

    public InputDialog(Context context, String name, OnSetName setName) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        this.context = context;
        this.name = name;
        this.setName = setName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_input, null);
        setContentView(view);
        ((Button) (view.findViewById(R.id.btn_dialog_input_cancle))).setOnClickListener(clickListener);
        ((Button) (view.findViewById(R.id.btn_dialog_input_confirm))).setOnClickListener(clickListener);
        et = view.findViewById(R.id.et_dialog_input);
        et.setText(DBHelp.getInstance().getDeviceName(name));
        et.setSelection(DBHelp.getInstance().getDeviceName(name).length());
        et.clearFocus();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.9);

        dialogWindow.setAttributes(lp);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_dialog_input_cancle:
                    dismiss();
                    break;
                case R.id.btn_dialog_input_confirm:
                    if (!TextUtils.isEmpty(et.getText())) {
                        List<NameSaveBean> newsList = DataSupport.where("name_new=?", et.getText().toString()).find(NameSaveBean.class);
                        if (newsList != null && newsList.size() > 0) {
                            Toast.makeText(getContext(), R.string.device_name_exists, Toast.LENGTH_LONG).show();
                        } else {
                            DBHelp.getInstance().setDeviceName(name, et.getText().toString());
                            Toast.makeText(getContext(), R.string.device_name_set_sucess, Toast.LENGTH_LONG).show();
                            if (setName != null) {
                                setName.onSucess();
                            }
                            dismiss();
                        }

                    } else {
                        Toast.makeText(getContext(), R.string.device_name_set_failed, Toast.LENGTH_LONG).show();
                        if (setName != null) {
                            setName.onFaile();
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    };
}
