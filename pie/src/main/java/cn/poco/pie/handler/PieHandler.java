package cn.poco.pie.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.poco.pie.IActivityLifecycleMirror;
import cn.poco.pie.Pie;
import cn.poco.pie.PieContent;
import cn.poco.pie.PieImageHelper;
import cn.poco.pie.PieRouter;
import cn.poco.pie.ThirdPartConfig;
import cn.poco.pie.listener.PieAuthListener;
import cn.poco.pie.listener.PieShareListener;

/**
 * Date  : 2016/11/8
 * Author: MarkChan
 * Desc  :
 */
public abstract class PieHandler implements IActivityLifecycleMirror {

    protected Context mContext;

    private ThirdPartConfig.Platform mPlatform;

    protected PieShareListener mShareListener;

    protected PieImageHelper mImageHelper;

    /**
     * 通过 {@link PieRouter} 进行初始化
     */
    public void onCreate(Activity activity, ThirdPartConfig.Platform platform) {
        initContext(activity);
        mPlatform = platform;
        mImageHelper = new PieImageHelper(activity, Pie.get(activity).getConfig());
    }

    private void initContext(Activity activity) {
        if (isNeedActivityContext()) {
            mContext = activity;
        } else {
            mContext = activity.getApplicationContext();
        }
    }

    /**
     * 获取上下文
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 获取该平台相关的配置信息
     */
    public ThirdPartConfig.Platform getPlatform() {
        return mPlatform;
    }

    /**
     * 分享
     */
    public abstract void share(PieContent content, PieShareListener listener) throws Exception;

    /**
     * 设置分享的监听器
     */
    public void setShareListener(PieShareListener listener) {
        mShareListener = listener;
    }

    /**
     * 获取分享的监听器
     */
    public PieShareListener getShareListener() {
        return mShareListener;
    }

    /**
     * 是否支持授权
     */
    public boolean isSupportAuth() {
        return false;
    }

    /**
     * 授权
     */
    public void auth(PieAuthListener listener) {
        // do nothing
    }

    /**
     * 删除相关的授权信息
     */
    public void deleteAuth(PieAuthListener listener) {
        // do nothing
    }

    /**
     * 是否支持该平台进行相关的社会化操作
     */
    public boolean isSupport() {
        return true;
    }

    /**
     * 客户端是否已经安装
     */
    public boolean isInstall() {
        return true;
    }

    /**
     * 获取该平台的SDK版本
     */
    public String getSdkVersion() {
        return "";
    }

    /**
     * 是否为一次性的操作(不关心具体的操作结果)
     *
     * @return true则直接调用release()方法进行释放
     */
    public boolean isDisposable() {
        return false;
    }

    protected boolean isNeedActivityContext() {
        return false;
    }

    public void release() {
        mImageHelper = null;
        mShareListener = null;
        mContext = null;
    }

    // Helper
    //-----------------------------------------------------------------------
    protected void doOnWorkThread(final Runnable runnable) {
        Pie.get(mContext)
           .getTaskExecutor()
           .execute(runnable);
    }

    protected void doOnMainThread(final Runnable runnable) {
        Pie.get(mContext).getDroid().execute(runnable);
    }

    protected int getDefaultShareImageResId() {
        return Pie.get(mContext).getConfig().getDefaultShareImageResId();
    }

    // Activity生命周期回调
    //-----------------------------------------------------------------------

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        // 相信美好的事情即将发生
    }

    @Override
    public void onActivityNewIntent(Activity activity, Intent intent) {
        // 相信美好的事情即将发生
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        // 相信美好的事情即将发生
    }

    @Override
    public void onActivityDestroy() {
        release();
    }
}
