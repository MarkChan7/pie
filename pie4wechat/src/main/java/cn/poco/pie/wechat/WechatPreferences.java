package cn.poco.pie.wechat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public class WechatPreferences {

    public static final String KEY_OPENID = "openid";
    public static final String KEY_UNIONID = "unionid";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_RT_EXPIRES_IN = "rt_expires_in";

    private SharedPreferences mSharedPreferences;

    private String mOpenid;
    private String mUnionid;
    private String mAccessToken;
    private String mRefreshToken;
    private long mExpiresIn;
    private long mRtExpiresIn;

    public WechatPreferences(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name, 0);
        mOpenid = mSharedPreferences.getString(KEY_OPENID, null);
        mUnionid = mSharedPreferences.getString(KEY_UNIONID, null);
        mAccessToken = mSharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        mRefreshToken = mSharedPreferences.getString(KEY_REFRESH_TOKEN, null);
        mExpiresIn = mSharedPreferences.getLong(KEY_EXPIRES_IN, 0L);
        mRtExpiresIn = mSharedPreferences.getLong(KEY_RT_EXPIRES_IN, 0L);
    }

    public WechatPreferences setBundle(Bundle bundle) {
        mOpenid = bundle.getString(KEY_OPENID);
        mUnionid = bundle.getString(KEY_UNIONID);
        mAccessToken = bundle.getString(KEY_ACCESS_TOKEN);
        mRefreshToken = bundle.getString(KEY_REFRESH_TOKEN);
        String time = bundle.getString(KEY_EXPIRES_IN);
        if (!TextUtils.isEmpty(time)) {
            mExpiresIn = Long.valueOf(time) * 1000L + System.currentTimeMillis();
        }

        try {
            String rtExpiresIn = bundle.getString(KEY_RT_EXPIRES_IN);
            if (!TextUtils.isEmpty(rtExpiresIn)) {
                mRtExpiresIn = Long.valueOf(rtExpiresIn) * 1000L + System.currentTimeMillis();
            }
        } catch (ClassCastException e) {
            // ignore
        }

        commit();
        return this;
    }

    public String getOpenid() {
        return mOpenid;
    }

    public String getUnionid() {
        return mUnionid;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public Map<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_OPENID, mOpenid);
        map.put(KEY_UNIONID, mUnionid);
        map.put(KEY_ACCESS_TOKEN, mAccessToken);
        map.put(KEY_REFRESH_TOKEN, mRefreshToken);
        map.put(KEY_EXPIRES_IN, String.valueOf(mExpiresIn));
        return map;
    }

    public boolean isAccessTokenAvailable() {
        return isAuthorized() && !(mExpiresIn - System.currentTimeMillis() <= 0L);
    }

    public boolean isAuthValid() {
        return isAuthorized() && !(mRtExpiresIn - System.currentTimeMillis() <= 0L);
    }

    public boolean isAuthorized() {
        return !TextUtils.isEmpty(getAccessToken());
    }

    public void delete() {
        mSharedPreferences.edit().clear().apply();
    }

    public void commit() {
        mSharedPreferences.edit()
                          .putString(KEY_OPENID, mOpenid)
                          .putString(KEY_UNIONID, mUnionid)
                          .putString(KEY_ACCESS_TOKEN, mAccessToken)
                          .putString(KEY_REFRESH_TOKEN, mRefreshToken)
                          .putLong(KEY_EXPIRES_IN, mExpiresIn)
                          .putLong(KEY_RT_EXPIRES_IN, mRtExpiresIn)
                          .apply();
    }
}
