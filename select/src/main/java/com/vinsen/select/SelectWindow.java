package com.vinsen.select;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SelectWindow extends Service {


    private String json;

    private String background = "#ffffff";

    private String title;

    private String titleColor = "#515151";


    private WindowManager windowManager;
    private WindowManager.LayoutParams params;

    private ConcurrentHashMap<String, SelectResult> resultMap = new ConcurrentHashMap<>();


    private LinearLayout itemContainer;
    private View rootView;

    public class SelectWindowBinder extends Binder {

        public SelectWindow getService() {
            return SelectWindow.this;
        }

    }


    private SelectResultListener selectResultListener;

    public void setSelectResultListener(SelectResultListener selectResultListener) {
        this.selectResultListener = selectResultListener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                json = extras.getString("json");
                background = extras.getString("background");
                title = extras.getString("title");
                titleColor = extras.getString("titleColor");
            }

        }
        try {
            itemContainer.setBackgroundColor(Color.parseColor(background));
        } catch (Exception e) {
        }


        try {
            addTop(title, titleColor);

            JSONObject object = new JSONObject(json);
            JSONArray items = object.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String type = item.getString("type");
                String name = item.getString("name");
                if (type.equalsIgnoreCase("select")) {
                    JSONArray options = item.getJSONArray("values");
                    addSpinner(name, options);
                } else if (type.equalsIgnoreCase("input")) {
                    addEditText(name, item.getString("value"));
                } else if (type.equalsIgnoreCase("switch")) {
                    addSwitch(name, item.getString("value"));
                } else if (type.equalsIgnoreCase("checkbox")) {
                    addCheckBox(name, item.getString("value"));
                } else if (type.equalsIgnoreCase("radio")) {
                    JSONArray radios = item.getJSONArray("values");
                    addRadioBtn(name, radios);
                }
            }

            addBottom();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return new SelectWindowBinder();
    }

    private void addTop(String title, String titleColor) {
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(20);

        try {
            textView.setTextColor(Color.parseColor(titleColor));
        } catch (IllegalArgumentException e) {
            textView.setTextColor(Color.parseColor("#000000"));
        }


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(20, 5, 20, 5);

        itemContainer.addView(textView, params);
    }

    private void addBottom() {
        TextView textView = new TextView(this);
        textView.setText("完成");
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(0, 20, 0, 20);
        textView.setTextSize(16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 5, 0, 5);

        textView.setOnClickListener(finishListener);

        itemContainer.addView(textView, params);
    }


    private final View.OnClickListener finishListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectResultListener == null) return;

            try {

                JSONObject jsonObject = new JSONObject();

                Set<Map.Entry<String, SelectResult>> entries = resultMap.entrySet();
                Iterator<Map.Entry<String, SelectResult>> iterator = entries.iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, SelectResult> next = iterator.next();
                    SelectResult value = next.getValue();
                    JSONObject item = new JSONObject();

                    item.put("result", value.getResult());

                    jsonObject.put(value.getName(), item);

                }

                selectResultListener.onResult(jsonObject);

            } catch (Exception e) {
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        initView();
    }

    private void initView() {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        if (inflater == null) return;

        rootView = inflater.inflate(UZResourcesIDFinder.getResLayoutID("mo_select_layout"), null);

        itemContainer = rootView.findViewById(UZResourcesIDFinder.getResIdID("item_container"));

        windowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);

        getLayoutParams();
        // root view

        windowManager.addView(rootView, params);

    }


    public void removeRootView() {
        if (windowManager != null && rootView != null) {
            windowManager.removeView(rootView);
        }
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
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.width = with * 4 / 5;
        params.height = height * 4 / 5;

    }


    private void addCheckBox(final String name, String value) {
        CheckBox checkBox = new CheckBox(this);

        checkBox.setText(value);

        checkBox.setTextColor(Color.parseColor("#ffffff"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (resultMap.get(name) == null) {
                    resultMap.put(name, new SelectResult());
                }

                resultMap.get(name).setType("checkbox");
                resultMap.get(name).setName(name);
                resultMap.get(name).setResult(isChecked ? "开启" : "关闭");

            }
        });

        params.setMargins(0, 5, 0, 5);

        itemContainer.addView(checkBox, params);

    }


    private void addSwitch(final String name, String value) {
        Switch sh = new Switch(this);

        sh.setText(value);

        sh.setTextColor(Color.parseColor("#ffffff"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);

        sh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (resultMap.get(name) == null) {
                    resultMap.put(name, new SelectResult());
                }

                resultMap.get(name).setType("switch");
                resultMap.get(name).setName(name);
                resultMap.get(name).setResult(isChecked ? "开启" : "关闭");

            }
        });

        params.setMargins(0, 5, 0, 5);

        itemContainer.addView(sh, params);

    }


    private void addEditText(final String name, String value) {

        final EditText editText = new EditText(this);

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setClickable(true);

        editText.setTextColor(Color.parseColor("#ffffff"));

        editText.setHint(value);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (resultMap.get(name) == null) {
                    resultMap.put(name, new SelectResult());
                }

                resultMap.get(name).setType("input");
                resultMap.get(name).setName(name);
                resultMap.get(name).setResult(editText.getText().toString());
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);

        params.setMargins(0, 5, 0, 5);

        itemContainer.addView(editText, params);

    }


    private void addRadioBtn(final String name, JSONArray values) {

        final RadioGroup radioGroup = new RadioGroup(this);

        radioGroup.setOrientation(RadioGroup.VERTICAL);


        try {
            for (int i = 0; i < values.length(); i++) {
                JSONObject valueJson = values.getJSONObject(i);

                RadioButton radioButton = new RadioButton(this);

                radioButton.setTextColor(Color.parseColor("#ffffff"));

                radioGroup.addView(radioButton);

                radioButton.setText(valueJson.getString("value"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton childAt = group.findViewById(checkedId);
                if (resultMap.get(name) == null) {
                    resultMap.put(name, new SelectResult());
                }

                resultMap.get(name).setType("radio");
                resultMap.get(name).setName(name);
                resultMap.get(name).setResult(childAt.getText().toString());

            }
        });


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);

        params.setMargins(0, 5, 0, 5);

        itemContainer.addView(radioGroup, params);

    }


    private void addSpinner(final String name, JSONArray options) {

        Spinner spinner = new Spinner(this);

        final String[] arr = new String[options.length()];

        try {
            for (int i = 0; i < options.length(); i++) {
                JSONObject option = options.getJSONObject(i);
                arr[i] = option.getString("value");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //创建ArrayAdapter对象
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, UZResourcesIDFinder.getResLayoutID("mo_select_spinner_item_layout"), arr);

        spinner.setAdapter(adapter);

        spinner.setGravity(Gravity.CENTER);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (resultMap.get(name) == null) {
                    resultMap.put(name, new SelectResult());
                }

                resultMap.get(name).setType("select");
                resultMap.get(name).setName(name);
                resultMap.get(name).setResult(arr[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(30, 5, 0, 5);

        itemContainer.addView(spinner, params);

    }


}
