package cn.poco.jane.social;

import cn.poco.pie.ThirdPartConfig;

/**
 * Date  : 2016/11/8
 * Author: MarkChan
 * Desc  :
 */
public class TpConfigHelper {

    public static final String QQ_APP_ID = "101127414";
    public static final String QQ_APP_KEY = "ec53d2e9ad7e468e6a640cfffe05398b";
    public static final String WECHAT_APP_ID = "wx6f8328fceaf3443a";
    public static final String WECHAT_APP_SECRET = "7d06a6714d3cde059a632d7dd9942456";
    public static final String WEIBO_APP_KEY = "3093324165";
    public static final String WEIBO_APP_SECRET = "181b081ad1f97a3dadd423ccf2ac9029";
    public static final String WEIBO_REDIRECT_URL = "http://www.poco.cn";

    public static void config() {
        ThirdPartConfig.configWechat(WECHAT_APP_ID, WECHAT_APP_SECRET);
        ThirdPartConfig.configQq(QQ_APP_ID, QQ_APP_KEY);
        ThirdPartConfig.configWeibo(WEIBO_APP_KEY, WEIBO_APP_SECRET, WEIBO_REDIRECT_URL);
    }
}
