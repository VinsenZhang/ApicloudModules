package com.vinsen.select;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class SelectWindow extends Service {


    private String type;


    private String value;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private View getItemView(String name, String[] value) {

        if (name.equalsIgnoreCase("input")) {
            // edit text
            EditText editText = new EditText(getApplicationContext());
            editText.setGravity(Gravity.CENTER);
            editText.setHint(value[0]);


            return editText;
        } else if (name.equalsIgnoreCase("select")) {
            // spinner
            Spinner spinner = new Spinner(getApplicationContext());

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
