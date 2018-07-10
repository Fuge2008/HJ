package com.haoji.haoji.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.haoji.haoji.R;

import java.util.List;




public class ShowImgGridViewAdapter extends BaseAdapter {

    private Context context;
    private List imgs;

    public ShowImgGridViewAdapter(Context context, List imgs){
        this.context=context;
        this.imgs=imgs;
    }

    @Override
    public int getCount() {
        return imgs.size();
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
        convertView= LayoutInflater.from(context).inflate(R.layout.gridview_item_showimg,null);
        ImageView item_img= (ImageView) convertView.findViewById(R.id.item_img);
        Glide.with(context).load(imgs.get(position)).centerCrop().into(item_img);
        System.out.println(imgs.get(position));
        return convertView;
    }
}
