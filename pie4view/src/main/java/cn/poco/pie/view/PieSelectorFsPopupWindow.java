package cn.poco.pie.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class PieSelectorFsPopupWindow extends PieSelector implements View.OnClickListener {

    protected PopupWindow mShareWindow;
    protected View mAnchorView;
    protected RelativeLayout mContainerView;

    private GridView mGridView;
    private Animation enterAnimation;

    public PieSelectorFsPopupWindow(FragmentActivity context, View anchorView,
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
        showEnterAnimation();
    }

    private void showEnterAnimation() {
        if (enterAnimation == null) {
            enterAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.pie_share_selector_anim_enter);
        }
        mGridView.setAnimation(enterAnimation);
        enterAnimation.start();
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
        super.release();
        mAnchorView = null;
        mShareWindow = null;
        mGridView = null;
        enterAnimation = null;
    }

    private void createShareWindowIfNeed() {
        if (mShareWindow != null) {
            return;
        }

        Context context = getContext();

        mGridView = createPanel(context, getItemClickListener());
        RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        gridParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mGridView.setLayoutParams(gridParams);

        mContainerView = new RelativeLayout(getContext());
        mContainerView.setBackgroundColor(getContext().getResources().getColor(R.color.pie_black_trans));
        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mContainerView.setLayoutParams(containerParams);
        mContainerView.addView(mGridView);
        mContainerView.setOnClickListener(this);

        mShareWindow = new PopupWindow(mContainerView, WindowManager.LayoutParams.MATCH_PARENT,
                                       WindowManager.LayoutParams.MATCH_PARENT, true);
        mGridView.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mShareWindow.setOutsideTouchable(true);
        mShareWindow.setAnimationStyle(-1);
        mShareWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                if (getSelectorDismissListener() != null) {
                    getSelectorDismissListener().onDismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mContainerView) {
            dismiss();
        }
    }
}
