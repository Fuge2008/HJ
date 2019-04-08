package com.haoji.haoji.download.core.listener;

import android.content.Context;

public class ActivityListener {

    protected Context context;

    public ActivityListener(Context context)
    {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
