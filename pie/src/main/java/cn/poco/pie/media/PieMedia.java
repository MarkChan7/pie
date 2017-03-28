package cn.poco.pie.media;

import android.os.Parcelable;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public interface PieMedia extends Parcelable {

    enum PMediaType {
        // 仅用于PieContent的分享类型标示, 当PieContent.media == null 时, 将其设置文本类型
        TEXT,
        IMAGE, AUDIO, VIDEO, WEB_PAGE
    }

    PMediaType getType();
}
