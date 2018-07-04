package blocker.call.wallpaper.screen.caller.ringtones.callercolor.update;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;

/**
 * Created by Administrator on 2015/8/15.
 */
public class HttpUtilUpdate {
    public static final String UPDATE_URL = "http://updater.elitegames.mobi/api/check_update";
    public static final String VALIDATION = ConstantUtils.KEY_HTTP;
    public static final String KEY_DATA  = "data";
    public static final String KEY_VALIDATION = "v";

    public static JSONObject doPost(String stringURl, Map param) throws IOException, JSONException {
        // 请求参数json
        String jsonData = changeToJsonString(param);
        // 加上延值的验证string
        StringBuffer sb = new StringBuffer(jsonData);
        sb.append(VALIDATION);
        String encrypMd5 = MD5(sb.toString());

        // 请求服务器
        URL postUrl = new URL(stringURl);
        HttpURLConnection connection =  (HttpURLConnection) postUrl.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);

        connection.connect();
        DataOutputStream out = new DataOutputStream(connection
                .getOutputStream());
        StringBuffer content = new StringBuffer("");
        content.append(KEY_DATA).append("=").append(URLEncoder.encode(jsonData, "utf-8"));
        content.append("&").append(KEY_VALIDATION).append("=").append(encrypMd5.toLowerCase());
        out.writeBytes(content.toString());
        out.flush();
        out.close();


        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        String line = "";
        StringBuffer response = new StringBuffer("");
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();

        return new JSONObject(response.toString());
    }

    public static String changeToJsonString(Map param) {
        JSONObject obj = new JSONObject();
        try{
            Set set = param.keySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                obj.put(key, param.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    public static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
