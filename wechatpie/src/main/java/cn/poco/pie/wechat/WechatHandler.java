package cn.poco.pie.wechat;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import cn.poco.pie.IShare;
import cn.poco.pie.PieContent;
import cn.poco.pie.PieImageHelper;
import cn.poco.pie.PieStatusCode;
import cn.poco.pie.SocialNetwork;
import cn.poco.pie.ThirdPartConfig;
import cn.poco.pie.exception.InvalidParamException;
import cn.poco.pie.exception.PieException;
import cn.poco.pie.handler.PieHandler;
import cn.poco.pie.listener.PieAuthListener;
import cn.poco.pie.listener.PieShareListener;
import cn.poco.pie.media.PieAudio;
import cn.poco.pie.media.PieImage;
import cn.poco.pie.media.PieVideo;
import cn.poco.pie.media.PieWebpage;
import cn.poco.pie.utils.PieUtils;
import timber.log.Timber;

/**
 * Date  : 2016/11/8
 * Author: MarkChan
 * Desc  :
 */
public class WechatHandler extends PieHandler implements IShare {

    private static String sScope = "snsapi_userinfo,snsapi_friend,snsapi_message";

    private static final String TYPE_TEXT = "text";
    private static final String TYPE_IMAGE = "img";
    private static final String TYPE_AUDIO = "music";
    private static final String TYPE_VIDEO = "video";
    private static final String TYPE_WEBPAGE = "webpage";

    private String mShareType;

    private IWXAPI mApi;
    private SocialNetwork mTarget;

    private WechatPreferences mWechatPreferences;
    private PieAuthListener mAuthListener;

    private ThirdPartConfig.WechatPlatform mConfig;

    public WechatHandler() {
        mTarget = SocialNetwork.WECHAT;
    }

