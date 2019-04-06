package com.haoji.haoji.base;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haoji.haoji.R;
import com.haoji.haoji.app.ActivityManager;
import com.haoji.haoji.receiver.ConnectionChangeReceiver;
import com.haoji.haoji.util.StatusBarManager;
import com.haoji.haoji.util.ToastUtils;
import com.luck.picture.lib.model.FunctionConfig;
import com.umeng.analytics.MobclickAgent;


/**
 * 界面基类
 */
public abstract class BaseActivity extends FragmentActivity {
    protected TextView mHeadTitle;
    protected Window window;

    protected ImageView iv_back;// 返回图片
    private ConnectionChangeReceiver mReceiver;// 网络连接状态监听广播

    //	protected FlippingLoadingDialog mLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StatusBarManager.compat(this, 0x002B2B2B);//设置状态栏颜色
        StatusBarManager.compat(this);
        super.onCreate(savedInstanceState);
        ActivityManager.init().addActivity(this);
        registerReceiver();
        initMainView();
        initUi();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);// 注销网络广播监听

    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new ConnectionChangeReceiver();
        this.registerReceiver(mReceiver, filter);
    }
//	public FlippingLoadingDialog getLoadingDialog(String msg) {
//		if (mLoadingDialog == null)
//			mLoadingDialog = new FlippingLoadingDialog(this, msg);
//		return mLoadingDialog;
//	}

    /**
     * 针对6.0动态请求权限问题
     * 判断是否允许此权限
     *
     * @param permissions
     * @return
     */
    protected boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void setHeadTitle(String text) {
        try {
            mHeadTitle = (TextView) findViewById(R.id.head_title);
            mHeadTitle.setText(text);
        } catch (Exception e) {
            //SystemDialog.DialogToast(this, "初始化标题出错" + e.toString());
            ToastUtils.showShortToastSafe(this, "初始化标题出错" + e.toString());
        }
    }

    public void setHeadTitleMore(String title, boolean leftOnClick, boolean rightOnClick) {
        try {
            mHeadTitle = (TextView) findViewById(R.id.head_title);
            mHeadTitle.setText(title);
            findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            findViewById(R.id.head_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } catch (Exception e) {
            //SystemDialog.DialogToast(this, "初始化标题出错" + e.toString());
            ToastUtils.showShortToastSafe(this, "初始化标题出错" + e.toString());
        }
    }

    public void setHeadTitleSearch(String title, boolean leftOnClick, boolean rightOnClick) {
        try {
            mHeadTitle = (TextView) findViewById(R.id.head_title);
            mHeadTitle.setText(title);
            findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            findViewById(R.id.head_search).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } catch (Exception e) {
            //SystemDialog.DialogToast(this, "初始化标题出错" + e.toString());
            ToastUtils.showShortToastSafe(this, "初始化标题出错" + e.toString());
        }
    }

    /**
     * 动态请求权限
     *
     * @param code
     * @param permissions
     */
    protected void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FunctionConfig.CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    showToast("拍照权限已被拒绝");

                }
                break;
        }
    }

    /**
     * 启动相机
     */
    protected void startCamera() {
    }

    protected void showToast(String msg) {
        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 初始化视图
     *
     */
    public abstract void initMainView();

    /**
     * 初始化界面
     */
    public abstract void initUi();

    /**
     * 初始化数据
     */
    public abstract void loadData();

    public void onResume() {//友盟统计
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}
