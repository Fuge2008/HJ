package com.haoji.haoji.download.util.content.analyzer;

import android.content.Context;
import android.os.AsyncTask;

import com.haoji.haoji.R;
import com.haoji.haoji.download.core.exception.URLInvalidException;
import com.haoji.haoji.download.core.os.AsyncTaskResult;
import com.haoji.haoji.download.util.content.analyzer.app.DouyinV2;
import com.haoji.haoji.download.util.contract.VideoParser;
import com.haoji.haoji.download.util.exception.VideoException;
import com.haoji.haoji.download.util.model.Video;


public class AnalyzerTask extends AsyncTask<String, Integer, AsyncTaskResult<Video>> {

    private Context context;
    private AnalyzeListener listener;

    public AnalyzerTask(Context context, AnalyzeListener listener)
    {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected AsyncTaskResult<Video> doInBackground(String...params) {
        String str = params[0];
        VideoParser parser = null;

        if (str.matches(this.context.getString(R.string.url_douyin_regex))) {
            parser = DouyinV2.getInstance(this.context);
        }

        try {
            if (parser == null)
                throw new URLInvalidException(this.context.getString(R.string.exception_invalid_url));
            Video video = parser.get(str);

            if (video == null || video.isEmpty())
                throw new VideoException(this.context.getString(R.string.exception_invalid_video));

            return new AsyncTaskResult<>(video);

        } catch (Exception e) {
            return new AsyncTaskResult<>(e);
        }

    }

    @Override
    protected void onPostExecute(AsyncTaskResult<Video> result) {
        super.onPostExecute(result);

        if (isCancelled())
            listener.onAnalyzeCanceled();
        else if (result.getError() != null)
            listener.onAnalyzeError(result.getError());
        else
            listener.onAnalyzed(result.getResult());

    }

    public interface AnalyzeListener {
        void onAnalyzed(Video video);
        void onAnalyzeCanceled();
        void onAnalyzeError(Exception e);
    }
}
