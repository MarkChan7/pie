package cn.poco.pie.media;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.text.TextUtils;

import java.io.File;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public class PieImage implements PieMedia {

    @Override
    public PMediaType getType() {
        return PMediaType.IMAGE;
    }

    private static final int INVALID = -1;

    private File localFile;
    private Bitmap bitmap;
    private String netImageUrl;
    private int resId = INVALID;

    public PieImage(File localFile) {
        this.localFile = localFile;
    }

    public PieImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public PieImage(String netImageUrl) {
        this.netImageUrl = netImageUrl;
    }

    public PieImage(int resId) {
        this.resId = resId;
    }

    private void setConverter(Object src) {

    }

    public File getLocalFile() {
        return localFile;
    }

    public String getLocalPath() {
        return localFile == null ? null : localFile.exists() ? localFile.getAbsolutePath() : null;
    }

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
        resId = INVALID;
        netImageUrl = null;
        bitmap = null;
    }

    public String getNetImageUrl() {
        return netImageUrl;
    }

    public void setNetImageUrl(String imageUrl) {
        netImageUrl = imageUrl;
        bitmap = null;
        resId = INVALID;
        localFile = null;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
        localFile = null;
        netImageUrl = null;
        bitmap = null;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        resId = INVALID;
        localFile = null;
        netImageUrl = null;
    }

    public boolean isNetImage() {
        return getImageType() == ImageType.NET;
    }

    public boolean isLocalImage() {
        return getImageType() == ImageType.LOCAL;
    }

    public boolean isBitmapImage() {
        return getImageType() == ImageType.BITMAP;
    }

    public boolean isResImage() {
        return getImageType() == ImageType.RES;
    }

    public boolean isUnknownImage() {
        return getImageType() == ImageType.UNKNOWN;
    }

    public ImageType getImageType() {
        if (!TextUtils.isEmpty(netImageUrl)) {
            return ImageType.NET;
        } else if (localFile != null && localFile.exists()) {
            return ImageType.LOCAL;
        } else if (resId != INVALID) {
            return ImageType.RES;
        } else if (bitmap != null && !bitmap.isRecycled()) {
            return ImageType.BITMAP;
        } else {
            return ImageType.UNKNOWN;
        }
    }

    public enum ImageType {
        LOCAL, NET, BITMAP, RES, BINARY, UNKNOWN
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.localFile);
        dest.writeParcelable(this.bitmap, flags);
        dest.writeString(this.netImageUrl);
        dest.writeInt(this.resId);
    }

    protected PieImage(Parcel in) {
        this.localFile = (File) in.readSerializable();
        this.bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.netImageUrl = in.readString();
        this.resId = in.readInt();
    }

    public static final Creator<PieImage> CREATOR = new Creator<PieImage>() {

        @Override
        public PieImage createFromParcel(Parcel source) {
            return new PieImage(source);
        }

        @Override
        public PieImage[] newArray(int size) {
            return new PieImage[size];
        }
    };
}
