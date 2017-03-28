package cn.poco.pie.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Date  : 2016/11/10
 * Author: MarkChan
 * Desc  :
 */
public class Droid {

    private static final Droid DROID = droid();

    public static Droid an() {
        return DROID;
    }

    private static Droid droid() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return new Android();
            }
        } catch (Exception e) {
            // ignore
        }

        return new Droid();
    }

    public Executor defaultCallbackExecutor() {
        return Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {
        defaultCallbackExecutor().execute(runnable);
    }

    private static class Android extends Droid {

        @Override
        public Executor defaultCallbackExecutor() {
            return new MainThreadExecutor();
        }

        private class MainThreadExecutor implements Executor {

            private final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void execute(Runnable runnable) {
                handler.post(runnable);
            }
        }
    }
}
