package com.vinsen.select;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.text.Editable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class SelectWindow extends Service {


    private String type;


    private String value;

    private int orientation;

    private int w, h;// select float view width and height
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        this.orientation = newConfig.orientation;


    }

    private void initView() {
        windowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);


        getLayoutParams();
        // root view
        LinearLayout rootView = new LinearLayout(getApplicationContext());


        //


        rootView.addView(getItemView("", null));


        windowManager.addView(rootView, params);

    }


    private void getLayoutParams() {

        Display display = windowManager.getDefaultDisplay();
        int with = display.getWidth();
        int height = display.getHeight();

        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);
        //设置View默认的摆放位置
        params.gravity = Gravity.CENTER;
        //设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // android 8
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            // android 7.1
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // android 4.4 ~ 8
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //设置背景为透明
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (orientation == 2) {
            params.width = with * 3 / 5;
            params.height = height * 4 / 5;
            w = with * 3 / 5;
            h = height * 4 / 5;
        } else {
            params.width = with * 4 / 5;
            params.height = height * 3 / 5;

            w = with * 4 / 5;
            h = height * 3 / 5;
        }

    }


    // 处理数据
    private String[] getValues(String value) {

        if (value.contains(";")) {
            return value.split(";");
        }
        return new String[]{value};
    }


    private View getItemView(String name, final String[] value) {

        if (name.equalsIgnoreCase("input")) {
            // edit text
            EditText editText = new EditText(getApplicationContext());
            editText.setGravity(Gravity.CENTER);
            editText.setHint(value[0]);


            return editText;
        } else if (name.equalsIgnoreCase("select")) {
            // spinner
            Spinner spinner = new Spinner(getApplicationContext());

            ArrayAdapter<String> adapter =  new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, value);

            spinner.setAdapter(adapter);

            spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String temp = value[position];
                }
            });

            return spinner;

        } else if (name.equalsIgnoreCase("checkbox")) {
            // check box
            CheckBox checkBox = new CheckBox(getApplicationContext());


            checkBox.setText(value[0]);
            return checkBox;
        } else if (name.equalsIgnoreCase("switch")) {
            // switch
            Switch switchBtn = new Switch(getApplicationContext());


            switchBtn.setText(value[0]);
            return switchBtn;


        } else {
            // text view
            TextView textView = new TextView(getApplicationContext());


            textView.setText(value[0]);
            return textView;

        }

    }


}
