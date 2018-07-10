package com.haoji.haoji.fragment;

import android.content.Intent;
import android.view.View;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseFragment;
import com.haoji.haoji.fragment.tab2.VideoActivity;
import com.haoji.haoji.fragment.tab2.VideoListActivity;



public class ShareMultimediaFragment extends BaseFragment {

    @Override
    public int InitMainView() {
        return R.layout.fragment_tab2;
    }

    @Override
    public void initUI() {

        rootview.findViewById(R.id.lin_click_one).setOnClickListener(this);
        rootview.findViewById(R.id.lin_click_two).setOnClickListener(this);
        rootview.findViewById(R.id.lin_click_three).setOnClickListener(this);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.lin_click_one:
                intent=new Intent(getActivity(), VideoListActivity.class);
                intent.putExtra("type",1);
                break;
            case R.id.lin_click_two:
                intent=new Intent(getActivity(), VideoActivity.class);
                intent.putExtra("type",1);
                break;
            case R.id.lin_click_three:
                intent=new Intent(getActivity(), VideoListActivity.class);
                intent.putExtra("type",2);
                break;
        }
        if (intent!=null){
            startActivity(intent);
        }
    }
}
