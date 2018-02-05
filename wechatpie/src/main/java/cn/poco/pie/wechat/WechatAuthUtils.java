package cn.poco.pie.wechat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Date  : 2016/11/9
 * Author: MarkChan
 * Desc  :
 */
public class WechatAuthUtils {

    public static String request(String urlStr) {
        String result = "";

        try {
            URL e = new URL(urlStr);
            URLConnection conn = e.openConnection();
            if (conn == null) {
                return result;
            } else {
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                return inputStream == null ? result : convertStreamToString(inputStream);
            }
        } catch (Exception e) {
            return result;
        }
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
