package cn.poco.pie.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;

import java.util.Map;

import cn.poco.pie.Pie;
import cn.poco.pie.PieContent;
import cn.poco.pie.PieStatusCode;
import cn.poco.pie.SocialNetwork;
import cn.poco.pie.ThirdPartConfig;
import cn.poco.pie.exception.PieException;
import cn.poco.pie.listener.PieAuthListener;
import cn.poco.pie.listener.PieShareListener;
import cn.poco.pie.utils.SerializableMap;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public class WeiboAssistActivity extends Activity implements IWeiboHandler.Response {

    public static final String EXTRA_SOCIAL_ACTION = "cn.poco.pie.weibo.WeiboAssistActivity.extra_social_action";
    public static final String EXTRA_SOCIAL_CONTENT = "cn.poco.pie.weibo.WeiboAssistActivity.extra_social_content";

    public static final int SOCIAL_ACTION_SHARE = 943;
    public static final int SOCIAL_ACTION_AUTH = 720;
    public static final int SOCIAL_ACTION_DELETE_AUTH = 612;

    protected WeiboHandler mHandler;

    private boolean mActivityResultCanceled;
    private boolean mOnNewIntentCalled;
    private boolean mResponseCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int mAction = getIntent().getIntExtra(EXTRA_SOCIAL_ACTION, SOCIAL_ACTION_SHARE);

        mHandler = (WeiboHandler) Pie.get(this).getHandler(SocialNetwork.WEIBO_SOUL);
        if (mHandler != null) {
            mHandler.onCreate(this, ThirdPartConfig.getPlatform(SocialNetwork.WEIBO));
            mHandler.onActivityCreated(this, savedInstanceState);

            switch (mAction) {
                case SOCIAL_ACTION_AUTH:
                    try {
                        mHandler.auth(mAuthListenerProxy);
                    } catch (Exception e) {
                        mAuthListenerProxy.onError(SocialNetwork.WEIBO, PieAuthListener.ACTION_AUTH,
                                                   PieStatusCode.ERR_EXCEPTION, e);
                    }
                    break;
                case SOCIAL_ACTION_DELETE_AUTH:
                    try {
                        mHandler.deleteAuth(mAuthListenerProxy);
                    } catch (Exception e) {
                        mAuthListenerProxy.onError(SocialNetwork.WEIBO, PieAuthListener.ACTION_DELETE_AUTH,
                                                   PieStatusCode.ERR_EXCEPTION, e);
                    }
                    break;
                case SOCIAL_ACTION_SHARE:
                default:
                    PieContent content = getIntent().getParcelableExtra(EXTRA_SOCIAL_CONTENT);
                    if (content == null) {
                        shareFinishWithErrorResult(PieStatusCode.ERR_SHARE_EMPTY_CONTENT,
                                                   new PieException("PieContent can\'t be null"));
                    } else {
                        try {
                            mHandler.share(content, mShareListenerProxy);
                        } catch (PieException e) {
                            mShareListenerProxy.onError(SocialNetwork.WEIBO, e.getCode(), e);
                        } catch (Exception e) {
                            mShareListenerProxy.onError(SocialNetwork.WEIBO, PieStatusCode.ERR_EXCEPTION, e);
                        }
                    }
                    break;
            }
        } else {
            if (mAction == SOCIAL_ACTION_SHARE) {
                shareFinishWithErrorResult(PieStatusCode.ERR_NOT_HANDLER, new PieException("No corresponding handler"));
            } else {
                authOptsFinishWithErrorResult(PieStatusCode.ERR_NOT_HANDLER,
                                              new PieException("No corresponding handler"));
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mOnNewIntentCalled = true;

        setIntent(intent);
        mHandler = (WeiboHandler) Pie.get(this).getHandler(SocialNetwork.WEIBO_SOUL);
        if (mHandler != null) {
            mHandler.onActivityNewIntent(this, intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mOnNewIntentCalled || mResponseCalled) {
            return;
        }

        if (mHandler != null && mHandler.getApi() != null && mHandler.isInstall() &&
                mActivityResultCanceled && !isFinishing()) {
            if (mHandler != null) {
                mHandler.release();
            }
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mActivityResultCanceled = resultCode == Activity.RESULT_CANCELED;

        if (mHandler != null) {
            mHandler.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        mResponseCalled = true;
        if (mHandler != null) {
            mHandler.onResponse(baseResponse);
        }

        finish();
    }

    private PieAuthListener mAuthListenerProxy = new PieAuthListener() {

        @Override
        public void onSuccess(SocialNetwork socialNetwork, int action, Map<String, String> data) {
            authOptsFinishWithSuccessResult(data);
        }

        @Override
        public void onCancel(SocialNetwork socialNetwork, int action) {
            authOptsFinishWithCancelResult();
        }

        @Override
        public void onError(SocialNetwork socialNetwork, int action, int errCode, Throwable tr) {
            authOptsFinishWithErrorResult(errCode, tr);
        }
    };

    private void authOptsFinishWithErrorResult(int errCode, Throwable tr) {
        authOptsFinishWithResult(PieStatusCode.ERROR, null, errCode, tr);
    }

    private void authOptsFinishWithCancelResult() {
        authOptsFinishWithResult(PieStatusCode.CANCEL, null, 0, null);
    }

    private void authOptsFinishWithSuccessResult(Map<String, String> data) {
        authOptsFinishWithResult(PieStatusCode.SUCCESS, data, 0, null);
    }

    private void authOptsFinishWithResult(int resultCode, Map<String, String> data, int errCode, Throwable tr) {
        if (mHandler != null) {
            mHandler.onActivityDestroy();
        }

        Intent intent = new Intent();
        intent.putExtra(WeiboTransitHandler.SOCIAL_ACTION_RESULT, resultCode);

        if (resultCode == PieStatusCode.SUCCESS) {
            SerializableMap serializableMap = new SerializableMap();
            serializableMap.setMap(data);
            intent.putExtra(WeiboTransitHandler.SOCIAL_ACTION_DATA, serializableMap);
        } else if (resultCode == PieStatusCode.ERROR) {
            intent.putExtra(WeiboTransitHandler.SOCIAL_ACTION_ERR_CODE, errCode);
            intent.putExtra(WeiboTransitHandler.SOCIAL_ACTION_ERR_TR, tr);
        }

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private PieShareListener mShareListenerProxy = new PieShareListener() {

        @Override
        public void onSuccess(SocialNetwork socialNetwork) {
            shareFinishWithSuccessResult();
        }

        @Override
        public void onCancel(SocialNetwork socialNetwork) {
            shareFinishWithCancelResult();
        }

        @Override
        public void onError(SocialNetwork socialNetwork, int errCode, Throwable tr) {
            shareFinishWithErrorResult(errCode, tr);
        }
    };

    private void shareFinishWithCancelResult() {
        shareFinishWithResult(PieStatusCode.CANCEL, 0, null);
    }

    private void shareFinishWithErrorResult(int errCode, Throwable tr) {
        shareFinishWithResult(PieStatusCode.ERROR, errCode, tr);
    }

    private void shareFinishWithSuccessResult() {
        shareFinishWithResult(PieStatusCode.SUCCESS, 0, null);
    }

    private void shareFinishWithResult(int result, int errCode, Throwable tr) {
        if (mHandler != null) {
            mHandler.onActivityDestroy();
        }

        Intent intent = new Intent();
        intent.putExtra(WeiboTransitHandler.SOCIAL_ACTION_RESULT, result);

        if (result == PieStatusCode.ERROR) {
            intent.putExtra(WeiboTransitHandler.SOCIAL_ACTION_ERR_CODE, errCode);
            intent.putExtra(WeiboTransitHandler.SOCIAL_ACTION_ERR_TR, tr);
        }

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
