package blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper;

import android.content.Context;

import com.md.block.callback.PhoneStateChangeCallback;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog.CallFlashDialog;

/**
 * Created by ChenR on 2018/7/5.
 */

public class PhoneStateListenImpl implements PhoneStateChangeCallback {
    private Context mContext = null;

    public PhoneStateListenImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public void onPhoneIdle(String number) {
        CallFlashDialog.getInstance().hideFloatView();
    }

    @Override
    public void onPhoneRinging(String number) {
        CallFlashDialog.getInstance().showFloatView(number);
    }

    @Override
    public void onPhoneOffHook(String number) {
    }

    @Override
    public void onPhoneOutCall(String number) {

    }

    @Override
    public void onPhoneReject(String number) {

    }

    @Override
    public void onPhoneHangUp(String number) {
    }

    @Override
    public void onPhoneAnswer(String number) {
    }

    @Override
    public void onPhoneBlock(String number) {
    }
}
