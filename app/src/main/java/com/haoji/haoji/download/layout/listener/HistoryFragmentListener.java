package com.haoji.haoji.download.layout.listener;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;


import com.haoji.haoji.download.core.listener.FragmentListener;
import com.haoji.haoji.download.layout.fragment.HistoryRecyclerViewAdapter;
import com.haoji.haoji.ui.DownloadActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryFragmentListener extends FragmentListener {

    private Unbinder unbinder;


    public HistoryFragmentListener(Fragment fragment, Context context)
    {
        super(fragment, context);
    }

    public void onListFragmentInteraction(HistoryRecyclerViewAdapter.ViewHolder holder)
    {
        ((DownloadActivity)fragment.getActivity()).onVideoChange(holder.video, true);
    }

    @Override
    public void onCreateView(View view)
    {
        unbinder = ButterKnife.bind(this.context, view);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
    }


}
