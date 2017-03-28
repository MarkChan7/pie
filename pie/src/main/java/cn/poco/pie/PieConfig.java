package cn.poco.pie;

import android.content.Context;
import android.text.TextUtils;

import cn.poco.pie.download.DefaultImageDownloader;
import cn.poco.pie.download.ImageDownloader;
import cn.poco.pie.utils.StorageUtils;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public final class PieConfig {

    private final String mImageCacheDirPath;

    private final int mDefaultShareImageResId;

    private final ImageDownloader mImageDownloader;

    private final String mAppNameForQq;

    private PieConfig(Builder builder) {
        mImageCacheDirPath = builder.imageCacheDirPath;
        mDefaultShareImageResId = builder.defaultShareImageResId;
        mImageDownloader = builder.imageDownloader;
        mAppNameForQq = builder.appNameForQq;
    }

    public String getImageCacheDirPath() {
        return mImageCacheDirPath;
    }

    public int getDefaultShareImageResId() {
        return mDefaultShareImageResId;
    }

    public ImageDownloader getImageDownloader() {
        return mImageDownloader;
    }

    public String getAppNameForQq() {
        return mAppNameForQq;
    }

    public static class Builder {

        private static final String DEFAULT_IMAGE_CACHE_DIR_PATH = "Pie/cache";

        private Context context;

        private String imageCacheDirPath;

        private int defaultShareImageResId = R.drawable.pie_default_share_image;

        private ImageDownloader imageDownloader;

        private String appNameForQq;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder imageCacheDirPath(String path) {
            imageCacheDirPath = path;
            return this;
        }

        public Builder defaultShareImageResId(int resId) {
            defaultShareImageResId = resId;
            return this;
        }

        public Builder imageDownloader(ImageDownloader imageDownloader) {
            this.imageDownloader = imageDownloader;
            return this;
        }

        public Builder appNameForQq(String appNameForQq) {
            this.appNameForQq = appNameForQq;
            return this;
        }

        public PieConfig build() {
            checkFields();
            return new PieConfig(this);
        }

        private void checkFields() {
            if (TextUtils.isEmpty(imageCacheDirPath)) {
                imageCacheDirPath = DEFAULT_IMAGE_CACHE_DIR_PATH;
            }
            imageCacheDirPath = StorageUtils.getOwnCacheDirectory(context, imageCacheDirPath, true).getAbsolutePath();

            if (TextUtils.isEmpty(imageCacheDirPath)) {
                imageCacheDirPath = DEFAULT_IMAGE_CACHE_DIR_PATH;
            }

            if (imageDownloader == null) {
                imageDownloader = new DefaultImageDownloader();
            }
        }
    }
}
