package cn.poco.pie.handler;

import android.content.ActivityNotFoundException;
import android.content.Intent;

import cn.poco.pie.PieContent;
import cn.poco.pie.PieStatusCode;
import cn.poco.pie.R;
import cn.poco.pie.SocialNetwork;
import cn.poco.pie.listener.PieShareListener;

/**
 * Date  : 2016/11/8
 * Author: MarkChan
 * Desc  :
 */
public class PieMoreHandler extends PieHandler {

    @Override
    public void share(PieContent content, PieShareListener listener) {
        Intent shareIntent = createIntent(content.getTitle(), content.getText());
        Intent chooser = Intent.createChooser(shareIntent, mContext.getString(R.string.pie_share_more_title));
        try {
            mContext.startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            listener.onError(SocialNetwork.MORE, PieStatusCode.ERROR, e);
        }
    }

    private Intent createIntent(String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        return intent;
    }

    @Override
    protected boolean isNeedActivityContext() {
        return true;
    }

    @Override
    public boolean isDisposable() {
        return true;
    }
}
