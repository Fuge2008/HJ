package com.haoji.haoji.comment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haoji.haoji.R;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.AddEnergyActivity;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SPUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.squareup.okhttp.Request;

import java.util.List;


public class SocialFriendAdapter extends BaseAdapter implements NetIntentCallBackListener.INetIntentCallBack{

    private SocialFriendActivity context;
    private List<JSONObject> users;
    private LayoutInflater inflater;
    public RelativeLayout re_edittext;
    private LinearLayout ll_publish;
    private TextView tv_nickname,tv_moto;
    private ImageView iv_avatar;

    private String recordDate = "";
    private String myuserID;
    private String myAvatar;
    private String myNick;
    private String nickName,friendId;
    private SharedPreferencesUtil util;


    public SocialFriendAdapter(SocialFriendActivity context1,
                               List<JSONObject> jsonArray) {
        this.context = context1;

        this.users = jsonArray;
        inflater = LayoutInflater.from(context);
        util=new SharedPreferencesUtil(context);
        myuserID = util.getUserId();
        myNick=util.getNickName();
        myAvatar =util.getPicture();

        re_edittext = (RelativeLayout) context.findViewById(R.id.re_edittext);
        LogUtils.i("users=====>"+users.size());
        if (users.size()>0){
            friendId=users.get(0).getString("userid");
            LogUtils.i("friendid=====>"+friendId);
        }//TODO 无效代码

    }

    @Override
    public int getCount() {
        return users.size()+2;
    }

    @Override
    public JSONObject getItem(int position) {
        if (position == 0) {
            return null;
        } else {
            return users.get(position - 2);
        }

    }

    public List<JSONObject> getJSONs() {

        return users;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            View  view = inflater.inflate(R.layout.item_moments_header, null, false);
            ImageView iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);

            TextView tv_nickname= (TextView)view. findViewById(R.id.tv_nickname);
            TextView tv_moto= (TextView) view.findViewById(R.id.tv_moto);
            String pic="",nickname="",motto="";
            if(StringUtils.equals((String)SPUtils.get(context,"friendid",""),util.getUserId())){
                pic=util.getPicture();
                motto=util.getMotto();
                nickname=util.getNickName();
            }else{
                pic= (String) SPUtils.get(context,"friendHead","");
                motto= (String) SPUtils.get(context,"friendMotto","");
                nickname= (String) SPUtils.get(context,"friendNickname","");

            }
            tv_nickname.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tv_nickname.setText(nickname);
            Glide.with(context).load(pic).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_avatar);//TODO   展示顶部自己的头像
                if(StringUtils.isNotEmpty(motto,true)){
                    tv_moto.setText(motto);
            }
            return view;
        } else if(position==1){
            View  view = inflater.inflate(R.layout.item_xiangce_publish, null, false);
            LinearLayout ll_camare_public= (LinearLayout) view.findViewById(R.id.ll_camare_public);
            ll_publish= (LinearLayout) view.findViewById(R.id.ll_publish);
            if(StringUtils.equals((String)SPUtils.get(context,"friendid",""),util.getUserId())){
                ll_camare_public.setVisibility(View.VISIBLE);
                ll_publish.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, AddEnergyActivity.class));
                    }
                });
            }else{
                ll_camare_public.setVisibility(View.GONE);
            }


            return view;
        }else {

            convertView = inflater.inflate(R.layout.item_moments_me, parent, false);
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.image_1 = (ImageView) convertView.findViewById(R.id.image_1);
                holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
                holder.tv_day = (TextView) convertView.findViewById(R.id.tv_day);
                holder.tv_month = (TextView) convertView.findViewById(R.id.tv_month);
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
                holder.view_header = (View) convertView.findViewById(R.id.view_header);
                convertView.setTag(holder);
            }

            JSONObject json = users.get(position-2);
            // 如果数据出错....

            if (json == null || json.size() == 0) {
                users.remove(position-2);
                this.notifyDataSetChanged();
            }
            final String userID = json.getString("userid");
            String content = json.getString("content");
            String imageStr = json.getString("apppics");
            String location = json.getString("place");
            final String sID = json.getString("id");

            String rel_time = json.getString("updatetime");
            String userHead = json.getString("pic");


            int favoriteStatus = json.getInteger("favoritestatus");
            int praiseStatus = json.getInteger("praisestatus");
            String praiseNum = json.getString("praisenum");
            String browseNum = json.getString("browsenum");
            nickName = json.getString("nickname");
            String praiseName = json.getString("praisename");

            // 设置文章中的图片

            if (imageStr != null) {
               // Log.e("imageStr--->>", imageStr);
                String[] images = imageStr.split(";");
                int imNumb = images.length;
                //Log.e("imNumb--->>", imNumb + "");
                if (StringUtils.isUrl(images[0]) & imNumb > 0 ) {
                    holder.image_1.setVisibility(View.VISIBLE);
                    //holder.image_1.setImageURI(Uri.parse(images[0]));
                    //Util.ImageLoaderToPicAuto(context, images[0],  holder.image_1);
                    Glide.with(context).load(images[0]).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into( holder.image_1);//TODO   展示顶部自己的头像
                    holder.tv_num.setVisibility(View.VISIBLE);
                    holder.tv_num.setText("共" + String.valueOf(imNumb) + "张");

                }

            } else {

                holder.image_1.setVisibility(View.GONE);
                holder.tv_num.setVisibility(View.GONE);
            }
