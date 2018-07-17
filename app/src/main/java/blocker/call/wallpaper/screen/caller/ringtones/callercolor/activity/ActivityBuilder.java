package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.content.Context;
import android.content.Intent;

import com.md.flashset.bean.CallFlashInfo;


/**
 * Activity跳转管理类
 */
public class ActivityBuilder {
    /**
     * 表示从callFlash 结果页返回时回到MainActivity 不需要改变page
     */
    public static final int BACK_FROM_CALL_FLASH_RESULT = -1024;
    public static final String MAIN_FRAGMENT_INDEX = "main_fragment_index";
    public static final String IS_ONLINE_FOR_CALL_FLASH = "is_online_for_call_flash";
    public static final String IS_COME_FROM_DESKTOP = "is_come_from_desktop";
    public static final String IS_COME_FROM_CALL_AFTER = "is_come_from_call_after";
    public static final String CALL_FLASH_INFO = "call_flash_info";

    public static final int FRAGMENT_HOME = 0;
    public static final int FRAGMENT_CATEGORY = 1;
    public static final int MAX_FRAGEMNTS = 4;

    public static final String SMS_COME_MESSAGE = "sms_come_message";


    /**
     * 主页（ClEAR_TOP）
     *
     * @param context
     * @param fragmentIndex (DEFAULT: -1)
     */
    public static void toMain(Context context, int fragmentIndex) {
        if (context != null) {
            context.startActivity(getMainIntent(context, fragmentIndex));
        }
    }

    public static Intent getMainIntent(Context context, int fragmentIndex) {
        Intent intent = new Intent(context, MainActivity.class);
        if (fragmentIndex != -1) {
            intent.putExtra(MAIN_FRAGMENT_INDEX, fragmentIndex);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public static void toCallFlashPreview(Context context, CallFlashInfo info, boolean isOnlineCallFlash, boolean isComeDesktop) {
        if (context != null) {
            Intent intent = new Intent();
            intent.setClass(context, CallFlashPreviewActivity.class);
//            intent.putExtra(ConstantUtils.COME_FROM_CALLAFTER, isComeCallAfter);
//            intent.putExtra(ConstantUtils.COME_FROM_PHONEDETAIL, mIsComePhoneDetail);
            intent.putExtra(IS_ONLINE_FOR_CALL_FLASH, isOnlineCallFlash);
            intent.putExtra(IS_COME_FROM_DESKTOP, isComeDesktop);
            intent.putExtra(CALL_FLASH_INFO, info);
            context.startActivity(intent);
        }
    }

    public static void toCallFlashPreview(Context context, CallFlashInfo info, boolean isOnlineCallFlash) {
        toCallFlashPreview(context, info, isOnlineCallFlash, false);
    }

    public static void toCallFlashDetail(Context context, CallFlashInfo info, boolean isComeDesktop) {
        Intent intent = new Intent();
        intent.setClass(context, CallFlashDetailActivity.class);
//        intent.putExtra(ConstantUtils.COME_FROM_CALLAFTER, getIntent().getBooleanExtra(ConstantUtils.COME_FROM_CALLAFTER, false));
//        intent.putExtra(ConstantUtils.COME_FROM_PHONEDETAIL, getIntent().getBooleanExtra(ConstantUtils.COME_FROM_PHONEDETAIL, false));
        intent.putExtra(IS_COME_FROM_DESKTOP, isComeDesktop);
        intent.putExtra(CALL_FLASH_INFO, info);
        context.startActivity(intent);
    }
}
