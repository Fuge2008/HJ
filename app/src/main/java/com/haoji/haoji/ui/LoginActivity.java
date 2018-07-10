package com.haoji.haoji.ui;

import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.ActionSheetDialog;
import com.haoji.haoji.custom.ActionSheetDialog.OnSheetItemClickListener;
import com.haoji.haoji.custom.ClearEditText;
import com.haoji.haoji.custom.CustomProgress;
import com.haoji.haoji.custom.MyCheckBox;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.NetUtil;
import com.haoji.haoji.util.SPUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;





public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = LoginActivity.class.getSimpleName();
//    @BindView(R.id.iv_qq)
//    ImageView ivQq;
//    @BindView(R.id.iv_weibo)
//    ImageView ivWeibo;
//    @BindView(R.id.iv_weixin)
//    ImageView ivWeixin;
    private ClearEditText edit_password, edit_userid;
    private SharedPreferencesUtil util;
    private JSONObject json;
    private CustomProgress dialog;
    private ImageView userimg;
    private CheckBox cb_hide_password;
    private MyCheckBox cb_save_psw, cb_login_self;
    private String strUsername, strPassword;
    private TextView tv_more;


    @Override
    public void initMainView() {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        util = new SharedPreferencesUtil(this);
        Util.setHeadTitleMore(this, "登    录", true);
        findViewById(R.id.head_more).setVisibility(View.GONE);

    }

    @Override
    public void initUi() {
        findViewById(R.id.btn_login).setOnClickListener(this);
//        findViewById(R.id.btn_register).setOnClickListener(this);
//        findViewById(R.id.btn_wj).setOnClickListener(this);
        userimg = (ImageView) findViewById(R.id.userimg);

        edit_userid = (ClearEditText) findViewById(R.id.ce_input_username);
        edit_password = (ClearEditText) findViewById(R.id.ce_input_password);
        cb_hide_password = (CheckBox) findViewById(R.id.cb_hide_password);

        cb_save_psw = (MyCheckBox) findViewById(R.id.cb_save_psw);
        cb_login_self = (MyCheckBox) findViewById(R.id.cb_login_self);
        tv_more= (TextView) findViewById(R.id.tv_more);
        tv_more.setOnClickListener(this);


    }

    @Override
    public void loadData() {
        if ((boolean) SPUtils.get(this, "is_save_psw", false)) {
            edit_userid.setText((String) SPUtils.get(this, "username1", ""));
            edit_password.setText((String) SPUtils.get(this, "password1", ""));
            cb_save_psw.setChecked(true);
            Util.ImageLoaderToPicAuto(this, util.getPicture(), userimg);

            if ((boolean) SPUtils.get(this, "is_login_self", false)) {
                cb_login_self.setChecked(true);
                login();
            }
        }
        setlisteners();

    }

    private void setlisteners() {
        cb_save_psw
                .setOnCheckedChangeListener(new MyCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(MyCheckBox checkBox,
                                                 boolean isChecked) {

                        if (isChecked) {
                            SPUtils.put(LoginActivity.this, "is_save_psw", true);
                        } else {
                            SPUtils.put(LoginActivity.this, "is_save_psw", false);
                        }
                    }
                });
        cb_login_self
                .setOnCheckedChangeListener(new MyCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(MyCheckBox checkBox,
                                                 boolean isChecked) {
                        if (isChecked) {
                            SPUtils.put(LoginActivity.this, "is_login_self", true);
                        } else {
                            SPUtils.put(LoginActivity.this, "is_login_self", false);
                        }

                    }
                });

        edit_userid.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                edit_password.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        cb_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {// 选中隐藏
                    edit_password.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
            }
        });

        if ((boolean) SPUtils.get(LoginActivity.this, "is_login_self", false) == true) {
            login();
        }

    }


    private void saveUser() {
        try {
            util.setUserId(json.getString("userid"));
            util.setUserName(json.getString("username"));
            util.setPassWord(json.getString("password"));
            util.setMobilePhone(json.getString("phone"));
            util.setIsLogin(true);
            if(StringUtils.isEmpty(util.getNickName())){//先默认数据，防止空指针
                util.setNickName(getResources().getString(R.string.init_nickname));
            }
            if(StringUtils.isEmpty(util.getPicture())){
                util.setPicture(getResources().getString(R.string.init_picture_path));
            }
            if(StringUtils.isEmpty(util.getMotto())){
                util.setMotto(getResources().getString(R.string.init_motto));
            }
            SPUtils.put(LoginActivity.this, "username1", json.getString("userid").substring(14, 25));

            SPUtils.put(LoginActivity.this, "password1", json.getString("password"));
            LogUtils.i("info", "返回数据----->" + json.getString("userid") + json.getString("password"));
           startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
//            case R.id.btn_register:
//                startActivity(new Intent(this, RegisterActivity.class));
//                break;
//            case R.id.btn_wj:
//                startActivity(new Intent(this, ModifyPasswordActivity.class));
//                break;
            case R.id.tv_more:
                showDialog();
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
                if (jsonObject.getBoolean("code")) {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                    json = jsonArray.getJSONObject(0);
                    saveUser();

                }else {
                    ToastUtils.showShortToast(LoginActivity.this,jsonObject.getString("msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

//    // 执行授权,获取用户信息
//    // 文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
//    private void authorize(Platform plat) {
//
//        if (plat == null) {
//            ToastUtils.showShortToastSafe(LoginActivity.this, "当前无其他第三方登录");
//            return;
//        }
//
//        if (plat.isValid()) {
//            plat.removeAccount();
//        }
//
//        plat.setPlatformActionListener(this);
//        plat.SSOSetting(false);
//        plat.showUser(null);
//    }

//    @OnClick({R.id.iv_qq, R.id.iv_weibo, R.id.iv_weixin})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.iv_qq:
//                Platform qzone = ShareSDK.getPlatform(QZone.NAME);
//                authorize(qzone);
//                break;
//            case R.id.iv_weibo:
//                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
//                authorize(sina);
//                break;
//            case R.id.iv_weixin:
//                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
//                authorize(wechat);
//                break;
//        }
//    }
//
//    private Handler handler;
//    private static final int MSG_SMSSDK_CALLBACK = 1;
//    private static final int MSG_AUTH_CANCEL = 2;
//    private static final int MSG_AUTH_ERROR = 3;
//    private static final int MSG_AUTH_COMPLETE = 4;
//    private OnLoginListener signupListener;

//    /**
//     * 设置授权回调，用于判断是否进入注册
//     */
//    public void setOnLoginListener(OnLoginListener l) {
//        this.signupListener = l;
//    }
//
//
//    public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
//        if (action == Platform.ACTION_USER_INFOR) {
//            Message msg = new Message();
//            msg.what = MSG_AUTH_COMPLETE;
//            msg.obj = new Object[]{platform.getName(), res};
//            handler.sendMessage(msg);
//            //TODO   存储回调信息
//            String str1 = platform.getDb().getUserId();
//            String str2 = platform.getDb().getUserName();
//            String str3 = platform.getDb().getUserIcon();
//            // MyApplication.other_token
//            String str4 = platform.getName();
//            LogUtils.e("info", "zuo----otherlogin==" + str1 + "   MyApplication.other_username="
//                    + str2 + "   platform.getDb().getUserIcon()=" + platform.getDb().getUserIcon()
//                    + " MyApplication.other_loginType=" + str3);
//
//
//        }
//    }
//
//    public void onError(Platform platform, int action, Throwable t) {
//        if (action == Platform.ACTION_USER_INFOR) {
//            handler.sendEmptyMessage(MSG_AUTH_ERROR);
//        }
//        t.printStackTrace();
//        System.out.println("zuo错误=" + t.toString());
//    }
//
//    public void onCancel(Platform platform, int action) {
//        if (action == Platform.ACTION_USER_INFOR) {
//            handler.sendEmptyMessage(MSG_AUTH_CANCEL);
//        }
//    }

    @SuppressWarnings("unchecked")
    private void login() {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtils.showShortToastSafe(LoginActivity.this, "您的设备尚未连接到网络！");
            return;
        } else {
            strUsername = edit_userid.getText().toString();
            strPassword = edit_password.getText().toString();
            if (StringUtils.isPhone(strUsername)
                    && (StringUtils.getLength(strPassword, true) >= 6 && 16 >= StringUtils
                    .getLength(strPassword, true))) {
                dialog = CustomProgress.show(this, "登录中..", true, null);
                NetIntent intent = new NetIntent();
                intent.client_login(strUsername, strPassword,
                        new NetIntentCallBackListener(intentCallBack));


            } else if (!StringUtils.isPhone(strUsername)) {
                ToastUtils.showShortToastSafe(LoginActivity.this,
                        "请输入11位的手机号码！");
                return;

            } else if (6 > StringUtils.getLength(strPassword, true)
                    || 17 < StringUtils.getLength(strPassword, true)) {
                ToastUtils.showShortToastSafe(LoginActivity.this,
                        "请输入6~16位的密码！");
                return;
            }

        }

    }
    private  void showDialog(){
        new ActionSheetDialog(LoginActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .addSheetItem("短信登录", ActionSheetDialog.SheetItemColor.Blue,
                new OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        startActivity(new Intent(LoginActivity.this, SMSLoginActivity.class));

                    }
                }).addSheetItem("忘记密码", ActionSheetDialog.SheetItemColor.Blue,
                        new OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                startActivity(new Intent(LoginActivity.this, ModifyPasswordActivity.class));
                            }
                        }).addSheetItem("前往注册", ActionSheetDialog.SheetItemColor.Blue,
                        new OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                            }
                        }).show();

    }


