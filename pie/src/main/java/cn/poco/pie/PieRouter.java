package cn.poco.pie;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.poco.pie.exception.PieException;
import cn.poco.pie.handler.PieHandler;
import cn.poco.pie.listener.PieAuthListener;
import cn.poco.pie.listener.PieShareListener;
import timber.log.Timber;

/**
 * Date  : 2016/11/8
 * Author: MarkChan
 * Desc  :
 */
public class PieRouter {

    private final Map<SocialNetwork, PieHandler> PLATFORM_HANDLERS = new HashMap<>();
    private final List<Pair<SocialNetwork, String>> SUPPORTED_PLATFORM = new ArrayList<>();

    private Guard mGuard;

    private PieHandler mCurrentHandler;

    public PieRouter() {
        SUPPORTED_PLATFORM.add(new Pair<>(SocialNetwork.MORE, "cn.poco.pie.handler.PieMoreHandler"));
        SUPPORTED_PLATFORM.add(new Pair<>(SocialNetwork.COPY, "cn.poco.pie.handler.PieCopyHandler"));
        SUPPORTED_PLATFORM.add(new Pair<>(SocialNetwork.WECHAT, "cn.poco.pie.wechat.WechatHandler"));
        SUPPORTED_PLATFORM.add(new Pair<>(SocialNetwork.WECHAT_MOMENT, "cn.poco.pie.wechat.WechatHandler"));
        SUPPORTED_PLATFORM.add(new Pair<>(SocialNetwork.QQ, "cn.poco.pie.qq.QqHandler"));
        SUPPORTED_PLATFORM.add(new Pair<>(SocialNetwork.QZONE, "cn.poco.pie.qq.QqHandler"));
        SUPPORTED_PLATFORM.add(new Pair<>(SocialNetwork.WEIBO_SOUL, "cn.poco.pie.weibo.WeiboHandler"));
        SUPPORTED_PLATFORM.add(new Pair<>(SocialNetwork.WEIBO, "cn.poco.pie.weibo.WeiboTransitHandler"));

        mGuard = new Guard(PLATFORM_HANDLERS);
        init();
    }

    private void init() {
        Pair<SocialNetwork, String> pair;
        PieHandler handler;
        Iterator<Pair<SocialNetwork, String>> iterator = SUPPORTED_PLATFORM.iterator();
        do {
            pair = iterator.next();
            if (pair.first != SocialNetwork.WECHAT_MOMENT) {
                handler = newHandler(pair.second);
            } else {
                handler = PLATFORM_HANDLERS.get(SocialNetwork.WECHAT);
            }
            PLATFORM_HANDLERS.put(pair.first, handler);
        } while (iterator.hasNext());
    }

