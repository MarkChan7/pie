package cn.poco.pie.download;

import android.content.Context;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public interface ImageDownloader {

    void download(Context context, String imageUrl, String targetDirPath, OnImageDownloadListener listener);

    interface OnImageDownloadListener {

        void onSuccess(String filePath);

        void onFailed(String imgUrl);
    }
}
