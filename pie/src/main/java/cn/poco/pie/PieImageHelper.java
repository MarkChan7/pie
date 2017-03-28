package cn.poco.pie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import cn.poco.pie.download.ImageDownloader;
import cn.poco.pie.media.PieAudio;
import cn.poco.pie.media.PieImage;
import cn.poco.pie.media.PieMedia;
import cn.poco.pie.media.PieVideo;
import cn.poco.pie.media.PieWebpage;
import cn.poco.pie.utils.BitmapUtils;
import cn.poco.pie.utils.FileUtils;

public class PieImageHelper {

    private static final int THUMB_RESOLUTION_SIZE = 150;
    private static final int THUMB_MAX_SIZE = 30 * 1024;
    private static final int BITMAP_SAVE_THRESHOLD = 32 * 1024;

    private Context mContext;
    private PieConfig mConfig;

    public PieImageHelper(Context context, PieConfig config) {
        mContext = context.getApplicationContext();
        mConfig = config;
    }

    /**
     * 如果Bitmap/Res体积太大, 保存到本地
     */
    public PieImage saveBitmapToExternalIfNeed(PieContent content) {
        return saveBitmapToExternalIfNeed(getShareImage(content));
    }

    public PieImage saveBitmapToExternalIfNeed(PieImage pieImage) {
        if (pieImage == null) {
            return null;
        }

        if (pieImage.isBitmapImage()) {
            if (pieImage.getBitmap().getByteCount() > BITMAP_SAVE_THRESHOLD) {
                File file = BitmapUtils.saveBitmapToExternal(pieImage.getBitmap(), mConfig.getImageCacheDirPath());
                if (file != null && file.exists()) {
                    pieImage.setLocalFile(file);
                }
            }
        } else if (pieImage.isResImage()) {
            Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), pieImage.getResId());
            if (bmp.getByteCount() > BITMAP_SAVE_THRESHOLD) {
                File file = BitmapUtils.saveBitmapToExternal(bmp, mConfig.getImageCacheDirPath());
                if (file != null && file.exists()) {
                    pieImage.setLocalFile(file);
                    bmp.recycle();
                }
            }
        }
        return pieImage;
    }

    public void copyImageToCacheFileDirIfNeed(PieContent content) {
        copyImageToCacheFileDirIfNeed(getShareImage(content));
    }

    public void copyImageToCacheFileDirIfNeed(PieImage PieImage) {
        if (PieImage == null) {
            return;
        }

        File localFile = PieImage.getLocalFile();
        if (localFile == null || !localFile.exists()) {
            return;
        }

        String localFilePath = localFile.getAbsolutePath();
        if (!localFilePath.startsWith(mContext.getCacheDir().getParentFile().getAbsolutePath())
                && localFilePath.startsWith(mConfig.getImageCacheDirPath())) {
            return;
        }

        File targetFile = copyFile(localFile, mConfig.getImageCacheDirPath());
        if (targetFile != null && targetFile.exists()) {
            PieImage.setLocalFile(targetFile);
        }
    }

    private File copyFile(File srcFile, String targetCacheDirPath) {
        if (srcFile == null || !srcFile.exists()) {
            return null;
        }

        File targetFileDir = new File(targetCacheDirPath);
        File targetFile = new File(targetFileDir, srcFile.getName());

        if (!targetFileDir.exists() && !targetFileDir.mkdirs()) {
            return null;
        }

        try {
            FileUtils.copyFile(srcFile, targetFile);
            return targetFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 缩略图(32kb限制), 注意: 要在工作线程调用
     */
    public byte[] buildThumbData(final PieImage pieImage) {
        return buildThumbData(pieImage, THUMB_MAX_SIZE, THUMB_RESOLUTION_SIZE, THUMB_RESOLUTION_SIZE, false);
    }

    public byte[] buildThumbData(final PieImage pieImage, int maxSize, int widthMax, int heightMax, boolean isFixSize) {
        if (pieImage == null) {
            return new byte[0];
        }

        boolean isRecycleSrcBitmap = true;
        Bitmap bmp = null;

        if (pieImage.isNetImage()) {
            bmp = BitmapUtils.decodeUrl(pieImage.getNetImageUrl());
        } else if (pieImage.isLocalImage()) {
            bmp = BitmapUtils.decodeFile(pieImage.getLocalPath(), THUMB_RESOLUTION_SIZE, THUMB_RESOLUTION_SIZE);
        } else if (pieImage.isResImage()) {
            bmp = BitmapFactory.decodeResource(mContext.getResources(), pieImage.getResId());
        } else if (pieImage.isBitmapImage()) {
            isRecycleSrcBitmap = false;
            bmp = pieImage.getBitmap();
        }

        if (bmp != null && !bmp.isRecycled()) {
            if (!isFixSize) {
                int bmpWidth = bmp.getWidth();
                int bmpHeight = bmp.getHeight();
                double scale = BitmapUtils.getScale(widthMax, heightMax, bmpWidth, bmpHeight);
                widthMax = (int) (bmpWidth / scale);
                heightMax = (int) (bmpHeight / scale);
            }

            final Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, widthMax, heightMax, true);
            if (isRecycleSrcBitmap && thumbBmp != bmp) {
                bmp.recycle();
            }
            byte[] tempArr = BitmapUtils.bmpToByteArray(thumbBmp, maxSize, true);
            return tempArr == null ? new byte[0] : tempArr;
        }

        return new byte[0];
    }

    public void downloadImageIfNeed(final PieContent content, Callback callback) {
        downloadImageIfNeed(getShareImage(content), callback);
    }

    public void downloadImageIfNeed(final PieImage image, final Callback callback) {
        if (image == null || !image.isNetImage()) {
            if (callback != null) {
                callback.onSuccess();
            }
            return;
        }

        String imageUrl = image.getNetImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            mConfig.getImageDownloader()
                   .download(mContext, image.getNetImageUrl(), mConfig.getImageCacheDirPath(),
                             new ImageDownloader.OnImageDownloadListener() {

                                 @Override
                                 public void onSuccess(String filePath) {
                                     image.setLocalFile(new File(filePath));
                                     copyImageToCacheFileDirIfNeed(image);
                                     if (callback != null) {
                                         callback.onSuccess();
                                     }
                                 }

                                 @Override
                                 public void onFailed(String imageUrl) {
                                     if (callback != null) {
                                         callback.onFailed();
                                     }
                                 }
                             });
        } else {
            if (callback != null) {
                callback.onFailed();
            }
        }
    }

    public PieImage getShareImage(PieContent content) {
        if (content == null || content.getShareType() == PieMedia.PMediaType.TEXT) {
            return null;
        }

        PieImage image;
        PieMedia media = content.getMedia();
        switch (content.getShareType()) {
            case AUDIO:
                image = ((PieAudio) media).getThumbImage();
                break;
            case VIDEO:
                image = ((PieVideo) media).getThumbImage();
                break;
            case WEB_PAGE:
                image = ((PieWebpage) media).getThumbImage();
                break;
            case IMAGE:
            default:
                image = (PieImage) media;
                break;
        }
        return image;
    }

    public interface Callback {

        void onSuccess();

        void onFailed();
    }
}
