package cn.poco.pie.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import cn.poco.pie.Pie;
import cn.poco.pie.SocialNetwork;

/**
 * Date  : 2016/11/7
 * Author: MarkChan
 * Desc  :
 */
public abstract class WechatEntryActivity extends Activity implements IWXAPIEventHandler {

    private WechatHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = (WechatHandler) Pie.get(getApplicationContext()).getHandler(SocialNetwork.WECHAT);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mHandler = (WechatHandler) Pie.get(getApplicationContext()).getHandler(SocialNetwork.WECHAT);
        handleIntent(intent);
    }

    protected void handleIntent(Intent intent) {
        mHandler.getApi().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (mHandler != null && baseResp != null) {
            mHandler.onResp(baseResp);
        }
        finish();
    }
}
