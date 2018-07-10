package com.haoji.haoji.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haoji.haoji.R;
import com.haoji.haoji.app.RongCloudEvent;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.common.Constants;
import com.haoji.haoji.custom.dialog.FXAlertDialog;
import com.haoji.haoji.custom.zxing.QRGenerateActivity;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.TimeUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.haoji.haoji.common.Constants.DIR_AVATAR;


public class ProfileActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack,View.OnClickListener{
    private static String TAG = ProfileActivity.class.getSimpleName();
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final int UPDATE_ADDRESS = 4;
    private static final int UPDATE_NICK = 5;
    private static final int UPDATE_SIGN = 6;
    public static final int REQUSET = 7;
    private ImageView iv_avatar,iv_back;
    private TextView tv_name,tv_hjid,tv_sex,tv_sign,tv_region,tv_address;
    private String imageName,province,city,imagePath;
    private JSONObject json;
    private SharedPreferencesUtil util;
    private boolean isFresh = false;
    private boolean hasChange=false;    //头像，昵称，好记号是否发生变化
    private ProgressDialog progressDialog;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateUser();
                    updateView();
                    break;
                case 1:

                    NetIntent netIntent = new NetIntent();
                    netIntent.client_updateUserById(util.getUserId(), "",   "","", "","", imagePath,new NetIntentCallBackListener(ProfileActivity.this));
                    LogUtils.i("更新头像2=====》"+imagePath);
                    Bitmap  bitmap = BitmapFactory.decodeFile(DIR_AVATAR+imageName);
                    util.setPicture(Constants.HttpRootPic+imagePath);
                    iv_avatar.setImageBitmap(bitmap);
                    hasChange=false;
//                    LogUtils.i("更新头像4=====》"+file.getPath());
//                    Glide.with(ProfileActivity.this).load(file).into(iv_avatar);
 //                   }
                    break;
                case 2:

