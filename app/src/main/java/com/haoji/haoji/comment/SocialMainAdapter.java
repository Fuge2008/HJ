package com.haoji.haoji.comment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.haoji.haoji.custom.ninegridview.NineGridLayout;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.ArticleActivity;
import com.haoji.haoji.ui.MyUnreadNewsActivity;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SPUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.TimeUtils;
import com.haoji.haoji.util.ToastUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jaydenxiao.com.expandabletextview.ExpandableTextView;

import static com.haoji.haoji.R.id.view_pop;


public class SocialMainAdapter extends BaseAdapter implements NetIntentCallBackListener.INetIntentCallBack{
    private static String TAG = SocialMainActivity.class.getSimpleName();
    private Activity context;
    private List<JSONObject> users;
    private LayoutInflater inflater;
    public RelativeLayout re_edittext;
    EditText et_comment;
    private String myAvatar,myNick,myuserID,strGood,strCollection;
    private SharedPreferencesUtil util;
    private boolean isCollection=false,isGood=false,isrefresh=false;
    private int favoriteStatus,praiseStatus;




    public SocialMainAdapter(Activity context, List<JSONObject> jsonArray) {
        this.context = context;
        this.users = jsonArray;

        inflater = LayoutInflater.from(context);
        util=new SharedPreferencesUtil(context);
        // 底部评论输入框
        re_edittext = (RelativeLayout) context.findViewById(R.id.re_edittext);
        myuserID = util.getUserId();
        myNick = util.getNickName();
        myAvatar = util.getPicture();
        et_comment = (EditText) re_edittext
                .findViewById(R.id.et_comment);



    }

    @Override
    public int getCount() {
        return users.size() + 1;
    }

