package com.haoji.haoji.comment;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.StringUtils;


public class AddPopWindow extends PopupWindow {
    private View conentView;
    private  Activity context;

    public AddPopWindow(final Activity context, ImageView iv_good, final ClickCallBack clickCallBack) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popwindow_moments, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        // 刷新状态
        this.update();
        this.setAnimationStyle(R.style.popWindow_anim_tool);
        LinearLayout ll_zan = (LinearLayout) conentView
                .findViewById(R.id.ll_zan);
        LinearLayout ll_pl = (LinearLayout) conentView.findViewById(R.id.ll_pl);
        LinearLayout ll_sc = (LinearLayout) conentView.findViewById(R.id.ll_sc);
        TextView tv_good = (TextView) conentView.findViewById(R.id.tv_good);
        TextView tv_sc = (TextView) conentView.findViewById(R.id.tv_sc);

        String tag = iv_good.getTag().toString();
        if (tag.length()>3) {
            String strGood=tag.substring(0,2);
            String strCollection=tag.substring(2,4);
            tv_sc.setText(strCollection);
            tv_good.setText(strGood);
        }else {

            if (tag != null && (StringUtils.equals(tag, "移除") || StringUtils.equals(tag, "收藏"))) {
                tv_sc.setText(tag);
                LogUtils.i("收藏移除操作：" + tag);
            }
            if(tag != null && (StringUtils.equals(tag, "点赞") || StringUtils.equals(tag, "取消"))) {
                tv_good.setText(tag);
                LogUtils.i("点赞取消操作：" + tag);

            }
        }
        ll_zan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickCallBack.clicked(1);
                AddPopWindow.this.dismiss();
            }

        });
        ll_pl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                clickCallBack.clicked(2);

                AddPopWindow.this.dismiss();

            }

        });
        ll_sc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCallBack.clicked(3);

                AddPopWindow.this.dismiss();

            }

        });

    }



    /**显示popupWindow*/
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            conentView.measure(w, h);
            int height =conentView.getMeasuredHeight();
            int width =conentView.getMeasuredWidth();
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            this.showAtLocation(parent, Gravity.NO_GRAVITY,
                    location[0]-width, location[1]);

        } else {
            this.dismiss();
        }
    }

    public interface ClickCallBack {
        void clicked(int type);
    }
}
