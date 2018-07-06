package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * Created by admin on 2017/1/11.
 * 号码信息，号码唯一，ID可能相同
 */

public class NumberInfo implements Serializable {
    public long id;
    public String number;
    public int numberType;//联系人信息中获取的信息：住宅，公司等 // 在new call 悬窗中为来电类型; 1: 呼入, 2: 呼出
    public String location;
    public boolean isBlack;
    public boolean isWhite;
    public String countryCode; //国家区号
    public String country;
    public String photoId;
    public boolean isSpam;
    public boolean isHavePhoto;
    public Bitmap photoIcon;
    public String sortKey;
    public String name;
    public String version;
    public String operators;
    public String lookupKey;

    public List<SMSInfo> smsInfoList;

    /**
     * 该处的通话记录集合是该号码下的所有通话记录，与contanctInfo中的通话记录不同
     */
    public List<CallLogInfo> callLogInfoListForNumber;

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NumberInfo) {
            NumberInfo info = (NumberInfo) obj;
            if (this.number != null && info.number != null && this.number.replace(" ", "") != null && info.number.replace(" ", "") != null) {
                return this.number.replace(" ", "").equals(info.number.replace(" ", ""));
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return number.replace(" ", "").hashCode();
    }
}
