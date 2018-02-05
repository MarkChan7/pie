package cn.poco.pie.weibo;

import android.app.Activity;
import android.content.Intent;

import java.util.Map;

import cn.poco.pie.PieContent;
import cn.poco.pie.PieImageHelper;
import cn.poco.pie.PieStatusCode;
import cn.poco.pie.SocialNetwork;
import cn.poco.pie.ThirdPartConfig;
import cn.poco.pie.exception.PieException;
import cn.poco.pie.handler.PieHandler;
import cn.poco.pie.listener.PieAuthListener;
import cn.poco.pie.listener.PieShareListener;
import cn.poco.pie.utils.SerializableMap;

/**
 * Date  : 2016/11/10
 * Author: MarkChan
 * Desc  :
 */
public class WeiboTransitHandler extends PieHandler {

    private static final int REQ_CODE_SHARE = 972;
    private static final int REQ_CODE_AUTH = 844;
    private static final int REQ_CODE_DELETE_AUTH = 580;

    public static final String SOCIAL_ACTION_RESULT = "social_action_result";
    public static final String SOCIAL_ACTION_DATA = "social_action_data";
    public static final String SOCIAL_ACTION_ERR_CODE = "social_action_err_code";
    public static final String SOCIAL_ACTION_ERR_TR = "social_action_err_tr";

    private PieAuthListener mAuthListener;

    @Override
    public void onCreate(Activity activity, ThirdPartConfig.Platform platform) {
        super.onCreate(activity, platform);
    }

    @Override
    public boolean isSupportAuth() {
        return true;
    }

    @Override
    public void auth(PieAuthListener listener) {
        mAuthListener = listener;

        if (mContext != null && !((Activity) mContext).isFinishing()) {
            Intent intent = new Intent(getContext(), WeiboAssistActivity.class);
            intent.putExtra(WeiboAssistActivity.EXTRA_SOCIAL_ACTION, WeiboAssistActivity.SOCIAL_ACTION_AUTH);
            ((Activity) mContext).startActivityForResult(intent, REQ_CODE_AUTH);
        }
    }

    @Override
    public void deleteAuth(PieAuthListener listener) {
        mAuthListener = listener;

        if (mContext != null && !((Activity) mContext).isFinishing()) {
            Intent intent = new Intent(getContext(), WeiboAssistActivity.class);
            intent.putExtra(WeiboAssistActivity.EXTRA_SOCIAL_ACTION, WeiboAssistActivity.SOCIAL_ACTION_DELETE_AUTH);
            ((Activity) mContext).startActivityForResult(intent, REQ_CODE_DELETE_AUTH);
        }
    }

    @Override
    public void share(final PieContent content, PieShareListener listener) {
        mShareListener = listener;

        mImageHelper.saveBitmapToExternalIfNeed(content);
        mImageHelper.copyImageToCacheFileDirIfNeed(content);
        mImageHelper.downloadImageIfNeed(content, new PieImageHelper.Callback() {

            @Override
            public void onSuccess() {
                if (mContext != null && !((Activity) mContext).isFinishing()) {
                    Intent intent = new Intent(getContext(), WeiboAssistActivity.class);
                    intent.putExtra(WeiboAssistActivity.EXTRA_SOCIAL_ACTION, WeiboAssistActivity.SOCIAL_ACTION_SHARE);
                    intent.putExtra(WeiboAssistActivity.EXTRA_SOCIAL_CONTENT, content);
                    ((Activity) mContext).startActivityForResult(intent, REQ_CODE_SHARE);
                }
            }

            @Override
            public void onFailed() {
                PieException ex = new PieException("Download picture failed");
                mShareListener.onError(SocialNetwork.WEIBO, PieStatusCode.ERR_SHARE_DOWNLOAD_IMG_FAILED, ex);
            }
        });
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        int socialActionResult = data.getIntExtra(SOCIAL_ACTION_RESULT, PieStatusCode.ERROR);
        int errCode = data.getIntExtra(SOCIAL_ACTION_ERR_CODE, PieStatusCode.ERROR);

        Map<String, String> map = null;
        SerializableMap serializableMap = data.getParcelableExtra(SOCIAL_ACTION_DATA);
        if (serializableMap != null) {
            map = serializableMap.getMap();
        }

        Throwable tr = (Throwable) data.getSerializableExtra(SOCIAL_ACTION_ERR_TR);

        switch (requestCode) {
            case REQ_CODE_AUTH:
                handleAuthOptsResult(PieAuthListener.ACTION_AUTH, socialActionResult, map, errCode, tr);
                break;
            case REQ_CODE_DELETE_AUTH:
                handleAuthOptsResult(PieAuthListener.ACTION_DELETE_AUTH, socialActionResult, null, errCode, tr);
                break;
            case REQ_CODE_SHARE:
                handleShareResult(socialActionResult, errCode, tr);
            default:
                break;
        }
    }

    private void handleShareResult(int socialActionResult, int errCode, Throwable tr) {
        switch (socialActionResult) {
            case PieStatusCode.SUCCESS:
                mShareListener.onSuccess(SocialNetwork.WEIBO);
                break;
            case PieStatusCode.CANCEL:
                mShareListener.onCancel(SocialNetwork.WEIBO);
                break;
            case PieStatusCode.ERROR:
            default:
                mShareListener.onError(SocialNetwork.WEIBO, errCode, tr);
                break;
        }
    }

    private void handleAuthOptsResult(final int action, int socialActionResult, final Map<String, String> data,
                                      int errCode, final Throwable tr) {
        switch (socialActionResult) {
            case PieStatusCode.SUCCESS:
                mAuthListener.onSuccess(SocialNetwork.WEIBO, action, data);
                break;
            case PieStatusCode.CANCEL:
                mAuthListener.onCancel(SocialNetwork.WEIBO, action);
                break;
            case PieStatusCode.ERROR:
            default:
                mAuthListener.onError(SocialNetwork.WEIBO, action, errCode, tr);
                break;
        }
    }

    @Override
    protected boolean isNeedActivityContext() {
        return true;
    }

    @Override
    public void release() {
        super.release();
        mAuthListener = null;
    }
}
