package com.haoji.haoji.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.fragment.tab2.VideoPlayActivity;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;


public class ListGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ArrayList<HashMap>> datas;

    public ListGridAdapter(Context context, ArrayList<ArrayList<HashMap>> datas) {
        this.context = context;
        this.datas = datas;
        System.out.println("datas:" + datas.size());
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_list_grid, null);
        ImageView item_img = (ImageView) view.findViewById(R.id.item_img);
        GridView item_gridview = (GridView) view.findViewById(R.id.item_gridview);
        TextView item_name = (TextView) view.findViewById(R.id.item_name);
        TextView item_time = (TextView) view.findViewById(R.id.item_time);
        TextView item_num = (TextView) view.findViewById(R.id.item_num);
        TextView item_date = (TextView) view.findViewById(R.id.item_date);
        ArrayList<HashMap> maps = new ArrayList<HashMap>();
        maps.addAll(datas.get(i));

           // Collections.sort(maps,new MyComparator());


        final HashMap map = maps.get(0);
        maps.remove(0);
        VideoListAdapter adapter = new VideoListAdapter(context, maps);
        item_gridview.setAdapter(adapter);
        Util.setListViewHeightBasedOnChildren(item_gridview, 2, 5);
        Util.loadIntoUseFitWidth(context, map.get("videopic").toString(), item_img);
        item_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.putExtra("id", map.get("id").toString());
                context.startActivity(intent);
            }
        });
        item_name.setText(map.get("videotitle").toString());
        item_num.setText(map.get("browernum").toString()+"次播放");
        if(StringUtils.isNotEmpty(map.get("videoduration").toString(),true)){
            item_time.setText(map.get("videoduration").toString());
        }

        String time=map.get("updatetime").toString().replace("-","");
        item_date.setText(time);
        return view;
    }

    class MyComparator implements Comparator<HashMap> {

        @Override
        public int compare(HashMap lhs, HashMap rhs) {
            //反向排序
            if (Integer.parseInt(lhs.get("score").toString()) > Integer.parseInt(rhs.get("score").toString())) {
                return -1;
            } else if (Integer.parseInt(lhs.get("score").toString()) < Integer.parseInt(rhs.get("score").toString())) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
