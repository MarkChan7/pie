package cn.poco.pie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.poco.pie.handler.PieHandler;
import cn.poco.pie.listener.PieAuthListener;
import cn.poco.pie.listener.PieShareListener;
import cn.poco.pie.utils.Droid;

/**
 * Date  : 2016/11/8
 * Author: MarkChan
 * Desc  :
 */
public class Pie {

    private static Pie INSTANCE;

    public static Pie get(Context context) {
        if (INSTANCE == null || INSTANCE.mRouter == null) {
            INSTANCE = new Pie(context);
        }
        return INSTANCE;
    }

    private PieRouter mRouter;
    private PieConfig mConfig;
    private Droid mDroid;
    private Executor mTaskExecutor;

    private Pie(Context context) {
        mRouter = new PieRouter();
        if (mConfig == null) {
            mConfig = new PieConfig.Builder(context).build();
        }

        mDroid = Droid.an();
        mTaskExecutor = Executors.newCachedThreadPool();
    }

    public Executor getDroid() {
        return mDroid.defaultCallbackExecutor();
    }

    public Executor getTaskExecutor() {
        return mTaskExecutor;
    }

    public PieConfig getConfig() {
        return mConfig;
    }

    public PieHandler getHandler(SocialNetwork socialNetwork) {
        return mRouter.getHandler(socialNetwork);
    }

    public void share(Activity activity, SocialNetwork socialNetwork, PieContent content, PieShareListener listener) {
        mRouter.share(activity, socialNetwork, content, listener);
    }

    public void auth(Activity activity, SocialNetwork socialNetwork, PieAuthListener listener) {
        mRouter.auth(activity, socialNetwork, listener);
    }

    public void deleteAuth(Activity activity, SocialNetwork socialNetwork, PieAuthListener listener) {
        mRouter.deleteAuth(activity, socialNetwork, listener);
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        mRouter.onActivityResult(activity, requestCode, resultCode, data);
    }
}