    protected void onResp(BaseResp baseResp) {
        switch (baseResp.getType()) {
            case ConstantsAPI.COMMAND_SENDAUTH:
                onAuthCallback((SendAuth.Resp) baseResp);
                break;
            case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                onShareCallback((SendMessageToWX.Resp) baseResp);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Activity activity, ThirdPartConfig.Platform platform) {
        super.onCreate(activity, platform);
        mContext = activity;
        mConfig = (ThirdPartConfig.WechatPlatform) platform;
        mTarget = platform.getName();
        mApi = WXAPIFactory.createWXAPI(mContext.getApplicationContext(), mConfig.appId);
        mApi.registerApp(mConfig.appId);

        mWechatPreferences = new WechatPreferences(activity, "pie_wechat");
    }

    @Override
    public void share(PieContent pieContent, PieShareListener listener) throws Exception {
        if (!isInstall()) {
            if (listener != null) {
                PieException ex = new PieException("WeChat no install");
                listener.onError(mTarget, PieStatusCode.ERR_NOT_INSTALL, ex);
            }
            Timber.w("WeChat no install");
            return;
        }

        mShareListener = listener;
        switch (pieContent.getShareType()) {
            case IMAGE:
                PieImage pieImage = (PieImage) pieContent.getMedia();
                if (mTarget == SocialNetwork.WECHAT_MOMENT) {
                    if (!pieImage.isUnknownImage()) {
                        mShareType = TYPE_IMAGE;
                        shareImage(pieContent, pieImage);
                    } else {
                        PieWebpage pieWebpage = new PieWebpage(pieImage);
                        mShareType = TYPE_WEBPAGE;
                        shareWebpage(pieContent, pieWebpage);
                    }
                } else {
                    mShareType = TYPE_IMAGE;
                    shareImage(pieContent, pieImage);
                }
                break;
            case WEB_PAGE:
                mShareType = TYPE_WEBPAGE;
                PieWebpage pieWebpage = (PieWebpage) pieContent.getMedia();
                shareWebpage(pieContent, pieWebpage);
                break;
            case AUDIO:
                mShareType = TYPE_AUDIO;
                PieAudio pieAudio = (PieAudio) pieContent.getMedia();
                shareAudio(pieContent, pieAudio);
                break;
            case VIDEO:
                mShareType = TYPE_VIDEO;
                PieVideo pieVideo = (PieVideo) pieContent.getMedia();
                shareVideo(pieContent, pieVideo);
                break;
            case TEXT:
            default:
                mShareType = TYPE_TEXT;
                shareText(pieContent);
                break;
        }
    }

    private void checkText(String text) throws PieException {
        if (TextUtils.isEmpty(text)) {
            throw new InvalidParamException("Text is empty or illegal");
        }
    }

    protected WXImageObject buildWechatImageObj(final PieImage image) {
        WXImageObject wechatImg = new WXImageObject();

        if (image.isLocalImage()) {
            wechatImg.setImagePath(image.getLocalPath());
        } else if (!image.isUnknownImage()) {
            wechatImg.imageData = mImageHelper.buildThumbData(image);
        }

        return wechatImg;
    }

    @Override
    public void shareText(PieContent pieContent) throws PieException {
        String text = pieContent.getText();

        checkText(text);

        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage wechatMediaMsg = new WXMediaMessage();
        wechatMediaMsg.mediaObject = textObj;
        wechatMediaMsg.description = text;

        shareDirectly(wechatMediaMsg);
    }

    @Override
    public void shareImage(PieContent pieContent, final PieImage pieImage) throws PieException {
        mImageHelper.downloadImageIfNeed(pieImage, new PieImageHelper.Callback() {

            @Override
            public void onSuccess() {
                WXImageObject wechatImage = buildWechatImageObj(pieImage);

                WXMediaMessage wechatMsg = new WXMediaMessage();
                wechatMsg.mediaObject = wechatImage;
                wechatMsg.thumbData = mImageHelper.buildThumbData(pieImage);

                shareDirectly(wechatMsg);
            }

            @Override
            public void onFailed() {
                sendDownloadImageFailed();
            }
        });
    }

    @Override
    public void shareWebpage(final PieContent pieContent, final PieWebpage pieWebpage) throws PieException {
        if (TextUtils.isEmpty(pieContent.getTargetUrl())) {
            throw new InvalidParamException("Target url is empty or illegal");
        }

        mImageHelper.downloadImageIfNeed(pieContent, new PieImageHelper.Callback() {

            @Override
            public void onSuccess() {
                WXWebpageObject wechatWebpage = new WXWebpageObject();
                wechatWebpage.webpageUrl = pieContent.getTargetUrl();

                WXMediaMessage wechatMediaMsg = new WXMediaMessage(wechatWebpage);
                wechatMediaMsg.title = pieContent.getTitle();
                wechatMediaMsg.description = pieContent.getText();
                wechatMediaMsg.thumbData = mImageHelper.buildThumbData(pieWebpage.getThumbImage());

                shareDirectly(wechatMediaMsg);
            }

            @Override
            public void onFailed() {
                sendDownloadImageFailed();
            }
        });
    }

    private void sendDownloadImageFailed() {
        doOnMainThread(new Runnable() {

            @Override
            public void run() {
                if (mShareListener != null) {
                    PieException ex = new PieException("Download picture failed");
                    mShareListener.onError(mTarget, PieStatusCode.ERR_SHARE_DOWNLOAD_IMG_FAILED, ex);
                }
                Timber.d("Download picture failed");
            }
        });
    }

    @Override
    public void shareAudio(final PieContent pieContent, final PieAudio pieAudio) throws PieException {
        if (TextUtils.isEmpty(pieContent.getTargetUrl()) && TextUtils.isEmpty(pieAudio.getSrcUrl())) {
            throw new InvalidParamException("Target url or audio url is empty or illegal");
        }

        mImageHelper.downloadImageIfNeed(pieContent, new PieImageHelper.Callback() {

            @Override
            public void onSuccess() {
                WXMusicObject wechatAudio = new WXMusicObject();
                if (!TextUtils.isEmpty(pieAudio.getH5Url())) {
                    wechatAudio.musicUrl = pieAudio.getH5Url();
                } else {
                    wechatAudio.musicUrl = pieContent.getTargetUrl();
                }
                WXMediaMessage wechatMediaMsg = new WXMediaMessage(wechatAudio);
                wechatMediaMsg.title = pieContent.getTitle();
                wechatMediaMsg.description = pieContent.getText();
                wechatMediaMsg.thumbData = mImageHelper.buildThumbData(pieAudio.getThumbImage());

                shareDirectly(wechatMediaMsg);
            }

            @Override
            public void onFailed() {
                sendDownloadImageFailed();
            }
        });
    }

    @Override
    public void shareVideo(final PieContent pieContent, final PieVideo pieVideo) throws PieException {
        if (TextUtils.isEmpty(pieContent.getTargetUrl()) && (TextUtils.isEmpty(pieVideo.getSrcUrl()))) {
            throw new InvalidParamException("Target url or video url is empty or illegal");
        }

        mImageHelper.downloadImageIfNeed(pieContent, new PieImageHelper.Callback() {

            @Override
            public void onSuccess() {
                WXVideoObject wechatVideoObj = new WXVideoObject();
                if (!TextUtils.isEmpty(pieVideo.getH5Url())) {
                    wechatVideoObj.videoUrl = pieVideo.getH5Url();
                } else {
                    wechatVideoObj.videoUrl = pieContent.getTargetUrl();
                }
                WXMediaMessage wechatMediaMsg = new WXMediaMessage(wechatVideoObj);
                wechatMediaMsg.title = pieContent.getTitle();
                wechatMediaMsg.description = pieContent.getText();
                wechatMediaMsg.thumbData = mImageHelper.buildThumbData(pieVideo.getThumbImage());

                shareDirectly(wechatMediaMsg);
            }

            @Override
            public void onFailed() {
                sendDownloadImageFailed();
            }
        });
    }

    private void shareDirectly(WXMediaMessage wechatMediaMsg) {
        final SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(mShareType);
        req.message = wechatMediaMsg;
        switch (mTarget) {
            case WECHAT_MOMENT:
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                break;
            case WECHAT:
                req.scene = SendMessageToWX.Req.WXSceneSession;
            default:
                break;
        }

        doOnMainThread(new Runnable() {

            @Override
            public void run() {
                boolean sendReq = mApi.sendReq(req);
                if (!sendReq) {
                    mShareListener.onError(mTarget, 0, new Throwable("发送请求失败"));
                }
            }
        });
    }

    @Override
    public boolean isSupportAuth() {
        return true;
    }

    @Override
    public void auth(PieAuthListener listener) {
        mAuthListener = listener;
        if (mWechatPreferences.isAuthValid()) {
            String refreshToken;
            if (mWechatPreferences.isAccessTokenAvailable()) {
                refreshToken = mWechatPreferences.getRefreshToken();
                String urlStr = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + mConfig.appId
                        + "&grant_type=refresh_token&refresh_token=" + refreshToken;
                loadOauthData(urlStr);
            }

            refreshToken = mWechatPreferences.getRefreshToken();
            listener.onSuccess(SocialNetwork.WECHAT, PieAuthListener.ACTION_AUTH,
                    getAuthWithRefreshToken(refreshToken));
        } else {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = sScope;
            req.state = "none";

            boolean send = mApi.sendReq(req);
            if (!send) {
                listener.onError(SocialNetwork.WECHAT, PieAuthListener.ACTION_AUTH, PieStatusCode.ERROR,
                        new PieException("Send auth request error"));
            }
        }
    }

    private void loadOauthData(String url) {
        String response = WechatAuthUtils.request(url);
        Bundle bundle = parseAuthData(response);
        mWechatPreferences.setBundle(bundle);
    }

    private Bundle parseAuthData(String response) {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(response)) {
            return bundle;
        }

        try {
            JSONObject jsonObj = new JSONObject(response);
            Iterator iterator = jsonObj.keys();

            String key;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                bundle.putString(key, jsonObj.optString(key));
            }

            bundle.putLong(WechatPreferences.KEY_RT_EXPIRES_IN, 604800L);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bundle;
    }

