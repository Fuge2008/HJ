package com.haoji.haoji.download.util.content.history;

import android.content.Context;
import android.os.AsyncTask;


import com.haoji.haoji.download.core.os.AsyncTaskResult;
import com.haoji.haoji.download.util.model.Video;

import java.util.ArrayList;

public class HistoryReadTask extends AsyncTask<Integer, Integer, AsyncTaskResult<ArrayList<Video>>> {
    private Context context;
    private HistoryReadTask.HistoryListener listener;

    public HistoryReadTask(Context context, HistoryReadTask.HistoryListener listener)
    {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<ArrayList<Video>> result) {
        super.onPostExecute(result);

        if (isCancelled())
            listener.onCanceled();
        else if (result.getError() != null)
            listener.onError(result.getError());
        else
            listener.onGot(result.getResult());
    }

    @Override
    protected AsyncTaskResult<ArrayList<Video>> doInBackground(Integer... integers) {
        int page = integers[0];
        int size = integers[1];
        try {
            return new AsyncTaskResult(History.get(page, size));
        } catch (Exception e) {
            return new AsyncTaskResult(e);
        }
    }

    public interface HistoryListener {
        void onGot(ArrayList<Video> videos);
        void onCanceled();
        void onError(Exception e);
    }
}
