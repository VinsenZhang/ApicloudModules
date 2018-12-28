package com.vinsen.manager;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Manager extends UZModule {

    private UZModuleContext callBack;

    public Manager(UZWebView webView) {
        super(webView);

    }

    public void jsmethod_getAppsInfo(UZModuleContext uzModuleContext) {

        this.callBack = uzModuleContext;

        try {

            JSONArray array = new JSONArray();
            PackageManager pManager = activity().getPackageManager();
            //获取手机内所有应用
            List<PackageInfo> paklist = pManager.getInstalledPackages(0);
            for (int i = 0; i < paklist.size(); i++) {
                PackageInfo packageInfo = paklist.get(i);
                //判断是否为非系统预装的应用程序
                if ((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) ==
                        0) {


                    String appName = packageInfo.applicationInfo.loadLabel(pManager)
                            .toString();
                    final String packageName = packageInfo.packageName;
                    String versionName = packageInfo.versionName;
                    final BitmapDrawable appicon = (BitmapDrawable) packageInfo.applicationInfo
                            .loadIcon
                                    (pManager);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.saveImage(packageName, appicon.getBitmap());
                        }
                    }).start();

                    JSONObject object = new JSONObject();

                    object.put("appName", appName);
                    object.put("packageName", packageName);
                    object.put("versionName", versionName);
                    object.put("appIcon", Utils.savePath + packageName + ".jpg");

                    array.put(object);
                }
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result", array);

            callBack.success(jsonObject, false);

        } catch (Exception e) {

        }
    }

}
