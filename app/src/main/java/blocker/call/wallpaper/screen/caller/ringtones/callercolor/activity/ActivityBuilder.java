package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.content.Context;
import android.content.Intent;

import com.md.flashset.bean.CallFlashInfo;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;


/**
 * Activity跳转管理类
 */
public class ActivityBuilder {
    /**
     * 表示从callFlash 结果页返回时回到MainActivity 不需要改变page
     */
    public static final int BACK_FROM_CALL_FLASH_RESULT = -1024;

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
            intent.putExtra("fragment_index", fragmentIndex);
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
            intent.putExtra(ConstantUtils.IS_ONLINE_FOR_CALL_FLASH, isOnlineCallFlash);
            intent.putExtra(ConstantUtils.COME_FROM_DESKTOP, isComeDesktop);
            intent.putExtra("flash_theme", info);
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
        intent.putExtra(ConstantUtils.COME_FROM_DESKTOP, isComeDesktop);
        intent.putExtra("flash_theme", info);
        context.startActivity(intent);
    }
}
