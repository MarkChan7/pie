package cn.poco.pie.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Date  : 2016/11/14
 * Author: MarkChan
 * Desc  :
 */
public class SerializableMap implements Parcelable {

    private Map<String, String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.map.size());
        for (Map.Entry<String, String> entry : this.map.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    public SerializableMap() {
    }

    protected SerializableMap(Parcel in) {
        int mapSize = in.readInt();
        this.map = new HashMap<String, String>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.map.put(key, value);
        }
    }

    public static final Creator<SerializableMap> CREATOR = new Creator<SerializableMap>() {

        @Override
        public SerializableMap createFromParcel(Parcel source) {
            return new SerializableMap(source);
        }

        @Override
        public SerializableMap[] newArray(int size) {
            return new SerializableMap[size];
        }
    };
}