    private Map<String, String> getAuthWithRefreshToken(String refresh_token) {
        StringBuilder authUrl = new StringBuilder();
        authUrl.append("https://api.weixin.qq.com/sns/oauth2/refresh_token?");
        authUrl.append("appid=").append(mConfig.appId);
        authUrl.append("&grant_type=refresh_token");
        authUrl.append("&refresh_token=").append(refresh_token);
        String response = WechatAuthUtils.request(authUrl.toString());
        Map<String, String> map = null;

        try {
            map = PieUtils.jsonToMap(response);
        } catch (Exception e) {
            // ignore
        }

        return map;
    }

    @Override
    public void deleteAuth(PieAuthListener listener) {
        if (isInstall()) {
            mWechatPreferences.delete();
            listener.onSuccess(SocialNetwork.WECHAT, PieAuthListener.ACTION_DELETE_AUTH, null);
        }
    }

    @Override
    public boolean isInstall() {
        return mApi != null && mApi.isWXAppInstalled();
    }

    @Override
    public String getSdkVersion() {
        return "3.1.1";
    }

    protected void onAuthCallback(SendAuth.Resp resp) {
        if (mAuthListener == null) {
            return;
        }

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                getAuthWithCode(resp.code, mAuthListener);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                mAuthListener.onCancel(mTarget, PieAuthListener.ACTION_AUTH);
                break;
            default:
                String errStr = "Wechat auth error (" + resp.errCode + "):" + resp.errStr;
                mAuthListener.onError(mTarget, PieAuthListener.ACTION_AUTH, 0, new PieException(errStr));
                break;
        }
    }

    private void getAuthWithCode(String code, final PieAuthListener listener) {
        final StringBuilder authUrl = new StringBuilder();
        authUrl.append("https://api.weixin.qq.com/sns/oauth2/access_token?");
        authUrl.append("appid=").append(mConfig.appId);
        authUrl.append("&secret=").append(mConfig.appSecret);
        authUrl.append("&code=").append(code);
        authUrl.append("&grant_type=authorization_code");
        (new Thread(new Runnable() {

            public void run() {
                final String response = WechatAuthUtils.request(authUrl.toString());

                try {
                    doOnMainThread(new Runnable() {

                        @Override
                        public void run() {
                            Map<String, String> map = PieUtils.jsonToMap(response);
                            if (map == null || map.size() == 0) {
                                map = mWechatPreferences.getMap();
                            }

                            Bundle bundle = parseAuthData(response);
                            mWechatPreferences.setBundle(bundle);
                            if (map.get("errcode") != null) {
                                listener.onError(mTarget, PieAuthListener.ACTION_AUTH, 0,
                                        new Throwable(map.get("errmsg") + ""));
                            } else {
                                listener.onSuccess(mTarget, PieAuthListener.ACTION_AUTH, map);
                            }
                        }
                    });
                } catch (Exception e) {
                    // ignore
                }
            }
        })).start();
    }

    private void onShareCallback(SendMessageToWX.Resp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                mShareListener.onSuccess(mTarget);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                mShareListener.onCancel(mTarget);
                break;
            case BaseResp.ErrCode.ERR_COMM:
            case BaseResp.ErrCode.ERR_SENT_FAILED:
            default:
                mShareListener.onError(mTarget, PieStatusCode.ERROR, new PieException(resp.errCode, resp.errStr));
                break;
        }
    }

    private String buildTransaction(String type) {
        return type == null ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    protected IWXAPI getApi() {
        return mApi;
    }

    @Override
    protected boolean isNeedActivityContext() {
        return true;
    }

    @Override
    public void release() {
        super.release();
        mApi = null;
        mConfig = null;
        mTarget = null;
        mWechatPreferences = null;
    }
}
