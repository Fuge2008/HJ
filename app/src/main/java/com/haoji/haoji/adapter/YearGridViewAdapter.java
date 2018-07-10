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





public class YearGridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap> maps;

    public YearGridViewAdapter(Context context, ArrayList<HashMap> maps){
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
        convertView= LayoutInflater.from(context).inflate(R.layout.gridview_item_year,null);
        TextView item_month= (TextView) convertView.findViewById(R.id.item_month);
        TextView item_year= (TextView) convertView.findViewById(R.id.item_year);
        HashMap map=maps.get(position);
        item_month.setText(map.get("month").toString());
        item_year.setText(map.get("year").toString());
        return convertView;
    }
}
