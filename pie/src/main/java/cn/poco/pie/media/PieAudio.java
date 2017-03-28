package cn.poco.pie.media;

import android.os.Parcel;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public class PieAudio implements PieMedia  {

    @Override
    public PMediaType getType() {
        return PMediaType.AUDIO;
    }

    private PieImage thumbImage;
    private String srcUrl;
    private String h5Url;
    private String desc;

    public PieAudio() {
        // 相信美好的事情即将发生
    }

    public PieAudio(String srcUrl, String h5Url, String desc) {
        this.srcUrl = srcUrl;
        this.h5Url = h5Url;
        this.desc = desc;
    }

    public PieAudio(PieImage thumbImage, String srcUrl, String desc) {
        this.thumbImage = thumbImage;
        this.srcUrl = srcUrl;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
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

    protected PieAudio(Parcel in) {
        this.thumbImage = in.readParcelable(PieImage.class.getClassLoader());
        this.srcUrl = in.readString();
        this.h5Url = in.readString();
        this.desc = in.readString();
    }

    public static final Creator<PieAudio> CREATOR = new Creator<PieAudio>() {

        @Override
        public PieAudio createFromParcel(Parcel source) {
            return new PieAudio(source);
        }

        @Override
        public PieAudio[] newArray(int size) {
            return new PieAudio[size];
        }
    };
}

