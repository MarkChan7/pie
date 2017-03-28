package cn.poco.pie.listener;

import cn.poco.pie.PieStatusCode;
import cn.poco.pie.SocialNetwork;

/**
 * Date  : 2016/11/11
 * Author: MarkChan
 * Desc  :
 */
public abstract class PieShareListenerAdapter implements PieShareListener {

    protected abstract void onComplete(SocialNetwork socialNetwork, int statusCode, Throwable tr);

    @Override
    public final void onSuccess(SocialNetwork socialNetwork) {
        onComplete(socialNetwork, PieStatusCode.SUCCESS, null);
    }

    @Override
    public final void onCancel(SocialNetwork socialNetwork) {
        onComplete(socialNetwork, PieStatusCode.CANCEL, null);
    }

    @Override
    public final void onError(SocialNetwork socialNetwork, int errCode, Throwable tr) {
        onComplete(socialNetwork, errCode, tr);
    }
}
