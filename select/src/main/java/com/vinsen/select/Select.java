package com.vinsen.select;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONObject;

public class Select extends UZModule {


    private UZModuleContext callBack;

    private static final int CHESS_SYSTEM_ALERT_WINDOW = 1001;


    private String background = "";

    private String title = "请选择一种模式";

    private String titleColor = "#000000";

    private String json;


    public Select(UZWebView webView) {
        super(webView);
    }


    public void jsmethod_choose(UZModuleContext uzModuleContext) {
        this.callBack = uzModuleContext;

        if (!TextUtils.isEmpty(uzModuleContext.optString("params"))) {
            this.json = uzModuleContext.optString("params");
        } else {
            return;
        }

        if (!TextUtils.isEmpty(uzModuleContext.optString("background"))) {
            this.background = uzModuleContext.optString("background");
        }

        if (!TextUtils.isEmpty(uzModuleContext.optString("title"))) {
            this.title = uzModuleContext.optString("title");
        }

        if (!TextUtils.isEmpty(uzModuleContext.optString("titleColor"))) {
            this.titleColor = uzModuleContext.optString("titleColor");
        }

        checkPermissions();
    }



    private void showChoose() {
        Intent intent = new Intent(context(), SelectWindow.class);
        Bundle bundle = new Bundle();
        bundle.putString("json", json);
        bundle.putString("background", background);
        bundle.putString("title", title);
        bundle.putString("titleColor", titleColor);
        intent.putExtras(bundle);
        activity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }


    public void jsmethod_close(UZModuleContext uzModuleContext) {
        if (selectWindow != null) {
            selectWindow.removeRootView();
            activity().unbindService(connection);
        }

    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(activity())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, CHESS_SYSTEM_ALERT_WINDOW);
                return;
            } else {
                showChoose();
            }
        } else {
            showChoose();
        }
    }


    private SelectWindow selectWindow;

  private SelectResultListener selectResultListener = new SelectResultListener() {
      @Override
      public void onResult(JSONObject json) {
          if (callBack != null){
              callBack.success(json, false);
              selectWindow.removeRootView();
              activity().unbindService(connection);
          }
      }
  };


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            if (service instanceof SelectWindow.SelectWindowBinder) {
                selectWindow = ((SelectWindow.SelectWindowBinder) service).getService();
                selectWindow.setSelectResultListener(selectResultListener);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            selectWindow = null;
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHESS_SYSTEM_ALERT_WINDOW) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(activity())) {
                    Toast.makeText(activity(), "授权失败，检查是否授权成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity(), "授权成功～", Toast.LENGTH_SHORT).show();
                    showChoose();
                }
            }
        }
    }

    @Override
    protected void onClean() {
        super.onClean();
        if (null != callBack) {
            callBack = null;
        }
    }


}
