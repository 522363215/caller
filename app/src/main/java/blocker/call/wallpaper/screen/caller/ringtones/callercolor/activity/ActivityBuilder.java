package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;

import com.md.flashset.bean.CallFlashInfo;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;


/**
 * Activity跳转管理类
 */
public class ActivityBuilder {
    /**
     * 表示从callFlash 结果页返回时回到MainActivity 不需要改变page
     */
    public static final int BACK_FROM_CALL_FLASH_RESULT = -1024;
    public static final String MAIN_FRAGMENT_INDEX = "main_fragment_index";
    public static final String IS_COME_FROM_DESKTOP = "is_come_from_desktop";
    public static final String IS_COME_FROM_CALL_AFTER = "is_come_from_call_after";
    public static final String CALL_FLASH_INFO = "call_flash_info";
    public static final String CALL_FLASH_DATA_TYPE = "call_flash_data_type";

    public static final int FRAGMENT_HOME = 0;
    public static final int FRAGMENT_MINE = 1;
    public static final int FRAGMENT_CATEGORY = 2;
    public static final int MAX_FRAGEMNTS = 2;

    public static final String SMS_COME_MESSAGE = "sms_come_message";
    public static final String IS_LETS_START = "is_lets_start";

    public static final String CALL_FLASH_SHARE_PREVIEW = "call_flash_preview";

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

    public static void toCallFlashPreview(Context context, CallFlashInfo info, GlideView glideView, boolean isComeDesktop) {
        if (context != null) {
            Intent intent = new Intent();
            intent.setClass(context, CallFlashPreviewActivity.class);
//            intent.putExtra(ConstantUtils.COME_FROM_CALLAFTER, isComeCallAfter);
//            intent.putExtra(ConstantUtils.COME_FROM_PHONEDETAIL, mIsComePhoneDetail);
            intent.putExtra(IS_COME_FROM_DESKTOP, isComeDesktop);
            intent.putExtra(CALL_FLASH_INFO, info);
            if (context instanceof Activity && glideView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation((Activity) context, glideView, CALL_FLASH_SHARE_PREVIEW);
                ActivityCompat.startActivity(context, intent, options.toBundle());
            } else {
                context.startActivity(intent);
            }
        }
    }

    public static void toCallFlashPreview(Context context, CallFlashInfo info, GlideView glideView) {
        toCallFlashPreview(context, info, glideView, false);
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

    /**
     * @param dataType CallFlashDataType 中的type
     */
    public static void toCallFlashList(Context context, int dataType) {
        Intent intent = new Intent();
        intent.setClass(context, CallFlashListActivity.class);
        intent.putExtra(CALL_FLASH_DATA_TYPE, dataType);
        context.startActivity(intent);
    }

    /**
     * @param isLetsStart true:显示title为check，false:请求权限
     */
    public static void toPermissionActivity(Context context, boolean isLetsStart) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(ActivityBuilder.IS_LETS_START, isLetsStart);
        context.startActivity(intent);
    }
}
