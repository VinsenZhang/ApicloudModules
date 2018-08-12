package com.vinsen.circle;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONObject;


public class CircleWindow extends UZModule {


    private UZModuleContext circleClickJsallBack;

    private static final int CIRCLE_SYSTEM_ALERT_WINDOW = 100002;


    public CircleWindow(UZWebView webView) {
        super(webView);

    }


    private String circleContent;

    public void jsmethod_showFloatCircle(UZModuleContext uzModuleContext) {
        //UZResourcesIDFinder.init(activity().getApplication());
        this.circleClickJsallBack = uzModuleContext;
        this.circleContent = uzModuleContext.optString("circleContent");
        checkPermissions();
    }

    private void showFloatCircle() {
        Intent intent = new Intent(context(), FloatCircleService.class);
        intent.putExtra("circleContent", circleContent);
        activity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


    public void jsmethod_closeFloatCircle(UZModuleContext uzModuleContext) {
        if (chessCircle != null) {
            chessCircle.removeContentView();
            activity().unbindService(connection);
        }
    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(activity())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, CIRCLE_SYSTEM_ALERT_WINDOW);
                return;
            } else {
                showFloatCircle();
            }
        }else {
            showFloatCircle();
        }
    }


    private FloatCircleService chessCircle;

    private View.OnClickListener onCircleClickListenner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (circleClickJsallBack == null) return;
            circleClickJsallBack.success(new JSONObject(), false);
        }
    };


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            if (service instanceof FloatCircleService.CircleServiceBinder) {
                chessCircle = ((FloatCircleService.CircleServiceBinder) service).getService();
                chessCircle.setCircleClickListener(onCircleClickListenner);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            chessCircle = null;
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CIRCLE_SYSTEM_ALERT_WINDOW) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(activity())) {
                    Toast.makeText(activity(), "授权失败，检查是否授权成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity(), "授权成功～", Toast.LENGTH_LONG).show();
                    showFloatCircle();
                }
            }
        }
    }

    @Override
    protected void onClean() {
        super.onClean();

    }


}

