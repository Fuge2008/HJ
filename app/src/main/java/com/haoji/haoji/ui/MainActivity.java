package com.haoji.haoji.ui;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haoji.haoji.R;
import com.haoji.haoji.app.ActivityManager;
import com.haoji.haoji.app.RongCloudEvent;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.comment.AddFriendActivity;
import com.haoji.haoji.custom.DragPointView;
import com.haoji.haoji.custom.TitlePopup;
import com.haoji.haoji.custom.zxing.QRGenerateActivity;
import com.haoji.haoji.custom.zxing.QRScanActivity;
import com.haoji.haoji.fragment.NewsCenterFragment;
import com.haoji.haoji.fragment.NiceRecordFragment;
import com.haoji.haoji.fragment.ProfileFragment;
import com.haoji.haoji.fragment.ShareMultimediaFragment;
import com.haoji.haoji.model.ActionItem;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.service.AppService;
import com.haoji.haoji.util.SPUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.EventMore;
import io.rong.imkit.HomeWatcherReceiver;
import io.rong.imkit.IUnReadMessageObserver;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import kr.co.namee.permissiongen.PermissionGen;
import me.leolin.shortcutbadger.ShortcutBadger;


public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, DragPointView.OnDragListencer, IUnReadMessageObserver, NetIntentCallBackListener.INetIntentCallBack {
    private static String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.head_back)
    LinearLayout headBack;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.head_more)
    LinearLayout headMore;

    @BindView(R.id.rb_nice_record)
    RadioButton rbNiceRecord;
    @BindView(R.id.rb_share_media)
    RadioButton rbShareMedia;
    @BindView(R.id.rb_news_cneter)
    RadioButton rbNewsCneter;
    @BindView(R.id.rb_profile_cnter)
    RadioButton rbProfileCnter;
    @BindView(R.id.rg_home)
    RadioGroup rgHome;

    private SharedPreferencesUtil util;
    private TitlePopup titlePopup; // 定义标题栏弹窗按钮
    private DragPointView mUnreadNumView;
    private Conversation.ConversationType[] mConversationsTypes = null;
    List<Fragment> list = null;
    @Override
    public void initMainView() {
        setContentView(R.layout.activity_maintab);
        ButterKnife.bind(this);
        ActivityManager.init().addActivity(this);
        ButterKnife.bind(this);
        util = new SharedPreferencesUtil(this);
        mUnreadNumView = (DragPointView) findViewById(R.id.seal_num);
        mUnreadNumView.setText("12");
        mUnreadNumView.setDragListencer(this);


        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE
        };
        EventMore.addUnReadMessageCountChangedObserver(this, conversationTypes);
        mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP,
                Conversation.ConversationType.PUBLIC_SERVICE,
                Conversation.ConversationType.APP_PUBLIC_SERVICE,
                Conversation.ConversationType.SYSTEM};

    }


    @Override
    public void initUi() {
        headBack.setVisibility(View.GONE);
        rgHome.setOnCheckedChangeListener(this);
        rbNiceRecord.setChecked(true);
        headTitle.setText("好  记");
        if (!util.getIsLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        Intent intent = new Intent(this, AppService.class);
        startService(intent);
        initRong();

       // Intent intent2 = new Intent(Intent.ACTION_MAIN);
        //intent2.addCategory(Intent.CATEGORY_HOME);
        //ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent2, PackageManager.MATCH_DEFAULT_ONLY);
       // String currentHomePackage = resolveInfo.activityInfo.packageName;

//        TextView textViewHomePackage = (TextView) findViewById(R.id.textViewHomePackage);
//        textViewHomePackage.setText("launcher:" + currentHomePackage);

    }




private void changeFragment(Fragment targetFragment) {
    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, targetFragment, "fragment")
            .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
}

    @Override
    public void onCheckedChanged(RadioGroup arg0, int checkedId) {
        if (rbNiceRecord.getId() == checkedId) {
            changeFragment(new NiceRecordFragment());
            headTitle.setText("好  记");
        } else if (rbShareMedia.getId() == checkedId) {
            headTitle.setText("共享记");
            changeFragment(new ShareMultimediaFragment());
        } else if (rbNewsCneter.getId() == checkedId) {
            changeFragment(new NewsCenterFragment());
            headTitle.setText("消  息");
        }else if (rbProfileCnter.getId() == checkedId) {
            changeFragment(new ProfileFragment());
            headTitle.setText("我  的");
        }
    }

    @Override
    public void loadData() {
        headMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(v);
            }
        });
        if(util.getFistStart()){
            PermissionGen.with(this)
                    .addRequestCode(100)
                    .permissions(
                            //电话通讯录
                            Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.READ_PHONE_STATE,
                            //位置
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            //相机、麦克风
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.CAMERA,
                            //存储空间
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_SETTINGS
                    )
                    .request();
        }



    }

    private void showPopWindow(View v) {
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // 给标题栏弹窗添加子类

        titlePopup.addAction(new ActionItem(this, "添加朋友",
                R.drawable.popwindow_add_icon));
        titlePopup.addAction(new ActionItem(this, "更多正能量",
                R.drawable.popwindow_more_icon));
        titlePopup.addAction(new ActionItem(this, "我的二维码",
                R.drawable.popwindow_msg_icon));
        titlePopup.addAction(new ActionItem(this, "扫一扫",
                R.drawable.popwindow_sc_icon));

        titlePopup.show(v);
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {

            @Override
            public void onItemClick(ActionItem item, int position) {
                if(util.getIsLogin()){
                    if (item.mTitle.equals("添加朋友")) {
                        startActivity(new Intent(MainActivity.this, AddFriendActivity.class));
                    } else if (item.mTitle.equals("我的二维码")) {

                        startActivity(new Intent(MainActivity.this, QRGenerateActivity.class));

                    } else if (item.mTitle.equals("扫一扫")) {

                        startActivity(new Intent(MainActivity.this, QRScanActivity.class));

                    } else if (item.mTitle.equals("更多正能量")) {

                        startActivity(new Intent(MainActivity.this, MoreByYearActivity.class));
                    }
                }else {
                    ToastUtils.showShortToast(MainActivity.this,"抱歉，请您先登录！！");
                }


            }

        });
    }

