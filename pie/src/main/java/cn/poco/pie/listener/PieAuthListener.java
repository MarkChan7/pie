package cn.poco.pie.listener;

import java.util.Map;

import cn.poco.pie.SocialNetwork;

/**
 * Date  : 2016/11/8
 * Author: MarkChan
 * Desc  :
 */
public interface PieAuthListener {

    int ACTION_AUTH = 0;
    int ACTION_DELETE_AUTH = 1;

    void onSuccess(SocialNetwork socialNetwork, int action, Map<String, String> data);

    void onCancel(SocialNetwork socialNetwork, int action);

    void onError(SocialNetwork socialNetwork, int action, int errCode, Throwable tr);
}
