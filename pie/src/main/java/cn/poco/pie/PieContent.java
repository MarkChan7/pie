package cn.poco.pie;

import android.os.Parcel;
import android.os.Parcelable;

import cn.poco.pie.media.PieMedia;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public class PieContent implements Parcelable {

    private String title;
    private String text;
    private String targetUrl;

    private PieMedia media;

    public PieMedia.PMediaType getShareType() {
        if (media == null) {
            return PieMedia.PMediaType.TEXT;
        } else {
            return media.getType();
        }
    }

    public PieContent() {
        // 相信美好的事情即将发生
    }

    public PieContent(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public PieContent(String title, String text, String targetUrl) {
        this.title = title;
        this.text = text;
        this.targetUrl = targetUrl;
    }

    public PieContent(String title, String text, String targetUrl, PieMedia media) {
        this.title = title;
        this.text = text;
        this.targetUrl = targetUrl;
        this.media = media;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public PieMedia getMedia() {
        return media;
    }

    public void setMedia(PieMedia media) {
        this.media = media;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeString(this.targetUrl);
        dest.writeParcelable(this.media, flags);
    }

    protected PieContent(Parcel in) {
        this.title = in.readString();
        this.text = in.readString();
        this.targetUrl = in.readString();
        this.media = in.readParcelable(PieMedia.class.getClassLoader());
    }

    public static final Creator<PieContent> CREATOR = new Creator<PieContent>() {

        @Override
        public PieContent createFromParcel(Parcel source) {
            return new PieContent(source);
        }

        @Override
        public PieContent[] newArray(int size) {
            return new PieContent[size];
        }
    };
}
