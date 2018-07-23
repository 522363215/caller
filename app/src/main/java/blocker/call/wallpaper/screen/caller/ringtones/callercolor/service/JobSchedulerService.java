package blocker.call.wallpaper.screen.caller.ringtones.callercolor.service;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

import java.util.concurrent.TimeUnit;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ServiceProcessingManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    private static final String TAG = "JobSchedulerService";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "JobSchedulerService onCreate");
        ServiceProcessingManager.getInstance().create(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "JobSchedulerService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        LogUtil.d(TAG, "JobSchedulerService onStartJob");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(this, JobSchedulerService.class);
            startService(intent);
        }
        SchedulerJob(2);//每2分钟检查一次
        return true;
    }

    public void SchedulerJob(int delayMin) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this, JobSchedulerService.class));  //指定哪个JobService执行操作
            builder.setMinimumLatency(TimeUnit.MINUTES.toMillis(delayMin)); //执行的最小延迟时间 2分钟
            builder.setOverrideDeadline(TimeUnit.MINUTES.toMillis(3));  //执行的最长延时时间 3分钟
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);  //非漫游网络状态
            builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR);  //线性重试方案
            builder.setRequiresCharging(false); // 未充电状态
            builder.setPersisted(true);
            jobScheduler.schedule(builder.build());
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LogUtil.d(TAG, "JobSchedulerService onStopJob");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "JobSchedulerService onDestroy");
        ServiceProcessingManager.getInstance().destroy(getApplicationContext());
        SchedulerJob(2);//每2分钟检查一次
    }
}
