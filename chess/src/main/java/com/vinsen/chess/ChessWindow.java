package com.vinsen.chess;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
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

    private String background;

    public void jsmethod_chooseChess(UZModuleContext uzModuleContext) {
        //UZResourcesIDFinder.init(activity().getApplication());
        this.callBack = uzModuleContext;
        this.type = uzModuleContext.optInt("type");
        this.background = uzModuleContext.optString("background");
        checkPermissions();
    }

    private void showChessView() {
        Intent intent = new Intent(context(), ChessMainService.class);
        intent.putExtra("chessType", type);
        intent.putExtra("background", background);
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
        }
    }


    private ChessMainService chessMainService;

    private OnChessConfirmResult onChessConfirmResult = new OnChessConfirmResult() {

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
                chessMainService.setResultListener(onChessConfirmResult);
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

