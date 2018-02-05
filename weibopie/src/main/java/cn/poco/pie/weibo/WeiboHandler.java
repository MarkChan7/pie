package cn.poco.pie.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;

import java.io.File;

import cn.poco.pie.IShare;
import cn.poco.pie.PieContent;
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

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public class WeiboHandler extends PieHandler implements IShare {

    public static final String SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";

    private ThirdPartConfig.WeiboPlatform mConfig;
    private WeiboPreferences mWeiboPreferences;
    private SsoHandler mSsoHandler;
    private AuthInfo mAuthInfo;
    private IWeiboShareAPI mApi;

    public WeiboHandler() {
        // 相信美好的事情即将发生
    }

    @Override
    public void onCreate(Activity activity, ThirdPartConfig.Platform config) {
        super.onCreate(activity, config);
        mConfig = (ThirdPartConfig.WeiboPlatform) config;

        mApi = WeiboShareSDK.createWeiboAPI(activity.getApplicationContext(), mConfig.appKey);
        mApi.registerApp();

        mWeiboPreferences = new WeiboPreferences(activity, "pie_weibo");

        mAuthInfo = new AuthInfo(activity, mConfig.appKey, mConfig.redirectUrl, SCOPE);
        mSsoHandler = new SsoHandler(activity, mAuthInfo);
    }

    public IWeiboShareAPI getApi() {
        return mApi;
    }

    @Override
    public boolean isInstall() {
        return mApi != null && mApi.isWeiboAppInstalled();
    }

    public boolean isAuthorized() {
        return mWeiboPreferences != null && mWeiboPreferences.isAuthorized();
    }

    @Override
    public boolean isSupport() {
        return mApi != null && mApi.isWeiboAppSupportAPI();
    }

    @Override
    public String getSdkVersion() {
        return "3.1.4";
    }

    @Override
    protected boolean isNeedActivityContext() {
        return true;
    }

    @Override
    public void auth(PieAuthListener listener) {
        mSsoHandler.authorize(new MyWeiboAuthListener(listener));
    }

    @Override
    public void deleteAuth(PieAuthListener listener) {
        mWeiboPreferences.delete();
        listener.onSuccess(SocialNetwork.WEIBO, PieAuthListener.ACTION_DELETE_AUTH, null);
    }

    @Override
    public void share(PieContent content, final PieShareListener listener) throws Exception {
        mShareListener = listener;
        switch (content.getShareType()) {
            case IMAGE:
                PieImage pieImage = (PieImage) content.getMedia();
                shareImage(content, pieImage);
                break;
            case WEB_PAGE:
                PieWebpage pieWebpage = (PieWebpage) content.getMedia();
                shareWebpage(content, pieWebpage);
                break;
            case AUDIO:
                PieAudio pieAudio = (PieAudio) content.getMedia();
                shareAudio(content, pieAudio);
                break;
            case VIDEO:
                PieVideo pieVideo = (PieVideo) content.getMedia();
                shareVideo(content, pieVideo);
                break;
            case TEXT:
            default:
                shareText(content);
                break;
        }
    }

    private String getAccessToken() {
        String accessToken = null;
        if (mWeiboPreferences != null) {
            accessToken = mWeiboPreferences.getAccessToken();
        }
        return accessToken;
    }

    private void allInOneShare(WeiboMultiMessage weiboMultiMessage) {
        final SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMultiMessage;
        final AuthInfo authInfo = new AuthInfo(mContext, mConfig.appKey, mConfig.redirectUrl, SCOPE);
        final String token = getAccessToken();

        if (mContext != null && !((Activity) mContext).isFinishing()) {
            doOnMainThread(new Runnable() {

                @Override
                public void run() {
                    boolean result = mApi.sendRequest((Activity) mContext, request, authInfo, token,
                                                      new WeiboAuthListener() {

                                                          public void onWeiboException(WeiboException e) {
                                                              mShareListener.onError(SocialNetwork.WEIBO,
                                                                                     PieStatusCode.ERR_EXCEPTION,
                                                                                     e);
                                                          }

                                                          public void onComplete(Bundle bundle) {
                                                              mShareListener.onSuccess(SocialNetwork.WEIBO);

                                                              mWeiboPreferences.setAuthData(bundle).commit();
                                                          }

                                                          public void onCancel() {
                                                              mShareListener.onCancel(SocialNetwork.WEIBO);
                                                          }
                                                      });
                    if (!result) {
                        PieException e = new PieException("Weibo share request failure occurred");
                        mShareListener.onError(SocialNetwork.WEIBO, PieStatusCode.ERR_REQUEST, e);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        if (savedInstanceState != null && mApi != null && mApi != null) {
            mApi.handleWeiboResponse(activity.getIntent(), (IWeiboHandler.Response) activity);
        }
    }

    @Override
    public void onActivityNewIntent(Activity activity, Intent intent) {
        super.onActivityNewIntent(activity, intent);
        if (mApi != null) {
            try {
                mApi.handleWeiboResponse(intent, (IWeiboHandler.Response) activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(activity, requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        mSsoHandler = null;
    }

    @Override
    public boolean isSupportAuth() {
        return true;
    }

    public void setScope(String[] permissions) {
    }

    public void onResponse(BaseResponse resp) {
        switch (resp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                if (isInstall()) {
                    mShareListener.onSuccess(SocialNetwork.WEIBO);
                }
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                mShareListener.onCancel(SocialNetwork.WEIBO);
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                mShareListener.onError(SocialNetwork.WEIBO, PieStatusCode.ERROR, new PieException(resp.errMsg));
        }
    }

    private void checkText(String text) throws PieException {
        if (TextUtils.isEmpty(text)) {
            throw new InvalidParamException("Text is empty or illegal");
        }
    }

    private void checkImage(PieImage pieImage) throws PieException {
        if (pieImage == null) {
            throw new InvalidParamException("PieImage can't be null");
        } else if (pieImage.isLocalImage()) {
            if (TextUtils.isEmpty(pieImage.getLocalPath()) || !new File(pieImage.getLocalPath()).exists()) {
                throw new InvalidParamException("PieImage\'s path is empty or illegal");
            }
        } else if (pieImage.isNetImage()) {
            if (TextUtils.isEmpty(pieImage.getNetImageUrl())) {
                throw new InvalidParamException("PieImage's url is empty or illegal");
            }
        } else if (pieImage.isResImage()) {
            throw new InvalidParamException("Unsupport PieImage type");
        } else if (pieImage.isBitmapImage()) {
            if (pieImage.getBitmap().isRecycled()) {
                throw new InvalidParamException("Can\'t share recycled bitmap");
            }
        } else {
            throw new InvalidParamException("Invaild PieImage");
        }
    }

    /**
     * text
     */
    @Override
    public void shareText(PieContent pieContent) throws PieException {
        String text = pieContent.getText();

        checkText(text);

        WeiboMultiMessage weiboMsg = new WeiboMultiMessage();
        weiboMsg.textObject = getTextObj(text);

        allInOneShare(weiboMsg);
    }

    /**
     * text and image
     */
    @Override
    public void shareImage(PieContent pieContent, final PieImage pieImage) throws PieException {
        final String text = pieContent.getText();

        checkText(text);

        checkImage(pieImage);

        doOnWorkThread(new Runnable() {

            @Override
            public void run() {
                WeiboMultiMessage weiboMsg = new WeiboMultiMessage();
                weiboMsg.textObject = getTextObj(text);
                weiboMsg.imageObject = getImageObj(pieImage);

                allInOneShare(weiboMsg);
            }
        });
    }

    /**
     * text, targetUrl and image
     */
    @Override
    public void shareWebpage(final PieContent pieContent, final PieWebpage pieWebpage) throws PieException {
        final String text = pieContent.getText();
        if (isInstall()) {
            checkText(text);
        }

        String targetUrl = pieContent.getTargetUrl();
        if (TextUtils.isEmpty(targetUrl)) {
            throw new InvalidParamException("Target url is empty or illegal");
        }

        doOnWorkThread(new Runnable() {

            @Override
            public void run() {
                WeiboMultiMessage weiboMsg = new WeiboMultiMessage();

                if (!isInstall()) {
                    weiboMsg.textObject = getTextObj(text);
                }

                try {
                    checkImage(pieWebpage.getThumbImage());
                    weiboMsg.imageObject = getImageObj(pieWebpage.getThumbImage());
                } catch (Exception ignore) {
                    weiboMsg.textObject = getTextObj(text);
                }

                weiboMsg.mediaObject = getWebPageObj(pieContent, pieWebpage);

                allInOneShare(weiboMsg);
            }
        });
    }

    /**
     * text, targetUrl and audio
     */
    @Override
    public void shareAudio(final PieContent pieContent, final PieAudio pieAudio) throws PieException {
        final String text = pieContent.getText();

        checkText(text);

        if (TextUtils.isEmpty(pieContent.getTargetUrl())) {
            throw new PieException("Target url is empty or illegal");
        }

        if (pieAudio == null) {
            throw new PieException("Audio is empty or illegal");
        }

        doOnWorkThread(new Runnable() {

            @Override
            public void run() {
                WeiboMultiMessage weiboMsg = new WeiboMultiMessage();

                if (!isInstall()) {
                    weiboMsg.textObject = getTextObj(text);
                }

                try {
                    checkImage(pieAudio.getThumbImage());
                    weiboMsg.imageObject = getImageObj(pieAudio.getThumbImage());
                } catch (Exception e) {
                    weiboMsg.textObject = getTextObj(text);
                }

                weiboMsg.mediaObject = getAudioObj(pieContent, pieAudio);

                allInOneShare(weiboMsg);
            }
        });
    }

    /**
     * text, targetUrl and video
     */
    @Override
    public void shareVideo(final PieContent pieContent, final PieVideo pieVideo) throws PieException {
        final String text = pieContent.getText();

        checkText(text);

        if (TextUtils.isEmpty(pieContent.getTargetUrl())) {
            throw new PieException("Target url is empty or illegal");
        }

        if (pieVideo == null) {
            throw new PieException("Video is empty or illegal");
        }

        doOnWorkThread(new Runnable() {

            @Override
            public void run() {
                WeiboMultiMessage weiboMsg = new WeiboMultiMessage();

                if (!isInstall()) {
                    weiboMsg.textObject = getTextObj(text);
                }

                try {
                    checkImage(pieVideo.getThumbImage());
                    weiboMsg.imageObject = getImageObj(pieVideo.getThumbImage());
                } catch (Exception ignore) {
                    weiboMsg.textObject = getTextObj(text);
                }

                weiboMsg.mediaObject = getVideoObj(pieContent, pieVideo);

                allInOneShare(weiboMsg);
            }
        });
    }

    /**
     * 创建文本消息对象
     */
    private TextObject getTextObj(String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        return textObject;
    }

    /**
     * 创建图片消息对象
     */
    private ImageObject getImageObj(PieImage image) {
        ImageObject imageObject = new ImageObject();

        if (image == null) {
            return imageObject;
        }

        if (image.isLocalImage()) {
            imageObject.imagePath = image.getLocalPath();
        } else {
            imageObject.imageData = mImageHelper.buildThumbData(image);
        }

        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象
     */
    private WebpageObject getWebPageObj(PieContent content, PieWebpage pieWebpage) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = content.getText();
        mediaObject.description = content.getTitle();

        byte[] thumbData = mImageHelper.buildThumbData(pieWebpage.getThumbImage());
        if (thumbData == null || thumbData.length == 0) {
            mediaObject.thumbData = mImageHelper.buildThumbData(
                    new PieImage(getDefaultShareImageResId()));
        } else {
            mediaObject.thumbData = thumbData;
        }

        mediaObject.actionUrl = content.getTargetUrl();
        mediaObject.defaultText = content.getText();
        return mediaObject;
    }

    /**
     * 创建多媒体（音乐）消息对象
     */
    private MusicObject getAudioObj(PieContent content, PieAudio pieAudio) {
        MusicObject musicObject = new MusicObject();
        musicObject.identify = Utility.generateGUID();
        musicObject.title = content.getText();
        musicObject.description = content.getTitle();

        byte[] thumbData = mImageHelper.buildThumbData(pieAudio.getThumbImage());
        if (thumbData == null || thumbData.length == 0) {
            musicObject.thumbData = mImageHelper.buildThumbData(new PieImage(getDefaultShareImageResId()));
        } else {
            musicObject.thumbData = thumbData;
        }

        musicObject.actionUrl = content.getTargetUrl();

        if (pieAudio != null) {
            musicObject.dataUrl = pieAudio.getSrcUrl();
            musicObject.dataHdUrl = pieAudio.getSrcUrl();
            musicObject.h5Url = pieAudio.getH5Url();
            musicObject.duration = 10;
            musicObject.defaultText = pieAudio.getDesc();
        }
        return musicObject;
    }

    /**
     * 创建多媒体（视频）消息对象
     */
    private VideoObject getVideoObj(PieContent content, PieVideo pieVideo) {
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = content.getText();
        videoObject.description = content.getTitle();

        byte[] thumbData = mImageHelper.buildThumbData(pieVideo.getThumbImage());
        if (thumbData == null || thumbData.length == 0) {
            videoObject.thumbData = mImageHelper.buildThumbData(new PieImage(getDefaultShareImageResId()));
        } else {
            videoObject.thumbData = thumbData;
        }

        videoObject.actionUrl = content.getTargetUrl();

        if (pieVideo != null) {
            videoObject.dataUrl = pieVideo.getSrcUrl();
            videoObject.dataHdUrl = pieVideo.getSrcUrl();
            videoObject.h5Url = pieVideo.getH5Url();
            videoObject.duration = 10;
            videoObject.defaultText = pieVideo.getDesc();
        }
        return videoObject;
    }

    private class MyWeiboAuthListener implements WeiboAuthListener {

        private PieAuthListener mListener;

        MyWeiboAuthListener(PieAuthListener listener) {
            mListener = listener;
        }

        public void onComplete(Bundle values) {
            mWeiboPreferences.setAuthData(values).commit();
            mListener.onSuccess(SocialNetwork.WEIBO, PieAuthListener.ACTION_AUTH, PieUtils.bundleToMap(values));
        }

        public void onCancel() {
            mListener.onCancel(SocialNetwork.WEIBO, PieAuthListener.ACTION_AUTH);
        }

        public void onWeiboException(WeiboException e) {
            mListener.onError(SocialNetwork.WEIBO, PieAuthListener.ACTION_AUTH, PieStatusCode.ERR_EXCEPTION, e);
        }
    }

    @Override
    public void release() {
        super.release();
        mConfig = null;
        mAuthInfo = null;
        mSsoHandler = null;
        mApi = null;
        mWeiboPreferences = null;
    }
}
