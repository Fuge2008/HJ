package com.haoji.haoji.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haoji.haoji.R;
import com.haoji.haoji.fragment.tab2.VideoPlayActivity;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;





public class VideoListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap> maps;
    private SharedPreferencesUtil util;

    public VideoListAdapter(Context context, ArrayList<HashMap> maps) {
        this.context = context;
        this.maps = maps;
        util = new SharedPreferencesUtil(context);
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
        convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item_video, null);
        ImageView item_img = (ImageView) convertView.findViewById(R.id.item_img);
        ImageView item__play_nomal= (ImageView) convertView.findViewById(R.id.item__play_nomal);
        ImageView item_head= (ImageView) convertView.findViewById(R.id.item_head);
        TextView item_time= (TextView) convertView.findViewById(R.id.item_time);
        TextView item_name = (TextView) convertView.findViewById(R.id.item_name);
        TextView item_num = (TextView) convertView.findViewById(R.id.item_num);
        TextView item_date = (TextView) convertView.findViewById(R.id.item_date);

        final HashMap map = maps.get(position);
        Glide.with(context).load(map.get("videopic").toString()).into(item_img);
        item_name.setText(map.get("videotitle").toString());
        item_num.setText(map.get("browernum").toString()+"次播放");
 //       item_date.setText(map.get("updatetime").toString());
        if(StringUtils.isNotEmpty(map.get("videoduration").toString(),true)){
            item_time.setText(map.get("videoduration").toString());
        }
        String time=map.get("updatetime").toString().replace("-","");
        item_date.setText(time);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.putExtra("id", map.get("id").toString());
                context.startActivity(intent);
            }
        });
        return convertView;
    }

//    private String getDate(String date) {
//        return date.substring(0, 4) + "年,第" + date.substring(4, 6) + "周";
//    }

//    private String getWeek(int week) {
//        String we = "";
//        switch (week) {
//            case 0:
//                we = "日";
//                break;
//            case 1:
//                we = "一";
//                break;
//            case 2:
//                we = "二";
//                break;
//            case 3:
//                we = "三";
//                break;
//            case 4:
//                we = "四";
//                break;
//            case 5:
//                we = "五";
//                break;
//            case 6:
//                we = "六";
//                break;
//        }
//        return "星期" + we;
//    }

}
