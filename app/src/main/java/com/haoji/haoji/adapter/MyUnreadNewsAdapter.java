package com.haoji.haoji.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haoji.haoji.R;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;

import java.util.List;




public class MyUnreadNewsAdapter extends BaseAdapter {

    private Context context;
    private List<JSONObject> users;
    private LayoutInflater inflater;

    private String myAvatar,myNick,myuserID;
    private SharedPreferencesUtil util;


    public MyUnreadNewsAdapter(Activity context, List<JSONObject> jsonArray) {
        this.context = context;
        this.users = jsonArray;
        inflater = LayoutInflater.from(context);
        util=new SharedPreferencesUtil(context);
        // 底部评论输入框
        myuserID = util.getUserId();
        myNick = util.getNickName();
        myAvatar = util.getPicture();


    }



    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return users.get(position);
    }
    public List<JSONObject> getJSONs() {
        return users;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=inflater.inflate(R.layout.item_my_unread_news, parent, false);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname1);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time1);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content1);
            holder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head1);
            holder.iv_image= (ImageView) convertView.findViewById(R.id.iv_imageview11);
            holder.iv_good= (ImageView) convertView.findViewById(R.id.iv_good1);
            convertView.setTag(holder);
        }

        final JSONObject json = users.get(position);//.    position  从0开始计数

        if (json == null || json.size() == 0) {
            users.remove(position);
            this.notifyDataSetChanged();
        }

        String content = json.getString("content");
        String nickname = json.getString("nickname");
        LogUtils.i("未读消息数据："+json.toString());
        String head = json.getString("pic");
        String str = json.getString("apppics");
        String image1=null;
        if(StringUtils.isNotEmpty(str,true)){
            image1 = str.substring(0,str.length()-1);
        }



         String type = json.getString("type");
         String id = json.getString("id");
        String infocount = json.getString("infoCnt");
        String updatetime = json.getString("updatetime");
         String userid = json.getString("userid");
         String energyid = json.getString("energyid");
        holder.tv_nickname.setText(nickname);
        if(StringUtils.equals(type,"0")){
            holder.tv_content.setText(content);
            holder.iv_good.setImageResource(R.drawable.icon_coment);
        }else {
            holder.iv_good.setImageResource(R.drawable.icon_heart_blue);
        }

        holder.tv_time.setText(updatetime);
        Glide.with(context).load(head).diskCacheStrategy(DiskCacheStrategy.ALL).into( holder.iv_head);
        if(StringUtils.isNotEmpty(image1,true) && StringUtils.isUrl(image1)){

            holder.iv_image.setVisibility(View.VISIBLE);
            Glide.with(context).load(image1).diskCacheStrategy(DiskCacheStrategy.ALL).into( holder.iv_image);
        }else{
            holder.iv_image.setVisibility(View.GONE);
        }
        return convertView;
    }

    class  ViewHolder{
        TextView tv_nickname;
        TextView tv_time;
        TextView tv_content;
        ImageView iv_head;
        ImageView iv_image;
        ImageView iv_good;

    }

}
