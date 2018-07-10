package com.haoji.haoji.fragment.tab2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.adapter.CommentAdapter;
import com.haoji.haoji.custom.CustomProgress;
import com.haoji.haoji.custom.GoodView;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.LoginActivity;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;



public class BookFragment extends Fragment implements NetIntentCallBackListener.INetIntentCallBack, View.OnClickListener {

    private View rootView;
    private JSONObject json;
    private TextView title, content, date, looks;
    private String id;
    private SharedPreferencesUtil util;
    private CustomProgress dialog;
    private ListView listview_comment;
    private CommentAdapter adapter;
    private ArrayList<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>>();
    private EditText edit_comment_content;
    private boolean isSc = false;
    private ImageView btn_sc;

    private GoodView mGoodView;

    public BookFragment(String id) {
        this.id=id;
    }
    public BookFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.layout_book_info, container, false);
            initUI();
            loadData();
        }
        return rootView;
    }

    private void loadData() {

    }

    private void initUI() {
        dialog = CustomProgress.show(getActivity(), "加载中..", true, null);
        util=new SharedPreferencesUtil(getActivity());
        mGoodView = new GoodView(getActivity());
        edit_comment_content = (EditText) rootView.findViewById(R.id.edit_comment_content);
        rootView.findViewById(R.id.btn_comment_submit).setOnClickListener(this);
        title = (TextView) rootView.findViewById(R.id.item_title);
        content = (TextView) rootView.findViewById(R.id.item_content);
        date = (TextView) rootView.findViewById(R.id.item_date);
        looks = (TextView) rootView.findViewById(R.id.item_looks);
        adapter = new CommentAdapter(getActivity(), maps, listener);
        listview_comment = (ListView) rootView.findViewById(R.id.listview_comment);
        listview_comment.setAdapter(adapter);
        btn_sc= (ImageView) rootView.findViewById(R.id.btn_sc);
        btn_sc.setOnClickListener(this);
        edit_comment_content = (EditText) rootView.findViewById(R.id.edit_comment_content);
        rootView.findViewById(R.id.btn_comment_submit).setOnClickListener(this);
        new NetIntent().client_getDiaryById(id, new NetIntentCallBackListener(this));
        if (util.getIsLogin())
            new NetIntent().client_getCommentList(util.getUserId(), id, "3", new NetIntentCallBackListener(this));
    }

    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.comment_item_likes_click:
                    HashMap map= (HashMap) v.getTag();
                    new NetIntent().client_addPraise(util.getUserId(),map.get("id").toString(),"4",new NetIntentCallBackListener(BookFragment.this));
                    handler.sendEmptyMessageDelayed(2,1000);
                    ((ImageView) v).setImageResource(R.drawable.good_checked);
                    mGoodView.setText("+1");
                    mGoodView.show(v);
                    break;
            }
        }
    };

    private void updateView() {
        try {
            if (json != null) {
                if (json.has("title"))
                    title.setText(json.getString("title"));
                if (json.has("content"))
                    content.setText(Html.fromHtml(json.getString("content")));
                if (json.has("updatetime"))
                    date.setText(json.getString("updatetime"));
                if (json.has("videotitle"))
                    title.setText(json.get("videotitle").toString());
                if (json.has("videocontent"))
                    content.setText(json.get("videocontent").toString());
                if (json.has("browernum"))
                    looks.setText("得分:" + json.get("score").toString());
                if (json.has("updatetime"))
                    date.setText(json.get("updatetime").toString());
            } else {
                System.out.println("json is null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateView();
                    break;
                case 1:
                    adapter.notifyDataSetChanged();
                    Util.setListViewHeightBasedOnChildren(listview_comment);
                    break;
                case 2:
                    new NetIntent().client_getCommentList(util.getUserId(), id, "3", new NetIntentCallBackListener(BookFragment.this));
                    break;
                case 3:
                    //SystemDialog.DialogToast(getActivity(), "数据异常");
                    ToastUtils.showShortToastSafe(getActivity(),"数据异常");
                    break;
            }
        }
    };

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        System.out.println(response);
        if (dialog != null)
            dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("code")) {
                if (jsonObject.has("diary")) {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("diary"));
                    json = jsonArray.getJSONObject(0);
                    handler.sendEmptyMessage(0);
                }
                if (jsonObject.has("commentList")) {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("commentList"));
                    maps.clear();
                    maps.addAll(Util.toHashMap(jsonArray));
                    handler.sendEmptyMessage(1);
                }
            } else {
                ToastUtils.showShortToastSafe(getActivity(), jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            handler.sendEmptyMessage(3);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (util.getIsLogin()) {
            switch (v.getId()) {
                case R.id.btn_comment_submit:
                    if (!edit_comment_content.getText().toString().equals("")) {
                        new NetIntent().client_addComment(util.getUserId(), id, edit_comment_content.getText().toString(), "3", "", new NetIntentCallBackListener(this));
                        handler.sendEmptyMessageDelayed(2, 1000);
                        edit_comment_content.setText("");
                        ToastUtils.showShortToastSafe(getActivity(), "评论成功");
                    }
                    break;
                case R.id.btn_sc:
                    if (!isSc){
                        new NetIntent().client_addContain(util.getUserId(),id,"2",new NetIntentCallBackListener(this));
                        //btn_sc.setImageResource(R.mipmap.ysc60);
                        ((ImageView) v).setImageResource(R.mipmap.ysc60);
                        mGoodView.setTextInfo("收藏成功", Color.parseColor("#f66467"), 12);
                        mGoodView.show(v);
                        isSc=true;
                        //SystemDialog.DialogToast(getActivity(), "收藏成功");
                    }else{
                        new NetIntent().client_deleteContain(util.getUserId(),id,"2",new NetIntentCallBackListener(this));
                        ((ImageView) v).setImageResource(R.mipmap.sc60);
                        mGoodView.setTextInfo("取消收藏", Color.parseColor("#f66467"), 12);
                        mGoodView.show(v);
                       // btn_sc.setImageResource(R.mipmap.sc60);
                        isSc=false;
                        //SystemDialog.DialogToast(getActivity(), "取消收藏");
                    }
                    break;
            }
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
}
