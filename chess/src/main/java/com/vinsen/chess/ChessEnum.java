package com.vinsen.chess;

public enum ChessEnum {

    CHESS(100001, "棋牌"),
    MAJONG(100002, "麻将"),
    ZHIPAI(100003, "纸牌"),
    PAI(100004, "牌");

    private int code;

    private String desc;

    ChessEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
