package cn.poco.pie.utils;

import android.os.Bundle;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Date  : 2016/11/7
 * Author: MarkChan
 * Desc  :
 */
public class PieUtils {

    public static Map<String, String> jsonToMap(String jsonStr) {
        HashMap<String, String> map = new HashMap<>();

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            Iterator iterator = jsonObj.keys();

            String key;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                map.put(key, jsonObj.get(key) + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static Map<String, String> bundleToMap(Bundle bundle) {
        if (bundle != null && !bundle.isEmpty()) {
            Set keySet = bundle.keySet();
            Map<String, String> map = new HashMap<>();

            String key;
            for (Iterator iterator = keySet.iterator(); iterator.hasNext(); map.put(key, bundle.getString(key))) {
                key = (String) iterator.next();
                if (key.equals("com.sina.weibo.intent.extra.USER_ICON")) {
                    map.put("icon_url", bundle.getString(key));
                }
            }

            return map;
        } else {
            return null;
        }
    }
}
