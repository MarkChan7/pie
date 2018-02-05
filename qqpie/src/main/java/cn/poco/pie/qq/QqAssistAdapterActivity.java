package cn.poco.pie.qq;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.connect.common.AssistActivity;

/**
 * Date  : 2016/11/10
 * Author: MarkChan
 * Desc  :
 */
public class QqAssistAdapterActivity extends AssistActivity {

    private boolean mRestartFromQqSdk;
    private boolean mActivityResultCalled;
    private boolean mOnNewIntentCalled;

    @Override
    protected void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            if (bundle != null) {
                mRestartFromQqSdk = bundle.getBoolean("RESTART_FLAG");
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mOnNewIntentCalled || mActivityResultCalled) {
            return;
        }

        if (mRestartFromQqSdk && !isFinishing()) {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mOnNewIntentCalled = true;
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int i, int i1, Intent intent) {
        mActivityResultCalled = true;
        super.onActivityResult(i, i1, intent);
    }
}
