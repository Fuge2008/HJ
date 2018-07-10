package com.haoji.haoji.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.util.Util;

import java.util.ArrayList;
import java.util.HashMap;





public class EnergyAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<HashMap> maps;

    public EnergyAdapter(Context context,ArrayList<HashMap> maps){
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
        convertView= LayoutInflater.from(context).inflate(R.layout.listview_item,null);
        ImageView item_img= (ImageView) convertView.findViewById(R.id.item_img);
        TextView item_title= (TextView) convertView.findViewById(R.id.item_title);
        TextView item_content= (TextView) convertView.findViewById(R.id.item_content);
        TextView item_looks= (TextView) convertView.findViewById(R.id.item_looks);
        TextView item_date= (TextView) convertView.findViewById(R.id.item_date);
        HashMap map=maps.get(position);
        Util.ImageLoaderToPicAuto(context,map.get("pic").toString(),item_img);
        item_title.setText(map.get("title").toString());
        item_content.setText(Html.fromHtml(map.get("content").toString()));
        item_looks.setText(map.get("score").toString());
        item_date.setText(map.get("updatetime").toString());
        convertView.setTag(map);
        return convertView;
    }
}
