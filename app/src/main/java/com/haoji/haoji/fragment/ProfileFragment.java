package com.haoji.haoji.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haoji.haoji.R;
import com.haoji.haoji.app.RongCloudEvent;
import com.haoji.haoji.base.BaseFragment;
import com.haoji.haoji.comment.SocialFriendActivity;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.FeedBackActivity;
import com.haoji.haoji.ui.LoginActivity;
import com.haoji.haoji.ui.MyCollectionActivity;
import com.haoji.haoji.ui.ProfileActivity;
import com.haoji.haoji.ui.VideoListAcitivity;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;


public class ProfileFragment extends BaseFragment implements View.OnClickListener, NetIntentCallBackListener.INetIntentCallBack{
    private JSONObject json;
    private SharedPreferencesUtil util;
    //private LocalBroadcastManager broadcastManager;
   // private IntentFilter intentFilter;
    private ImageView iv_avatar;
    private TextView tv_name,tv_hjid;
   //private BadgeView badgeView;
    private boolean isFresh = false;


    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateUser();
                    updateView();
                    break;
                case 1:
                    isFresh = true;
                    NetIntent intent = new NetIntent();
                    intent.client_getUserByPhone(util.getMobilePhone(), new NetIntentCallBackListener(ProfileFragment.this));
                    break;
                case 2:
                    isFresh = false;
                    NetIntent intent1 = new NetIntent();
                    intent1.client_getUserByPhone(util.getMobilePhone(), new NetIntentCallBackListener(ProfileFragment.this));
                    break;
                case 3:
//                    badgeView.setBadgeCount(Integer.parseInt(msg.obj.toString()));
//                    if (Integer.parseInt(msg.obj.toString()) == 0) {
//                        badgeView.setVisibility(View.GONE);
//                    } else {
//                        badgeView.setVisibility(View.VISIBLE);
//                    }
                    break;
                case 4:
                    updateView();
                    break;
                case 5:

                    break;
            }
        }
    };
//    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Constants.Action_update)) {
//                handler.sendEmptyMessage(1);
//            }
//            if (intent.getAction().equals(Constants.Action_login)) {
//                handler.sendEmptyMessage(2);
//            }
//            if (intent.getAction().equals(Constants.Action_logout)) {
//                handler.sendEmptyMessage(4);
//            }
//            if (intent.getAction().equals(Constants.Action_badge)) {
//                Message message = handler.obtainMessage();
//                message.obj = intent.getIntExtra("point", 0);
//                message.what = 3;
//                handler.sendMessage(message);
//            }
//        }
//    };


    @Override
    public int InitMainView() {
        return R.layout.fragment_profile;
    }

    @Override
    public void initUI() {


        util = new SharedPreferencesUtil(getActivity());
        iv_avatar= (ImageView) rootview.findViewById(R.id.iv_avatar);
        tv_name= (TextView) rootview.findViewById(R.id.tv_name);
        tv_hjid= (TextView) rootview.findViewById(R.id.tv_hjid);
        rootview.findViewById(R.id.rl_myinfo).setOnClickListener(this);
        rootview.findViewById(R.id.re_setting).setOnClickListener(this);
        rootview.findViewById(R.id.re_wallet).setOnClickListener(this);
        rootview.findViewById(R.id.re_xiangce).setOnClickListener(this);
        rootview.findViewById(R.id.re_shoucang).setOnClickListener(this);
        rootview.findViewById(R.id.re_card_bag).setOnClickListener(this);

//        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(Constants.Action_update);
//        intentFilter.addAction(Constants.Action_login);
//        intentFilter.addAction(Constants.Action_badge);
//        intentFilter.addAction(Constants.Action_logout);
//        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
        if(StringUtils.isNotEmpty(util.getUserId(),true)&& StringUtils.isNotEmpty(util.getNickName(),true)&&StringUtils.isNotEmpty(util.getPicture(),true)){
        UserInfo userInfo=new UserInfo(util.getUserId(),util.getNickName(), Uri.parse(util.getPicture()));//更新头像昵称
        RongIM.getInstance().setCurrentUserInfo(userInfo);
        RongIM.getInstance().setMessageAttachedUserInfo(true);
        }
        updateView();


//
//        badgeView = new BadgeView(getActivity());
//        badgeView.setTargetView(rootview.findViewById(R.id.show_badge));
//        badgeView.setBadgeCount(0);
//        badgeView.setBadgeMargin(2);
//        badgeView.setBackground(12, Color.RED);
//       badgeView.setVisibility(View.GONE);
        if (util.getIsLogin()) {
            handler.sendEmptyMessage(2);
        }
        handler.sendEmptyMessage(5);

    }

    @Override
    public void loadData() {}
    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        System.out.println(response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            json = jsonObject.getJSONObject("user");
            handler.sendEmptyMessage(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void updateUser() {
        try {

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
                RongCloudEvent.init(getActivity());
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        try {
            if (util.getIsLogin()) {
              // Util.ImageLoaderToPicAuto(getActivity(), util.getPicture(), iv_avatar);
                Glide.with(this).load(util.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_avatar);
                tv_name.setText(util.getNickName());
                tv_hjid.setText("好记号:"+util.getMobilePhone());
            } else {
                iv_avatar.setImageResource(R.drawable.my_headphoto);
                tv_name.setText("请登录");
                tv_hjid.setText("");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(util.getIsLogin()){
            Glide.with(this).load(util.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_avatar);
            tv_name.setText(util.getNickName());
            String hjid=util.getMobilePhone();
            if(TextUtils.isEmpty(hjid)){
                hjid="未设置";
            }
            hjid="好记号:"+hjid;
            tv_hjid.setText(hjid);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_myinfo:
                if (util.getIsLogin()) {
                    startActivityForResult(new Intent(getActivity(), ProfileActivity.class),0);
                }else{

                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }

                break;

            case R.id.re_setting:
//                if (util.getIsLogin()) {
//                    startActivity(new Intent(getActivity(), SettingActivity.class));
//                }else{
//                    ToastUtils.showShortToast(getActivity(),"抱歉，请您先登录！");
//                }
                startActivity(new Intent(getActivity(),VideoListAcitivity.class));
                break;
            case R.id.re_wallet:

               // startActivity(new Intent(getActivity(), BadgeIntentActivity.class));

                break;

            case R.id.re_xiangce:
                if (util.getIsLogin()) {
                    startActivity(new Intent(getActivity(), SocialFriendActivity.class)
                                    .putExtra("friendID", util.getUserId()));
                }else{
                    ToastUtils.showShortToast(getActivity(),"抱歉，请您先登录！！");
                }


                break;
            case R.id.re_shoucang:

                if (util.getIsLogin()) {
                    startActivity(new Intent(getActivity(),MyCollectionActivity.class));
                }else{
                    ToastUtils.showShortToast(getActivity(),"抱歉，请您先登录！！");
                }
                break;
            case R.id.re_card_bag:
                if (util.getIsLogin()) {
                    startActivity(new Intent(getActivity(), FeedBackActivity.class ));
                }else{
                    ToastUtils.showShortToast(getActivity(),"抱歉，请您先登录！！");
                }


                break;

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(util.getIsLogin()){
            Glide.with(getActivity()).load(util.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_avatar);
        }

    }
}
