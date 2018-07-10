package com.superplayer.library;

import android.content.Context;


public class SuperPlayerManage {
    public static SuperPlayerManage videoPlayViewManage;
    private SuperPlayer videoPlayView;

    private SuperPlayerManage() {

    }

    public static SuperPlayerManage getSuperManage() {
        if (videoPlayViewManage == null) {
            videoPlayViewManage = new SuperPlayerManage();
        }
        return videoPlayViewManage;
    }

    public SuperPlayer initialize(Context context) {
        if (videoPlayView == null) {
            videoPlayView = new SuperPlayer(context);
        }
        return videoPlayView;
    }
}
