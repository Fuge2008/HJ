package com.haoji.haoji.comment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.ShowPictureActivity;
import com.haoji.haoji.ui.VerifyFriendInfoActivity;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;


public class UserDetailsActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack,View.OnClickListener{
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_sex)
    ImageView ivSex;
    @BindView(R.id.tv_region)
    TextView tvRegion;

    private RelativeLayout rlPhotoAlbum;

    @BindView(R.id.iv_image1)
    ImageView ivImage1;

    @BindView(R.id.iv_image2)
    ImageView ivImage2;

    @BindView(R.id.iv_image3)
    ImageView ivImage3;
    @BindView(R.id.tv_moto)
    TextView tvMoto;
    @BindView(R.id.btn_msg)
    Button btnMsg;
    @BindView(R.id.btn_add)
    Button btnAdd;

    private JSONObject json;
    private SharedPreferencesUtil util;
    private String phone,userid,hjid,naickname;
    private boolean isFriend=true;
    String avatar = null;
    String sex = null;
    String path = null;
    boolean byPhone=true;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateView();
                    break;
                case 1:
                    btnMsg.setText("添加好友");
                    break;
            }
        }
    };
    @Override
    public void initMainView() {
        setContentView(R.layout.activity_user_info);
    }
    @Override
    public void initUi() {
        ButterKnife.bind(this);
        phone = this.getIntent().getStringExtra("phone");
        if(StringUtils.equals("0",phone)){
            userid=this.getIntent().getStringExtra("userid");
            byPhone=false;
        }
        util=new SharedPreferencesUtil(this);
        rlPhotoAlbum= (RelativeLayout) findViewById(R.id.rl_photo_album);
        rlPhotoAlbum.setOnClickListener(this);
        NetIntent intent=new NetIntent();
        if(byPhone){
            intent.client_getUserByPhone(phone,new NetIntentCallBackListener(this));
        }else{
            intent.client_getUserById(userid,new NetIntentCallBackListener(this));
        }

        if (phone != null) {
            refresh(phone, true);
            return;
        }
        if (phone  == null) {
            finish();
            return;
        }
    }
    @Override
    public void loadData() {
    }
    public void updateView() {

        try {
            Util.ImageLoaderToPicAuto(this, json.getString("pic"), ivAvatar);

            tvMoto.setText(json.getString("motto"));
            naickname=json.getString("nickname");
            tvName.setText(naickname);
            avatar=json.getString("pic").toString();
           sex=json.getString("sex").toString();
            path=json.getString("pic");
            hjid=json.getString("userid");
            String imageStr = json.getString("apppics");

            if(!StringUtils.equals(hjid,util.getUserId())){
                new NetIntent().client_isFriend(util.getUserId(),hjid,new NetIntentCallBackListener(this));
            }
            System.out.print("imageStr--->>"+imageStr);
            if (imageStr!=null) {//.   如果有图片
                String[] images = imageStr.split(";");//.   将图像URL字符串剪切存入数组
                int imNumb = images.length;
                if(imNumb!=0 & images[0].contains("http://")){
                    List<String> list = new ArrayList<String>(Arrays.asList(images));
                    LogUtils.i("数组装化成集合结果："+list);
                if(list.size()>2 && StringUtils.isUrl(list.get(2))){
                    Glide.with(this).load(list.get(2)).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivImage3);
                } if(list.size()>1 && StringUtils.isUrl(list.get(1))){
                    Glide.with(this).load(list.get(1)).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivImage2);
                }if(list.size()>0 && StringUtils.isUrl(list.get(0))){
                    Glide.with(this).load(list.get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivImage1);
                }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Glide.with(this).load(avatar).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivAvatar);

        if (StringUtils.equals("1",sex)) {
            ivSex.setImageResource(R.drawable.icon_male);
        } else {
            ivSex.setImageResource(R.drawable.icon_female);
        }
        //资料是自己
        if (StringUtils.equals(hjid,util.getUserId())) {

            btnMsg.setVisibility(View.GONE);
            btnAdd.setVisibility(View.GONE);
            return;
        }else if(!StringUtils.equals(hjid,util.getUserId())){
            btnMsg.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.VISIBLE);
            //TODO 如果是好友 则显示发信息，关注
            //TODO 如果不是好友，则显示加好友，关注
        }
    }

    private void refresh(final String phone, boolean backTask) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载资料...");
        progressDialog.setCanceledOnTouchOutside(false);
        if (!backTask) {
            progressDialog.show();
        }
        NetIntent intent=new NetIntent();
        intent.client_getUserByPhone( phone, new NetIntentCallBackListener(this) );

    }


    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {

        System.out.println(response);
        try {
            JSONObject jsonObject=new JSONObject(response);
            if (jsonObject.has("msg"))
               // SystemDialog.DialogToast();
                ToastUtils.showShortToastSafe(UserDetailsActivity.this,jsonObject.getString("msg"));
            if (jsonObject.has("user")) {
                json = jsonObject.getJSONObject("user");
                handler.sendEmptyMessage(0);
            }
            if(jsonObject.has("isFriend")){
                if(StringUtils.equals(jsonObject.getString("isFriend"),"0")){
                    isFriend=false;
                    handler.sendEmptyMessage(1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @OnClick({R.id.iv_back, R.id.iv_avatar, R.id.btn_msg, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_avatar:
                if(StringUtils.isNotEmpty(path,true)&&StringUtils.isUrl(path)){
                    Intent intent = new Intent(this, ShowPictureActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("path",  path);
                    startActivity(intent);
                }else{
                    ToastUtils.showShortToast(UserDetailsActivity.this,"该用户暂未上传头像");
                }

                break;
            case R.id.btn_msg://TODO 加好友/发信息
                if(isFriend){//TODO 发起聊天
                    if (RongIM.getInstance()!=null && StringUtils.isNotEmpty(hjid,true)){
                        RongIM.getInstance().startPrivateChat(this,hjid, naickname);
                    }
                }else {
//                    NetIntent netIntent = new NetIntent();//TODO  添加好友
//                    netIntent.client_addFriend(util.getUserId(), hjid, new NetIntentCallBackListener(this));
//                    SystemDialog.DialogToast(getApplicationContext(),"发送请求成功");
                    startActivity(new Intent(UserDetailsActivity.this, VerifyFriendInfoActivity.class).putExtra("hjid",hjid).putExtra(" naickname", naickname));
                }




                break;
            case R.id.btn_add://TODO 关注或取消关注

                new NetIntent().client_addConcern(util.getUserId(),hjid,new NetIntentCallBackListener(this));
                //SystemDialog.DialogToast(getApplicationContext(),"关注成功");
                ToastUtils.showShortToastSafe(UserDetailsActivity.this,"关注成功");
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_photo_album:
                startActivity(new Intent(UserDetailsActivity.this, SocialFriendActivity.class).putExtra("friendID", hjid));
                break;
        }
    }
}