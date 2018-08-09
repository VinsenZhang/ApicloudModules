package com.vinsen.chess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;

import java.util.ArrayList;

public class ChessAdapter extends BaseAdapter {

    private ArrayList<ChessBean> mDatas = new ArrayList<>();

    private Context mContext;

    private int width;

    private int height;

    public ChessAdapter(Context mContext) {
        this.mContext = mContext;

    }

    public void setmDatas(ArrayList<ChessBean> datas) {
        if (mDatas.size() > 0) {
            mDatas.clear();
        }
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }


    public void setWidthAndHeight(int w, int h) {
        this.width = w;
        this.height = h;
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChessItemHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = inflater.inflate(UZResourcesIDFinder.getResLayoutID("mo_chess_adapter_item"), parent, false);
            holder = new ChessItemHolder();
            holder.chessAdapterItemImg = (ImageView) convertView.findViewById(UZResourcesIDFinder.getResIdID("chess_adapter_item_img"));
            convertView.setTag(holder);
        } else {
            holder = (ChessItemHolder) convertView.getTag();
        }

        convertView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        ChessBean chessBean = mDatas.get(position);
        if (chessBean.isFace()) {
            holder.chessAdapterItemImg.setImageResource(chessBean.getIconRes());
        } else {
            holder.chessAdapterItemImg.setImageResource(chessBean.getBgRes());
        }

        return convertView;
    }
}
