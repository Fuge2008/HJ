package com.haoji.haoji.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.util.Util;

import java.util.ArrayList;
import java.util.HashMap;




public class CommentAdapter extends BaseAdapter{

    private ArrayList<HashMap<String,Object>> maps;
    private Context context;
    private View.OnClickListener listener;

    public CommentAdapter(Context context, ArrayList<HashMap<String,Object>> maps, View.OnClickListener listener){
        this.context=context;
        this.maps=maps;
        this.listener=listener;
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
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.listview_item_comment,null);
            viewHolder.comment_item_img= (ImageView) convertView.findViewById(R.id.comment_item_img);
            viewHolder.comment_item_username= (TextView) convertView.findViewById(R.id.comment_item_username);
            viewHolder.comment_item_date= (TextView) convertView.findViewById(R.id.comment_item_date);
            viewHolder.comment_item_likes= (TextView) convertView.findViewById(R.id.comment_item_likes);
            viewHolder.comment_item_content= (TextView) convertView.findViewById(R.id.comment_item_content);
            viewHolder.comment_item_likes_click= (ImageView) convertView.findViewById(R.id.comment_item_likes_click);
            viewHolder.comment_item_comment_listview= (ListView) convertView.findViewById(R.id.comment_item_comment_listview);
            viewHolder.comment_item_layout_listview= (LinearLayout) convertView.findViewById(R.id.comment_item_layout_listview);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        HashMap map=maps.get(position);
        viewHolder.comment_item_username.setText(map.get("nickname").toString());
        viewHolder.comment_item_content.setText(map.get("content").toString());
        Util.ImageLoaderToPicAuto(context,map.get("pic").toString(),viewHolder.comment_item_img);
        viewHolder.comment_item_date.setText(map.get("updatetime").toString());
        viewHolder.comment_item_likes.setText(map.get("praisenum").toString());
        viewHolder.comment_item_likes_click.setOnClickListener(listener);
        viewHolder.comment_item_likes_click.setTag(map);
        return convertView;
    }

    static class ViewHolder{
        /*评论者头像*/
        ImageView comment_item_img;
        /*评论者用户名*/
        TextView comment_item_username;
        /*评论内容*/
        TextView comment_item_content;
        /*评论日期*/
        TextView comment_item_date;
        /*点赞按钮*/
        ImageView comment_item_likes_click;
        /*点赞人数*/
        TextView comment_item_likes;
        /*点击当前评论者的评论的按钮*/


        ListView comment_item_comment_listview;

        LinearLayout comment_item_layout_listview;

    }
}
