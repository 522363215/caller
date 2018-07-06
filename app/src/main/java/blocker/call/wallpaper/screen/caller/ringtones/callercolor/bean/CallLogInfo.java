package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by zhq on 2017/1/5.
 * 通话记录
 */

public class CallLogInfo implements Serializable {
    public long id;
    public String callName;//联系人名字
    public String callIconId;//联系人头像Id
    public Bitmap callIcon;//联系人头像
    //    public boolean isHaveCallIcon;//判断是否有联系人头像
    public String callNumber;//号码格式:从系统calllog取出来的号码，号码类型不定
    //    public String normalizedNumber; // 号码格式: +8613602739480;
    public String callNumberType;//电话号码的类型：住宅，手机，公司等
    public String callDate;//通话日期(年月日)
    public String callDateHm;//通话日期(小时分)
    public long callDuration;//通话时长
    public int callType;//通话类型：1.未接，2.来电，3.拨出，4.拒接
    public String callLoction;//号码归属地
    public String callTag;//号码标签
    public long callTagCount;//标签的数量
    public int isSpamByUser;
    public long date;//日期，long,排序用
    public int position = 0;//用于确定位置
    public boolean isHaveTag;
    public boolean isLionName;
    public boolean isContact;
    public boolean isRefreshing;
    public int blockType;
    public String formatNum;//只用于号码去重，其他地方不用
    public boolean isFakeCall;
    public boolean isSelected;//只有在删除模式的时候才使用
    public boolean isShowSpamInCalllog;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (this.callDate.equals(((CallLogInfo) obj).callDate)
                && this.formatNum.equals(((CallLogInfo) obj).formatNum)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (!TextUtils.isEmpty(callDate) && !TextUtils.isEmpty(formatNum)) {
            return callDate.hashCode() * formatNum.hashCode();
        }
        return -1;
    }
}
