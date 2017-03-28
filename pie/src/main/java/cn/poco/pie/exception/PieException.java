package cn.poco.pie.exception;

import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Date  : 2016/11/8
 * Author: MarkChan
 * Desc  :
 */
public class PieException extends RuntimeException {

    private String message;
    private int code;

    public PieException(String message) {
        this(0, message);
    }

    public PieException(int code, String message) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public PieException(String message, Throwable cause) {
        super(message, cause);
    }

    public PieException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public PieException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
