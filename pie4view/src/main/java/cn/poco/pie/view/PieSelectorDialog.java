package cn.poco.pie.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import java.lang.reflect.Field;

public class PieSelectorDialog extends PieSelector {

    private final String mShareDialogTag;
    private ShareDialogFragment mShareDialog;

    public PieSelectorDialog(FragmentActivity context, OnSelectorDismissListener onSelectorDismissListener,
                             AdapterView.OnItemClickListener onItemClickListener) {
        super(context, onSelectorDismissListener, onItemClickListener);
        mShareDialogTag = "share.dialog" + context.getComponentName().getShortClassName();
    }

    @Override
    public void show() {
        FragmentActivity context = getContext();
        if (mShareDialog == null
                && (mShareDialog = (ShareDialogFragment) context.getSupportFragmentManager()
                                                                .findFragmentByTag(mShareDialogTag)) == null) {
            mShareDialog = new ShareDialogFragment();
        }
        mShareDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (getSelectorDismissListener() != null) {
                    getSelectorDismissListener().onDismiss();
                }
            }
        });
        mShareDialog.setOnItemClickListener(getItemClickListener());
        mShareDialog.show(context.getSupportFragmentManager(), mShareDialogTag);
    }

    @Override
    public void dismiss() {
        if (mShareDialog != null) {
            mShareDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void release() {
        if (mShareDialog != null && mShareDialog.getActivity() == null) {
            return;
        }
        dismiss();
        mShareDialog = null;
        super.release();
    }

    public static class ShareDialogFragment extends DialogFragment {

        private AdapterView.OnItemClickListener mShareItemClick;
        private DialogInterface.OnDismissListener mDismiss;

        @Override
        public void show(FragmentManager manager, String tag) {
            try {
                Field field = DialogFragment.class.getDeclaredField("mShownByMe");
                if (field != null) {
                    field.setAccessible(true);
                    Boolean show = field.getBoolean(this);
                    if (show) {
                        return;
                    }
                }
            } catch (Exception ignored) {
            }
            super.show(manager, tag);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Use alert Dialog Theme here
            final TypedValue outValue = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.R.attr.alertDialogTheme, outValue, true);
            int theme = outValue.resourceId;
            setStyle(STYLE_NORMAL, theme);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            GridView gridView = createPanel(inflater.getContext(), mShareItemClick);
            gridView.setBackgroundColor(0xffffffff);
            return gridView;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setOnDismissListener(mDismiss);
            return dialog;
        }

        public void setOnItemClickListener(AdapterView.OnItemClickListener click) {
            mShareItemClick = click;
        }

        public void setOnDismissListener(DialogInterface.OnDismissListener dismiss) {
            mDismiss = dismiss;
        }
    }
}
