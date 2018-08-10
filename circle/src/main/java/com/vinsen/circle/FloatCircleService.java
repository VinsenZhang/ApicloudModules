package com.vinsen.circle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;

public class FloatCircleService extends Service {


    private WindowManager windowManager;
    private TextView floatCircle;
    private WindowManager.LayoutParams params;

    public class CircleServiceBinder extends Binder {

        public FloatCircleService getService() {
            return FloatCircleService.this;
        }

    }


    private String circleContent = "随便\n选牌";


    @Override
    public IBinder onBind(Intent intent) {
        if (intent != null) {
            circleContent = intent.getStringExtra("circleContent");
        }
        return new CircleServiceBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);
        //设置View默认的摆放位置
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        //设置window type
        if (Build.VERSION.SDK_INT >= 26) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //设置背景为透明
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = 100;
        params.height = 100;

        floatCircle = new TextView(getApplicationContext());
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(-1, -1);
        floatCircle.setLayoutParams(imgParams);
        floatCircle.setText(circleContent);
        floatCircle.setTextSize(10);
        floatCircle.setGravity(Gravity.CENTER);
        floatCircle.setBackgroundResource(UZResourcesIDFinder.getResDrawableID("mo_floatcircle_shape"));

        if (windowManager == null) return;
        windowManager.addView(floatCircle, params);


        floatCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleClickListener == null) return;
                circleClickListener.onClick(v);
            }
        });

    }


    public void removeContentView() {
        if (windowManager != null && floatCircle != null) {
            windowManager.removeView(floatCircle);
        }
    }


    private View.OnClickListener circleClickListener;

    public void setCircleClickListener(View.OnClickListener listener) {
        this.circleClickListener = listener;
    }
}
