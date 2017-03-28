package cn.poco.jane.social;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;

import cn.poco.pie.Pie;
import cn.poco.pie.PieContent;
import cn.poco.pie.SocialNetwork;
import cn.poco.pie.listener.PieShareListener;
import cn.poco.pie.listener.PieShareListenerAdapter;
import cn.poco.pie.view.PieSelector;
import cn.poco.pie.view.PieSelectorDialog;
import cn.poco.pie.view.PieSelectorFsPopupWindow;
import cn.poco.pie.view.PieSelectorPopupWindow;
import cn.poco.pie.view.ShareTarget;

public final class TpShareHelper {

    private FragmentActivity mContext;
    private Callback mCallback;
    private PieSelector mSelector;

    public static TpShareHelper instance(FragmentActivity context, Callback callback) {
        return new TpShareHelper(context, callback);
    }

    private TpShareHelper(FragmentActivity context, Callback callback) {
        if (context == null) {
            throw new NullPointerException();
        }
        mContext = context;
        mCallback = callback;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void showDialog() {
        mSelector = new PieSelectorDialog(mContext,
                                          new PieSelector.OnSelectorDismissListener() {

                                              @Override
                                              public void onDismiss() {
                                                  onShareSelectorDismiss();
                                              }
                                          }, mShareItemClick);
        mSelector.show();
    }

    public void showBottomPopupWindows(View anchor) {
        mSelector = new PieSelectorPopupWindow(mContext, anchor,
                                               new PieSelector.OnSelectorDismissListener() {

                                                   @Override
                                                   public void onDismiss() {
                                                       onShareSelectorDismiss();
                                                   }
                                               }, mShareItemClick);
        mSelector.show();
    }

    public void showBottomFsPopupWindows(View anchor) {
        mSelector = new PieSelectorFsPopupWindow(mContext, anchor,
                                                 new PieSelector.OnSelectorDismissListener() {

                                                     @Override
                                                     public void onDismiss() {
                                                         onShareSelectorDismiss();
                                                     }
                                                 }, mShareItemClick);
        mSelector.show();
    }

    void onShareSelectorDismiss() {
        if (mCallback != null) {
            mCallback.onDismiss(this);
        }
    }

    public void hideShareWindow() {
        if (mSelector != null) {
            mSelector.dismiss();
        }
    }

    private AdapterView.OnItemClickListener mShareItemClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ShareTarget item = (ShareTarget) parent.getItemAtPosition(position);
            shareTo(item);
            hideShareWindow();
        }
    };

    public void shareTo(ShareTarget item) {
        PieContent content = mCallback.provideShareContent(TpShareHelper.this, item.socialNetwork);
        if (content == null) {
            return;
        }
        Pie.get(mContext).share(mContext, item.socialNetwork, content, shareListener);
    }

    protected PieShareListener shareListener = new PieShareListenerAdapter() {

        @Override
        protected void onComplete(SocialNetwork socialNetwork, int statusCode, Throwable tr) {
            if (mCallback != null) {
                mCallback.onShareComplete(TpShareHelper.this, statusCode);
            }
        }
    };

    public Context getContext() {
        return mContext;
    }

    public void reset() {
        if (mSelector != null) {
            mSelector.release();
            mSelector = null;
        }
        mShareItemClick = null;
    }

    public interface Callback {

        PieContent provideShareContent(TpShareHelper helper, SocialNetwork target);

        void onShareStart(TpShareHelper helper);

        void onShareComplete(TpShareHelper helper, int code);

        void onDismiss(TpShareHelper helper);
    }
}
