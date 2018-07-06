package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Leo on 17/2/16.
 * 联系人信息，一个联系人对应多个号码,ID唯一
 */

public class ContactInfo implements Serializable {
    public long id;//非联系人则ID为-1
    public String contactName;//联系人才有，非联系人为空
    public String userAddName;//非联系人才有，联系人为空
    public String photoId;
    public String photoUri;
    public Bitmap photoIcon;
    public boolean isHavePhoto;
    public String country;
    public String lookupKey;//只有联系人才有，非联系人为空
    public String sortKey;//只有联系人才有，非联系人为空
    public boolean isContact;
    public int phoneCount;
    public List<NumberInfo> numberInfoList;
    public List<SMSInfo> smsInfoList;

    /**
     * 该处的通话记录集合是该联系人下所有号码的通话记录，与numberinfo中的通话记录不同
     * */
    public List<CallLogInfo> callLogInfoListForContact;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactInfo info = (ContactInfo) o;

        if (id != info.id) return false;
        if (isHavePhoto != info.isHavePhoto) return false;
        if (isContact != info.isContact) return false;
        if (phoneCount != info.phoneCount) return false;
        if (contactName != null ? !contactName.equals(info.contactName) : info.contactName != null)
            return false;
        if (userAddName != null ? !userAddName.equals(info.userAddName) : info.userAddName != null)
            return false;
        if (photoId != null ? !photoId.equals(info.photoId) : info.photoId != null) return false;
        if (photoUri != null ? !photoUri.equals(info.photoUri) : info.photoUri != null)
            return false;
        if (photoIcon != null ? !photoIcon.equals(info.photoIcon) : info.photoIcon != null)
            return false;
        if (country != null ? !country.equals(info.country) : info.country != null) return false;
        if (lookupKey != null ? !lookupKey.equals(info.lookupKey) : info.lookupKey != null)
            return false;
        if (sortKey != null ? !sortKey.equals(info.sortKey) : info.sortKey != null) return false;
        if (numberInfoList != null ? !numberInfoList.equals(info.numberInfoList) : info.numberInfoList != null)
            return false;
        if (smsInfoList != null ? !smsInfoList.equals(info.smsInfoList) : info.smsInfoList != null)
            return false;
        return callLogInfoListForContact != null ? callLogInfoListForContact.equals(info.callLogInfoListForContact) : info.callLogInfoListForContact == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (contactName != null ? contactName.hashCode() : 0);
        result = 31 * result + (userAddName != null ? userAddName.hashCode() : 0);
        result = 31 * result + (photoId != null ? photoId.hashCode() : 0);
        result = 31 * result + (photoUri != null ? photoUri.hashCode() : 0);
        result = 31 * result + (photoIcon != null ? photoIcon.hashCode() : 0);
        result = 31 * result + (isHavePhoto ? 1 : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (lookupKey != null ? lookupKey.hashCode() : 0);
        result = 31 * result + (sortKey != null ? sortKey.hashCode() : 0);
        result = 31 * result + (isContact ? 1 : 0);
        result = 31 * result + phoneCount;
        result = 31 * result + (numberInfoList != null ? numberInfoList.hashCode() : 0);
        result = 31 * result + (smsInfoList != null ? smsInfoList.hashCode() : 0);
        result = 31 * result + (callLogInfoListForContact != null ? callLogInfoListForContact.hashCode() : 0);
        return result;
    }
}
