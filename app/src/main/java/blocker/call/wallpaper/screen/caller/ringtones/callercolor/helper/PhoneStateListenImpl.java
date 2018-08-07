package blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper;

import android.content.Context;
import android.content.Intent;

import com.md.block.callback.PhoneStateChangeCallback;
import com.md.flashset.helper.CallFlashPreferenceHelper;

import java.util.Locale;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.CallAfterActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.CallLogInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ContactManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CallUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DateUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog.CallFlashDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.NumberUtil;

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
        boolean enableCallerId = PreferenceHelper.getBoolean(PreferenceHelper.PREF_KEY_ENABLE_SHOW_CALL_AFTER, PreferenceHelper.DEFAULT_VALUE_FOR_CALLER_ID);
        boolean installCID = AdvertisementSwitcher.isAppInstalled(ConstantUtils.PACKAGE_CID);
        if (!installCID && enableCallerId) {
            CallLogInfo info = new CallLogInfo();
            info.callNumber = number;
            info.callName = ContactManager.getInstance().getContactNameForNumber(number);
            info.callLoction = NumberUtil.getNumberLocationForCallLog(number);
            info.date = System.currentTimeMillis();
            info.callDate = DateUtils.getHmForTime(info.date, Locale.getDefault());
            info.callType = CallUtils.getCallLogType(number);

            Intent intent = new Intent();
            intent.putExtra("lm_call_after_info", info);
            intent.setClass(mContext, CallAfterActivity.class);
            mContext.startActivity(intent);
        }

        CallFlashDialog.getInstance().hideFloatView();
    }

    @Override
    public void onPhoneRinging(String number) {
        boolean isShowCallFlash = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
        if (isShowCallFlash) {
            CallFlashDialog.getInstance().showFloatView(number);
        }
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
