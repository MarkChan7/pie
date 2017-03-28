package cn.poco.pie.listener;

import cn.poco.pie.SocialNetwork;

/**
 * Date  : 2016/11/8
 * Author: MarkChan
 * Desc  :
 */
public interface PieShareListener {

    void onSuccess(SocialNetwork socialNetwork);

    void onCancel(SocialNetwork socialNetwork);

    void onError(SocialNetwork socialNetwork, int errCode, Throwable tr);
}
