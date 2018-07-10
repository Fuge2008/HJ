package com.haoji.haoji.app;

import android.app.Activity;

import java.util.ArrayList;



public class ActivityManager {

    static ActivityManager manager;
    static ArrayList<Activity> list=new ArrayList<Activity>();

    private ActivityManager(){

    }

    public static ActivityManager init(){
        if (manager==null){
            manager=new ActivityManager();
        }
        return manager;
    }

    public static void addActivity(Activity activity){
        list.add(activity);
    }

    public static void removeAll(){
        for (Activity activity : list) {
            activity.finish();
        }
    }

}