    @Override
    public JSONObject getItem(int position) {
        if (position == 0) {
            return null;
        } else {
            return users.get(position - 1);
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        if (position == 0) {
            View  view = inflater.inflate(R.layout.item_moments_header, null, false);
            ImageView iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
            String infoCnt = (String) SPUtils.get(context.getApplicationContext(),"infoCnt","");

            RelativeLayout ll_msg= (RelativeLayout) view.findViewById(R.id.ll_msg);
            ImageView msg_avatar= (ImageView) view.findViewById(R.id.msg_avatar);
            TextView msg_text= (TextView) view.findViewById(R.id.msg_text);
            if(StringUtils.isNotEmpty(infoCnt,true)){
                ll_msg.setVisibility(View.VISIBLE);
                Glide.with(context).load(myAvatar).diskCacheStrategy(DiskCacheStrategy.ALL).into(msg_avatar);//.   展示顶部自己的头像
                msg_text.setText(infoCnt+"条新消息");
            }else{
                ll_msg.setVisibility(View.GONE);
            }
            Glide.with(context).load(myAvatar).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_avatar);//.   展示顶部自己的头像
            iv_avatar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context,UserDetailsActivity.class).putExtra("phone",util.getMobilePhone()));
                }
            });
            ll_msg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    context.startActivity(new Intent(context, MyUnreadNewsActivity.class));
                    SPUtils.put(context.getApplicationContext(),"infoCnt","");
                }
            });
            return view;
        } else {
            convertView = inflater.inflate(R.layout.item_social_main, parent, false);
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.tv_nick = (TextView) convertView.findViewById(R.id.tv_nick);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.iv_avatar = (ImageView) convertView.findViewById(R.id.sdv_image);
                holder.ndv_nine_view= (NineGridLayout) convertView.findViewById(R.id.ndv_nine_view);

                holder.tv_content  = (ExpandableTextView) convertView.findViewById(R.id.tv_content);
                holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
                holder.iv_pop = (ImageView) convertView.findViewById(R.id.iv_pop);

                holder.tv_goodmembers = (TextView) convertView.findViewById(R.id.tv_goodmembers);
                holder.ll_goodmembers = (LinearLayout) convertView.findViewById(R.id.ll_goodmembers);
                holder.tv_commentmembers = (TextView) convertView.findViewById(R.id.tv_commentmembers);
                holder.view_pop = (View) convertView.findViewById(view_pop);
                holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
//                TextView config_hidden = (TextView) convertView.findViewById(R.id.config_hidden);
//                config_hidden.requestFocus();
                convertView.setTag(holder);
            }
            final View view_pop = holder.view_pop;
            final JSONObject json = users.get(position - 1);

            if (json == null || json.size() == 0) {
                users.remove(position - 1);
                this.notifyDataSetChanged();
            }
            final String userID = json.getString("userid");//. 把数据解析出来
            final String content = json.getString("content");
            String imageStr = json.getString("apppics");
            String location = json.getString("place");
            final String sID = json.getString("id");
            final String phone = json.getString("phone");


             favoriteStatus = json.getInteger("favoritestatus");
              praiseStatus = json.getInteger("praisestatus");
            String praiseNum = json.getString("praisenum");
            String browseNum = json.getString("browsenum");
            String nickName = json.getString("nickname");
            String praiseName = json.getString("praisename");
            String userHead = json.getString("pic");
            String realTime = json.getString("updatetime");
            Log.i("info",json.toString());





            // 点赞评论的数据
            String[] goodArray1 = new String[0];

            ArrayList<String> goodList = null;
            if(StringUtils.isNotEmpty(praiseName,true)){
                goodArray1 = praiseName.split(",");//.   将图像URL字符串剪切存入数组
                goodList = new ArrayList<String>(Arrays.asList(goodArray1));
            }else{
                goodList = new ArrayList<String>();
            }
            final JSONArray commentArray = json.getJSONArray("comlist");//.  评论的数据对象
            LogUtils.i("conment=====>"+commentArray.toString());
            // 设置删除键
            if (userID.equals(myuserID)) {//. 如果是自己发表的，则显示删除控件

                holder.tv_delete.setVisibility(View.VISIBLE);
                holder.tv_delete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showPhotoDialog(position - 1, sID);//.   点击能量删除，弹出提示框
                    }

                });
            } else {
                holder.tv_delete.setVisibility(View.GONE);//. 如果不是自己发表的，则隐藏删除控件
            }
            holder.tv_nick.setText(nickName );//. 展示昵称
            holder.tv_nick.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context,
                            SocialFriendActivity.class).putExtra("friendID",
                            userID));//. 点击能量上的头像，则跳转到对应用户的详细信息界面
                }

            });

            // 设置能量中的图片
            if (imageStr!=null) {//.   如果有图片
                String[] images = imageStr.split(";");//.   将图像URL字符串剪切存入数组
                int imNumb = images.length;
                if(imNumb!=0 & images[0].contains("http://")){
                    List<String> list = new ArrayList<String>(Arrays.asList(images));
                    holder.ndv_nine_view.setIsShowAll(true);
                    holder.ndv_nine_view.setUrlList(list);
                }
            }
            // 显示位置
            if (location != null && !location.equals("0")) {//.  当位置信息不为空，则展示位置信息
                holder.tv_location.setVisibility(View.VISIBLE);
                holder.tv_location.setText(location);
            }
            // 显示文章内容
            holder.tv_content.setText(Html.fromHtml(content));
            if (json.containsKey("type") && json.get("type").toString().equals("3")) {
                holder.tv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ArticleActivity.class);
                        intent.putExtra("id", json.get("id").toString());
                        intent.putExtra("type", 1);
                        context.startActivity(intent);
                    }
                });
            }
            final ImageView iv_temp = holder.iv_pop;
            final LinearLayout ll_goodmembers_temp = holder.ll_goodmembers;

            // 显示时间
            holder.tv_time.setText(TimeUtils.getTime(realTime, TimeUtils.getNowTime()));
            Glide.with(context).load(userHead).diskCacheStrategy(DiskCacheStrategy.ALL).into( holder.iv_avatar);
            holder.iv_avatar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("info","头像跳转："+phone+"当前时间"+TimeUtils.getNowTime());
                    context.startActivity(new Intent(context,UserDetailsActivity.class).putExtra("userid",userID).putExtra("phone","0"));
                }
            });


            // 评论
            if (commentArray != null && commentArray.size() != 0) {//. 如果评论不为空，则显示出评论展示框
                holder.tv_commentmembers.setVisibility(View.VISIBLE);
                setCommentTextClick(holder.tv_commentmembers, commentArray,
                        view_pop, goodList.size());//. 本地评论操作
            }



            setGoodTextClick(holder.tv_goodmembers, goodList,
                    ll_goodmembers_temp, view_pop, commentArray.size());//.  点赞操作方法
            final TextView tv_commentmembers_temp = holder.tv_commentmembers;
            final TextView tv_good_temp = holder.tv_goodmembers;
            final List<String> finalGoodArray = goodList;

            if(praiseStatus==1){isGood = true;}
            if(favoriteStatus==1){isCollection=true;}

            showView(iv_temp);
                LogUtils.i("内容："+content+",点赞状态："+praiseStatus+",收藏状态："+favoriteStatus);

            iv_temp.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final AddPopWindow addPopWindow = new AddPopWindow(context, iv_temp,
                            new AddPopWindow.ClickCallBack() {
                                @Override
                                public void clicked(int type) {
                                    // 点击取消
                                    if (type == 1) {
                                        if (!isGood) {
                                            setGood(sID, tv_good_temp,
                                                    finalGoodArray,
                                                    ll_goodmembers_temp,
                                                    view_pop,
                                                    commentArray.size());
                                            isGood = true;
                                            showView(iv_temp);
                                            //strGood="取消";
                                           // iv_temp.setTag(strGood+strCollection);



                                        } else {
                                            cancelGood(sID, tv_good_temp,
                                                    finalGoodArray,
                                                    ll_goodmembers_temp,
                                                    view_pop,
                                                    commentArray.size());

                                            isGood = false;
                                            showView(iv_temp);
                                            //iv_temp.setTag("点赞");
                                           // strGood="点赞";
                                            //iv_temp.setTag(strGood+strCollection);

                                        }
                                       // closeInputMethod();

                                    } else if (type==2){
                                        // 点击评论
                                        //showSoftInputFromWindow(context, et_comment);
                                        showCommentEditText(sID,
                                                tv_commentmembers_temp,
                                                commentArray, view_pop,
                                                finalGoodArray.size());
                                    }else if (type == 3) {//. 处理收藏逻辑
                                            NetIntent netIntent2 = new NetIntent();

                                            if (isCollection) {
                                                //strCollection="收藏";
                                                //iv_temp.setTag(strGood+strCollection);
                                                netIntent2.client_deleteContain(util.getUserId(), sID, "1", new NetIntentCallBackListener(SocialMainAdapter.this));
                                                isCollection=false;
                                                showView(iv_temp);
                                                Toast.makeText(context, "移除成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                               // strCollection="移除";
                                               // iv_temp.setTag(strGood+strCollection);
                                                netIntent2.client_addContain(util.getUserId(), sID, "1", new NetIntentCallBackListener(SocialMainAdapter.this));
                                                isCollection=true;
                                                showView(iv_temp);
                                                Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                                            }
                                        //closeInputMethod();

                                        }

                                }

                            });
                    addPopWindow.showPopupWindow(iv_temp);
                }
            });
            return convertView;
        }


    }

    private void showView(View v) {
        if(isGood){
            strGood="取消";
        }else{
            strGood="点赞";
        }
        if(isCollection){
            strCollection="移除";
        }else{
            strCollection="收藏";
        }
        v.setTag( strGood+strCollection);
    }


    public static class ViewHolder {
        NineGridLayout ndv_nine_view;
        ExpandableTextView tv_content;
        ImageView iv_pop,iv_avatar;
        LinearLayout ll_goodmembers;
        TextView tv_goodmembers,tv_commentmembers,tv_location,tv_delete,tv_time,tv_nick;
        View view_pop;
    }

    // 设置点赞的
    private void setGoodTextClick(TextView mTextView2, List<String> data,
                                  LinearLayout ll_goodmembers, View view, int cSize) {//.  点赞方法体
       // closeInputMethod();
        LogUtils.i("测试评论的长度1：=====》"+"长度："+cSize+"集合长度："+data.size());
        if (data == null || data.size()== 0) {//.  如果点赞人数为零，隐藏点赞显示框
            ll_goodmembers.setVisibility(View.GONE);
            LogUtils.i("测试评论的长度2：=====》"+cSize+data.size());
        } else {

            ll_goodmembers.setVisibility(View.VISIBLE);//.  如果点赞人数不为零，显示点赞展示框
        }
        if (cSize > 0 && data.size() > 0) {//.  如果点评论人数不为零，显示评论展示框，否则 隐藏
            LogUtils.i("测试评论的长度3：=====》"+cSize+data.size());
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);

        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();//.  作用跟String类似，但是可以设置文本样式

        int start = 0;
        for (int i = 0; i < data.size(); i++) { //.  遍历点赞数据体
            String nick= data.get(i);
            String userID_t = util.getUserId();
            if (i != (data.size() - 1) && data.size() > 1) {
                ssb.append(nick + ",");//.  如果不是最后一个点赞的人，则在后面拼加一个“，”
            } else {
                ssb.append(nick);

            }

            ssb.setSpan(new TextViewURLSpan(nick, userID_t, 0), start,
                    start + nick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//. 设置点赞列表有
            start = ssb.length();//. 设置新的起点

        }

        mTextView2.setText(ssb);
        mTextView2.setMovementMethod(LinkMovementMethod.getInstance());//. 设置长按事件监听


    }

    // 设置评论的
    private void setCommentTextClick(TextView mTextView2, JSONArray data,
                                     View view, int goodSize) {//. 本地评论操作
        if (goodSize > 0 && data.size() > 0) {//. 如果有评论则显示评论展示框，否则，则隐藏
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        if (data.size() == 0) {//. 如果评论人数不为零，则显示展示评论的Textview，否则，则隐藏
            mTextView2.setVisibility(View.GONE);
        } else {
            mTextView2.setVisibility(View.VISIBLE);
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();//.  作用跟String类似，但是可以设置文本样式
        int start = 0;
        for (int i = 0; i < data.size(); i++) {//.  遍历评论数据体，提取出全部评论数据的userID
            JSONObject json = data.getJSONObject(i);
            String userID_t = json.getString("userid");
            String nick = json.getString("nickname");
            String content = json.getString("content");//.  获取到评论的内容
            String scID = json.getString("id");//.  获取到评论的id
            String content_0 = "";
            String content_1 = ": " + content;
            String content_2 = ": " + content + "\n";
            if (json.size() == 1 && i == 0) {//.  设置第一条评论格式
                ssb.append(nick + content_1);
                content_0 = content_1;
            } else {

                    ssb.append(nick + content_2);//.  设置非首条评论格式
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

    private class TextViewURLSpan extends ClickableSpan {//处理被点击的Textview，改变相应的超链接的字体/背景颜色处理方法
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
        public void updateDrawState(TextPaint ds) {//   当为删除评论文字时的响应
            if (type != 2) {
                ds.setColor(
                        context.getResources().getColor(R.color.text_color));
            }
            ds.setUnderlineText(false); // 去掉下划线
        }

        @Override
        public void onClick(final View widget) {//. Textview文字超链接点击响应处理事件

            if (widget instanceof TextView) {//. 如果是Textview 对象
                ((TextView) widget).setHighlightColor(context.getResources()
                        .getColor(android.R.color.darker_gray));//.  设置当被选中时，文字高亮显示
                new Handler().postDelayed(new Runnable() {

                    public void run() {//.  更新UI显示  延迟1秒

                        ((TextView) widget)
                                .setHighlightColor(context.getResources()
                                        .getColor(android.R.color.transparent));

                    }

                }, 1000);

            }

            if (type == 2) {//.  如果是删除评论文字中的Textview,展示删除提示框
                showDeleteDialog(userID, postion, scID, type, ctextView, cjsons,
                        view, goodSize);

            } else {
                context.startActivity(
                        new Intent(context, SocialFriendActivity.class)
                                .putExtra("friendID", userID));
            }
        }

    }

    /**
     * 显示发表评论的输入框
     */

    public void showCommentEditText(final String sID, final TextView tv_comment,
                                    final JSONArray jsons, final View view, final int goodSize) {//.   显示评论输入框
        if (re_edittext == null
                || re_edittext.getVisibility() != View.VISIBLE) {

            re_edittext = (RelativeLayout) context
                    .findViewById(R.id.re_edittext);
            re_edittext.setVisibility(View.VISIBLE);

            Button btn_send = (Button) re_edittext.findViewById(R.id.btn_send);
            btn_send.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String comment = et_comment.getText().toString().trim();//.   发送评论监听
                    if (TextUtils.isEmpty(comment)) {
                        Toast.makeText(context, "请输入评论", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    submitComment(sID, comment, tv_comment, jsons, view,
                            goodSize);//.   发送请求到服务器
                    et_comment.setText("");
                    hideCommentEditText();
                }

            });
        }

    }

    /**
     * 隐藏发表评论的输入框
     */
    public void hideCommentEditText() {//.   隐藏输入框
        if (re_edittext != null && re_edittext.getVisibility() == View.VISIBLE)
            re_edittext.setVisibility(View.GONE);
    }

    /**
     * 提交评论
     */
    private void submitComment(String sID, String comment, TextView tv_comment,
                               JSONArray jsons, View view, int goodSize) {//.   发送请求到服务器
        String tag = String.valueOf(System.currentTimeMillis());

        // 即时改变当前UI
        JSONObject json = new JSONObject();
        json.put("userid", myuserID);
        json.put("content", comment);
        json.put("nickname",myNick);
        // 本地标记，方便本地定位删除，服务器端用不到这个字段
        json.put("tag", tag);
        jsons.add(json);
        setCommentTextClick(tv_comment, jsons, view, goodSize);//.   在本地操作评论
        closeInputMethod();
        //
        // 更新后台
        NetIntent netIntent1 = new NetIntent();//.   将评论请求发送到服务器
        netIntent1.client_addComment(util.getUserId(), sID, comment.toString(), "1", "", new NetIntentCallBackListener(this));

    }

    /**
     * 点赞
     */
    public void setGood(String sID, TextView tv_good, List<String> data,
                        LinearLayout ll_goodmembers_temp, View view, int cSize) {//.  将点赞请求发送到服务器
        // 即时改变当前UI
        data.add(util.getNickName());
        setGoodTextClick(tv_good,  data, ll_goodmembers_temp, view, cSize);
        // 更新后台
        NetIntent netIntent = new NetIntent();
        netIntent.client_addPraise(util.getUserId(), sID, "1", new NetIntentCallBackListener(this));



    }

    /**
     * 取消点赞
     */
    public void cancelGood(String sID, TextView tv_good, List<String> data,
                           LinearLayout ll_goodmembers_temp, View view, int cSize) {//. 取消点赞操作

        // 即时改变当前UI
        for (int i = 0; i < data.size(); i++) {
            data.remove(util.getNickName());
        }
        setGoodTextClick(tv_good, data, ll_goodmembers_temp, view, cSize);
        // 更新后台
        NetIntent netIntent = new NetIntent();//. 将取消点赞请求发送到服务器
        netIntent.client_deletePraise(util.getUserId(), sID, "1", new NetIntentCallBackListener(this));
    }

    private void showDeleteDialog(final String userID, final int postion,
                                  final String scID, final int type, final TextView ctextView,
                                  final JSONArray cjsons, final View view, final int goodSize) {//. 删除评论文字中的提示框
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
                        .trim());//. 复制评论文本内容操作


                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("删除");
        tv_xiangce.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                deleteComment(userID, postion, scID, type, ctextView, cjsons,
                        view, goodSize);//. 执行删除评论操作

                dlg.cancel();
            }
        });

    }

    // 删除评论
    private void deleteComment(String userID, final int postion, String scID,
                               int type, TextView ctextView, final JSONArray cjsons, View view,
                               int goodSize) {

        String tag = cjsons.getJSONObject(postion).getString("tag");//. 评论的标签
        if (tag == null) {
            tag = String.valueOf(System.currentTimeMillis());
        }
        // 更新UI
        cjsons.remove(postion);
        setCommentTextClick(ctextView, cjsons, view, goodSize);//. 执行删除评论请求服务器的操作
        // 更新服务器
        // 更新后台
        NetIntent netIntent = new NetIntent();
        netIntent.client_deleteComment(util.getUserId(),"1",scID,  new NetIntentCallBackListener(this));
        LogUtils.i("info====>scID:"+scID+"info===>userID:"+userID);
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
    /**关闭键盘*/
    public void closeInputMethod() {
        try {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        } finally {
        }

    }





    private void showPhotoDialog(final int index, final String sID) {//. 删除能量提示框
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        int fl=1;
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_social_delete);
        TextView tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(new OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                dlg.cancel();
            }
        });
        TextView tv_ok = (TextView) window.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                users.remove(index);
                notifyDataSetChanged();
                NetIntent netIntent = new NetIntent();
                netIntent.client_deleteEnergy(util.getUserId(), sID, new NetIntentCallBackListener(SocialMainAdapter.this));//.  需要测试是否请求服务器成功
                LogUtils.i("id："+sID.toString());
                dlg.cancel();
            }
        });
    }
    @Override
    public void onError(Request request, Exception e) {}
    @Override
    public void onResponse(String response) {
        LogUtils.i("返回结果："+response.toString());
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(response);

                ToastUtils.showShortToast(context,jsonObject.getString("msg"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
