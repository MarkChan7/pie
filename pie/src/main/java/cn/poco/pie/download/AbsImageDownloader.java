package cn.poco.pie.download;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

import cn.poco.pie.utils.FileNameGenerator;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public abstract class AbsImageDownloader implements ImageDownloader {

    @Override
    public void download(Context context, String imageUrl, String targetDirPath, OnImageDownloadListener listener) {
        if (TextUtils.isEmpty(imageUrl) || TextUtils.isEmpty(targetDirPath)) {
            if (listener != null) {
                listener.onFailed(imageUrl);
            }
            return;
        }

        String filePath = createFileIfNeed(imageUrl, targetDirPath);
        if (TextUtils.isEmpty(filePath)) {
            if (listener != null) {
                listener.onFailed(imageUrl);
            }
            return;
        }

        File targetFile = new File(filePath);
        if (targetFile.exists()) {
            if (listener != null) {
                listener.onSuccess(filePath);
            }
        }

        downloadDirectly(context, imageUrl, filePath, listener);
    }

    protected abstract void downloadDirectly(Context context, String imageUrl, String filePath,
                                             OnImageDownloadListener listener);

    private String createFileIfNeed(String imageUrl, String targetDirPath) {
        File parent = new File(targetDirPath);
        String filName = FileNameGenerator.hashCode(imageUrl);
        File file = new File(parent, filName);
        if (file.exists() && file.isFile()) {
            return file.getAbsolutePath();
        }

        boolean mk = true;
        if (!parent.exists() || (parent.exists() && !parent.isDirectory())) {
            mk = parent.mkdirs();
        }

        if (mk) {
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }
}
