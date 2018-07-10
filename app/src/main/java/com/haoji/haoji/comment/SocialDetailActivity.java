package com.haoji.haoji.comment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.ninegridview.NineGridLayout;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.ArticleActivity;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.TimeUtils;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jaydenxiao.com.expandabletextview.ExpandableTextView;


public class SocialDetailActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack , View.OnClickListener{

    private EditText et_comment;
    private JSONObject json = null;
    private Button btn_send;
    private TextView tv_commentmembers,tv_delete, tv_goodmembers,tool_zan_text;
    private JSONArray jsons_tag;
    private ImageView iv_back, iv_pop;
    private String myAvatar,myNick,nickName,myuserID,sID;
    private Context context;
    private SharedPreferencesUtil util;
    private RelativeLayout  rl_edittext;
    private LinearLayout ll_goodmembers;
    private View layout_tool_menu,view_pop;
    private PopupWindow popupWindow;
    private int toolWidth,toolHeight,toolXY,praiseStatus,goodSize_tag;
    private boolean isGood = false;


    @Override
    public void initMainView() {setContentView(R.layout.activity_moments_details);}
    @Override
    public void initUi() {
        util=new SharedPreferencesUtil(this);
        myuserID=util.getUserId();
        myNick=util.getNickName();
        myAvatar=util.getPicture();
        String jsonStr = this.getIntent().getStringExtra("json");
        if (jsonStr == null) {
            finish();
            return;
        }
        json = JSONObject.parseObject(jsonStr);
        initView();
    }

    @Override
    public void loadData() {

    }
    private void initView() {
        iv_back= (ImageView) findViewById(R.id.iv_back);
        et_comment = (EditText) findViewById(R.id.et_comment);
        btn_send = (Button) findViewById(R.id.btn_send);
        TextView tv_nick = (TextView) findViewById(R.id.tv_nick);
        TextView tv_time = (TextView) findViewById(R.id.tv_time);
        ImageView iv_avatar = (ImageView) findViewById(R.id.sdv_image);
        NineGridLayout ndv_nine_view= (NineGridLayout)findViewById(R.id.ndv_nine_view);
        rl_edittext= (RelativeLayout) findViewById(R.id.rl_edittext);
        ExpandableTextView tv_content  = (ExpandableTextView)findViewById(R.id.tv_content);
        TextView tv_location = (TextView)findViewById(R.id.tv_location);
        iv_pop = (ImageView) findViewById(R.id.iv_pop);
        tv_goodmembers = (TextView) findViewById(R.id.tv_goodmembers);
        ll_goodmembers = (LinearLayout) findViewById(R.id.ll_goodmembers);
        tv_commentmembers = (TextView) findViewById(R.id.tv_commentmembers);
        view_pop = (View) findViewById(R.id.view_pop);
        tv_delete = (TextView)findViewById(R.id.tv_delete);
        toolWidth = getResources().getDimensionPixelOffset(R.dimen.tool_layout_width);
        toolHeight = getResources().getDimensionPixelOffset(R.dimen.tool_layout_height);
        toolXY = getResources().getDimensionPixelOffset(R.dimen.tool_XY);
        layout_tool_menu = LayoutInflater.from(this).inflate(R.layout.layout_tool_menu_not_collection, null);
        LinearLayout ll_zan= (LinearLayout) layout_tool_menu.findViewById(R.id.btn_zan);
        LinearLayout ll_pl= (LinearLayout)layout_tool_menu.findViewById(R.id.btn_pl);
        tool_zan_text = (TextView) layout_tool_menu.findViewById(R.id.tool_zan_text);
        popupWindow = new PopupWindow(layout_tool_menu, toolWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.popWindow_anim_tool);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());


