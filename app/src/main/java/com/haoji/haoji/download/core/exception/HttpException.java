package com.haoji.haoji.download.core.exception;

import android.annotation.SuppressLint;

public class HttpException extends RuntimeException {

    @SuppressLint("NewApi")
    public HttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public HttpException() {
        super();
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
