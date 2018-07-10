package com.haoji.haoji.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haoji.haoji.R;
import com.haoji.haoji.model.User;
import com.haoji.haoji.util.ChineseToEnglish;
import com.haoji.haoji.util.SharedPreferencesUtil;

import java.util.List;




public class UserAdapter extends BaseAdapter {

    public final static String TAG="haoji";

    private Context mContext;
    private List<User> users;
    private SharedPreferencesUtil util;

    public UserAdapter(Context context,List<User> user) {
        this.mContext = context;
        this.users = user;
        util=new SharedPreferencesUtil(mContext);
    }

//    public void setData(List<User> data) {
//        this.users.clear();
//        this.users.addAll(data);
//    }


    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_txl, null);
            viewHolder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.item_userimg = (ImageView) convertView.findViewById(R.id.item_userimg);
            viewHolder.item_username = (TextView) convertView.findViewById(R.id.item_username);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        User user = users.get(position);
        viewHolder.item_username.setText(user.getNickname());

        Glide.with(mContext).load(user.getPic()).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.item_userimg);
        //当前的item的title与上一个item的title不同的时候回显示title(A,B,C......)
        if (position == getFirstLetterPosition(position) && !users.get(position).getLetter().equals("@")) {
            viewHolder.item_title.setVisibility(View.VISIBLE);
            viewHolder.item_title.setText(users.get(position).getLetter().toUpperCase());
        } else {
            viewHolder.item_title.setVisibility(View.GONE);
        }


        return convertView;
    }

    /**
     * 顺序遍历所有元素．找到position对应的title是什么（A,B,C?）然后找这个title下的第一个item对应的position
     */
    private int getFirstLetterPosition(int position) {

        String letter = users.get(position).getLetter();
        int cnAscii = ChineseToEnglish.getCnAscii(letter.toUpperCase().charAt(0));
        int size = users.size();
        for (int i = 0; i < size; i++) {
            if (cnAscii == users.get(i).getLetter().charAt(0)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 顺序遍历所有元素．找到letter下的第一个item对应的position
     */
    public int getFirstLetterPosition(String letter) {
        int size = users.size();
        for (int i = 0; i < size; i++) {
            if (letter.charAt(0) == users.get(i).getLetter().charAt(0)) {
                return i;
            }
        }
        return -1;
    }

    class ViewHolder {
        TextView item_title;
        ImageView item_userimg;
        TextView item_username;
    }

}
