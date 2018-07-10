package com.haoji.haoji.fragment.tab2;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haoji.haoji.R;
import com.haoji.haoji.adapter.CommentAdapter;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.GoodView;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.LoginActivity;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;
import com.superplayer.library.SuperPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.onekeyshare.OnekeyShare;



public class VideoPlayActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack, SuperPlayer.OnNetChangeListener {
    private static String TAG = VideoPlayActivity.class.getSimpleName();
    @BindView(R.id.view_super_player)
    SuperPlayer viewSuperPlayer;
    @BindView(R.id.video_pic)
    ImageView videoPic;
    @BindView(R.id.item_name)
    TextView itemName;
    @BindView(R.id.item_time)
    TextView itemTime;
    @BindView(R.id.item_date)
    TextView itemDate;
    @BindView(R.id.item_num)
    TextView itemNum;
    @BindView(R.id.listview_comment)
    ListView listviewComment;
    @BindView(R.id.edit_comment_content)
    EditText editCommentContent;
    @BindView(R.id.btn_comment_submit)
    Button btnCommentSubmit;
    @BindView(R.id.btn_sc)
    ImageView btnSc;
    @BindView(R.id.iv_priaise)
    ImageView ivPriaise;
    @BindView(R.id.rl_detail)
    RelativeLayout rlDetail;
    @BindView(R.id.ll_coment_praise)
    LinearLayout llComentPraise;