//    class MyFragmentAdapter extends FragmentStatePagerAdapter {
//
//        List<Fragment> list;
//
//        public MyFragmentAdapter(FragmentManager fm, List<Fragment> list) {
//            super(fm);
//            this.list = list;
//        }
//
//        @Override
//        public Fragment getItem(int arg0) {
//            return list.get(arg0);
//        }
//
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    private void initRong() {
        if (util.getIsLogin()) {
            Util.connect(util.getToken());
            RongCloudEvent.init(this);
        }
    }

    private static boolean isExit = false;// 定义是否已退出应用

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {// 返回事件
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {// 双击返回键退出应用
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            // 返回键双击延迟
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
        }
    }

    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onCountChanged(int count) {
        if (count == 0) {
            mUnreadNumView.setVisibility(View.GONE);
        } else if (count > 0 && count < 100) {
            mUnreadNumView.setVisibility(View.VISIBLE);
            mUnreadNumView.setText(String.valueOf(count));
        } else {
            mUnreadNumView.setVisibility(View.VISIBLE);
            mUnreadNumView.setText("99+");
        }
        if(count != 0){
            setInfoNumber(count);
        }


}

    private void removeInfoNumber() {
        boolean success = ShortcutBadger.removeCount(MainActivity.this);
        //Toast.makeText(getApplicationContext(), "success=" + success, Toast.LENGTH_SHORT).show();
    }

    private void setInfoNumber(int  badgeCount) {
        boolean success = ShortcutBadger.applyCount(MainActivity.this, badgeCount);
        //Toast.makeText(getApplicationContext(), "Set count=" + badgeCount + ", success=" + success, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDragOut() {
        mUnreadNumView.setVisibility(View.GONE);
        ToastUtils.showShortToast(this, "清理成功");
        RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations != null && conversations.size() > 0) {
                    for (Conversation c : conversations) {
                        RongIM.getInstance().clearMessagesUnreadStatus(c.getConversationType(), c.getTargetId(), null);
                    }
                }
                removeInfoNumber();
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        }, mConversationsTypes);

    }

    @Override
    protected void onDestroy() {
        EventMore.removeUnReadMessageCountChangedObserver(this);
        if (mHomeKeyReceiver != null)
            this.unregisterReceiver(mHomeKeyReceiver);
        super.onDestroy();
    }

    private HomeWatcherReceiver mHomeKeyReceiver = null;

    //如果遇见 Android 7.0 系统切换到后台回来无效的情况 把下面注册广播相关代码注释或者删除即可解决。下面广播重写 home 键是为了解决三星 note3 按 home 键花屏的一个问题
    private void registerHomeKeyReceiver(Context context) {
        if (mHomeKeyReceiver == null) {
            mHomeKeyReceiver = new HomeWatcherReceiver();
            final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            try {
                context.registerReceiver(mHomeKeyReceiver, homeFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        if(util.getUserId()!=null){
            new NetIntent().client_getMyUnreadComminfo(util.getUserId(),  "1", new NetIntentCallBackListener(MainActivity.this));
        }
    }

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        JSONObject jsonObject = null;

        try {
            jsonObject= JSON.parseObject(response);
            if(jsonObject.containsKey("infoCnt")) {
                String infoNum = jsonObject.getString("infoCnt");
                if (StringUtils.isNotEmpty(infoNum, true)) {
                    SPUtils.put(MainActivity.this, "infoCnt", infoNum);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
