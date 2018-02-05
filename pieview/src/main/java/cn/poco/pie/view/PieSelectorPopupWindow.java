package cn.poco.pie.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;

public class PieSelectorPopupWindow extends PieSelector {

    protected PopupWindow mShareWindow;
    protected View mAnchorView;

    public PieSelectorPopupWindow(FragmentActivity context, View anchorView,
                                  OnSelectorDismissListener onSelectorDismissListener,
                                  AdapterView.OnItemClickListener onItemClickListener) {
        super(context, onSelectorDismissListener, onItemClickListener);
        mAnchorView = anchorView;
    }

    @Override
    public void show() {
        createShareWindowIfNeed();
        if (!mShareWindow.isShowing()) {
            mShareWindow.showAtLocation(mAnchorView, Gravity.BOTTOM, 0, 0);
        }
    }

    @Override
    public void dismiss() {
        if (mShareWindow != null) {
            mShareWindow.dismiss();
        }
    }

    @Override
    public void release() {
        dismiss();
        mShareWindow = null;
        super.release();
        mAnchorView = null;
    }

    protected void createShareWindowIfNeed() {
        if (mShareWindow != null) {
            return;
        }

        Context context = getContext();
        GridView gridView = createPanel(context, getItemClickListener());
        mShareWindow = new PopupWindow(gridView, WindowManager.LayoutParams.MATCH_PARENT,
                                       WindowManager.LayoutParams.WRAP_CONTENT, true);
        mShareWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mShareWindow.setOutsideTouchable(true);
        mShareWindow.setAnimationStyle(R.style.PieSelectorAnimation);
        mShareWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                if (getSelectorDismissListener() != null) {
                    getSelectorDismissListener().onDismiss();
                }
            }
        });
    }
}
