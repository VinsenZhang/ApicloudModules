package com.vinsen.chess;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONObject;


public class ChessWindow extends UZModule {


    private UZModuleContext callBack;

    private static final int CHESS_SYSTEM_ALERT_WINDOW = 100001;


    public ChessWindow(UZWebView webView) {
        super(webView);

    }

    private int type = 100002;

    private String background = "#66666666";

    private String title = getAppName(activity().getApplicationContext());

    private String titleColor = "#515151";

    public void jsmethod_chooseChess(UZModuleContext uzModuleContext) {
        this.callBack = uzModuleContext;

        if (uzModuleContext.optInt("type") != 0){
            this.type = uzModuleContext.optInt("type");
        }

        if (!TextUtils.isEmpty(uzModuleContext.optString("background"))){
            this.background = uzModuleContext.optString("background");
        }

        if (!TextUtils.isEmpty(uzModuleContext.optString("title"))){
            this.title = uzModuleContext.optString("title");
        }

        if (!TextUtils.isEmpty(uzModuleContext.optString("titleColor"))){
            this.titleColor = uzModuleContext.optString("titleColor");
        }

        checkPermissions();
    }


    private static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "随便选牌";
    }


    private void showChessView() {
        Intent intent = new Intent(context(), ChessMainService.class);
        Bundle bundle = new Bundle();
        bundle.putInt("chessType", type);
        bundle.putString("background", background);
        bundle.putString("title", title);
        bundle.putString("titleColor", titleColor);
        intent.putExtras(bundle);
        activity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }


    public void jsmethod_closeChess(UZModuleContext uzModuleContext) {
        if (chessMainService != null) {
            chessMainService.removeContentView();
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
                showChessView();
            }
        } else {
            showChessView();
        }
    }


    private ChessMainService chessMainService;

    private OnChessConfirmResult confirmResult = new OnChessConfirmResult() {

        @Override
        public void onResult(JSONObject json) {
            if (callBack == null) return;
            callBack.success(json, true);
            activity().unbindService(connection);
        }
    };


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            if (service instanceof ChessMainService.ServiceBinder) {
                chessMainService = ((ChessMainService.ServiceBinder) service).getService();
                chessMainService.setResultListener(confirmResult);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            chessMainService = null;
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHESS_SYSTEM_ALERT_WINDOW) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(activity())) {
                    Toast.makeText(activity(), "授权失败，检查是否授权成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity(), "授权成功～", Toast.LENGTH_LONG).show();
                    showChessView();
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

