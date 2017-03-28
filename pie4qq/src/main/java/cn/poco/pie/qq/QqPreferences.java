package cn.poco.pie.qq;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Date  : 2016/11/14
 * Author: MarkChan
 * Desc  :
 */
public class QqPreferences {

    private static final String KEY_UID = "uid";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_EXPIRES_IN = "expires_in";

    private String mAccessToken;
    private static String mExpiresIn;
    private String mUID;

    private SharedPreferences mSP;

    public QqPreferences(Context context, String name) {
        mSP = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        mUID = mSP.getString(KEY_UID, null);
        mAccessToken = mSP.getString(KEY_ACCESS_TOKEN, null);
        mExpiresIn = mSP.getString(KEY_EXPIRES_IN, null);
    }

    public boolean isAuthValid() {
        return mAccessToken != null;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public static String getExpiresIn() {
        return mExpiresIn;
    }

    public String getUid() {
        return mUID;
    }

    public QqPreferences setAuthData(Bundle bundle) {
        mUID = bundle.getString(KEY_UID);
        mAccessToken = bundle.getString(KEY_ACCESS_TOKEN);
        mExpiresIn = bundle.getString(KEY_EXPIRES_IN);
        return this;
    }

    public void commit() {
        mSP.edit()
                .putString(KEY_UID, mUID)
                .putString(KEY_ACCESS_TOKEN, mAccessToken)
                .putString(KEY_EXPIRES_IN, mExpiresIn)
                .apply();
    }

    public void delete() {
        mSP.edit().clear().apply();
    }
}
