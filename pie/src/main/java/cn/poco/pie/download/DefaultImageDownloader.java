package cn.poco.pie.download;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.poco.pie.Pie;
import cn.poco.pie.utils.FileUtils;
import cn.poco.pie.utils.IoUtils;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public class DefaultImageDownloader extends AbsImageDownloader {

    @Override
    protected void downloadDirectly(Context context, String imageUrl, String filePath,
                                    OnImageDownloadListener listener) {
        new Task(context, imageUrl, filePath, listener).start();
    }

    private class Task extends Thread {

        private static final String TEMP_EXTENSION = ".temp";

        private Context mContext;
        private String mImageUrl;
        private String mFilePath;
        private OnImageDownloadListener mListener;

        private Task(Context context, String imageUrl, String filePath, OnImageDownloadListener listener) {
            mContext = context;
            mImageUrl = imageUrl;
            mFilePath = filePath;
            mListener = listener;
        }

        @Override
        public void run() {
            File tmpFile = new File(mFilePath + TEMP_EXTENSION);

            HttpURLConnection conn;
            try {
                URL url = new URL(mImageUrl);
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                onPostExecute(null);
                return;
            }

            try {
                conn.setConnectTimeout(5 * 1000);
                conn.setReadTimeout(10 * 1000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Referer", mImageUrl);
                conn.setRequestProperty("User-Agent",
                                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
                conn.setInstanceFollowRedirects(true);
                conn.connect();
                int code = conn.getResponseCode();
                OutputStream out = null;
                InputStream in = null;

                try {
                    if (code == HttpURLConnection.HTTP_OK) {
                        out = new BufferedOutputStream(new FileOutputStream(tmpFile));
                        in = conn.getInputStream();
                        IoUtils.copyLarge(in, out);
                    } else {
                        mFilePath = null;
                    }
                } catch (IOException e) {
                    mFilePath = null;
                } finally {
                    IoUtils.closeQuietly(out);
                    IoUtils.closeQuietly(in);
                }
                if (mFilePath != null) {
                    File file = new File(mFilePath);
                    if (!tmpFile.renameTo(file)) {
                        FileUtils.copyFile(tmpFile, file);
                        tmpFile.delete();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                mFilePath = null;
            } finally {
                conn.disconnect();
            }

            onPostExecute(mFilePath);
        }

        private void onPostExecute(final String filePath) {
            Pie.get(mContext)
               .getDroid()
               .execute(new Runnable() {

                   @Override
                   public void run() {
                       if (!TextUtils.isEmpty(filePath)) {
                           onDownloadSuccess(filePath);
                       } else {
                           onDownloadFailed();
                       }
                   }
               });
        }

        private void onDownloadSuccess(String filePath) {
            if (mListener != null) {
                mListener.onSuccess(filePath);
            }
        }

        private void onDownloadFailed() {
            if (mListener != null) {
                mListener.onFailed(mImageUrl);
            }
        }
    }
}