//            //隐藏位置显示
//            if (location != null && !location.equals("0")) {
//                holder.tv_location.setVisibility(View.VISIBLE);
//                holder.tv_location.setText(location);
//            }
            // 显示文章内容
            holder.tv_content.setText(content);

            // 显示时间

            setDateText(rel_time, util.getTime(),
                    holder.tv_day, holder.tv_month, holder.view_header);//TODO  时间设置

            return convertView;
        }


    }

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {

    }

    public static class ViewHolder {
        ImageView iv_avatar;

        // 时间
        TextView tv_num;

        ImageView image_1;

        // 动态内容
        TextView tv_content;

        // 位置
        TextView tv_location;
        TextView tv_month;
        TextView tv_day;
        // 顶部空格
        View view_header;

    }

    private void setDateText(String rel_time, String nowTime, TextView tv_day,
                             TextView tv_month, View view_header) {
        String date = rel_time.substring(0, 10);
        String moth = rel_time.substring(5, 7);
        String day = rel_time.substring(8, 10);
        if (moth.startsWith("0")) {
            moth = moth.substring(1);
        }
        if (!date.equals(recordDate)) {
            view_header.setVisibility(View.VISIBLE);
            tv_day.setVisibility(View.VISIBLE);
            tv_month.setVisibility(View.VISIBLE);
            tv_day.setText(day);
            tv_month.setText(moth + "月");
        } else {
            view_header.setVisibility(View.GONE);
            tv_day.setVisibility(View.GONE);
            tv_month.setVisibility(View.GONE);
        }
    }


    // 设置点赞的
    private void setGoodTextClick(TextView mTextView2, JSONArray data,
                                  LinearLayout ll_goodmembers, View view, int cSize) {
        if (data == null || data.size() == 0) {
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

            JSONObject json_good = data.getJSONObject(i);
            // String userID = json_good.getString("userID");

            String userID_temp = json_good.getString("userID");
            String nick = userID_temp;

            if (userID_temp.equals(myuserID)) {
                nick = myNick;
            } else {
                nick = nickName;

            }
            if (i != (data.size() - 1) && data.size() > 1) {
                ssb.append(nick + ",");
            } else {
                ssb.append(nick);

            }

            ssb.setSpan(new TextViewURLSpan(nick, userID_temp, 0), start, start
                    + nick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = ssb.length();

        }

        mTextView2.setText(ssb);
        mTextView2.setMovementMethod(LinkMovementMethod.getInstance());

    }

    // 设置点赞的
    private void setCommentTextClick(TextView mTextView2, JSONArray data,
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
            String userID_temp = json.getString("userID");
            String content = json.getString("content");
            String scID = json.getString("scID");
            String nick = userID_temp;

            if (userID_temp.equals(myuserID)) {
                nick = myNick;

            } else {

                nick = nickName;


            }
            String content_0 = "";
            String content_1 = ": " + content;
            String content_2 = ": " + content + "\n";
            if (i == (data.size() - 1) || (data.size() == 1 && i == 0)) {
                ssb.append(nick + content_1);
                content_0 = content_1;
            } else {

                ssb.append(nick + content_2);
                content_0 = content_2;
            }

            ssb.setSpan(new TextViewURLSpan(nick, userID_temp, 1), start, start
                    + nick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (userID_temp.equals(myuserID)) {

                ssb.setSpan(new TextViewURLSpan(nick, userID_temp, i, scID, 2,
                                mTextView2, data, view, goodSize), start,
                        start + nick.length() + content_0.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            start = ssb.length();

        }

        mTextView2.setText(ssb);
        mTextView2.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private class TextViewURLSpan extends ClickableSpan {
        private String userID;
        // 0是点赞里面的名字。1是评论里面的名字；2是评论中的删除
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
                ds.setColor(context.getResources().getColor(R.color.text_color));

            }
            ds.setUnderlineText(false); // 去掉下划线
        }

        @Override
        public void onClick(final View widget) {

            if (widget instanceof TextView) {
                ((TextView) widget).setHighlightColor(context.getResources()
                        .getColor(android.R.color.darker_gray));
                new Handler().postDelayed(new Runnable() {

                    public void run() {

                        ((TextView) widget).setHighlightColor(context
                                .getResources().getColor(
                                        android.R.color.transparent));

                    }

                }, 1000);

            }


            if (type == 2) {
                showDeleteDialog(userID, postion, scID, type, ctextView,
                        cjsons, view, goodSize);

            } else {

                Toast.makeText(context, userID, Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * 显示发表评论的输入框
     */

    public void showCommentEditText(final String sID,
                                    final TextView tv_comment, final JSONArray jsons, final View view,
                                    final int goodSize) {
        if (re_edittext == null || re_edittext.getVisibility() != View.VISIBLE) {
            re_edittext = (RelativeLayout) context
                    .findViewById(R.id.re_edittext);
            re_edittext.setVisibility(View.VISIBLE);
            final EditText et_comment = (EditText) re_edittext
                    .findViewById(R.id.et_comment);
            Button btn_send = (Button) re_edittext.findViewById(R.id.btn_send);
            btn_send.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String comment = et_comment.getText().toString().trim();
                    if (TextUtils.isEmpty(comment)) {
                        Toast.makeText(context, "请输入评论", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    submitComment(sID, comment, tv_comment, jsons, view,
                            goodSize);
                    et_comment.setText("");
                    hideCommentEditText();
                }

            });
        }

    }

    /**
     * 隐藏发表评论的输入框
     */
    public void hideCommentEditText() {
        if (re_edittext != null && re_edittext.getVisibility() == View.VISIBLE)
            re_edittext.setVisibility(View.GONE);
    }

    /**
     * 提交评论
     */
    private void submitComment(String sID, String comment, TextView tv_comment,
                               JSONArray jsons, View view, int goodSize) {
        String tag = String.valueOf(System.currentTimeMillis());

        // 即时改变当前UI
        JSONObject json = new JSONObject();
        json.put("userID", myuserID);
        json.put("content", comment);
        // 本地标记，方便本地定位删除，服务器端用不到这个字段
        json.put("tag", tag);
        jsons.add(json);
        setCommentTextClick(tv_comment, jsons, view, goodSize);
        //
        // 更新后台
        NetIntent netIntent1 = new NetIntent();
        netIntent1.client_addComment(util.getUserId(), sID, comment.toString(), "1", "", new NetIntentCallBackListener(this));



    }

    /**
     * 点赞
     */
    public void setGood(String sID, TextView tv_good, JSONArray jsons,
                        LinearLayout ll_goodmembers_temp, View view, int cSize) {
        // 即时改变当前UI
        JSONObject json = new JSONObject();
        json.put("userID", myuserID);
        jsons.add(json);
        setGoodTextClick(tv_good, jsons, ll_goodmembers_temp, view, cSize);
        // 更新后台

        NetIntent netIntent = new NetIntent();
        netIntent.client_addPraise(util.getUserId(), sID, "1", new NetIntentCallBackListener(this));


    }

    /**
     * 取消点赞
     */
    public void cancelGood(String sID, TextView tv_good, JSONArray jsons,
                           LinearLayout ll_goodmembers_temp, View view, int cSize) {

        // 即时改变当前UI
        for (int i = 0; i < jsons.size(); i++) {
            JSONObject json = jsons.getJSONObject(i);
            if (json.getString("userID").equals(myuserID)) {
                jsons.remove(i);
            }
        }
        setGoodTextClick(tv_good, jsons, ll_goodmembers_temp, view, cSize);
        NetIntent netIntent = new NetIntent();
        netIntent.client_deletePraise(util.getUserId(), sID, "1", new NetIntentCallBackListener(this));




    }

    private void showDeleteDialog(final String userID, final int postion,
                                  final String scID, final int type, final TextView ctextView,
                                  final JSONArray cjsons, final View view, final int goodSize) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_social_main);
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("复制");
        tv_paizhao.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(cjsons.getJSONObject(postion).getString("content")
                        .trim());

                // cmb.setPrimaryClip(ClipData clip)

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

    // 删除评论
    private void deleteComment(String userID, final int postion, String scID,
                               int type, TextView ctextView, final JSONArray cjsons, View view,
                               int goodSize) {

        if (scID == null) {
            scID = "LOCAL";
        }
        ;
        String tag = cjsons.getJSONObject(postion).getString("tag");
        if (tag == null) {
            tag = String.valueOf(System.currentTimeMillis());
        }
        // 更新UI
        cjsons.remove(postion);
        setCommentTextClick(ctextView, cjsons, view, goodSize);
        NetIntent netIntent = new NetIntent();
        netIntent.client_deleteComment(util.getUserId(), userID, "1", new NetIntentCallBackListener(this));


    }

}
