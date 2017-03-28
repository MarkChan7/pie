package cn.poco.pie.media;

import android.os.Parcel;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public class PieVideo implements PieMedia {

    @Override
    public PMediaType getType() {
        return PMediaType.VIDEO;
    }

    private PieImage thumbImage;
    // 源地址，比如http://xxx.com/..../hello_world.mp4
    private String srcUrl;
    private String h5Url;
    private String desc;

    public PieVideo() {
        // 相信美好的事情即将发生
    }

    public PieVideo(PieImage thumbImage, String h5Url) {
        this.thumbImage = thumbImage;
        this.h5Url = h5Url;
    }

    public PieVideo(PieImage thumbImage, String h5Url, String desc) {
        this.thumbImage = thumbImage;
        this.h5Url = h5Url;
        this.desc = desc;
    }

    public PieImage getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(PieImage thumbImage) {
        this.thumbImage = thumbImage;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.thumbImage, flags);
        dest.writeString(this.srcUrl);
        dest.writeString(this.h5Url);
        dest.writeString(this.desc);
    }

    protected PieVideo(Parcel in) {
        this.thumbImage = in.readParcelable(PieImage.class.getClassLoader());
        this.srcUrl = in.readString();
        this.h5Url = in.readString();
        this.desc = in.readString();
    }

    public static final Creator<PieVideo> CREATOR = new Creator<PieVideo>() {

        @Override
        public PieVideo createFromParcel(Parcel source) {
            return new PieVideo(source);
        }

        @Override
        public PieVideo[] newArray(int size) {
            return new PieVideo[size];
        }
    };
}
