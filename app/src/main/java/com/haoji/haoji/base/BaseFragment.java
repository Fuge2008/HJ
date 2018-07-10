package com.haoji.haoji.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.util.SystemUtil;
import com.haoji.haoji.util.ToastUtils;


public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    protected View rootview;
    protected TextView mHeadTitle;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity=getActivity();
        if (rootview != null) {
            ViewGroup parent = (ViewGroup) rootview.getParent();
            if (parent != null) {
                parent.removeView(rootview);
            }
        } else {
            rootview = inflater.inflate(InitMainView(), container, false);
            initUI();
            loadData();
        }
        return rootview;
    }

    /**
     * 增加bar高度
     */
    public void setBarHeight() {
        try {
            View view = rootview.findViewById(R.id.top_bar);
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int height = SystemUtil.getHeight(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                view.setLayoutParams(params);
           // }
        } catch (Exception e) {
            //SystemDialog.DialogToast(getActivity(), "设置bar出错" + e.toString());
            ToastUtils.showShortToastSafe(getActivity(), "设置bar出错" + e.toString());
        }

    }

    public abstract int InitMainView();

    public abstract void initUI();

    public abstract void loadData();

    public void setHeadTitle(String title) {
        try {
            mHeadTitle = (TextView) rootview.findViewById(R.id.head_title);
            mHeadTitle.setText(title);
        } catch (Exception e) {
            //SystemDialog.DialogToast(getActivity(), "初始化标题出错" + e.toString());
            ToastUtils.showShortToastSafe(getActivity(), "初始化标题出错" + e.toString());
        }
    }

    public void setHeadTitle(String title, boolean leftOnClick, boolean rightOnClick) {
        try {
            mHeadTitle = (TextView) rootview.findViewById(R.id.head_title);
            mHeadTitle.setText(title);
            rootview.findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
            rootview.findViewById(R.id.head_more).setOnClickListener(this);
        } catch (Exception e) {
            //SystemDialog.DialogToast(getActivity(), "初始化标题出错" + e.toString());
            ToastUtils.showShortToastSafe(getActivity(), "初始化标题出错" + e.toString());

        }
    }

}
