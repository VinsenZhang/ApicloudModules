package com.vinsen.chess;

import java.io.Serializable;

public class ChessBean implements Serializable{

    private String name;

    private int iconRes;

    private int bgRes;

    private boolean isFace = true;

    public ChessBean() {
    }

    public ChessBean(String name, int iconRes, int bgRes, boolean isFace) {
        this.name = name;
        this.iconRes = iconRes;
        this.bgRes = bgRes;
        this.isFace = isFace;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public boolean isFace() {
        return isFace;
    }

    public void setFace(boolean face) {
        isFace = face;
    }

    public int getBgRes() {
        return bgRes;
    }

    public void setBgRes(int bgRes) {
        this.bgRes = bgRes;
    }
}