//   private void loginOther() {
 //       ToastUtils.showShortToastSafe(LoginActivity.this, "转入第三方登录，稍等");
//        System.out.println("zuo111111111111111111");
//        RequestParams params = new RequestParams();
//        params.put("loginType", MyApplication.other_loginType);
//        params.put("type", 2);
//        params.put("token", MyApplication.other_token);
//        params.put("username", MyApplication.other_username);
//        params.put("url", MyApplication.other_url);
//        showProgressDialog("登录中...");
//        MyApplication.ahc.post(AppFinalUrl.userotherLogin, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                // TODO Auto-generated method stub
//                super.onSuccess(statusCode, headers, response);
//                System.out.println("zuologin" + response.toString());
//
//                String result = response.optString("result");
//                if (result.equals("0")) {
//                    int type = response.optInt("type");
//                    userId = response.optInt("userId");
//                    String huanxinPass = response.optString("huanxinPass");
//                    SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
//                    Editor edit = sp.edit();
//                    edit.putBoolean("islogin", true);
//                    edit.putInt("userId", userId);
//                    edit.putString("token", "");
//                    edit.putInt("type", type);
//                    edit.commit();
//                    if (userId != 0) {
//                        loginEM(userId, huanxinPass);
//                    }
//                    MyApplication.getInfo();
//                } else {
//                    dismissProgressDialog();
//                    Toast.makeText(Activity_Login.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
 //   }

//    @Override
//    public boolean handleMessage(Message msg) {
//        switch (msg.what) {
//            case MSG_AUTH_CANCEL: {
//                // 取消授权
//                Toast.makeText(LoginActivity.this, "取消授权", Toast.LENGTH_SHORT).show();
//            }
//            break;
//            case MSG_AUTH_ERROR: {
//                // 授权失败
//                Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
//            }
//            break;
//            case MSG_AUTH_COMPLETE: {
//                // 授权成功
//                Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
//                Object[] objs = (Object[]) msg.obj;
//                String platform = (String) objs[0];
//                HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
//                if (signupListener != null && signupListener.onSignin(platform,
//                        res)) {
//                    SignupPage signupPage = new SignupPage();
//                    signupPage.setOnLoginListener(signupListener);
//                    signupPage.setPlatform(platform);
//                    signupPage.show(LoginActivity.this, null);
//                }

//                if (MyApplication.other_token != null && !MyApplication.other_token.equals("")) {
                // loginOther();
//                } else {
                //  showToast("登录失败", 0);
 //               Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
//                }
//            }
//            break;
//
//        }
//        return false;
//    }


}
