package com.haoji.haoji.download.util.contract;

import android.content.Context;

import com.haoji.haoji.download.core.contract.AbstractSingleton;
import com.haoji.haoji.download.util.model.Video;


abstract public class VideoParser extends AbstractSingleton {

    protected Context context;

    public VideoParser(Context context) throws SingletonException {
        super();
        this.context = context;
    }

    protected String getString(int resID)
    {
        return this.context.getString(resID);
    }

    protected String getString(int resID, Object...formatArgs)
    {
        return this.context.getString(resID, formatArgs);
    }


    abstract public Video get(String str) throws Exception;
}
