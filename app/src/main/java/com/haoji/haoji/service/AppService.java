package com.haoji.haoji.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * app服务类接收实时信息
 */

public class AppService extends Service implements Runnable, NetIntentCallBackListener.INetIntentCallBack {


    private Thread thread;

    private boolean flag = true;

    private long SleepTime = 10 * 1000;

    private long runNum=0;

    private int point = 0;

    private SharedPreferencesUtil util;

    private ArrayList<HashMap> maps = new ArrayList<HashMap>();

    @Override
    public void onCreate() {
        super.onCreate();
        util = new SharedPreferencesUtil(this);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (util.getUserId()!=null){
                        new NetIntent().client_getSysInfo(util.getUserId(), new NetIntentCallBackListener(AppService.this));
                    }else{
                        System.out.println("用户未登录...");
                    }
                    break;
                case 1:
                    updateView();
                    break;
            }
        }
    };

    private void updateView() {
        point=0;
        for (int i = 0; i < maps.size(); i++) {
            HashMap map = maps.get(i);
            if (map.get("relevantresults").toString().equals("")&&map.get("type").toString().equals("2")) {
                point++;
            }
        }
//        Intent intent=new Intent();
//        intent.putExtra("point",point);
//        intent.setAction(Constants.Action_badge);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startThread();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startThread() {
        if (thread != null && !thread.isAlive()) {
            thread.start();
        } else {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        while (flag) {
            try {
                runNum++;
                System.out.println("运行次数:"+runNum);
                Thread.sleep(SleepTime);
                handler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("sysInfoList")) {
                maps.clear();
                maps.addAll(Util.toHashMap(new JSONArray(jsonObject.getString("sysInfoList"))));
                handler.sendEmptyMessage(1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
