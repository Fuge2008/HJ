package com.haoji.haoji.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.haoji.haoji.R;
import com.haoji.haoji.model.User;
import com.haoji.haoji.util.Util;

import java.util.ArrayList;




public class UserConcernAdapter extends BaseAdapter {

    public final static String TAG = "haoji";

    private Context mContext;
    private ArrayList<User> users;

    public UserConcernAdapter(Context context,ArrayList<User> users) {
        this.mContext = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_txl, null);
            viewHolder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.item_userimg = (ImageView) convertView.findViewById(R.id.item_userimg);
            viewHolder.item_username = (TextView) convertView.findViewById(R.id.item_username);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        User user = users.get(position);
        Log.d(TAG, "getView: " + new Gson().toJson(user).toString());
        viewHolder.item_username.setText(user.getNickname());
        Util.ImageLoaderToPicAuto(mContext, user.getPic(), viewHolder.item_userimg);
        viewHolder.item_title.setVisibility(View.GONE);
        return convertView;
    }

    class ViewHolder {
        TextView item_title;
        ImageView item_userimg;
        TextView item_username;
    }

}
