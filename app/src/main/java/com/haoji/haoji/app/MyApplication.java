package com.haoji.haoji.app;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.umeng.analytics.MobclickAgent;

import cn.sharesdk.framework.ShareSDK;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.ImageLoaderConfiguration;
import io.rong.imkit.RongIM;

import com.fly.iconify.Iconify;
import com.fly.iconify.fontawesome.module.FontAwesomeLightModule;
import com.fly.iconify.fontawesome.module.FontAwesomeModule;


public class MyApplication extends Application{
    private static Application app;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        //Fresco.initialize(this);
        //MultiDex.install(this);//分包设置
        SDKInitializer.initialize(this);
        RongIM.init(this);
        ShareSDK.initSDK(this);
        System.out.println("init...");
        initImageLoader();
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        //AnalyticsConfig.enableEncrypt(boolean enable);//6.0.0版本以前
        //MobclickAgent.enableEncrypt(true);//6.0.0版本及以后
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);//基本数据统计入口
        //MobclickAgent.setCatchUncaughtExceptions(true);
        //MobclickAgent.setDebugMode( true );调试时开启，防止数据污染

        Iconify.with(new FontAwesomeLightModule())
                .with(new FontAwesomeModule());

    }
    private void initImageLoader() {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

    }
//     @Override
//     protected void attachBaseContext(Context base) {//分包设置
//     super.attachBaseContext(base);
//     MultiDex.install(this);
//     }



    public static Application getApp()
    {
        return app;
    }

    public static Context getAppContext() {
        return getApp().getApplicationContext();
    }
}

