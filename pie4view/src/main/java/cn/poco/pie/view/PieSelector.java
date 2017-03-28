package cn.poco.pie.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import cn.poco.pie.SocialNetwork;

/**
 * Date  : 2016/11/10
 * Author: MarkChan
 * Desc  :
 */
public abstract class PieSelector {

    private FragmentActivity mContext;
    private OnSelectorDismissListener mOnSelectorDismissListener;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    private static ShareTarget[] shareTargets = {
            new ShareTarget(SocialNetwork.WEIBO, R.string.pie_share_social_notwork_weibo,
                            R.drawable.pie_ic_weibo),
            new ShareTarget(SocialNetwork.WECHAT, R.string.pie_share_social_notwork_wechat,
                            R.drawable.pie_ic_wechat),
            new ShareTarget(SocialNetwork.WECHAT_MOMENT, R.string.pie_share_social_notwork_wechat_moment,
                            R.drawable.pie_ic_wechat_moment),
            new ShareTarget(SocialNetwork.QQ, R.string.pie_share_social_notwork_qq, R.drawable.pie_ic_qq),
            new ShareTarget(SocialNetwork.QZONE, R.string.pie_share_social_notwork_qzone,
                            R.drawable.pie_ic_qzone),
            new ShareTarget(SocialNetwork.MORE, R.string.pie_share_social_notwork_more, R.drawable.pie_ic_more),
            new ShareTarget(SocialNetwork.COPY, R.string.pie_share_social_notwork_copy,
                            R.drawable.pie_ic_copy)
    };

    public PieSelector(FragmentActivity context, OnSelectorDismissListener onSelectorDismissListener,
                       AdapterView.OnItemClickListener onItemClickListener) {
        mContext = context;
        mOnSelectorDismissListener = onSelectorDismissListener;
        mOnItemClickListener = onItemClickListener;
    }

    public abstract void show();

    public abstract void dismiss();

    public void release() {
        mContext = null;
        mOnSelectorDismissListener = null;
        mOnItemClickListener = null;
    }

    protected static GridView createPanel(final Context context, AdapterView.OnItemClickListener onItemClickListener) {
        GridView gridView = new GridView(context);
        ListAdapter adapter = new ArrayAdapter<ShareTarget>(context, 0, shareTargets) {

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext())
                                          .inflate(R.layout.pie_item_share_selector, parent, false);
                view.setBackgroundDrawable(null);
                ImageView image = (ImageView) view.findViewById(R.id.pie_item_share_selector_iv_icon);
                TextView platform = (TextView) view.findViewById(R.id.pie_item_share_selector_tv_title);

                ShareTarget target = getItem(position);
                if (target != null) {
                    image.setImageResource(target.iconResId);
                    platform.setText(target.titleResId);
                }
                return view;
            }
        };
        gridView.setNumColumns(-1);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setColumnWidth(context.getResources().getDimensionPixelSize(R.dimen.pie_share_selector_item_height));
        gridView.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        gridView.setSelector(R.drawable.pie_bg_share_selector);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(onItemClickListener);
        return gridView;
    }

    public FragmentActivity getContext() {
        return mContext;
    }

    public AdapterView.OnItemClickListener getItemClickListener() {
        return mOnItemClickListener;
    }

    public OnSelectorDismissListener getSelectorDismissListener() {
        return mOnSelectorDismissListener;
    }

    public interface OnSelectorDismissListener {

        void onDismiss();
    }
}
