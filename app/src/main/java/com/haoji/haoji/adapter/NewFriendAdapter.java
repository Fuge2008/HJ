package com.haoji.haoji.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.comment.UserDetailsActivity;
import com.haoji.haoji.util.Util;

import java.util.ArrayList;
import java.util.HashMap;




public class NewFriendAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap> maps;
    private View.OnClickListener listener;

    public NewFriendAdapter(Context context, ArrayList<HashMap> maps, View.OnClickListener listener) {
        this.context = context;
        this.maps = maps;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return maps.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_new_friend, null);
        final ImageView userimg = (ImageView) convertView.findViewById(R.id.userimg);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView usermessage = (TextView) convertView.findViewById(R.id.usermessage);
        Button friend_ty = (Button) convertView.findViewById(R.id.friend_ty);
        final HashMap map = maps.get(position);
        Util.ImageLoaderToPicAuto(context, map.get("pic").toString(), userimg);
        Button friend_jj = (Button) convertView.findViewById(R.id.friend_jj);
        String name = "";
        userimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,UserDetailsActivity.class).putExtra("phone",map.get("phone").toString()));
            }
        });
        if (!map.get("nickname").toString().equals("")) {
            name = map.get("nickname").toString();
        } else {
            name = map.get("phone").toString();
        }
        username.setText(name);
        if (map.get("type").toString().equals("1")) {
            if (!map.get("relevantresults").toString().equals("")) {
                if (map.get("relevantresults").toString().equals("0")) {
                    friend_jj.setText("已同意");
                } else {
                    friend_jj.setText("已拒绝");
                }
            } else {
                friend_jj.setText("已发送");
            }
            usermessage.setText("您添加 " + name + " 为好友");
            friend_ty.setVisibility(View.GONE);
        } else {
            usermessage.setText(name + " 请求添加您为好友"+"["+map.get("remark").toString()+"]");
            if (!map.get("relevantresults").toString().equals("")) {
                if (map.get("relevantresults").toString().equals("0")) {
                    friend_ty.setVisibility(View.GONE);
                    friend_jj.setText("已同意");
                } else {
                    friend_ty.setVisibility(View.GONE);
                    friend_jj.setText("已拒绝");
                }
            } else {
                friend_ty.setTag(map);
                friend_ty.setOnClickListener(listener);
                friend_jj.setTag(map);
                friend_jj.setOnClickListener(listener);
            }
        }
        return convertView;
    }
}
