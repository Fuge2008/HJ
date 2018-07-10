package com.haoji.haoji.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haoji.haoji.R;

import java.util.ArrayList;
import java.util.HashMap;





public class ArticleAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap> maps;
    private int type;

    public ArticleAdapter(Context context, ArrayList<HashMap> maps, int type) {
        this.context = context;
        this.maps = maps;
        this.type = type;
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
        convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_video, null);
        ImageView item_img = (ImageView) convertView.findViewById(R.id.item_img);
        TextView item_title = (TextView) convertView.findViewById(R.id.item_title);
        TextView item_content = (TextView) convertView.findViewById(R.id.item_content);
        TextView item_looks = (TextView) convertView.findViewById(R.id.item_looks);
        TextView item_date = (TextView) convertView.findViewById(R.id.item_date);
        TextView item_title_date = (TextView) convertView.findViewById(R.id.item_title_date);
        LinearLayout item_layout = (LinearLayout) convertView.findViewById(R.id.item_layout);
        HashMap map = maps.get(position);
        //Util.ImageLoaderToPicAuto(context,map.get("pic").toString(),item_img);
        if (type == 0) {
            Glide.with(context).load(map.get("videopic").toString()).into(item_img);
            item_title.setText(map.get("videotitle").toString());
            item_content.setText(map.get("videocontent").toString());
            item_looks.setText(map.get("browernum").toString());
            item_date.setText(map.get("updatetime").toString());
        } else if (type == 1) {
            item_title.setText(map.get("title").toString());
            item_content.setText(map.get("content").toString());
            item_date.setText(map.get("updatetime").toString()+" "+getWeek(Integer.parseInt(map.get("weekday").toString())));
            item_looks.setText("得分:"+map.get("score").toString());
            item_title_date.setText(getDate(map.get("weeks").toString()));
            if (getIsShow(position))
                item_layout.setVisibility(View.VISIBLE);
            else
                item_layout.setVisibility(View.GONE);
        }
        return convertView;
    }

    public boolean getIsShow(int position) {
        if (position == 0) {
            return true;
        }
        if (!maps.get(position - 1).get("weeks").toString().equals(maps.get(position).get("weeks").toString()))
            return true;
        return false;
    }

    private String getDate(String date) {
        return date.substring(0, 4) + "年,第" + date.substring(4, 6) + "周";
    }

    private String getWeek(int week) {
        String we = "";
        switch (week) {
            case 0:
                we = "日";
                break;
            case 1:
                we = "一";
                break;
            case 2:
                we = "二";
                break;
            case 3:
                we = "三";
                break;
            case 4:
                we = "四";
                break;
            case 5:
                we = "五";
                break;
            case 6:
                we = "六";
                break;
        }
        return "星期" + we;
    }
}
