package com.vinsen.chess;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChessMainService extends Service {


    /**
     * 1 ： 棋牌
     * 2 ： 麻将
     * 3 ： 牌
     * 4 ： 纸牌
     * <p>
     * 默认给棋牌吧～
     */

    private int chessType;
    private String backgroundColor = "#66666666";
    private String chessTitleStr;
    private String chessTitleColor = "#000000";

    private int orientation;


    private ChessAdapter adapter;


    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private GridView chessContainer;
    private LinearLayout resultContainer;
    private ImageView confirm;

    private int w, h;// chess view width and height
    private WindowManager.LayoutParams params;
    private TextView chessTitle;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientation = newConfig.orientation;

        try {
            mWindowManager.removeView(contentView);
        } catch (Exception ignore) {
            //    ignore this exception
        }
        getLayoutParams();
        mWindowManager.updateViewLayout(contentView, params);
        chessContainer.setNumColumns(getItemNum(orientation));
        adapter.setWidthAndHeight(getEveryChessW(orientation), getEveryChessH(orientation));
        adapter.notifyDataSetChanged();


    }

    public class ServiceBinder extends Binder {

        public ChessMainService getService() {
            return ChessMainService.this;
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                chessType = extras.getInt("chessType", 100002);
                backgroundColor = extras.getString("background");
                chessTitleStr = extras.getString("title");
                chessTitleColor = extras.getString("titleColor");
            }
        }

        try {
            contentView.setBackgroundColor(Color.parseColor(backgroundColor));
        } catch (IllegalArgumentException e) {
            contentView.setBackgroundColor(Color.parseColor("#66666666"));
        }

        try {
            chessTitle.setTextColor(Color.parseColor(chessTitleColor));
        } catch (IllegalArgumentException e) {
            chessTitle.setTextColor(Color.parseColor("#000000"));
        }

        chessTitle.setText(chessTitleStr);

        orientation = getResources().getConfiguration().orientation;

        chessContainer.setNumColumns(getItemNum(orientation));

        addData();
        adapter.setmDatas(datas);
        return new ServiceBinder();
    }


    private int getEveryChessW(int orientation) {
        return w / getItemNum(orientation);
    }

    private int getEveryChessH(int orientation) {
        int itemNum = getItemNum(orientation);
        int chessTotalH = h * 4 / 5;
        if (chessType == ChessEnum.PAI.getCode()) {
            /**
             * 牌 28
             * 0 1|0
             */
            int lineNum = 28 / itemNum + (28 % itemNum > 0 ? 1 : 0);
            return (chessTotalH / lineNum) - 5;

        } else if (chessType == ChessEnum.MAJONG.getCode()) {
            /**
             *麻将
             *饼子，条子，万子 1～9 加上东南西北中发白 34个～
             */
            int lineNum = 34 / itemNum + (34 % itemNum > 0 ? 1 : 0);
            return (chessTotalH / lineNum) - 5;
        } else if (chessType == ChessEnum.ZHIPAI.getCode()) {
            /**
             * 1~10 红黑两色
             */
            int lineNum = 20 / itemNum + (20 % itemNum > 0 ? 1 : 0);
            return (chessTotalH / lineNum) - 5;
        } else {
            /**
             默认是  棋牌  ChessEnum.CHESS
             1,2,3,4,5,6,7,8,9,10,J,Q,K
             一共13个
             一共54个
             */

            int lineNum = 54 / itemNum + (54 % itemNum > 0 ? 1 : 0);
            return (chessTotalH / lineNum) - 5;

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initView();


        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        adapter = new ChessAdapter(getApplicationContext());

        chessContainer.setOnItemClickListener(onChessItemClick);
        chessContainer.setAdapter(adapter);

        try {
            mWindowManager.removeView(contentView);
        } catch (Exception e) {

        }
        orientation = getResources().getConfiguration().orientation;
        getLayoutParams();

        mWindowManager.addView(contentView, params);


    }


    private void getLayoutParams() {

        Display display = mWindowManager.getDefaultDisplay();
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
            params.height = 150 + height * 4 / 5;
            w = with * 3 / 5;
            h = height * 4 / 5;
        } else {
            params.width = with * 4 / 5;
            w = with * 4 / 5;
            if (chessType == ChessEnum.ZHIPAI.getCode()) {
                params.height = 150 + height * 4 / 5;
                h = height * 4 / 5;
            } else {
                params.height = 150 + height * 3 / 5;
                h = height * 3 / 5;
            }

        }

    }

    private View contentView;

    private void initView() {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            TextView textView = new TextView(this);
            textView.setText("啊欧～～～");
            contentView = textView;
            return;
        }
        contentView = inflater.inflate(getLayoutRes("mo_chess_contentview"), null);


        chessTitle = contentView.findViewById(getId("mo_chess_title"));


        chessContainer = contentView.findViewById(getId("mo_chess_ry_container"));

        resultContainer = contentView.findViewById(getId("mo_chess_result_container"));
        confirm = contentView.findViewById(getId("mo_chess_result_confirm"));

        confirm.setOnClickListener(confirmClickListener);

    }


    private final View.OnClickListener confirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            JSONArray array = new JSONArray(result);
            JSONObject json = new JSONObject();
            try {
                json.put("result", array.toString());
                resultListener.onResult(json);
            } catch (JSONException e) {
                e.printStackTrace();
                resultListener.onResult(new JSONObject());
            }

            removeContentView();

        }
    };

    private OnChessConfirmResult resultListener;

    public void setResultListener(OnChessConfirmResult listener) {
        this.resultListener = listener;
    }

    public void removeContentView() {
        if (mWindowManager != null && contentView != null) {
            mWindowManager.removeView(contentView);
        }
    }

    public void addContentView() {
        if (mWindowManager != null && contentView != null) {
            mWindowManager.addView(contentView, params);
        }
    }


    private final AdapterView.OnItemClickListener onChessItemClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ChessBean chessBean = datas.get(position);
            addToResult(chessBean);
            chessBean.setFace(false);
            adapter.notifyDataSetChanged();
        }
    };

    private void addToResult(final ChessBean chessBean) {
        if (!chessBean.isFace()) {
            return;
        }
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(chessBean.getIconRes());
        int height = h / 5 - 10;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height * 2 / 3, height);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        params.setMargins(1, 1, 1, 1);
        imageView.setLayoutParams(params);
        imageView.setTag(chessBean.getName());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultContainer.removeView(view);
                result.remove(view.getTag());
                chessBean.setFace(true);
                adapter.notifyDataSetChanged();
            }
        });

        resultContainer.addView(imageView, params);

        // 添加结果集
        result.add(chessBean.getName());
    }


    private ArrayList<ChessBean> datas = new ArrayList<>();

    private ArrayList<String> result = new ArrayList<>();


    private void addData() {
        datas.clear();
        adapter.setWidthAndHeight(getEveryChessW(orientation), getEveryChessH(orientation));
        if (chessType == ChessEnum.MAJONG.getCode()) {
            // 添加麻将数据
            addMajong();
        } else if (chessType == ChessEnum.ZHIPAI.getCode()) {
            addZHIPAI();
        } else if (chessType == ChessEnum.PAI.getCode()) {
            addPAI();
        } else {
            addQIPAI();
        }
    }


    private int getItemNum(int orientation) {
        if (orientation == 1) {
            return 7;
        }
        if (chessType == ChessEnum.PAI.getCode()) {
            /**
             * 牌
             * 0 1|0
             */
            return 7;

        } else if (chessType == ChessEnum.MAJONG.getCode()) {
            /**
             *麻将
             *饼子，条子，万子 1～9 加上东南西北中发白 34个～
             */
            return 9;
        } else if (chessType == ChessEnum.ZHIPAI.getCode()) {
            /**
             * 1~10 红黑两色
             */
            return 5;
        } else {
            /**
             默认是  棋牌  ChessEnum.CHESS
             1,2,3,4,5,6,7,8,9,10,J,Q,K
             一共13个
             */
            return 13;

        }
    }


    private int getLayoutRes(String resName) {
        return UZResourcesIDFinder.getResLayoutID(resName);
    }


    private int getId(String resName) {
        return UZResourcesIDFinder.getResIdID(resName);
    }

    private int getDrawableRes(String resName) {
        return UZResourcesIDFinder.getResDrawableID(resName);
    }

    /**
     * 初始化棋牌的数据
     */

    private void addQIPAI() {

        datas.add(new ChessBean("hongxin_3", getDrawableRes("mo_chess_hongxin_3"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_2", getDrawableRes("mo_chess_hongxin_2"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_4", getDrawableRes("mo_chess_hongxin_4"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_5", getDrawableRes("mo_chess_hongxin_5"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_6", getDrawableRes("mo_chess_hongxin_6"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_7", getDrawableRes("mo_chess_hongxin_7"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_8", getDrawableRes("mo_chess_hongxin_8"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_9", getDrawableRes("mo_chess_hongxin_9"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_10", getDrawableRes("mo_chess_hongxin_10"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_a", getDrawableRes("mo_chess_hongxin_a"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_j", getDrawableRes("mo_chess_hongxin_j"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_q", getDrawableRes("mo_chess_hongxin_q"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("hongxin_k", getDrawableRes("mo_chess_hongxin_k"), getDrawableRes("mo_chess_qp_bg"), true));

        datas.add(new ChessBean("heixin_2", getDrawableRes("mo_chess_heixin_2"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_3", getDrawableRes("mo_chess_heixin_3"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_4", getDrawableRes("mo_chess_heixin_4"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_5", getDrawableRes("mo_chess_heixin_5"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_6", getDrawableRes("mo_chess_heixin_6"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_7", getDrawableRes("mo_chess_heixin_7"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_8", getDrawableRes("mo_chess_heixin_8"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_9", getDrawableRes("mo_chess_heixin_9"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_10", getDrawableRes("mo_chess_heixin_10"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_a", getDrawableRes("mo_chess_heixin_a"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_j", getDrawableRes("mo_chess_heixin_j"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_q", getDrawableRes("mo_chess_heixin_q"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("heixin_k", getDrawableRes("mo_chess_heixin_k"), getDrawableRes("mo_chess_qp_bg"), true));


        datas.add(new ChessBean("fangpian_2", getDrawableRes("mo_chess_fangpian_2"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_3", getDrawableRes("mo_chess_fangpian_3"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_4", getDrawableRes("mo_chess_fangpian_4"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_5", getDrawableRes("mo_chess_fangpian_5"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_6", getDrawableRes("mo_chess_fangpian_6"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_7", getDrawableRes("mo_chess_fangpian_7"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_8", getDrawableRes("mo_chess_fangpian_8"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_9", getDrawableRes("mo_chess_fangpian_9"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_10", getDrawableRes("mo_chess_fangpian_10"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_a", getDrawableRes("mo_chess_fangpian_a"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_j", getDrawableRes("mo_chess_fangpian_j"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_q", getDrawableRes("mo_chess_fangpian_q"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("fangpian_k", getDrawableRes("mo_chess_fangpian_k"), getDrawableRes("mo_chess_qp_bg"), true));

        datas.add(new ChessBean("meihua_2", getDrawableRes("mo_chess_meihua_2"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_3", getDrawableRes("mo_chess_meihua_3"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_4", getDrawableRes("mo_chess_meihua_4"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_5", getDrawableRes("mo_chess_meihua_5"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_6", getDrawableRes("mo_chess_meihua_6"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_7", getDrawableRes("mo_chess_meihua_7"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_8", getDrawableRes("mo_chess_meihua_8"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_9", getDrawableRes("mo_chess_meihua_9"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_10", getDrawableRes("mo_chess_meihua_10"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_a", getDrawableRes("mo_chess_meihua_a"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_j", getDrawableRes("mo_chess_meihua_j"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_q", getDrawableRes("mo_chess_meihua_q"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("meihua_k", getDrawableRes("mo_chess_meihua_k"), getDrawableRes("mo_chess_qp_bg"), true));

        datas.add(new ChessBean("dawang", getDrawableRes("mo_chess_zp_dw"), getDrawableRes("mo_chess_qp_bg"), true));
        datas.add(new ChessBean("xiaowang", getDrawableRes("mo_chess_zp_xw"), getDrawableRes("mo_chess_qp_bg"), true));

    }

    /**
     * 初始化牌的数据
     */
    private void addPAI() {
        datas.add(new ChessBean("pai_0", getDrawableRes("mo_chess_pai_0"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_1", getDrawableRes("mo_chess_pai_1"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_2", getDrawableRes("mo_chess_pai_2"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_3", getDrawableRes("mo_chess_pai_3"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_4", getDrawableRes("mo_chess_pai_4"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_5", getDrawableRes("mo_chess_pai_5"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_5", getDrawableRes("mo_chess_pai_6"), getDrawableRes("mo_chess_pai_bj"), true));

        datas.add(new ChessBean("pai_21", getDrawableRes("mo_chess_pai_21"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_22", getDrawableRes("mo_chess_pai_22"), getDrawableRes("mo_chess_pai_bj"), true));

        datas.add(new ChessBean("pai_31", getDrawableRes("mo_chess_pai_31"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_32", getDrawableRes("mo_chess_pai_32"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_33", getDrawableRes("mo_chess_pai_33"), getDrawableRes("mo_chess_pai_bj"), true));

        datas.add(new ChessBean("pai_41", getDrawableRes("mo_chess_pai_41"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_42", getDrawableRes("mo_chess_pai_42"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_43", getDrawableRes("mo_chess_pai_43"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_44", getDrawableRes("mo_chess_pai_44"), getDrawableRes("mo_chess_pai_bj"), true));

        datas.add(new ChessBean("pai_51", getDrawableRes("mo_chess_pai_51"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_52", getDrawableRes("mo_chess_pai_52"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_53", getDrawableRes("mo_chess_pai_53"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_54", getDrawableRes("mo_chess_pai_54"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_55", getDrawableRes("mo_chess_pai_55"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_6", getDrawableRes("mo_chess_pai_6"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_61", getDrawableRes("mo_chess_pai_61"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_62", getDrawableRes("mo_chess_pai_62"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_63", getDrawableRes("mo_chess_pai_63"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_64", getDrawableRes("mo_chess_pai_64"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_65", getDrawableRes("mo_chess_pai_65"), getDrawableRes("mo_chess_pai_bj"), true));
        datas.add(new ChessBean("pai_66", getDrawableRes("mo_chess_pai_66"), getDrawableRes("mo_chess_pai_bj"), true));
    }

    /**
     * 初始化纸牌的数据
     */
    private void addZHIPAI() {
        datas.add(new ChessBean("zp_1", getDrawableRes("mo_chess_zp_1"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_2", getDrawableRes("mo_chess_zp_2"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_3", getDrawableRes("mo_chess_zp_3"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_4", getDrawableRes("mo_chess_zp_4"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_5", getDrawableRes("mo_chess_zp_5"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_6", getDrawableRes("mo_chess_zp_6"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_7", getDrawableRes("mo_chess_zp_7"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_8", getDrawableRes("mo_chess_zp_8"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_9", getDrawableRes("mo_chess_zp_9"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_10", getDrawableRes("mo_chess_zp_10"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_d1", getDrawableRes("mo_chess_zp_d1"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_d2", getDrawableRes("mo_chess_zp_d2"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_d3", getDrawableRes("mo_chess_zp_d3"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_d4", getDrawableRes("mo_chess_zp_d4"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_d5", getDrawableRes("mo_chess_zp_d5"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_d6", getDrawableRes("mo_chess_zp_d6"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_d7", getDrawableRes("mo_chess_zp_d7"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_d8", getDrawableRes("mo_chess_zp_d8"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_d9", getDrawableRes("mo_chess_zp_d9"), getDrawableRes("mo_chess_zp_bj"), true));
        datas.add(new ChessBean("zp_d10", getDrawableRes("mo_chess_zp_d10"), getDrawableRes("mo_chess_zp_bj"), true));
    }

    /**
     * 初始化麻将的数据
     */
    private void addMajong() {
        datas.add(new ChessBean("tong_1", getDrawableRes("mo_chess_tong_1"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tong_2", getDrawableRes("mo_chess_tong_2"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tong_3", getDrawableRes("mo_chess_tong_3"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tong_4", getDrawableRes("mo_chess_tong_4"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tong_5", getDrawableRes("mo_chess_tong_5"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tong_6", getDrawableRes("mo_chess_tong_6"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tong_7", getDrawableRes("mo_chess_tong_7"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tong_8", getDrawableRes("mo_chess_tong_8"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tong_9", getDrawableRes("mo_chess_tong_9"), getDrawableRes("mo_chess_ma_bj"), true));

        datas.add(new ChessBean("tiao_1", getDrawableRes("mo_chess_tiao_1"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tiao_2", getDrawableRes("mo_chess_tiao_2"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tiao_3", getDrawableRes("mo_chess_tiao_3"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tiao_4", getDrawableRes("mo_chess_tiao_4"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tiao_5", getDrawableRes("mo_chess_tiao_5"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tiao_6", getDrawableRes("mo_chess_tiao_6"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tiao_7", getDrawableRes("mo_chess_tiao_7"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tiao_8", getDrawableRes("mo_chess_tiao_8"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("tiao_9", getDrawableRes("mo_chess_tiao_9"), getDrawableRes("mo_chess_ma_bj"), true));

        datas.add(new ChessBean("wan_1", getDrawableRes("mo_chess_wan_1"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("wan_2", getDrawableRes("mo_chess_wan_2"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("wan_3", getDrawableRes("mo_chess_wan_3"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("wan_4", getDrawableRes("mo_chess_wan_4"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("wan_5", getDrawableRes("mo_chess_wan_5"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("wan_6", getDrawableRes("mo_chess_wan_6"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("wan_7", getDrawableRes("mo_chess_wan_7"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("wan_8", getDrawableRes("mo_chess_wan_8"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("wan_9", getDrawableRes("mo_chess_wan_9"), getDrawableRes("mo_chess_ma_bj"), true));

        datas.add(new ChessBean("dong", getDrawableRes("mo_chess_dong"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("nan", getDrawableRes("mo_chess_nan"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("xi", getDrawableRes("mo_chess_xi"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("bei", getDrawableRes("mo_chess_bei"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("zhong", getDrawableRes("mo_chess_zhong"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("fa", getDrawableRes("mo_chess_fa"), getDrawableRes("mo_chess_ma_bj"), true));
        datas.add(new ChessBean("bai", getDrawableRes("mo_chess_bai"), getDrawableRes("mo_chess_ma_bj"), true));
    }


}
