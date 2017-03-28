package cn.poco.pie.qq;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.utils.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public class QqHandler extends PieHandler implements IShare {

    private Tencent mTencent;
    private ThirdPartConfig.QqPlatform mConfig;
    private SocialNetwork mTarget;

    private QqPreferences mQqPreferences;

    protected final IUiListener mUiListener = new IUiListener() {

        @Override
        public void onCancel() {
            mShareListener.onCancel(mTarget);
        }

        @Override
        public void onComplete(Object response) {
            mShareListener.onSuccess(mTarget);
        }

        @Override
        public void onError(UiError e) {
            mShareListener.onError(mTarget, PieStatusCode.ERROR, new PieException(e.errorMessage));
        }
    };

    public QqHandler() {
        mTarget = SocialNetwork.QQ;
    }

    @Override
    public void onCreate(Activity activity, ThirdPartConfig.Platform platform) {
        super.onCreate(activity, platform);
        mConfig = (ThirdPartConfig.QqPlatform) platform;
        mTarget = mConfig.getName();

        if (mTencent == null) {
            mTencent = Tencent.createInstance(mConfig.appId, activity.getApplicationContext());
        }

        mQqPreferences = new QqPreferences(activity, "pie_qq");
    }

    @Override
    public void auth(PieAuthListener listener) {
        if (isInstall()) {
            if (mContext != null && !((Activity) mContext).isFinishing()) {
                mTencent.login((Activity) mContext, "all", getAuthListener(listener));
            }
        } else if (mContext != null && !((Activity) mContext).isFinishing()) {
            mTencent.loginServerSide((Activity) mContext, "all", getAuthListener(listener));
        }
    }

    private IUiListener getAuthListener(final PieAuthListener listener) {
        return new IUiListener() {

            public void onError(UiError e) {
                if (e != null) {
                    Timber.d("Auth error: errorCode=%d, errorMsg=%s, detail=%s", e.errorCode, e.errorMessage,
                             e.errorDetail);
                }

                if (listener != null) {
                    listener.onError(SocialNetwork.QQ, PieAuthListener.ACTION_AUTH, PieStatusCode.ERROR,
                                     new PieException(e.errorMessage));
                }
            }

            public void onCancel() {
                if (listener != null) {
                    listener.onCancel(SocialNetwork.QQ, PieAuthListener.ACTION_AUTH);
                }
            }

            public void onComplete(Object response) {
                Bundle values = parseOauthData(response);

                if (mQqPreferences == null && mContext != null) {
                    mQqPreferences = new QqPreferences(mContext, "pie_qq");
                }

                if (mQqPreferences != null) {
                    mQqPreferences.setAuthData(values).commit();
                }

                initOpenIdAndToken((JSONObject) response);

                if (listener != null) {
                    listener.onSuccess(SocialNetwork.QQ, PieAuthListener.ACTION_AUTH, PieUtils.bundleToMap(values));
                }
            }
        };
    }

    public void initOpenIdAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString("access_token");
            String expires = jsonObject.getString("expires_in");
            String openId = jsonObject.getString("openid");
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    protected Bundle parseOauthData(Object response) {
        Bundle bundle = new Bundle();
        if (response == null) {
            return bundle;
        } else {
            String jsonStr = response.toString().trim();
            if (TextUtils.isEmpty(jsonStr)) {
                return bundle;
            } else {
                JSONObject jsonObj = null;

                try {
                    jsonObj = new JSONObject(jsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsonObj == null) {
                    return bundle;
                } else {
                    bundle.putString("auth_time", jsonObj.optString("auth_time", ""));
                    bundle.putString("pay_token", jsonObj.optString("pay_token", ""));
                    bundle.putString("pf", jsonObj.optString("pf", ""));
                    bundle.putString("ret", String.valueOf(jsonObj.optInt("ret", -1)));
                    bundle.putString("sendinstall", jsonObj.optString("sendinstall", ""));
                    bundle.putString("page_type", jsonObj.optString("page_type", ""));
                    bundle.putString("appid", jsonObj.optString("appid", ""));
                    bundle.putString("openid", jsonObj.optString("openid", ""));
                    bundle.putString("uid", jsonObj.optString("openid", ""));
                    String expiresStr = jsonObj.optString("expires_in", "");
                    bundle.putString("expires_in", expiresStr);
                    bundle.putString("pfkey", jsonObj.optString("pfkey", ""));
                    bundle.putString("access_token", jsonObj.optString("access_token", ""));
                    return bundle;
                }
            }
        }
    }

    @Override
    public void deleteAuth(PieAuthListener listener) {
        mTencent.logout(mContext);
        if (mQqPreferences != null) {
            mQqPreferences.delete();
        }

        listener.onSuccess(SocialNetwork.QQ, PieAuthListener.ACTION_DELETE_AUTH, null);
    }

    @Override
    public void share(PieContent content, PieShareListener listener) {
        if (!isInstall()) {
            listener.onError(mTarget, PieStatusCode.ERR_NOT_INSTALL, new PieException("QQ not install"));
            return;
        }

        if (mContext != null && !Util.isMobileQQSupportShare(mContext)) {
            PieException ex = new PieException("The current version of WeChat does not support share");
            listener.onError(mTarget, PieStatusCode.ERR_NOT_SUPPORT_SHARE, ex);
            return;
        }

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

    private void shareDirectly(final Bundle bundle) {
        doOnMainThread(new Runnable() {

            @Override
            public void run() {
                if (mConfig.getName() == SocialNetwork.QQ) {
                    mTencent.shareToQQ((Activity) mContext, bundle, mUiListener);
                } else {
                    mTencent.shareToQzone((Activity) mContext, bundle, mUiListener);
                }
            }
        });
    }

    @Override
    public void shareText(PieContent pieContent) throws PieException {
        shareImageText(pieContent, null);
    }

    @Override
    public void shareImage(PieContent pieContent, PieImage pieImage) throws PieException {
        if (mTarget == SocialNetwork.QZONE || pieImage == null || (!pieImage.isLocalImage() && !pieImage.isNetImage())) {
            shareImageText(pieContent, pieImage);
        } else {
            shareOnlyImage(pieContent, pieImage);
        }
    }

    @Override
    public void shareWebpage(PieContent pieContent, PieWebpage pieWebpage) throws PieException {
        shareImageText(pieContent, pieWebpage.getThumbImage());
    }

    @Override
    public void shareAudio(PieContent pieContent, PieAudio pieAudio) throws PieException {
        if (mTarget == SocialNetwork.QZONE) {
            shareImageText(pieContent, pieAudio.getThumbImage());
        } else {
            if (TextUtils.isEmpty(pieContent.getTitle()) || TextUtils.isEmpty(pieContent.getTargetUrl())) {
                throw new InvalidParamException("Title or target url is empty or illegal");
            }
            if (TextUtils.isEmpty(pieAudio.getSrcUrl())) {
                throw new InvalidParamException("Audio url is empty or illegal");
            }

            final Bundle bundle = new Bundle();
            PieImage thumb = pieAudio.getThumbImage();
            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
            bundle.putString(QQShare.SHARE_TO_QQ_TITLE, pieContent.getTitle());
            bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, pieContent.getText());
            bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, pieContent.getTargetUrl());

            if (thumb != null) {
                if (thumb.isNetImage()) {
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, thumb.getNetImageUrl());
                } else if (thumb.isLocalImage()) {
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, thumb.getLocalPath());
                }
            }

            bundle.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, pieAudio.getSrcUrl());
            shareDirectly(bundle);
        }
    }

    @Override
    public void shareVideo(PieContent pieContent, PieVideo pieVideo) throws PieException {
        shareImageText(pieContent, pieVideo.getThumbImage());
    }

    /**
     * 图文模式: title, targetURL不能为空
     */
    private void shareImageText(PieContent pieContent, PieImage pieImage) throws PieException {
        if (TextUtils.isEmpty(pieContent.getTitle()) || TextUtils.isEmpty(pieContent.getTargetUrl())) {
            throw new InvalidParamException("Title or target url is empty or illegal");
        }

        final Bundle bundle = new Bundle();
        if (mTarget == SocialNetwork.QZONE) {
            bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        } else {
            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        }
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, pieContent.getTitle());
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, pieContent.getText());
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, pieContent.getTargetUrl());

        if (mTarget == SocialNetwork.QZONE) {
            ArrayList<String> imageUrls = new ArrayList<>();
            if (pieImage != null) {
                if (pieImage.isNetImage()) {
                    imageUrls.add(pieImage.getNetImageUrl());
                } else if (pieImage.isLocalImage()) {
                    imageUrls.add(pieImage.getLocalPath());
                }
            }
            bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        } else {
            if (pieImage != null) {
                if (pieImage.isNetImage()) {
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, pieImage.getNetImageUrl());
                } else if (pieImage.isLocalImage()) {
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, pieImage.getLocalPath());
                }
            }
        }

        shareDirectly(bundle);
    }

    /**
     * 纯图模式: localPath不能为空
     */
    private void shareOnlyImage(PieContent params, final PieImage image) throws PieException {
        mImageHelper.downloadImageIfNeed(image, new PieImageHelper.Callback() {

            @Override
            public void onSuccess() {
                Bundle bundle = new Bundle();
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);

                if (image.isLocalImage()) {
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, image.getLocalPath());
                }

                shareDirectly(bundle);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(activity, requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mUiListener);
            if (resultCode == Constants.ACTIVITY_OK) {
                Tencent.handleResultData(data, mUiListener);
            }
        }
    }

    @Override
    public String getSdkVersion() {
        return "3.1.0";
    }

    @Override
    public boolean isInstall() {
        PackageManager pkgMgr = mContext.getPackageManager();
        PackageInfo pkgInfo;
        if (Util.isTablet(mContext)) {
            try {
                pkgInfo = pkgMgr.getPackageInfo("com.tencent.minihd.qq", 0);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                // ignore
            }
        }

        try {
            pkgInfo = pkgMgr.getPackageInfo("com.tencent.mobileqq", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // ignore
        }
        return false;
    }

    @Override
    public boolean isSupportAuth() {
        return true;
    }

    @Override
    protected boolean isNeedActivityContext() {
        return true;
    }
}
