package com.haoji.haoji.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;



public class TxlAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<HashMap> maps;

    public TxlAdapter(Context context, ArrayList<HashMap> maps) {
        this.context = context;
        this.maps = maps;
    }

    @Override
    public int getGroupCount() {
        return maps.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int getChildrenCount(int groupPosition) {
        JSONArray jsonArray=null;
        try {
            jsonArray = new JSONArray(maps.get(groupPosition).get("list").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.length();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView=LayoutInflater.from(context).inflate(R.layout.expandablelistview_item_menu,null);
        TextView textView= (TextView) convertView.findViewById(R.id.menu);
        textView.setText(maps.get(groupPosition).get("tag").toString());
        convertView.setTag(R.layout.expandablelistview_item_menu,groupPosition);
        convertView.setTag(R.layout.listview_item_txl,-1);
        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_txl, null);
        ImageView userimg = (ImageView) convertView.findViewById(R.id.item_userimg);
        TextView username = (TextView) convertView.findViewById(R.id.item_username);
        HashMap map = maps.get(groupPosition);
        try {
            JSONArray jsonArray=new JSONArray(map.get("list").toString());
            JSONObject jsonObject=jsonArray.getJSONObject(childPosition);
            Util.ImageLoaderToPicAuto(context, jsonObject.getString("pic"), userimg);
            username.setText(jsonObject.getString("nickname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        convertView.setTag(R.layout.expandablelistview_item_menu,groupPosition);
        convertView.setTag(R.layout.listview_item_txl,childPosition);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
