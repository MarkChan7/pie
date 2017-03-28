package cn.poco.pie.handler;

import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import cn.poco.pie.PieContent;
import cn.poco.pie.listener.PieShareListener;
import cn.poco.pie.R;

/**
 * Date  : 2016/11/10
 * Author: MarkChan
 * Desc  :
 */
public class PieCopyHandler extends PieHandler {

    @Override
    public void share(PieContent content, PieShareListener listener) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        String text = content.getText();
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ((android.text.ClipboardManager) clip).setText(text);
        Toast.makeText(context, R.string.pie_share_copy_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isDisposable() {
        return true;
    }
}
