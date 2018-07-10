package com.haoji.haoji.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.ClearEditText;
import com.haoji.haoji.custom.CustomProgress;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.NetUtil;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegisterActivity extends BaseActivity {
    private static String TAG = RegisterActivity.class.getSimpleName();


    @BindView(R.id.ce_input_phone)
    ClearEditText ceInputPhone;
    @BindView(R.id.ce_input_password)
    ClearEditText ceInputPassword;
    @BindView(R.id.ce_confirm_password)
    ClearEditText ceConfirmPassword;
    @BindView(R.id.et_input_code)
    EditText etInputCode;
    @BindView(R.id.btn_get_code)
    Button btnGetCode;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.tv_lagel)
    TextView tvLagel;
    @BindView(R.id.cb_choose_lagel)
    CheckBox cbChooseLagel;
    private CustomProgress dialog;
    private String strUsername, strPassword, strCode,strConfirmPassword;
    private int i = 120;
    private boolean isSend = false;
//    private LocalBroadcastManager localBroadcastManager;
//    private IntentFilter intentFilter;
   // private SmsContent content;
    int requst_type=0;
    SharedPreferencesUtil util;

//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Constants.Action_select_school)) {
////                register_school.setText(intent.getStringExtra("text"));
////                universityid = intent.getStringExtra("universityid");
//            } else if (intent.getAction().equals(Constants.Action_select_zy)) {
////                register_zy.setText(intent.getStringExtra("text"));
////                majorid = intent.getStringExtra("majorid");
//            }
//        }
//    };
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (i == 0) {
                        btnGetCode.setText("重新发送");
                        isSend = false;

                    } else {
                        btnGetCode.setText("(" + (i--) + ")已发送");
                        sendCode();
                    }

                    break;

                default:
                    break;
            }
        };
    };
  
    @Override
    public void initMainView() {
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        util=new SharedPreferencesUtil(this);

    }

    @Override
    public void initUi() {
//        localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(Constants.Action_select_school);
//        intentFilter.addAction(Constants.Action_select_zy);
//        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        Util.setHeadTitleMore(this, "注    册", true);
        findViewById(R.id.head_more).setVisibility(View.GONE);

//        content = new SmsContent(new Handler());// 注册短信变化监听
//        this.getContentResolver().registerContentObserver(
//                Uri.parse("content://sms/"), true, content);

        cbChooseLagel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {// 选中隐藏
                    tvLagel.setTextColor(getResources().getColor(R.color.tableMenuFontColor));
                    btnRegister.setBackground(getResources().getDrawable(R.drawable.btn_login_selector));
                    btnRegister.setEnabled(true);
                } else {
                    tvLagel.setTextColor(getResources().getColor(R.color.status_color));
                    btnRegister.setBackground(getResources().getDrawable(R.drawable.btn_login_grey));
                    btnRegister.setEnabled(false);
                    ToastUtils.showShortToast(RegisterActivity.this,"需要同意该条款才能使用软件软件！");

                }
            }
        });


    }
    private void getCode() {
        strUsername = ceInputPhone.getText().toString();
        strPassword = ceInputPassword.getText().toString();
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtils.showShortToastSafe(this, "您的设备尚未连接到网络！");
            return;
        } else {
            if (StringUtils.isPhone(strUsername)
                    && ((StringUtils
                    .getLength(strPassword, true) >= 6 && 16 >= StringUtils
                    .getLength(strPassword, true)))) {
                // 设置显示倒计时
                handler.sendEmptyMessage(1);
                isSend = true;
                i = 120;
                requst_type=0;
                // 获取验证码
                NetIntent netIntent1 = new NetIntent();
                netIntent1.client_sendSms(ceInputPhone.getText().toString(), new NetIntentCallBackListener(new NetIntentCallBackListener.INetIntentCallBack() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if (jsonObject.getBoolean("code")){
                                ToastUtils.showShortToastSafe(RegisterActivity.this,"发送成功");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }));

            } else if (!StringUtils.isPhone(strUsername)) {
                ToastUtils.showShortToastSafe(this,
                        "请输入11位的手机号码！");
                return;
            } else if (6 > StringUtils.getLength(strPassword, true)
                    || 17 < StringUtils.getLength(strPassword, true)) {
                ToastUtils.showShortToastSafe(this,
                        "请输入6~16位的密码！");
                return;
            }
        }
    }

    private void regist() {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtils.showShortToastSafe(RegisterActivity.this, "当前尚未连接网络！");
            return;
        } else {
            strUsername = ceInputPhone.getText().toString();
            strConfirmPassword = ceConfirmPassword.getText().toString();
            strPassword = ceInputPassword.getText().toString();
            strCode = etInputCode.getText().toString();
            if (StringUtils.isPhone(strUsername)
                    && ((StringUtils.getLength(strPassword, true) >= 6 && StringUtils.equals(strPassword,strConfirmPassword)&& 16 >= StringUtils
                   .getLength(strPassword, true)))&& StringUtils.isNotEmpty(strCode,true)) {

                dialog = CustomProgress.show(this, "注册中", true, null);
                requst_type=0;
                NetIntent netIntent = new NetIntent();
                netIntent.client_register(ceInputPhone.getText().toString(),
                        ceInputPassword.getText().toString()
                        , etInputCode.getText().toString()
                        , new NetIntentCallBackListener(intentCallBack));
            } else if (!StringUtils
                    .isPhone(ceInputPhone.getText().toString())) {
                ToastUtils.showShortToastSafe(RegisterActivity.this,
                        "请输入11位的手机号码！");
                return;
            } else if (6 > StringUtils.getLength(strPassword, true)
                    || 16 < StringUtils.getLength(strPassword, true)) {
                ToastUtils.showShortToastSafe(RegisterActivity.this,
                        "请输入6~16位的密码！");
                return;
            }  else if (!StringUtils.equals(strPassword,strConfirmPassword)) {
                ToastUtils.showShortToastSafe(RegisterActivity.this,
                        "密码不一致！");
                return;
            }else if (StringUtils.isNotEmpty(strCode,true)) {
                ToastUtils.showShortToastSafe(RegisterActivity.this,
                        "验证码不能为空");
                return;
            }else if (StringUtils.isNumber(strCode)) {
                ToastUtils.showShortToastSafe(RegisterActivity.this, "验证码格式不正确！");
            }
        }
    }

    private void sendCode() {
        handler.sendEmptyMessageDelayed(1, 1000);
    }

    @Override
    public void loadData() {

    }


    @OnClick({R.id.btn_get_code, R.id.btn_register, R.id.tv_lagel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_code:
                if (!isSend) {
                    getCode();
                }
                break;
            case R.id.btn_register:
                regist();
                break;
            case R.id.tv_lagel:
                startActivity(new Intent(RegisterActivity.this,HaojiLagelActivity.class));
                break;
        }
    }
    private NetIntentCallBackListener.INetIntentCallBack intentCallBack = new NetIntentCallBackListener.INetIntentCallBack() {
        @Override
        public void onError(Request request, Exception e) {
            if (dialog != null)
                dialog.dismiss();
        }

        @Override
        public void onResponse(String response) {
            if (dialog != null)
                dialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                ToastUtils.showShortToastSafe(RegisterActivity.this, jsonObject.getString("msg"));
                if (jsonObject.has("newFileName")) {

                } else {
                    if (jsonObject.getBoolean("code")) {
                        handler.sendEmptyMessage(3);
                    }else{

                       util.setPicture(getResources().getString(R.string.init_picture_path));//先默认数据，防止空指针
                        util.setNickName(getResources().getString(R.string.init_nickname));
                        util.setMotto(getResources().getString(R.string.init_motto));
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
//    /**
//     * 监听短信数据库
//     */
//    class SmsContent extends ContentObserver {
//        private Cursor cursor = null;
//
//        public SmsContent(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            String listenerPhone = "10698000171101135848"; // 监听的号码
//            cursor = managedQuery(Uri.parse("content://sms/inbox"),
//                    new String[]{"_id", "address", "read", "body"},
//                    " address=? and read=?",
//                    new String[]{listenerPhone, "0"}, "_id desc");
//            if (cursor != null && cursor.getCount() > 0) {
//                ContentValues values = new ContentValues();
//                values.put("read", "1");
//                cursor.moveToNext();
//                int smsbodyColumn = cursor.getColumnIndex("body");
//                String smsBody = cursor.getString(smsbodyColumn);
//
//                Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{"
//                        + 6 + "})(?![0-9])");
//                Matcher m = continuousNumberPattern.matcher(smsBody);
//                String dynamicPassword = "";
//                while (m.find()) {
//                    System.out.print(m.group());
//                    dynamicPassword = m.group();
//                }
//                String sms = dynamicPassword;
//                if (sms != null ) {
//                    etInputCode.setText(sms);
//                }
//
//            }
//            if (Build.VERSION.SDK_INT < 14) {
//                cursor.close();
//            }
//        }
//    }

}
