package cn.poco.pie;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Date  : 2016/11/8
 * Author: MarkChan
 * Desc  :
 */
public class ThirdPartConfig {

    private static final Map<SocialNetwork, Platform> CONFIGS = new HashMap<>();

    static {
        CONFIGS.put(SocialNetwork.WECHAT, new WechatPlatform(SocialNetwork.WECHAT));
        CONFIGS.put(SocialNetwork.WECHAT_MOMENT, new WechatPlatform(SocialNetwork.WECHAT_MOMENT));
        CONFIGS.put(SocialNetwork.QQ, new QqPlatform(SocialNetwork.QQ));
        CONFIGS.put(SocialNetwork.QZONE, new QqPlatform(SocialNetwork.QZONE));
        CONFIGS.put(SocialNetwork.WEIBO, new WeiboPlatform(SocialNetwork.WEIBO));
        CONFIGS.put(SocialNetwork.MORE, new PiePlatform(SocialNetwork.MORE));
        CONFIGS.put(SocialNetwork.COPY, new PiePlatform(SocialNetwork.COPY));
    }

    public static Platform getPlatform(SocialNetwork name) {
        return CONFIGS.get(name);
    }

    public static void configWechat(String id, String secret) {
        WechatPlatform wechat = (WechatPlatform) CONFIGS.get(SocialNetwork.WECHAT);
        wechat.appId = id;
        wechat.appSecret = secret;

        WechatPlatform wechatMoment = (WechatPlatform) CONFIGS.get(SocialNetwork.WECHAT_MOMENT);
        wechatMoment.appId = id;
        wechatMoment.appSecret = secret;
    }

    public static void configQq(String id, String key) {
        QqPlatform qq = (QqPlatform) CONFIGS.get(SocialNetwork.QQ);
        qq.appId = id;
        qq.appKey = key;

        QqPlatform qzone = (QqPlatform) CONFIGS.get(SocialNetwork.QZONE);
        qzone.appId = id;
        qzone.appKey = key;
    }

    public static void configWeibo(String key, String secret, String redirectUrl) {
        WeiboPlatform weibo = (WeiboPlatform) CONFIGS.get(SocialNetwork.WEIBO);
        weibo.appKey = key;
        weibo.appSecret = secret;
        weibo.redirectUrl = redirectUrl;
    }

    public static class WeiboPlatform implements Platform {

        public static final String DEFAULT_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

        private SocialNetwork socialNetwork;

        public String appKey;
        public String appSecret;
        public String redirectUrl = DEFAULT_REDIRECT_URL;

        public WeiboPlatform(SocialNetwork socialNetwork) {
            this.socialNetwork = socialNetwork;
        }

        @Override
        public SocialNetwork getName() {
            return SocialNetwork.WEIBO;
        }

        @Override
        public boolean isConfigured() {
            return !TextUtils.isEmpty(appKey) && !TextUtils.isEmpty(appSecret);
        }

        @Override
        public boolean isAuthorized() {
            return false;
        }
    }

    public static class QqPlatform implements Platform {

        private SocialNetwork socialNetwork;

        public String appId;
        public String appKey;

        public QqPlatform(SocialNetwork socialNetwork) {
            this.socialNetwork = socialNetwork;
        }

        @Override
        public SocialNetwork getName() {
            return socialNetwork;
        }

        @Override
        public boolean isConfigured() {
            return !TextUtils.isEmpty(appId) && !TextUtils.isEmpty(appKey);
        }

        @Override
        public boolean isAuthorized() {
            return false;
        }
    }

    public static class WechatPlatform implements Platform {

        private SocialNetwork socialNetwork;

        public String appId;
        public String appSecret;

        public WechatPlatform(SocialNetwork socialNetwork) {
            this.socialNetwork = socialNetwork;
        }

        @Override
        public SocialNetwork getName() {
            return socialNetwork;
        }

        @Override
        public boolean isConfigured() {
            return !TextUtils.isEmpty(appId) && !TextUtils.isEmpty(appSecret);
        }

        @Override
        public boolean isAuthorized() {
            return false;
        }
    }

    public static class PiePlatform implements Platform {

        private SocialNetwork socialNetwork;

        public PiePlatform(SocialNetwork socialNetwork) {
            this.socialNetwork = socialNetwork;
        }

        @Override
        public SocialNetwork getName() {
            return socialNetwork;
        }

        @Override
        public boolean isConfigured() {
            return true;
        }

        @Override
        public boolean isAuthorized() {
            return false;
        }
    }

    public interface Platform {

        SocialNetwork getName();

        boolean isConfigured();

        boolean isAuthorized();
    }
}