    private CommentAdapter adapter;
    private ArrayList<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>>();
    private JSONObject json;
    private String id, videoTitle;
    private SharedPreferencesUtil util;
    private boolean isLike = false;
    private boolean isSc = false;
    private SuperPlayer player;
    private GoodView mGoodView;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    loadData();
                    break;
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    new NetIntent().client_getCommentList(util.getUserId(), id, "2", new NetIntentCallBackListener(VideoPlayActivity.this));
                    break;
            }
        }
    };



    @Override
    public void initMainView() {
        setContentView(R.layout.activity_video_info);
        ButterKnife.bind(this);
        id = this.getIntent().getStringExtra("id");
        util = new SharedPreferencesUtil(this);

    }

    @Override
    public void initUi() {
        player = (SuperPlayer) findViewById(R.id.view_super_player);
        mGoodView = new GoodView(this);
        adapter = new CommentAdapter(this, maps, listener);
        listviewComment.setAdapter(adapter);

            new NetIntent().client_getVideoById(id, new NetIntentCallBackListener(this));
        if(util.getIsLogin()){
            new NetIntent().client_getCommentList(util.getUserId(), id, "2", new NetIntentCallBackListener(this));
        }
        player.getView(R.id.view_jky_player_iv_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });

    }


    @Override
    public void loadData() {
        try {
            if (json != null) {
                if (json.has("videourl")) {
                    final String videPath = json.getString("videourl");
                    Glide.with(this).load(json.getString("videopic")).into(videoPic);
                    videoTitle = json.getString("videotitle");
                    final String videoTitle = json.getString("videotitle");
                    initPlayer(videoTitle, videPath);
                }
                if (json.has("praisestatus")) {
                    if (json.getString("praisestatus").equals("1")) {
                        isLike = true;
                        btnSc.setImageResource(R.mipmap.ysc60);
                    } else {
                        isLike = false;
                        btnSc.setImageResource(R.mipmap.sc60);
                    }
                }
                if (json.has("favoritestatus")) {
                    if (json.getString("favoritestatus").toString().equals("1")) {
                        isSc = true;
                    } else {
                        isSc = false;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.comment_item_likes_click:
                    if(util.getIsLogin()){
                        HashMap map = (HashMap) v.getTag();
                        new NetIntent().client_addPraise(util.getUserId(), map.get("id").toString(), "4", new NetIntentCallBackListener(VideoPlayActivity.this));
                        handler.sendEmptyMessageDelayed(2,1000);
                        ((ImageView) v).setImageResource(R.drawable.good_checked);
                        mGoodView.setText("+1");
                        mGoodView.show(v);
                    }

                    break;
            }
        }
    };

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        Log.d(TAG, "onResponse: " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("code")) {
                if (jsonObject.has("video")) {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("video"));
                    json = jsonArray.getJSONObject(0);
                    handler.sendEmptyMessage(0);
                } else if (jsonObject.has("commentList")) {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("commentList"));
                    maps.clear();
                    maps.addAll(Util.toHashMap(jsonArray));
                    handler.sendEmptyMessage(1);
                }
            } else {
                ToastUtils.showShortToastSafe(VideoPlayActivity.this, jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            handler.sendEmptyMessage(3);
            e.printStackTrace();
        }
    }


    /**
     * 初始化播放器
     */
    private void initPlayer(String videoTitle, String videPath) {

        player.setNetChangeListener(true)//设置监听手机网络的变化
                .setOnNetChangeListener(this)//实现网络变化的回调
                .onPrepared(new SuperPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
                         */
                        videoPic.setVisibility(View.GONE);

                    }
                }).onComplete(new Runnable() {
            @Override
            public void run() {
                /**
                 * 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
                 */videoPic.setVisibility(View.VISIBLE);


            }
        }).onInfo(new SuperPlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {
                /**
                 * 监听视频的相关信息。
                 */

            }
        }).onError(new SuperPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                /**
                 * 监听视频播放失败的回调
                 */

            }
        }).setTitle(videoTitle)//设置视频的titleName
                .play(videPath);//开始播放视频
        player.setScaleType(SuperPlayer.SCALETYPE_FITXY);
        player.setPlayerWH(0, player.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置

    }


    /**
     * 网络链接监听类
     */
    @Override
    public void onWifi() {
        Toast.makeText(this, "当前网络环境是WIFI", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMobile() {
        Toast.makeText(this, "当前网络环境是手机网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisConnect() {
        Toast.makeText(this, "网络链接断开", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoAvailable() {
        Toast.makeText(this, "无网络链接", Toast.LENGTH_SHORT).show();
    }


    /**
     * 下面的这几个Activity的生命状态很重要
     */
    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }


    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @OnClick({R.id.btn_comment_submit, R.id.btn_sc, R.id.iv_priaise})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_comment_submit:
                if (!editCommentContent.getText().toString().equals("") && util.getIsLogin()) {
                    new NetIntent().client_addComment(util.getUserId(), id, editCommentContent.getText().toString(), "2", "", new NetIntentCallBackListener(this));
                    handler.sendEmptyMessageDelayed(2, 1000);
                    editCommentContent.setText("");
                    ToastUtils.showShortToastSafe(VideoPlayActivity.this,"评论成功");
                }else {
                    startActivity(new Intent(VideoPlayActivity.this, LoginActivity.class));
                }
                break;
            case R.id.btn_sc:
                if(util.getIsLogin()){
                if (!isSc) {
                    new NetIntent().client_addContain(util.getUserId(), id, "2", new NetIntentCallBackListener(this));
                    //btnSc.setImageResource(R.mipmap.ysc60);
                    isSc = true;
                   // SystemDialog.DialogToast(this, "收藏成功");
                    ((ImageView) view).setImageResource(R.mipmap.ysc60);
                    mGoodView.setTextInfo("收藏成功", Color.parseColor("#f66467"), 12);
                    mGoodView.show(view);
                } else {
                    new NetIntent().client_deleteContain(util.getUserId(), id, "2", new NetIntentCallBackListener(this));
                    //btnSc.setImageResource(R.mipmap.sc60);
                    isSc = false;
                    //SystemDialog.DialogToast(this, "取消收藏");
                    ((ImageView) view).setImageResource(R.mipmap.sc60);
                    mGoodView.setTextInfo("取消收藏", Color.parseColor("#f66467"), 12);
                    mGoodView.show(view);
                }
                }else {
                    startActivity(new Intent(VideoPlayActivity.this, LoginActivity.class));
                }

                break;
            case R.id.iv_priaise:
                ((ImageView) view).setImageResource(R.drawable.good_checked);
                mGoodView.setImage(getResources().getDrawable(R.drawable.good_checked));
                mGoodView.show(view);
                ToastUtils.showShortToastSafe(VideoPlayActivity.this, "点赞成功");

                break;
        }
    }

    // 一键分享
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("传递正知，记录美好1！欢迎访问：http://www.hao-ji.cn/");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://www.hao-ji.cn/");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("传递正知，记录美好2！");
        // 分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://www.hao-ji.cn/uploads/kind/image/20170215/20170215071149_50979.png");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.hao-ji.cn/");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("传递正知，记录美好3！");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("传递正知，记录美好4！");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.hao-ji.cn/");

        // 启动分享GUI
        oks.show(this);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);


        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            rlDetail.setVisibility(View.VISIBLE);
            llComentPraise.setVisibility(View.VISIBLE);
            listviewComment.setVisibility(View.VISIBLE);

        }

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rlDetail.setVisibility(View.GONE);
            llComentPraise.setVisibility(View.GONE);
            listviewComment.setVisibility(View.GONE);
        }
        if (player != null) {
            player.onConfigurationChanged(config);
        }
    }


}
