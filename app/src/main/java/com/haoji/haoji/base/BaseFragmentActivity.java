//package com.zhulei.haoji.base;
//
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.FragmentActivity;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.TextView;
//
//import com.zhulei.haoji.R;
//import SystemDialog;
//import SystemUtil;
//
//import java.util.List;
//
///**
// * Created by Administrator on 2016/11/2.
// * 父类activity
// */
//
//public abstract class BaseFragmentActivity extends FragmentActivity implements View.OnClickListener{
//
//    protected TextView mHeadTitle;
//    protected Window window;
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if (SystemBarTotop()){
//            SystemBar();
//        }else{
//            SystemBarTint();
//        }
//        initViews();
//        initDatas();
//
//    }
//
//    /**
//     * 是否顶到状态栏
//     */
//    public abstract boolean SystemBarTotop();
//
//    public void SystemBar(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window = getWindow();// 获取window
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }else{
//            SystemUtil.showSystemBarTint(this,true);
//        }
//    }
//
//    public void SystemBarTint(){
//        SystemUtil.showSystemBarTint(this,true);
//    }
//
//    public void setHeadTitle(String text){
//        try {
//            mHeadTitle= (TextView) findViewById(R.id.head_title);
//            mHeadTitle.setText(text);
//        }catch (Exception e){
//            SystemDialog.DialogToast(this,"初始化标题出错"+e.toString());
//        }
//    }
//
//    public void setHeadTitleMore(String title,boolean leftOnClick,boolean rightOnClick){
//        try {
//            mHeadTitle= (TextView) findViewById(R.id.head_title);
//            mHeadTitle.setText(title);
//            findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//            findViewById(R.id.head_more).setOnClickListener(this);
//        }catch (Exception e){
//            SystemDialog.DialogToast(this,"初始化标题出错"+e.toString());
//        }
//    }
//
//    public void setHeadTitleSearch(String title,boolean leftOnClick,boolean rightOnClick){
//        try {
//            mHeadTitle= (TextView) findViewById(R.id.head_title);
//            mHeadTitle.setText(title);
//            findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//            findViewById(R.id.head_search).setOnClickListener(this);
//        }catch (Exception e){
//            SystemDialog.DialogToast(this,"初始化标题出错"+e.toString());
//        }
//    }
//
//
//    /**
//     * 初始化界面
//     */
//    public abstract void initMainView();
//    /**
//     * 初始化数据
//     */
//    public abstract void initUi();
//
//
//
//}