                    break;
            }
        }
    };
    @Override
    public void initMainView() {
        setContentView(R.layout.activity_myinfo);
        util = new SharedPreferencesUtil(this);
    }

    @Override
    public void initUi() {

        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_hjid = (TextView) findViewById(R.id.tv_hjid);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_sign = (TextView) findViewById(R.id.tv_sign);
        tv_region= (TextView) findViewById(R.id.tv_region);
        tv_address= (TextView) findViewById(R.id.tv_address);
        String nick = util.getNickName();
        String fxid = util.getMobilePhone();
        String sex = util.getSex();
        String sign = util.getMotto();
        String avatarUrl = util.getPicture();
        Glide.with(ProfileActivity.this).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_avatar);
        tv_name.setText(nick);
        if (TextUtils.isEmpty(fxid)) {
            tv_hjid.setText("未设置");

        } else {
            tv_hjid.setText(fxid);
        }
        tv_sex.setText(sex);
        if (TextUtils.isEmpty(sign)) {
            tv_sign.setText("未填写");
        } else {
            tv_sign.setText(sign);
        }

        //设置监听
        this.findViewById(R.id.re_avatar).setOnClickListener(this);
        this.findViewById(R.id.re_name).setOnClickListener(this);
        this.findViewById(R.id.re_hjid).setOnClickListener(this);
        this.findViewById(R.id.re_sex).setOnClickListener(this);
        this.findViewById(R.id.re_region).setOnClickListener(this);
        this.findViewById(R.id.re_sign).setOnClickListener(this);
        this.findViewById(R.id.re_qrcode).setOnClickListener(this);
        this.findViewById(R.id.iv_avatar).setOnClickListener(this);
        this.findViewById(R.id.re_address).setOnClickListener(this);

    }

    @Override
    public void loadData() {
        NetIntent intent = new NetIntent();
        intent.client_getUserById(util.getUserId(), new NetIntentCallBackListener(this));
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void updateUser() {
        try {
            Log.i("info",json.toString());
            util.setPicture(json.getString("pic"));
            util.setProvince(json.getString("province"));
            util.setCity(json.getString("city"));
            util.setToken(json.getString("token"));
            util.setNickName(json.getString("nickname"));
            util.setRealName(json.getString("realname"));
            util.setMotto(json.getString("motto"));
            util.setBirthday(json.getString("birthday"));
            util.setZone(json.getString("zone"));
            util.setStudentid(json.getString("studentid"));
            util.setUniversity(json.getString("university"));
            util.setCollege(json.getString("college"));
            util.setMajor(json.getString("major"));
            util.setSex(json.getInt("sex"));
            System.out.println("sex:" + json.getInt("sex"));
            if (util.getIsLogin() && !isFresh) {
                Util.connect(util.getToken());
                RongCloudEvent.init(ProfileActivity.this);
            } else {}
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void updateView() {
        try {
            if (util.getIsLogin()) {
                tv_name.setText(util.getNickName());
                tv_hjid.setText(util.getMobilePhone());
            } else {
                iv_avatar.setImageResource(R.drawable.my_headphoto);
                tv_name.setText("请登录");
                tv_hjid.setText(" ");

            }
            tv_region.setText(util.getProvince()+" "+util.getCity());
            tv_address.setText(util.getAddress());
            tv_sign.setText(util.getMotto());
            tv_sex.setText(util.getSex());
            //Util.ImageLoaderToPicAuto(this, util.getPicture(), iv_avatar);
            Glide.with(ProfileActivity.this).load(util.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_avatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showPhotoDialog() {

        List<String> items = new ArrayList<String>();
        items.add("拍照");
        items.add("相册");
        FXAlertDialog fxAlertDialog = new FXAlertDialog(ProfileActivity.this, null, items);
        fxAlertDialog.init(new FXAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        imageName = TimeUtils.getNowTime() + ".png";
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(DIR_AVATAR, imageName)));
                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                        break;
                    case 1:
                        imageName = TimeUtils.getNowTime() + ".png";
                        Intent intent2 = new Intent(Intent.ACTION_PICK, null);
                        intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent2, PHOTO_REQUEST_GALLERY);
                        break;
                }
            }
        });
    }

    private void showSexDialog() {
        String title = "性别";
        List<String> items = new ArrayList<String>();
        items.add("男");
        items.add("女");
        FXAlertDialog fxAlertDialog = new FXAlertDialog(ProfileActivity.this, title, items);
        fxAlertDialog.init(new FXAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        if (!util.getSex().equals("男")) {

                            updateInServer(Constants.JSON_KEY_SEX, "男","");
                        }
                        break;
                    case 1:
                        if (!util.getSex().equals("女")) {

                            updateInServer(Constants.JSON_KEY_SEX, "女","");
                        }
                        break;
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case PHOTO_REQUEST_TAKEPHOTO:

                    startPhotoZoom(
                            Uri.fromFile(new File(DIR_AVATAR, imageName)),
                            480);
                    break;

                case PHOTO_REQUEST_GALLERY:
                    if (data != null)
                        startPhotoZoom(data.getData(), 480);
                    break;

                case PHOTO_REQUEST_CUT:
                    updateInServer(Constants.JSON_KEY_AVATAR, DIR_AVATAR+imageName,"");
                    NetIntent netIntent = new NetIntent();
                    File file=new File(DIR_AVATAR, imageName);
                    Log.i("info","加载头像--------->+:"+file.getPath());
                    netIntent.client_uploadImg( file, new NetIntentCallBackListener(ProfileActivity.this));
                    hasChange=true;
                    break;
                case UPDATE_ADDRESS:
                    String address= data.getStringExtra("value");
                    if (address != null) {
                        util.setAddress(address);
                        tv_hjid.setText(address);
                        hasChange=true;
                    }

                    break;
                case UPDATE_NICK:
                    String nick = data.getStringExtra("value");
                    if (nick != null) {
                        util.setNickName(nick);
                        tv_name.setText(nick);
                        hasChange=true;
                    }
                    break;
                case UPDATE_SIGN:
                    String sign = data.getStringExtra("value");
                    util.setMotto(sign);
                    if (sign != null) {
                        tv_sign.setText(sign);
                        hasChange=true;

                    }
                    break;
                case REQUSET:
                    if (requestCode == REQUSET && resultCode == RESULT_OK) {
                        province=data.getStringExtra("province");
                        city=data.getStringExtra("city");
                        String region=province+" "+city;
                        if(region!=null){
                            tv_region.setText(region);
                            util.setProvince(province);
                            util.setCity(city);
                            hasChange=true;
                        }
                        updateInServer(Constants.JSON_KEY_CITY, city,province);
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void startPhotoZoom(Uri uri1, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri1, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(DIR_AVATAR, imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }




    private void updateInServer(final String key, final String value ,final String value2) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在更新...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        NetIntent netIntent = new NetIntent();
        if(StringUtils.equals(key,"sex")){
            int tag=1;

            if(StringUtils.equals(value,"女")){
                tag=2;
            }
            netIntent.client_updateUserById(util.getUserId(), tag+"", "",  "","", "", "",new NetIntentCallBackListener(ProfileActivity.this));
            tv_sex.setText(value);


        }else if(StringUtils.equals(key,"city") & value2!=null){
            netIntent.client_updateUserById(util.getUserId(), "",   "",value, value2,"", "",new NetIntentCallBackListener(ProfileActivity.this));
        }

        }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            ckeckChange();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void ckeckChange(){

        if( hasChange){
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void onError(Request request, Exception e) {
        if( progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onResponse(String response) {
        System.out.println(response);
        if( progressDialog!=null){
            progressDialog.dismiss();
        }
        try {
            LogUtils.i("返回数据："+response);
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("newFileName")) {
                imagePath = jsonObject.getString("newFileName");
                util.setPicture(imagePath);
                handler.sendEmptyMessage(1);
                hasChange=true;
            }
            if(jsonObject.has("user") && jsonObject.getJSONObject("user")!=null){
                json = jsonObject.getJSONObject("user");
                handler.sendEmptyMessage(0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_avatar:
                showPhotoDialog();
                break;
            case R.id.re_name:
                startActivityForResult(new Intent(ProfileActivity.this,
                        ProfileUpdateActivity.class).putExtra("type", ProfileUpdateActivity.TYPE_NICK).putExtra("default", util.getNickName()), UPDATE_NICK);
                break;
            case R.id.re_address:
                    startActivityForResult(new Intent(ProfileActivity.this,
                            ProfileUpdateActivity.class).putExtra("type", ProfileUpdateActivity.TYPE_ADDRESS), UPDATE_ADDRESS);
                break;
            case R.id.re_sex:
                showSexDialog();
                break;
            case R.id.re_region:
                Intent intent3 = new Intent(ProfileActivity.this, CitySelectActivity.class);
                startActivityForResult(intent3, REQUSET);
                break;
            case R.id.re_sign:
                startActivityForResult(new Intent(ProfileActivity.this,
                        ProfileUpdateActivity.class).putExtra("type", ProfileUpdateActivity.TYPE_SIGN).putExtra("default", util.getMotto()), UPDATE_SIGN);
                break;
            case R.id.re_qrcode:
                startActivity(new Intent(ProfileActivity.this,QRGenerateActivity.class));
                break;
            case R.id.iv_avatar:
                Intent intent = new Intent(ProfileActivity.this, ShowPictureActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("path", util.getPicture());
                startActivity(intent);
                break;

        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        if(StringUtils.isNotEmpty(util.getAddress(),true)){
            tv_address.setText(util.getAddress());
        }
//        if(hasChange){
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    RongCloudEvent.getInstance().RefreshUserInfo(util.getUserId(),util.getPicture(),util.getPicture());
//                }
//            }).start();
//
//        }

    }
}
