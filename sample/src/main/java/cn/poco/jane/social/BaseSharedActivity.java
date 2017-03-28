package cn.poco.jane.social;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cn.poco.jane.R;
import cn.poco.pie.Pie;
import cn.poco.pie.PieStatusCode;

/**
 * Date  : 2016/11/10
 * Author: MarkChan
 * Desc  :
 */
public abstract class BaseSharedActivity extends AppCompatActivity implements TpShareHelper.Callback {

    protected TpShareHelper mShare;

    public void startShare(@Nullable View anchor) {
        startShare(anchor, false);
    }

    public void startShare(@Nullable View anchor, boolean isWindowFullScreen) {
        if (mShare == null) {
            mShare = TpShareHelper.instance(this, this);
        }
        if (anchor == null) {
            mShare.showDialog();
        } else {
            if (isWindowFullScreen) {
                mShare.showBottomFsPopupWindows(anchor);
            } else {
                mShare.showBottomFsPopupWindows(anchor);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Pie.get(this).onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (mShare != null) {
            mShare.reset(); // reset held instance
            mShare = null;
        }
        super.onDestroy();
    }

    @Override
    public void onShareStart(TpShareHelper helper) {
        // 相信美好的事情即将发生
    }

    @Override
    public void onShareComplete(TpShareHelper helper, int code) {
        if (code == PieStatusCode.SUCCESS) {
            Toast.makeText(this, R.string.pie_share_success, Toast.LENGTH_SHORT).show();
        } else if (code == PieStatusCode.ERROR) {
            Toast.makeText(this, R.string.pie_share_error, Toast.LENGTH_SHORT).show();
        } else if (code == PieStatusCode.CANCEL) {
            Toast.makeText(this, R.string.pie_share_cancel, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDismiss(TpShareHelper helper) {
        // 相信美好的事情即将发生
    }
}
