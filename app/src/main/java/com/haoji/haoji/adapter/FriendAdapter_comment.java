package com.haoji.haoji.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.comment.UserDetailsActivity;
import com.haoji.haoji.fragment.tab2.VideoPlayActivity;
import com.haoji.haoji.ui.ArticleActivity;
import com.haoji.haoji.ui.ShowPictureActivity;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jaydenxiao.com.expandabletextview.ExpandableTextView;




public class FriendAdapter_comment extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap> maps;
    private View.OnClickListener listener;
    private SharedPreferencesUtil util;
    private HashMap<String, ArrayList<HashMap<String, Object>>> commentMaps = new HashMap<String, ArrayList<HashMap<String, Object>>>();
    private boolean layoutCommentIsShow=true;

    public void setLayoutCommentIsShow(boolean layoutCommentIsShow){
        this.layoutCommentIsShow=layoutCommentIsShow;
    }

    public FriendAdapter_comment(Context context, ArrayList<HashMap> maps, View.OnClickListener listener) {
        this.context = context;
        this.maps = maps;
        this.listener = listener;
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
        convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_friend, null);
        ImageView item_img = (ImageView) convertView.findViewById(R.id.item_img);
        GridView gridview_pics = (GridView) convertView.findViewById(R.id.gridview_pics);
        TextView item_title = (TextView) convertView.findViewById(R.id.item_title);
        ExpandableTextView item_content = (ExpandableTextView) convertView.findViewById(R.id.item_content);
        LinearLayout show_tool = (LinearLayout) convertView.findViewById(R.id.show_tool);
        TextView item_zan = (TextView) convertView.findViewById(R.id.item_zan);
        LinearLayout layout_zan = (LinearLayout) convertView.findViewById(R.id.layout_zan);
        LinearLayout layout_line = (LinearLayout) convertView.findViewById(R.id.layout_line);
        final ListView listview_comment = (ListView) convertView.findViewById(R.id.listview_comment);
        LinearLayout item_comment= (LinearLayout) convertView.findViewById(R.id.item_comment);
        TextView item_looks= (TextView) convertView.findViewById(R.id.item_looks);
        TextView item_time= (TextView) convertView.findViewById(R.id.item_time);
        final HashMap map = maps.get(position);
        if (layoutCommentIsShow){
            item_comment.setVisibility(View.VISIBLE);
        }else{
            item_comment.setVisibility(View.GONE);
        }
        Util.ImageLoaderToPicAuto(context, map.get("pic").toString(), item_img);
        item_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(context, SocialDetailActivity.class);
//                intent.putExtra("userid",map.get("userid").toString());
//                intent.putExtra("type",1);
//                context.startActivity(intent);
                context.startActivity(new Intent(context,UserDetailsActivity.class).putExtra("userid",map.get("userid").toString()).putExtra("phone","0"));
            }
        });
        item_title.setText(map.get("nickname").toString());
        item_time.setText(map.get("updatetime").toString());
        item_looks.setText(map.get("browsenum").toString());
        item_content.setText(Html.fromHtml(map.get("content").toString()));
        if (map.containsKey("type")&&map.get("type").toString().equals("3")){
            item_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ArticleActivity.class);
                    intent.putExtra("id",map.get("id").toString());
                    intent.putExtra("type",1);
                    context.startActivity(intent);
                }
            });
        }
        int top=0;
        if (map.containsKey("praisenum")&&!map.get("praisenum").toString().equals("null"))
            top = Integer.parseInt(map.get("praisenum").toString());
        if (top > 0) {//如果点赞大于0显示视图
            layout_zan.setVisibility(View.VISIBLE);
            item_zan.setText(map.get("praisenum").toString());
        } else {
            layout_zan.setVisibility(View.GONE);
        }
        ArrayList<HashMap<String, Object>> hashMaps = new ArrayList();
        CommentStyleAdapter adapter = new CommentStyleAdapter(context, hashMaps);
        listview_comment.setAdapter(adapter);
        try {
            hashMaps.addAll(Util.toHashMap(new JSONArray(map.get("comlist").toString())));
            adapter.notifyDataSetChanged();
            Util.setListViewHeightBasedOnChildren(listview_comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (map.containsKey("apppics")&&map.get("apppics").toString().indexOf("http")!=-1) {
            List list=Arrays.asList(map.get("apppics").toString().split(";"));
            String imgs="";
            for (int i = 0; i < list.size(); i++) {
                imgs+=list.get(i)+",";
            }
            final String path=imgs;
            ShowImgGridViewAdapter imgGridViewAdapter = new ShowImgGridViewAdapter(context,list);
            gridview_pics.setAdapter(imgGridViewAdapter);
            gridview_pics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (map.containsKey("type")){
                        Intent intent=null;
                        switch (Integer.parseInt(map.get("type").toString())){
                            case 1:
                                intent=new Intent(context,ShowPictureActivity.class);
                                intent.putExtra("type",1);
                                intent.putExtra("index",position);
                                intent.putExtra("path",path);
                                break;
                            case 2:
                                intent=new Intent(context, VideoPlayActivity.class);
                                intent.putExtra("id",map.get("id").toString());
                                break;

                        }
                        context.startActivity(intent);
                    }else{
                        Intent intent=new Intent(context,ShowPictureActivity.class);
                        intent.putExtra("type",1);
                        intent.putExtra("index",position);
                        intent.putExtra("path",path);
                        context.startActivity(intent);
                    }

                }
            });
            Util.setListViewHeightBasedOnChildren(gridview_pics, 3);
            gridview_pics.setVisibility(View.VISIBLE);
        } else {
            gridview_pics.setVisibility(View.GONE);
        }
        show_tool.setOnClickListener(listener);
        show_tool.setTag(map);
        if (top > 0 && hashMaps.size() > 0) {
            layout_line.setVisibility(View.VISIBLE);
        } else {
            layout_line.setVisibility(View.GONE);
        }
        if (top==0&&hashMaps.size()==0){
            item_comment.setVisibility(View.GONE);
        }else{
            item_comment.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
