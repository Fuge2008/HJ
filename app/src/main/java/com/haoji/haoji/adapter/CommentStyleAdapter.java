package com.haoji.haoji.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haoji.haoji.R;

import java.util.ArrayList;
import java.util.HashMap;




public class CommentStyleAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String,Object>> maps;

    public CommentStyleAdapter(Context context, ArrayList<HashMap<String,Object>> maps){
        this.context=context;
        this.maps=maps;
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
        convertView= LayoutInflater.from(context).inflate(R.layout.layout_comment_style,null);
        TextView item_username= (TextView) convertView.findViewById(R.id.item_username);
        TextView item_content= (TextView) convertView.findViewById(R.id.item_content);
        HashMap map=maps.get(position);
        item_username.setText(map.get("nickname").toString());
        item_content.setText(map.get("content").toString());
        return convertView;
    }
}