    private PieHandler newHandler(String className) {
        PieHandler handler = null;

        try {
            Class clazz = Class.forName(className);
            handler = (PieHandler) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return handler;
    }

    public PieHandler getHandler(SocialNetwork socialNetwork) {
        return PLATFORM_HANDLERS.get(socialNetwork);
    }

    public void share(Activity activity, SocialNetwork socialNetwork, PieContent content, PieShareListener listener) {
        if (mCurrentHandler != null) {
            mCurrentHandler.release();
        }

        if (socialNetwork == null) {
            Timber.e("SocialNetwork can\'t be null");
            return;
        }

        if (content == null) {
            if (listener != null) {
                PieException ex = new PieException("Share content can\'t  be null");
                listener.onError(socialNetwork, PieStatusCode.ERR_SHARE_EMPTY_CONTENT, ex);
            }
            Timber.e("Share content can't  be null");
        }

        if (activity != null && !activity.isFinishing()) {
            if (mGuard.share(socialNetwork)) {
                PieHandler handler = getHandler(socialNetwork);
                handler.onCreate(activity, ThirdPartConfig.getPlatform(socialNetwork));

                mCurrentHandler = handler;

                final SparseArray<PieShareListener> listenerRef = new SparseArray<>();
                listenerRef.put(0, listener);
                PieShareListener shareListener = new PieShareListener() {

                    @Override
                    public void onSuccess(SocialNetwork socialNetwork) {
                        PieShareListener ref = listenerRef.get(0, null);
                        if (ref != null) {
                            ref.onSuccess(socialNetwork);
                        }

                        listenerRef.clear();
                    }

                    @Override
                    public void onCancel(SocialNetwork socialNetwork) {
                        PieShareListener ref = listenerRef.get(0, null);
                        if (ref != null) {
                            ref.onCancel(socialNetwork);
                        }

                        listenerRef.clear();
                    }

                    @Override
                    public void onError(SocialNetwork socialNetwork, int errCode, Throwable tr) {
                        PieShareListener ref = listenerRef.get(0, null);
                        if (ref != null) {
                            ref.onError(socialNetwork, errCode, tr);
                        }

                        listenerRef.clear();
                    }
                };

                try {
                    handler.share(content, shareListener);
                    if (handler.isDisposable()) {
                        handler.release();
                    }
                } catch (PieException e) {
                    shareListener.onError(socialNetwork, e.getCode(), e);
                } catch (Exception e) {
                    shareListener.onError(socialNetwork, PieStatusCode.ERR_EXCEPTION, e);
                }
            } else {
                if (listener != null) {
                    PieException ex = new PieException("No configuration information or jar for " + socialNetwork);
                    listener.onError(socialNetwork, PieStatusCode.ERR_NOT_CONFIG, ex);
                }
            }
        } else {
            if (listener != null) {
                PieException ex = new PieException("Activity is null or finishing");
                listener.onError(socialNetwork, PieStatusCode.ERR_EMPTY_ATY_OR_FINISHING, ex);
            }
            Timber.w("Activity is null or finishing");
        }
    }

    public void auth(Activity activity, SocialNetwork socialNetwork, PieAuthListener listener) {
        if (mCurrentHandler != null) {
            mCurrentHandler.release();
        }

        if (socialNetwork == null) {
            Timber.e("SocialNetwork can\'t be null");
            return;
        }

        if (activity != null && !activity.isFinishing()) {
            if (mGuard.auth(socialNetwork)) {
                PieHandler handler = getHandler(socialNetwork);
                handler.onCreate(activity, ThirdPartConfig.getPlatform(socialNetwork));

                mCurrentHandler = handler;

                final SparseArray<PieAuthListener> listenerRef = new SparseArray<>();
                listenerRef.put(0, listener);
                PieAuthListener authListener = new PieAuthListener() {

                    @Override
                    public void onSuccess(SocialNetwork socialNetwork, int action, Map<String, String> data) {
                        PieAuthListener ref = listenerRef.get(0, null);
                        if (ref != null) {
                            ref.onSuccess(socialNetwork, action, data);
                        }

                        listenerRef.clear();
                    }

                    @Override
                    public void onCancel(SocialNetwork socialNetwork, int action) {
                        PieAuthListener ref = listenerRef.get(0, null);
                        if (ref != null) {
                            ref.onCancel(socialNetwork, action);
                        }

                        listenerRef.clear();
                    }

                    @Override
                    public void onError(SocialNetwork socialNetwork, int action, int errCode, Throwable tr) {
                        PieAuthListener ref = listenerRef.get(0, null);
                        if (ref != null) {
                            ref.onError(socialNetwork, action, errCode, tr);
                        }

                        listenerRef.clear();
                    }
                };
                handler.auth(authListener);
                if (handler.isDisposable()) {
                    handler.release();
                }
            } else {
                if (listener != null) {
                    PieException ex = new PieException("No configuration information or jar for " + socialNetwork);
                    listener.onError(socialNetwork, PieAuthListener.ACTION_AUTH, PieStatusCode.ERR_NOT_CONFIG, ex);
                }
            }
        } else {
            if (listener != null) {
                PieException ex = new PieException("Activity is null or finishing");
                listener.onError(socialNetwork, PieAuthListener.ACTION_AUTH, PieStatusCode.ERR_EMPTY_ATY_OR_FINISHING,
                                 ex);
            }
            Timber.w("Activity is null or finishing");
        }
    }

    public void deleteAuth(Activity activity, SocialNetwork socialNetwork, PieAuthListener listener) {
        if (mCurrentHandler != null) {
            mCurrentHandler.release();
        }

        if (activity != null && !activity.isFinishing()) {
            if (mGuard.auth(socialNetwork)) {
                PieHandler handler = getHandler(socialNetwork);
                handler.onCreate(activity, ThirdPartConfig.getPlatform(socialNetwork));

                mCurrentHandler = handler;

                if (listener == null) {
                    listener = new PieAuthListener() {

                        @Override
                        public void onSuccess(SocialNetwork socialNetwork, int action, Map<String, String> data) {
                            // do nothing
                        }

                        @Override
                        public void onCancel(SocialNetwork socialNetwork, int action) {
                            // do nothing
                        }

                        @Override
                        public void onError(SocialNetwork socialNetwork, int action, int errCode, Throwable tr) {
                            // do nothing
                        }
                    };
                }

                handler.deleteAuth(listener);
                if (handler.isDisposable()) {
                    handler.release();
                }
            } else {
                if (listener != null) {
                    PieException ex = new PieException("No configuration information or jar for " + socialNetwork);
                    listener.onError(socialNetwork, PieAuthListener.ACTION_DELETE_AUTH, PieStatusCode.ERR_NOT_CONFIG,
                                     ex);
                }
            }
        } else {
            if (listener != null) {
                PieException ex = new PieException("Activity is null or finishing");
                listener.onError(socialNetwork, PieAuthListener.ACTION_DELETE_AUTH,
                                 PieStatusCode.ERR_EMPTY_ATY_OR_FINISHING,
                                 ex);
            }
            Timber.w("Activity is null or finishing");
        }
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (mCurrentHandler != null) {
            mCurrentHandler.onActivityResult(activity, requestCode, resultCode, data);
        }
    }

    private static class Guard {

        private Map<SocialNetwork, PieHandler> mHandlers;

        Guard(Map<SocialNetwork, PieHandler> handlers) {
            mHandlers = handlers;
        }

        boolean share(SocialNetwork socialNetwork) {
            return checkPlatformConfig(socialNetwork);
        }

        boolean auth(SocialNetwork socialNetwork) {
            if (!checkPlatformConfig(socialNetwork)) {
                return false;
            } else {
                PieHandler handler = mHandlers.get(socialNetwork);
                if (!handler.isSupportAuth()) {
                    Timber.w(socialNetwork + " don\'t support authorization");
                    return false;
                } else {
                    return true;
                }
            }
        }

        private boolean checkPlatformConfig(SocialNetwork socialNetwork) {
            ThirdPartConfig.Platform platform = ThirdPartConfig.getPlatform(socialNetwork);
            if (platform != null && !platform.isConfigured()) {
                Timber.e("No configuration information for " + socialNetwork);
                return false;
            } else {
                PieHandler handler = mHandlers.get(socialNetwork);
                if (handler == null) {
                    Timber.e("No configuration jar for " + socialNetwork);
                    return false;
                } else {
                    return true;
                }
            }
        }
    }
}
