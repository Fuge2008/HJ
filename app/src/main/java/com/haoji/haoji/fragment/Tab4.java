//package com.zhulei.haoji.fragment;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Color;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v7.app.AlertDialog;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.jauker.widget.BadgeView;
//import com.squareup.okhttp.Request;
//import com.zhulei.haoji.R;
//import CommonAdapter;
//import ViewHolder;
//import ActivityManager;
//import RongCloudEvent;
//import BaseFragment;
//import Constants;
//import NetIntent;
//import NetIntentCallBackListener;
//import FriendSearchActivity;
//import LoginActivity;
//import MottoActivity;
//import MyCollectionActivity;
//import MyEnergyActivity;
//import com.zhulei.haoji.ui.PersonlCenterActivity;
//import SettingActivity;
//import ShowPictureActivity;
//import SharedPreferencesUtil;
//import Util;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import io.rong.imkit.RongIM;
//
///**
// * Created by Administrator on 2016/11/2.
// * 选项卡4
// */
//
//public class Tab4 extends BaseFragment implements AdapterView.OnItemClickListener, NetIntentCallBackListener.INetIntentCallBack {
//
//    private ListView mListview_tab4;
//    private ArrayList<HashMap<String, Object>> list = new ArrayList();
//    private int[] Imgs = {R.mipmap.icon_my_data, R.mipmap.icon_my_sc, R.mipmap.icon_my_gy, R.mipmap.icon_my_set, R.mipmap.exit};
//    private String[] Texts = {"我的正能量", "收藏", "个人资料", "设置", "退出"};
//    private CommonAdapter tab4Adapter;
//    private JSONObject json;
//    private SharedPreferencesUtil util;
//    private LocalBroadcastManager broadcastManager;
//    private IntentFilter intentFilter;
//    private ImageView tab4_my_Img;
//    private TextView tab4_my_nickname, tab4_my_geyan, tab4_my_phone;
//    private LinearLayout layout_motto, item_sysinfo;
//    private BadgeView badgeView;
//    private boolean isFresh = false;
//
//    @Override
//    public int InitMainView() {
//        return R.layout.fragment_tab4;
//    }
//
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
//
//    Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    updateUser();
//                    updateView();
//                    break;
//                case 1:
//                    isFresh = true;
//                    NetIntent intent = new NetIntent();
//                    intent.client_getUserByPhone(util.getMobilePhone(), new NetIntentCallBackListener(Tab4.this));
//                    break;
//                case 2:
//                    isFresh = false;
//                    NetIntent intent1 = new NetIntent();
//                    intent1.client_getUserByPhone(util.getMobilePhone(), new NetIntentCallBackListener(Tab4.this));
//                    break;
//                case 3:
//                    badgeView.setBadgeCount(Integer.parseInt(msg.obj.toString()));
//                    if (Integer.parseInt(msg.obj.toString()) == 0) {
//                        badgeView.setVisibility(View.GONE);
//                    } else {
//                        badgeView.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case 4:
//                    updateView();
//                    break;
//                case 5:
//                    tab4Adapter.notifyDataSetChanged();
//                    Util.setListViewHeightBasedOnChildren(mListview_tab4);
//                    break;
//            }
//        }
//    };
//
//    private void updateView() {
//        try {
//            if (util.getIsLogin()) {
//                Util.ImageLoaderToPicAuto(getActivity(), util.getPicture(), tab4_my_Img);
//                tab4_my_nickname.setText(util.getNickName());
//                if (util.getMotto().equals("") | util.getMotto().equals("null")) {
//                    tab4_my_geyan.setText("格言:点击可以发表个性格言");
//                    tab4_my_geyan.setTextColor(getResources().getColor(R.color.colorGray));
//                } else {
//                    tab4_my_geyan.setText("格言:" + util.getMotto());
//                    tab4_my_geyan.setTextColor(getResources().getColor(R.color.colorWhite));
//                }
//                tab4_my_phone.setText(util.getMobilePhone());
//            } else {
//                tab4_my_Img.setImageResource(R.drawable.my_headphoto);
//                tab4_my_nickname.setText("请登录");
//                tab4_my_geyan.setText("");
//                tab4_my_phone.setText("");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void updateUser() {
//        try {
//            util.setPicture(json.getString("pic"));
//            util.setProvince(json.getString("province"));
//            util.setCity(json.getString("city"));
//            util.setToken(json.getString("token"));
//            util.setNickName(json.getString("nickname"));
//            util.setRealName(json.getString("realname"));
//            util.setMotto(json.getString("motto"));
//            util.setBirthday(json.getString("birthday"));
//            util.setZone(json.getString("zone"));
//            util.setStudentid(json.getString("studentid"));
//            util.setUniversity(json.getString("university"));
//            util.setCollege(json.getString("college"));
//            util.setMajor(json.getString("major"));
//            util.setSex(json.getInt("sex"));
//            System.out.println("sex:" + json.getInt("sex"));
//            if (util.getIsLogin() && !isFresh) {
//                Util.connect(util.getToken());
//                RongCloudEvent.init(getActivity());
//            } else {
//                //SystemDialog.DialogToast(getActivity(), "已经登录了");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public void initUI() {
//        util = new SharedPreferencesUtil(getActivity());
//        tab4_my_Img = (ImageView) rootview.findViewById(R.id.tab4_my_Img);
//        tab4_my_nickname = (TextView) rootview.findViewById(R.id.tab4_my_nickname);
//        tab4_my_geyan = (TextView) rootview.findViewById(R.id.tab4_my_geyan);
//        layout_motto = (LinearLayout) rootview.findViewById(R.id.layout_motto);
//        layout_motto.setOnClickListener(this);
//        item_sysinfo = (LinearLayout) rootview.findViewById(R.id.item_sysinfo);
//        tab4_my_phone = (TextView) rootview.findViewById(R.id.tab4_my_phone);
//        mListview_tab4 = (ListView) rootview.findViewById(R.id.listview_tab4);
//        tab4Adapter = new CommonAdapter<HashMap<String, Object>>(getActivity(), list, R.layout.tab4_listview_item) {
//
//            @Override
//            public void convert(ViewHolder helper, HashMap<String, Object> item) {
//                helper.setText(R.id.item_text, item.get("text").toString());
//                helper.setImageResource(R.id.item_img, Integer.parseInt(item.get("img").toString()));
//            }
//        };
//        mListview_tab4.setAdapter(tab4Adapter);
//        mListview_tab4.setOnItemClickListener(this);
//        rootview.findViewById(R.id.tab4_my_Img).setOnClickListener(this);
//        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(Constants.Action_update);
//        intentFilter.addAction(Constants.Action_login);
//        intentFilter.addAction(Constants.Action_badge);
//        intentFilter.addAction(Constants.Action_logout);
//        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
//        updateView();
//
//        rootview.findViewById(R.id.item_sysinfo).setOnClickListener(this);
//
//        badgeView = new BadgeView(getActivity());
//        badgeView.setTargetView(rootview.findViewById(R.id.show_badge));
//        badgeView.setBadgeCount(0);
//        badgeView.setBadgeMargin(2);
//        badgeView.setBackground(12, Color.RED);
//        badgeView.setVisibility(View.GONE);
//        if (util.getIsLogin()) {
//            handler.sendEmptyMessage(2);
//        }
//        handler.sendEmptyMessage(5);
//    }
//
//    @Override
//    public void loadData() {
//        for (int i = 0; i < Imgs.length; i++) {
//            HashMap<String, Object> hashMap = new HashMap<String, Object>();
//            hashMap.put("img", Imgs[i]);
//            hashMap.put("text", Texts[i]);
//            list.add(hashMap);
//        }
//        tab4Adapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (util.getIsLogin()) {
//            switch (v.getId()) {
//                case R.id.tab4_my_Img:
//                    Intent intent = new Intent(getActivity(), ShowPictureActivity.class);
//                    intent.putExtra("type", 1);
//                    intent.putExtra("path", util.getPicture());
//                    startActivity(intent);
//                    break;
//                case R.id.layout_motto:
//                    startActivity(new Intent(getActivity(), MottoActivity.class));
//                    break;
//                case R.id.item_sysinfo:
//                    startActivity(new Intent(getActivity(), FriendSearchActivity.class));
//                    break;
//            }
//        } else {
//            startActivity(new Intent(getActivity(), LoginActivity.class));
//        }
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (util.getIsLogin()) {
//            switch (position) {
//                case 0:
//                    Intent intent = new Intent(getActivity(), MyEnergyActivity.class);
//                    startActivity(intent);
//                    break;
//                case 1:
//                    Intent intent1 = new Intent(getActivity(), MyCollectionActivity.class);
//                    startActivity(intent1);
//                    break;
//                case 2:
//                    startActivity(new Intent(getActivity(), PersonlCenterActivity.class));
//                    break;
//                case 3:
//                    startActivity(new Intent(getActivity(), SettingActivity.class));
//                    break;
//            }
//        } else {
//            startActivity(new Intent(getActivity(), LoginActivity.class));
//        }
//        if (position == 4) {
//            new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("确定注销登录？").setPositiveButton("退出帐号", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    util.setIsLogin(false);
//                    RongIM.getInstance().logout();
//                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent().setAction(Constants.Action_logout));
//                    startActivity(new Intent(getActivity(), LoginActivity.class));
//                }
//            }).setNegativeButton("退出软件", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    ActivityManager.init().removeAll();
//                }
//            }).create().show();
//        }
//    }
//
//    @Override
//    public void onError(Request request, Exception e) {
//
//    }
//
//    @Override
//    public void onResponse(String response) {
//        System.out.println(response);
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            json = jsonObject.getJSONObject("user");
//            handler.sendEmptyMessage(0);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//}
