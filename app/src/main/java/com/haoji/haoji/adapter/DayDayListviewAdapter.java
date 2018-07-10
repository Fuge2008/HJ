package com.haoji.haoji.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haoji.haoji.R;
import com.haoji.haoji.comment.SocialFriendActivity;
import com.haoji.haoji.comment.SocialMainActivity;
import com.haoji.haoji.comment.UserDetailsActivity;
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




public class DayDayListviewAdapter extends BaseAdapter implements NetIntentCallBackListener.INetIntentCallBack {
    private static String TAG = SocialMainActivity.class.getSimpleName();
    private Activity context;
    private List<JSONObject> users;
    private LayoutInflater inflater;
    //public RelativeLayout re_edittext;
    private String myAvatar,myNick,myuserID;
    private SharedPreferencesUtil util;
    private View.OnClickListener listener;



    public DayDayListviewAdapter(Activity context, List<JSONObject> jsonArray, View.OnClickListener listener) {
        this.context = context;
        this.users = jsonArray;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
        util=new SharedPreferencesUtil(context);
        // 底部评论输入框
        myuserID = util.getUserId();
        myNick = util.getNickName();
        myAvatar = util.getPicture();


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
            View  view = inflater.inflate(R.layout.item_moments_header_not_avatar, null, false);
            //Glide.with(context).load(myAvatar).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_avatar);
            return view;
        } else {
            convertView = inflater.inflate(R.layout.listview_item_friend2, parent, false);
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.tv_nick = (TextView) convertView.findViewById(R.id.tv_nick);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.iv_avatar = (ImageView) convertView.findViewById(R.id.sdv_image);
                holder.ndv_nine_view= (NineGridLayout) convertView.findViewById(R.id.ndv_nine_view);

                holder.tv_content  = (ExpandableTextView) convertView.findViewById(R.id.tv_content);
                holder.iv_show_pop  = (ImageView) convertView.findViewById(R.id.iv_show_pop);
                holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
                holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
                convertView.setTag(holder);
            }
            final JSONObject json = users.get(position - 1);//.    position  从0开始计数
            // 如果数据出错....

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


            //final String favoriteStatus = json.getString("favoritestatus");
            //final String praiseStatus = json.getString("praisestatus");
           // String praiseNum = json.getString("praisenum");
            //String browseNum = json.getString("browsenum");
            String nickName = json.getString("nickname");
            //String praiseName = json.getString("praisename");
            String userHead = json.getString("pic");
            String realTime = json.getString("updatetime");
            //String favoriteNum=json.get("favoritenum").toString();
            Log.i("info",json.toString());





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

            // 设置昵称。

            if(nickName==null){
                nickName="李云龙";
            }
            if(!StringUtils.isUrl(userHead)){
                userHead="http://p0.so.qhimgs1.com/t01cc0c59a76cd69160.jpg";
            }
            holder.tv_nick.setText(nickName );//. 展示昵称
            //holder.iv_avatar.setImageURI(Uri.parse(userHead));//. 展示头像

            holder.tv_nick.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context,
                            SocialFriendActivity.class).putExtra("friendID",
                            userID));//. 点击能量上的头像，则跳转到对应用户的详细信息界面
                }

            });

            // 设置能量中的图片
            System.out.print("imageStr--->>"+imageStr);
            if (imageStr!=null) {//.   如果有图片
                String[] images = imageStr.split(";");//.   将图像URL字符串剪切存入数组
                int imNumb = images.length;
                if(imNumb!=0 & images[0].contains("http://")){
                    List<String> list = new ArrayList<String>(Arrays.asList(images));
                    LogUtils.i("数组装化成集合结果："+list);
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
            //setUrlTextView(content, holder.tv_content);//.  显示发表的内容
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
            json.put("position",position);
            holder.iv_show_pop.setOnClickListener(listener);
            holder.iv_show_pop.setTag(json);
            return convertView;
        }


    }



    public static class ViewHolder {
        ImageView iv_avatar;
        NineGridLayout ndv_nine_view;
        TextView tv_nick;
        TextView tv_time;
        ExpandableTextView tv_content;
        ImageView iv_show_pop;
        TextView tv_delete;
        TextView tv_location;

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
                netIntent.client_deleteEnergy(util.getUserId(), sID, new NetIntentCallBackListener(DayDayListviewAdapter.this));//.  需要测试是否请求服务器成功
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
    }

}
