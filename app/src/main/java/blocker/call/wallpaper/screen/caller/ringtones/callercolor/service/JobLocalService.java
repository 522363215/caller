package blocker.call.wallpaper.screen.caller.ringtones.callercolor.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.SwipeManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

/**
 * Created by rockie on 2018/2/26.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobLocalService extends JobService {
    private static long mLastReceiveTime;

    private static boolean checkMinInterval() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - mLastReceiveTime < 10000) {
            return false;
        }
        mLastReceiveTime = currentTimeMillis;
        return true;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        LogUtil.d("JobLocalService", "onStartJob check: ");
        if (checkMinInterval()) {
            try {
                LogUtil.d("JobLocalService", "onStartJob: ");
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getPackageName(),
                        "blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.LocalService"));
                startService(intent);

                //启动 swipe service

                //没有安装call id 才启动
                if (!AdvertisementSwitcher.isAppInstalled(ConstantUtils.PACKAGE_CID)) {
                    SwipeManager.getInstance().checkSwipeService();
                    LogUtil.d("JobLocalService", "easy-swipe checkSwipeService: ");
                }
            } catch (Exception e) {
                LogUtil.e("JobLocalService", "easy-swipe onStartJob e:" + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
