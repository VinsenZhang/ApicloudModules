package com.vinsen.moduleAcb;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;


public class APIModuleAcb extends UZModule {


    private UZModuleContext jsCallBack;


    public APIModuleAcb(UZWebView webView) {
        super(webView);

    }


    public void jsmethod_isAcbOpen(UZModuleContext uzModuleContext) {
        this.jsCallBack = uzModuleContext;

        JSONObject res = new JSONObject();
        try {
            res.put("status", isAccessibilitySettingsOn(activity()) ? "success" : "err");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsCallBack.success(res, true);

    }


    public void jsmethod_goSetAcb(UZModuleContext uzModuleContext) {
        //if (isAccessibilitySettingsOn(activity())) {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        //}
    }


    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + ACBService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {

        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    protected void onClean() {
        super.onClean();
        if (null != jsCallBack) {
            jsCallBack = null;
        }
    }


}

