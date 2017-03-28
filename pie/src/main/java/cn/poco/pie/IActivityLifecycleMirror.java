package cn.poco.pie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Date  : 2016/11/10
 * Author: MarkChan
 * Desc  :
 */
public interface IActivityLifecycleMirror {

    void onActivityCreated(Activity activity, Bundle savedInstanceState);

    void onActivityNewIntent(Activity activity, Intent intent);

    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

    void onActivityDestroy();
}