        iv_pop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGood){
                    tool_zan_text.setText("取消");
                }else{
                    tool_zan_text.setText("点赞");
                }
                popupWindow.showAsDropDown(v, -toolWidth - toolXY, -(toolXY / 2) - (toolHeight / 2));
            }
        });
        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = et_comment.getText().toString().trim();
                if (TextUtils.isEmpty(comment)) {
                    Toast.makeText(SocialDetailActivity.this, "请输入评论",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                submitComment();
                et_comment.setText("");
                closeInputMethod();
                rl_edittext.setVisibility(View.GONE);

            }
        });
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

      String userID = json.getString("userid");
        String content = json.getString("content");
        String imageStr = json.getString("apppics");
        String location = json.getString("place");
        sID = json.getString("id");
        nickName=json.getString("nickname");
        String rel_time = json.getString("updatetime");
       praiseStatus = json.getInteger("praisestatus");
        String praiseName = json.getString("praisename");
        String userHead = json.getString("pic");

        String[] goodArray1 = new String[0];
        ArrayList<String> goodList = null;
        if(StringUtils.isNotEmpty(praiseName,true)){
            goodArray1 = praiseName.split(",");
            goodList = new ArrayList<String>(Arrays.asList(goodArray1));
        }else{
            goodList = new ArrayList<String>();
        }
        if(StringUtils.equals(praiseStatus+"","1")){
            isGood=true;
        }
        if (userID.equals(myuserID)) {
            tv_delete.setVisibility(View.VISIBLE);
            tv_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPhotoDialog(sID);
                }
            });
        } else {
            tv_delete.setVisibility(View.GONE);
        }
        tv_nick.setText(nickName);
        Glide.with(SocialDetailActivity.this).load(userHead).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_avatar);
        if (imageStr!=null) {
            String[] images = imageStr.split(";");
            int imNumb = images.length;
            if(imNumb!=0 & images[0].contains("http://")){
                List<String> list = new ArrayList<String>(Arrays.asList(images));
                ndv_nine_view.setIsShowAll(true);
               ndv_nine_view.setUrlList(list);
            }
        }
        if (location != null && !location.equals("0")) {
            tv_location.setVisibility(View.VISIBLE);
            tv_location.setText(location);
        }
      tv_content.setText(Html.fromHtml(content));
        if (json.containsKey("type") && json.get("type").toString().equals("3")) {
        tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ArticleActivity.class);
                    intent.putExtra("id", json.get("id").toString());
                    intent.putExtra("type", 1);
                    context.startActivity(intent);
                }
            });
        }
        final JSONArray commentArray = json.getJSONArray("comlist");
        jsons_tag = commentArray;
        setGoodTextClick(tv_goodmembers, goodList, ll_goodmembers,
                view_pop, commentArray.size());
        if (commentArray != null && commentArray.size() != 0) {
            tv_commentmembers.setVisibility(View.VISIBLE);
            setCommentTextClick(tv_commentmembers, commentArray, view_pop,
                    goodList.size());}
        final ArrayList<String> finalGoodList = goodList;
        ll_zan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGood){
                    cancelGood(sID, tv_goodmembers,
                            finalGoodList, ll_goodmembers,
                            view_pop, commentArray.size());
                    tool_zan_text.setText("点赞");
                    popupWindow.dismiss();

                }else {
                    setGood(sID, tv_goodmembers, finalGoodList,
                            ll_goodmembers, view_pop,
                            commentArray.size());
                    tool_zan_text.setText("取消");
                    popupWindow.dismiss();

                }

            }
        });
        ll_pl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentEditText(sID,
                        tv_commentmembers,
                        commentArray, view_pop,
                        finalGoodList.size());
                popupWindow.dismiss();
                rl_edittext.setVisibility(View.VISIBLE);
              //CommonUtils.showKeyboard(SocialDetailActivity.this);
                showSoftInputFromWindow(SocialDetailActivity.this,et_comment);
            }
        });
        tv_time.setText(TimeUtils.getTime(rel_time, util.getTime()));
    }
    public void showCommentEditText(final String sID,
            final TextView tv_comment, final JSONArray jsons, final View view,
            final int goodSize) {
       // openInputMethod(rl_edittext);

    }

    private void showPhotoDialog(final String sID) {
        final AlertDialog dlg = new AlertDialog.Builder(
                SocialDetailActivity.this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_social_delete);
        TextView tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
                dlg.cancel();
            }
        });
        TextView tv_ok = (TextView) window.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                // 更新服务器
                NetIntent netIntent = new NetIntent();
                netIntent.client_deleteEnergy(util.getUserId(), sID, new NetIntentCallBackListener(SocialDetailActivity.this));//TODO  需要测试是否请求服务器成功
                dlg.cancel();
                finish();
            }
        });

    }

    @Override
    public void onError(Request request, Exception e) {}
    @Override
    public void onResponse(String response) {}


    private void setGoodTextClick(TextView mTextView2, List<String> data,
            LinearLayout ll_goodmembers, View view, int cSize) {
        if (data == null || data.size()== 0) {
            ll_goodmembers.setVisibility(View.GONE);
        } else {
            ll_goodmembers.setVisibility(View.VISIBLE);
        }
        if (cSize > 0 && data.size() > 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int start = 0;
        for (int i = 0; i < data.size(); i++) { 
            String nick= data.get(i);
            String userID_t = util.getUserId();
            if (i != (data.size() - 1) && data.size() > 1) {
                ssb.append(nick + ",");
            } else {
                ssb.append(nick);
            }
            ssb.setSpan(new TextViewURLSpan(nick, userID_t, 0), start,
                    start + nick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = ssb.length();
        }
        mTextView2.setText(ssb);
        mTextView2.setMovementMethod(LinkMovementMethod.getInstance());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        


        }
    }

    private class TextViewURLSpan extends ClickableSpan {
        private String userID;
        private int type = 0;
        private TextView ctextView;
        private JSONArray cjsons;
        private View view;
        private int goodSize;
        private String scID;
        private int postion;

        public TextViewURLSpan(String nick, String userID, int postion,
                String scID, int type, TextView ctextView, JSONArray cjsons,
                View view, int goodSize) {
            this.userID = userID;
            this.type = type;
            this.ctextView = ctextView;
            this.cjsons = cjsons;
            this.view = view;
            this.goodSize = goodSize;
            this.scID = scID;
            this.postion = postion;
        }

        public TextViewURLSpan(String nick, String userID, int type) {
            this.userID = userID;
            this.type = type;
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            if (type != 2) {
                ds.setColor(getResources().getColor(R.color.text_color));

            }
            ds.setUnderlineText(false); 
        }

        @Override
        public void onClick(final View widget) {
            if (widget instanceof TextView) {
                ((TextView) widget).setHighlightColor(getResources().getColor(
                        android.R.color.darker_gray));
                new Handler().postDelayed(new Runnable() {

                    public void run() {

                        ((TextView) widget).setHighlightColor(getResources()
                                .getColor(android.R.color.transparent));
                    }
                }, 1000);
            }
            if (type == 2) {
                showDeleteDialog(userID, postion, scID, type, ctextView,
                        cjsons, view, goodSize);
            } else {
                startActivity(new Intent(SocialDetailActivity.this,
                        SocialFriendActivity.class)
                        .putExtra("friendID", userID));
            }
        }

    }

    private void showDeleteDialog(final String userID, final int postion,
            final String scID, final int type, final TextView ctextView,
            final JSONArray cjsons, final View view, final int goodSize) {
        final AlertDialog dlg = new AlertDialog.Builder(
                SocialDetailActivity.this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_social_main);
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("复制");
        tv_paizhao.setOnClickListener(new OnClickListener() {

             public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(cjsons.getJSONObject(postion).getString("content")
                        .trim());

                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("删除");
        tv_xiangce.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                deleteComment(userID, postion, scID, type, ctextView, cjsons,
                        view, goodSize);

                dlg.cancel();
            }
        });
    }
    
    private void deleteComment(String userID, final int postion, String scID,
            int type, TextView ctextView, final JSONArray cjsons, View view,
            int goodSize) {
        if (scID == null) {
            scID = "LOCAL";
        }
        String tag = cjsons.getJSONObject(postion).getString("tag");
        if (tag == null) {
            tag = String.valueOf(System.currentTimeMillis());
        }
        cjsons.remove(postion);
        setCommentTextClick(ctextView, cjsons, view, goodSize);
        NetIntent netIntent = new NetIntent();
        netIntent.client_deleteComment(util.getUserId(),"1",scID,  new NetIntentCallBackListener(this));
        LogUtils.i("info====>scID:"+scID+"info===>userID:"+userID);

    }
    
    private void setCommentTextClick(TextView mTextView2, JSONArray  data,
            View view, int goodSize) {
        if (goodSize > 0 && data.size() > 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        if (data.size() == 0) {
            mTextView2.setVisibility(View.GONE);
        } else {
            mTextView2.setVisibility(View.VISIBLE);
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int start = 0;
        for (int i = 0; i < data.size(); i++) {
            JSONObject json = data.getJSONObject(i);
            String userID_t = json.getString("userid");
            String nick = json.getString("nickname");
            String content = json.getString("content");
            String scID = json.getString("id");
            String content_0 = "";
            String content_1 = ": " + content;
            String content_2 = ": " + content + "\n";
            if (json.size() == 1 && i == 0) {

                ssb.append(nick + content_1);
                content_0 = content_1;
            } else {
                ssb.append(nick + content_2);
                content_0 = content_2;
            }
            ssb.setSpan(new TextViewURLSpan(nick, userID_t, 1), start,
                    start + nick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//.   设置评论列格式
            if (userID_t.equals(util.getUserId())) {
                ssb.setSpan(
                        new TextViewURLSpan(nick, userID_t, i, scID, 2,
                                mTextView2, data, view, goodSize),
                        start, start + nick.length() + content_0.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//.   设置自己评论格式
            }
            start = ssb.length();
        }

        mTextView2.setText(ssb);
        mTextView2.setMovementMethod(LinkMovementMethod.getInstance());//设置长按事件监听
    }
    
    public void setGood(String sID, TextView tv_good, List<String> data,
            LinearLayout ll_goodmembers, View view, int cSize) {
        data.add(util.getNickName());
        setGoodTextClick(tv_good,  data, ll_goodmembers, view, cSize);
        isGood = true;
        NetIntent netIntent = new NetIntent();
        netIntent.client_addPraise(util.getUserId(), sID, "1", new NetIntentCallBackListener(this));

    }
    public void cancelGood(String sID, TextView tv_good, List<String> data,
            LinearLayout ll_goodmembers, View view, int cSize) {
        for (int i = 0; i < data.size(); i++) {
            data.remove(util.getNickName());
        }
        isGood = false;
        setGoodTextClick(tv_good, data, ll_goodmembers, view, cSize);
        NetIntent netIntent = new NetIntent();
        netIntent.client_deletePraise(util.getUserId(), sID, "1", new NetIntentCallBackListener(this));

    }
    private void submitComment() {
        String tag = String.valueOf(System.currentTimeMillis());
        String comment = et_comment.getText().toString().trim();
        JSONObject json = new JSONObject();
        json.put("userid", myuserID);
        json.put("content", comment);
        json.put("nickname",util.getNickName());
        json.put("tag", tag);
        jsons_tag.add(json);
        setCommentTextClick(tv_commentmembers, jsons_tag, view_pop, goodSize_tag);
        NetIntent netIntent1 = new NetIntent();
        netIntent1.client_addComment(util.getUserId(),sID, comment.toString(), "1", "", new NetIntentCallBackListener(this));
        //CommonUtils.hideKeyboard(SocialDetailActivity.this);



    }
    
//    public static void openInputMethod(final View editText) {
//
//        InputMethodManager inputManager = (InputMethodManager) editText
//
//        .getContext().getSystemService(
//
//        Context.INPUT_METHOD_SERVICE);
//
//        inputManager.showSoftInput(editText, 0);
//
//    }

    /**关闭键盘*/
    public void  closeInputMethod(){
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        } finally {
        }

    }
    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
