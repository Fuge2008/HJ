package com.haoji.haoji.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.haoji.haoji.R;
import com.haoji.haoji.adapter.UserAdapter;
import com.haoji.haoji.app.RongCloudEvent;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.CustomProgress;
import com.haoji.haoji.custom.SideBarView;
import com.haoji.haoji.model.User;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.ChineseToEnglish;
import com.haoji.haoji.util.CompareSort;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;


public class TXLActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack
        , View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
        , IRongCallback.ISendMessageCallback, SideBarView.LetterSelectListener {
    private static String TAG = TXLActivity.class.getSimpleName();
    @BindView(R.id.ll_new_friends_info)
    LinearLayout llNewFriendsInfo;
    @BindView(R.id.ll_my_attention_friends)
    LinearLayout llMyAttentionFriends;
    @BindView(R.id.item_haoji_img)
    ImageView itemHaojiImg;
    @BindView(R.id.ll_haoji_team)
    LinearLayout llHaojiTeam;
    @BindView(R.id.item_user_img)
    ImageView itemUserImg;
    @BindView(R.id.ll_my_owner)
    LinearLayout llMyOwner;
    @BindView(R.id.tv_my_owner)
    TextView tvMyOwner;

    private ListView listview_user;
    private UserAdapter adapter;
    private ArrayList<User> users = new ArrayList<User>();
    private SharedPreferencesUtil util;
    /*如果为1是信息分享用户选择*/
    private int type = 0;
    private String title = "";
    private String content = "";
    SideBarView sideBarView;
    TextView mTip;

    private CustomProgress dialog;


    @Override
    public void initMainView() {
        setContentView(R.layout.actitivty_txl);
        ButterKnife.bind(this);
        util = new SharedPreferencesUtil(this);
        NetIntent netIntent = new NetIntent();
        if (util.getIsLogin()) {
            netIntent.client_getFriendsList(util.getUserId(), new NetIntentCallBackListener(this));
            findViewById(R.id.head_search).setOnClickListener(this);
        } else {
            ToastUtils.showShortToastSafe(TXLActivity.this, "请登录");
        }


    }

    @Override
    public void initUi() {
        type = this.getIntent().getIntExtra("type", 0);
        if (type == 1) {
            title = this.getIntent().getStringExtra("title");
            content = this.getIntent().getStringExtra("content");
            Util.setHeadTitleMore(this, title, true);
        } else {
            Util.setHeadTitleMore(this, "通讯录", true);
        }
        dialog = CustomProgress.show(this, "加载中..", true, null);
        sideBarView = (SideBarView) findViewById(R.id.sidebarview);
        mTip = (TextView) findViewById(R.id.tip);
        listview_user = (ListView) findViewById(R.id.listview_user);
        listview_user.setOnItemClickListener(this);
        listview_user.setOnItemLongClickListener(this);
        sideBarView.setOnLetterSelectListen(this);


    }

    @Override
    public void loadData() {
        Glide.with(this).load(getResources().getString(R.string.init_haoji_picture_path)).diskCacheStrategy(DiskCacheStrategy.ALL).into(itemHaojiImg);//TODO  写死，待处理头像,此处应由服务器返回自己信息及好几团队
        Glide.with(this).load(util.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).into(itemUserImg);
        tvMyOwner.setText(util.getNickName());
    }


    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateData();
                    break;
                case 1:
                    ToastUtils.showShortToastSafe(TXLActivity.this, "分享成功");
                    finish();
                    break;
            }
        }
    };

    private void updateData() {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getNickname().equals("null") || user.getNickname().equals("")) {
                user.setNickname("手机" + user.getUsername());
            }
            String firstSpell = ChineseToEnglish.getFirstSpell(user.getNickname());
            String sub = firstSpell.substring(0, 1).toUpperCase();
            if (sub.matches("[A-Z]")) {
                user.setLetter(sub);
            } else {
                user.setLetter("#");
            }
        }
        Collections.sort(users, new CompareSort());
        adapter = new UserAdapter(TXLActivity.this,users);
        listview_user.setAdapter(adapter);


    }

    @Override
    public void onError(Request request, Exception e) {
    }

    @Override
    public void onResponse(String response) {
        if (dialog != null)
            dialog.dismiss();
        System.out.println(response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("code")) {
                if (jsonObject.has("friendsList")) {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("friendsList"));
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    JSONArray list = jsonObject1.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        users.add(new Gson().fromJson(list.getJSONObject(i).toString(), User.class));
                    }
                    handler.sendEmptyMessage(0);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 自定义发送消息方法
     *
     * @param content
     * @param userid
     */
    public void sendMessage(String content, String userid) {
        TextMessage mymessage = TextMessage.obtain(content);
        io.rong.imlib.model.Message message =
                io.rong.imlib.model.Message.obtain(userid, Conversation.ConversationType.PRIVATE, mymessage);
        RongIM.getInstance().sendMessage(message, null, null, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_search:
                Intent intent = new Intent(this, FriendSearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final User user = users.get(position);
        final NetIntent netIntent = new NetIntent();
        new AlertDialog.Builder(this).setTitle("菜单")
                .setMessage("删除好友？")
                .setNegativeButton("删除好友", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        netIntent.client_deleteFriend(util.getUserId(), user.getUserid(), new NetIntentCallBackListener(TXLActivity.this));
                        users.remove(position);
                        adapter.notifyDataSetChanged();
                            RongCloudEvent.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, user.getUserid(), new RongIMClient.ResultCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    ToastUtils.showShortToastSafe(TXLActivity.this,"删除信息成功！");
                                    LogUtils.i("删除数据："+aBoolean);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    ToastUtils.showShortToastSafe(TXLActivity.this,"删除信息失败！");
                                    LogUtils.i("删除数据："+errorCode);
                                }
                            });
                    }
                }).create().show();

        return true;
    }

    @Override
    public void onAttached(io.rong.imlib.model.Message message) {

    }

    @Override
    public void onSuccess(io.rong.imlib.model.Message message) {
        ToastUtils.showShortToastSafe(TXLActivity.this, "分享成功");
    }

    @Override
    public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {

    }

    @Override
    public void onLetterSelected(String letter) {
        setListviewPosition(letter);
        mTip.setText(letter);
        mTip.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLetterChanged(String letter) {
        setListviewPosition(letter);
        mTip.setText(letter);
    }

    @Override
    public void onLetterReleased(String letter) {
        mTip.setVisibility(View.GONE);
    }

    private void setListviewPosition(String letter) {
        int firstLetterPosition = adapter.getFirstLetterPosition(letter);
        if (firstLetterPosition != -1) {
            listview_user.setSelection(firstLetterPosition);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = users.get(position);
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().startPrivateChat(this, user.getUserid(), user.getNickname());
        }
    }

    @OnClick({R.id.ll_new_friends_info, R.id.ll_my_attention_friends, R.id.ll_haoji_team, R.id.ll_my_owner})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_new_friends_info:
                Intent intent = new Intent(this, FriendSearchActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_my_attention_friends:
                Intent intent2 = new Intent(this, MyAttentionActivity.class);
                startActivity(intent2);
                break;
            case R.id.ll_haoji_team:
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(this, "admin", "好记团队");
                }
                break;
            case R.id.ll_my_owner:
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(this, util.getUserId(), util.getNickName());
                }
                break;
        }
    }



}
