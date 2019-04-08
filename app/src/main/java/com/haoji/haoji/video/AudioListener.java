package com.haoji.haoji.video;

/**
 * Created by Administrator on 2015/11/10 0010.
 */

public interface AudioListener {

    void onProgress(String outputPath, int percentage);
    void onComplete(String outputPath, short[] resampleBuffer);
    void onError(String outputPath, String error);
 }