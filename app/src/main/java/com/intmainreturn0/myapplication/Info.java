package com.intmainreturn0.myapplication;

import android.graphics.Bitmap;

/**
 * Created by lz on 15-5-23.
 */
public class Info {
    /**
     * 代表天气状况的图片
     */
    private Bitmap infoBitmap;
    /**
     * 具体天气的文字说明
     */
    private String infoText;
    public String pat;
    public String msg;

    public Info(Bitmap weatherBitmap, String weatherText, String ppat, String ms) {
        super();
        this.infoBitmap = weatherBitmap;
        this.infoText = weatherText;
        this.pat = ppat;
        this.msg = ms;
    }

    public Bitmap getBitmap() {
        return infoBitmap;
    }

    public void setBitmap(Bitmap weatherBitmap) {
        this.infoBitmap = weatherBitmap;
    }

    public String getText() {
        return infoText;
    }

    public void setText(String weatherText) {
        this.infoText = weatherText;
    }

}