package com.md.block.callback;

/**
 * Created by ChenR on 2018/7/4.
 */

public interface PhoneStateChangeCallback {

    void onPhoneIdle (String number);
    void onPhoneRinging (String number);
    void onPhoneOffHook (String number);

    void onPhoneOutCall (String number);

    void onPhoneReject (String number);
    void onPhoneHangUp (String number);
    void onPhoneAnswer (String number);

}
