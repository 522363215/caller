package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean;

import java.io.Serializable;

public class ExternalParam implements Serializable {
    public boolean mEnable;//是否开启
    public int mDelayTime;//第一次初始化 SDK 开始，延迟多长时间才开启该功能，单位小时
    public int mSelfInterval;//自身弹出的间隔时长，单位小时
    public int mPopupNumber;//每天弹出的最大次数
    public int mDelayedDisplayTime;//界面上关闭的 X 延迟出现的时间，单位毫秒
    public int mDelayedDisplayRate;//界面上关闭的 X 延迟出现的概率，0-100
    public int mRestartDay;//用户开闭该功能以后，多少天后会再次自动打开，单位天(默认为-1，不自动开启)

    public String fb_id;
    public String admob_id;

    public int mType; // 对应下面的type

    public enum MagicType {
        M_AB,//1 加速，开屏
        M_AC,//2 清理，程序安装与卸载
        M_BR,//3 电量提醒，拔下 usb
        M_BS,//4 省电，开屏
        M_CS,//5 充电提醒，充电 30 秒以上+开屏
        M_DW,//6 喝水提醒，开屏
        M_EC,//7 通话结束 M_HL,//运势预测，开屏
        M_NM,//8 颈部运动，开屏
        M_WS,//9 WIFI 检测，网络联通
        M_IT//10 外部插屏，程序安装的时候
    }
}
