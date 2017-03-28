package cn.poco.pie.weibo;

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
public class WeiboPreferences {

    public static final String KEY_UID = "uid";
    public static final String KEY_ACCESS_KEY = "access_key";
    public static final String KEY_ACCESS_SECRET = "access_secret";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_FOLLOW = "isfollow";

    private String mAccessKey;
    private String mAccessSecret;
    private String mUID;
    private long mExpiresIn;
    private String mAccessToken;
    private String mRefreshToken;
    private boolean mFollow;
    private SharedPreferences mSharedPreferences;

    public WeiboPreferences(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        mUID = mSharedPreferences.getString(KEY_UID, null);
        mAccessKey = mSharedPreferences.getString(KEY_ACCESS_KEY, null);
        mAccessSecret = mSharedPreferences.getString(KEY_ACCESS_SECRET, null);
        mAccessToken = mSharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        mRefreshToken = mSharedPreferences.getString(KEY_REFRESH_TOKEN, null);
        mExpiresIn = mSharedPreferences.getLong(KEY_EXPIRES_IN, 0L);
        mFollow = mSharedPreferences.getBoolean(KEY_FOLLOW, false);
    }

    public WeiboPreferences setAuthData(Map<String, String> data) {
        mAccessKey = data.get("access_key");
        mAccessSecret = data.get("access_secret");
        mAccessToken = data.get("access_token");
        mRefreshToken = data.get("refresh_token");
        mUID = data.get("uid");
        if (!TextUtils.isEmpty(data.get("expires_in"))) {
            mExpiresIn = Long.valueOf(data.get("expires_in")) * 1000L + System.currentTimeMillis();
        }

        return this;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public long getExpiresIn() {
        return mExpiresIn;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public String getUID() {
        return mUID;
    }

    public boolean isFollow() {
        return mFollow;
    }

    public void setFollow(boolean follow) {
        mSharedPreferences.edit().putBoolean("mFollow", follow).apply();
    }///

    public WeiboPreferences setAuthData(Bundle bundle) {
        mAccessToken = bundle.getString("access_token");
        mRefreshToken = bundle.getString("refresh_token");
        mUID = bundle.getString("uid");
        if (!TextUtils.isEmpty(bundle.getString("expires_in"))) {
            mExpiresIn = Long.valueOf(bundle.getString("expires_in")) * 1000L + System.currentTimeMillis();
        }

        return this;
    }

    public Map<String, String> getAuthData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("access_key", mAccessKey);
        map.put("access_secret", mAccessSecret);
        map.put("uid", mUID);
        map.put("expires_in", String.valueOf(mExpiresIn));
        return map;
    }

    public boolean isAuthorized() {
        return !TextUtils.isEmpty(mAccessToken);
    }

    public boolean isAuthValid() {
        boolean isAuthorized = isAuthorized();
        boolean isExpired = mExpiresIn - System.currentTimeMillis() <= 0L;
        return isAuthorized && !isExpired;
    }

    public void commit() {
        mSharedPreferences.edit()
                          .putString("access_key", mAccessKey)
                          .putString("access_secret", mAccessSecret)
                          .putString("access_token", mAccessToken)
                          .putString("refresh_token", mRefreshToken)
                          .putString("uid", mUID)
                          .putLong("expires_in", mExpiresIn)
                          .apply();
    }

    public void delete() {
        mAccessKey = null;
        mAccessSecret = null;
        mAccessToken = null;
        mUID = null;
        mExpiresIn = 0L;
        mSharedPreferences.edit().clear().apply();
    }
}
