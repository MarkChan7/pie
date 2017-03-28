package cn.poco.jane;

import android.app.Application;

import cn.poco.jane.social.TpConfigHelper;

/**
 * Date  : 2016/11/8
 * Author: MarkChan
 * Desc  :
 */
public class Jane extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TpConfigHelper.config();
    }
}
