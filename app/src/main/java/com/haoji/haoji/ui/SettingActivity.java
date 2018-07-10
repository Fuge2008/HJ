package com.haoji.haoji.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.DataCleanManagerUtils;
import com.haoji.haoji.util.SPUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;



public class SettingActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack {
    private static String TAG = SettingActivity.class.getSimpleName();
    @BindView(R.id.tv_modify_psw)
    TextView tvModifyPsw;
    @BindView(R.id.tv_info_notification)
    TextView tvInfoNotification;
    @BindView(R.id.tv_app_update)
    TextView tvAppUpdate;
    @BindView(R.id.tv_cache_clear)
    TextView tvCacheClear;
    @BindView(R.id.tv_cache_storage)
    TextView tvCacheStorage;
    @BindView(R.id.ll_cache_clear)
    LinearLayout llCacheClear;
    @BindView(R.id.tv_call_customer)
    TextView tvCallCustomer;
    @BindView(R.id.tv_about_haoji)
    TextView tvAboutHaoji;
    @BindView(R.id.btn_exit)
    Button btnExit;

    private SharedPreferencesUtil util;
    private JSONObject jsonkf;
    private JSONObject jsonabout;
    private String totalCache;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        new AlertDialog.Builder(SettingActivity.this).setTitle("客服").setMessage("客服电话:" + jsonkf.getString("content")).setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent phoneIntent = null;
                                try {
                                    phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + jsonkf.getString("content")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(phoneIntent);
                            }
                        }).setNegativeButton("取消", null).create().show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        new AlertDialog.Builder(SettingActivity.this).setTitle("关于我们").setMessage(jsonabout.getString("content")).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setNegativeButton("取消", null).create().show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };



    @Override
    public void initMainView() {
        setContentView(R.layout.em_activity_setting);
        ButterKnife.bind(this);
        util = new SharedPreferencesUtil(this);
        try {
            totalCache = DataCleanManagerUtils.getTotalCacheSize(SettingActivity.this);
            if (StringUtils.isNotEmpty(totalCache, true)) {
                tvCacheStorage.setText(totalCache);
            } else {
                tvCacheStorage.setText("无缓存");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initUi() {
        Util.setHeadTitleMore(this, "设置", true);
        findViewById(R.id.head_more).setVisibility(View.GONE);

    }

    @Override
    public void loadData() {
    }
    /*
     * 获取当前程序的版本号
	 */
    private String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packInfo.versionName;
    }



    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        System.out.println(response);
        try {
            final JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("code")) {
                if (jsonObject.has("checkVersion")) {
                    ToastUtils.showShortToastSafe(SettingActivity.this, jsonObject.getString("checkVersion"));
                    if (jsonObject.has("url")) {
                        new AlertDialog.Builder(SettingActivity.this).setTitle("更新提示").setMessage("有最新版本，需要升级吗?")
                                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = null;
                                        try {
                                            i = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("url")));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        startActivity(i);
                                    }
                                }).setNegativeButton("取消", null).create().show();
                    }
                }
                if (jsonObject.has("serviceCall")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("serviceCall");
                    jsonkf = jsonArray.getJSONObject(0);
                    handler.sendEmptyMessage(0);
                }
                if (jsonObject.has("ourInfo")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("ourInfo");
                    jsonabout = jsonArray.getJSONObject(0);
                    handler.sendEmptyMessage(1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.tv_modify_psw, R.id.tv_info_notification, R.id.tv_app_update, R.id.ll_cache_clear, R.id.tv_call_customer, R.id.tv_about_haoji, R.id.btn_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_modify_psw:
                startActivity(new Intent(SettingActivity.this,ModifyPasswordActivity.class));
                break;
            case R.id.tv_info_notification:
                startActivity(new Intent(SettingActivity.this, FriendSearchActivity.class));
                break;
            case R.id.tv_app_update:
                NetIntent netIntent = new NetIntent();
                netIntent.client_checkVersion(getVersionName(), new NetIntentCallBackListener(this));
                break;


            case R.id.ll_cache_clear:
                if (StringUtils.equals(tvCacheStorage.getText(), "无缓存")) {
                    ToastUtils.showShortToastSafe(SettingActivity.this, "暂无缓存");
                } else {
                    try {
                        DataCleanManagerUtils.clearAllCache(SettingActivity.this);
                        ToastUtils.showShortToastSafe(SettingActivity.this, "已清理缓存" + totalCache);
                        tvCacheStorage.setText("无缓存");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.tv_call_customer:
                NetIntent netIntent1 = new NetIntent();
                netIntent1.client_serviceCall(new NetIntentCallBackListener(this));
                break;
            case R.id.tv_about_haoji:

                Intent myIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(StringUtils.TITLE, "关于我们");
                bundle.putString(StringUtils.URL, "http://www.hao-ji.cn/");
                myIntent.putExtras(bundle);
                myIntent.setClass(SettingActivity.this, WebViewActivity.class);
                startActivity(myIntent);
                break;
            case R.id.btn_exit:
                exitApp();
                break;

        }
    }

    private void exitApp() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("确定注销登录？").setPositiveButton("退出帐号", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                util.setIsLogin(false);
                RongIM.getInstance().logout();
                //LocalBroadcastManager.getInstance(SettingActivity.this).sendBroadcast(new Intent().setAction(Constants.Action_logout));
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                SettingActivity.this.finish();
            }
        }).setNegativeButton("切换账号", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPUtils.put(SettingActivity.this, "is_login_self", false);
                SPUtils.put(SettingActivity.this, "is_save_psw", false);
                util.setIsLogin(false);
                RongIM.getInstance().logout();
                //LocalBroadcastManager.getInstance(SettingActivity.this).sendBroadcast(new Intent().setAction(Constants.Action_logout));
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                SettingActivity.this.finish();
            }
        }).create().show();
    }
}
