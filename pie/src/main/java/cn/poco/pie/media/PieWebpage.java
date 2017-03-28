package cn.poco.pie.media;

import android.os.Parcel;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public class PieWebpage implements PieMedia  {

    @Override
    public PMediaType getType() {
        return PMediaType.WEB_PAGE;
    }

    protected PieImage thumbImage;

    public PieWebpage() {
        // 相信美好的事情即将发生
    }

    public PieWebpage(PieImage thumbImage) {
        this.thumbImage = thumbImage;
    }

    public PieImage getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(PieImage thumbImage) {
        this.thumbImage = thumbImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.thumbImage, flags);
    }

    protected PieWebpage(Parcel in) {
        this.thumbImage = in.readParcelable(PieImage.class.getClassLoader());
    }

    public static final Creator<PieWebpage> CREATOR = new Creator<PieWebpage>() {

        @Override
        public PieWebpage createFromParcel(Parcel source) {
            return new PieWebpage(source);
        }

        @Override
        public PieWebpage[] newArray(int size) {
            return new PieWebpage[size];
        }
    };
}
