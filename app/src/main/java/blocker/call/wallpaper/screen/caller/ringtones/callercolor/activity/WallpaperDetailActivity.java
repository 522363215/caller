package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import com.flurry.android.FlurryAgent;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventBusIsSet;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventPostIsExist;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventPostIsExit;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.MajorityOfResult;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.MiPhoneResult;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.VideoLiveWallpaperService;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.LiveWallpaperService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.md.callring.Constant;
import com.md.serverflash.callback.OnDownloadListener;
import com.md.serverflash.download.ThemeResourceHelper;
import com.md.wallpaper.WallpaperPreferenceHelper;
import com.md.wallpaper.bean.WallpaperFormat;
import com.md.wallpaper.bean.WallpaperInfo;
import com.md.wallpaper.WallpaperUtil;

import java.io.File;
import java.io.IOException;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.BatteryProgressBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.WallpaperView;
import event.EventBus;

public class WallpaperDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1528;
    private static final int REQUEST_CODE_SET_WALLPAPER = 1529;

    private TextView tvDownload;
    private WallpaperInfo wallpaper;
    private GlideView gvBg;
    private BatteryProgressBar mPbDownloading;
    private WallpaperInfo setWallpaper;
    private WallpaperView mWallpaperView;
    private File file;
    private boolean mIsMute;
    private ImageView mIvWallSound;
    private boolean isExist;
    private boolean isDead = true;
    private Handler mMajorityOfHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    EventBus.getDefault().post(new EventBusIsSet());
                    WallpaperPreferenceHelper.putObject(WallpaperPreferenceHelper.SETED_WALLPAPERS, wallpaper);
                    ActivityBuilder.toAppointActivity(WallpaperDetailActivity.this,CallFlashSetResultActivity.class);
                    break;
                case 2:
                    if (!isDead){
                        EventBus.getDefault().post(new EventBusIsSet());
                        WallpaperPreferenceHelper.putObject(WallpaperPreferenceHelper.SETED_WALLPAPERS, wallpaper);
                        ActivityBuilder.toAppointActivity(WallpaperDetailActivity.this,CallFlashSetResultActivity.class);
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        super.onCreate(savedInstanceState);
        wallpaper = (WallpaperInfo) getIntent().getSerializableExtra(Constant.WALLPAPER_BUNDLE);
        setWallpaper = (WallpaperInfo) WallpaperPreferenceHelper.getObject(WallpaperPreferenceHelper.SETED_WALLPAPERS, WallpaperInfo.class);
        if (wallpaper == null) return;
        gvBg = findViewById(R.id.gv_bg);
        if (!TextUtils.isEmpty(wallpaper.img_vUrl)) {
            gvBg.showImageWithBlur(wallpaper.img_vUrl);
        }

        mWallpaperView = findViewById(R.id.wall_paper_view);
        mWallpaperView.showWallpaper(wallpaper);

        tvDownload = findViewById(R.id.tv_download);
        tvDownload.setOnClickListener(this);

        mIvWallSound = findViewById(R.id.iv_wall_sound);
        mIvWallSound.setOnClickListener(this);

        mPbDownloading = findViewById(R.id.pb_downloading);
        mPbDownloading.setMaxProgress(100);

        FontIconView fiv_back = findViewById(R.id.fiv_back);
        fiv_back.setOnClickListener(this);


        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        EventBus.getDefault().post(new EventPostIsExit());
    }

    @Override
    protected void translucentStatusBar() {

    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_message_picture;
    }


    private void getPicture() {
        ThemeResourceHelper.getInstance().downloadThemeResources(wallpaper.id, wallpaper.url, new OnDownloadListener() {
            @Override
            public void onFailure(String wallpaper) {
                if (isFinishing())return;
                tvDownload.setText(R.string.download);
                tvDownload.setBackgroundResource(R.color.color_FF27BB56);
            }

            @Override
            public void onFailureForIOException(String wallpaper) {
                if (isFinishing())return;
                LogUtil.e("askdjk", "文件存储失败");
            }

            @Override
            public void onProgress(String wallpaper, int progress) {
                if (isFinishing())return;
                tvDownload.setText(progress + "%");
                tvDownload.setVisibility(View.VISIBLE);
                mPbDownloading.setVisibility(View.VISIBLE);
                Log.e("onProgress: ", progress + "");
                mPbDownloading.setProgress(progress);
            }

            @Override
            public void onSuccess(String u, File file) {
                if (isFinishing())return;
                wallpaper.isDownloadSuccess = true;
                wallpaper.isDownloaded = false;
                wallpaper.path = file.getAbsolutePath();

                tvDownload.setBackgroundResource(R.color.progress_default_color);
                mWallpaperView.showWallpaper(wallpaper);
                WallpaperUtil.getInstance().saveDownloadedWallPaper(wallpaper);

                tvDownload.setText(R.string.set_pic);
                tvDownload.setVisibility(View.VISIBLE);
                mPbDownloading.setVisibility(View.INVISIBLE);

                setSound();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (tvDownload.getText().toString().equals(this.getString(R.string.download))){
//                        LogUtil.e("asdgauysd333333333","asduyasuyduaysg");
//                        tvDownload.setText(R.string.resume);
//                        tvDownload.setBackgroundResource(R.color.color_half_transparent);
//                        getPicture();
//                    }
                    setWall();
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(this, getString(R.string.get_power_result), Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWallpaperView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_download:
                if (tvDownload.getText().toString().equals(this.getString(R.string.download))) {
                    String[] PERMISSIONS = {
                            "android.permission.READ_EXTERNAL_STORAGE",
                            "android.permission.WRITE_EXTERNAL_STORAGE"};
                    //检测是否有写的权限
                    requestPermission(PERMISSIONS, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);

                } else if (tvDownload.getText().toString().equals(this.getString(R.string.set_pic))) {
                    String[] WALL = {
                            "android.permission.SET_WALLPAPER",};
                    requestPermission(WALL, REQUEST_CODE_SET_WALLPAPER);
                    if (isExist){
                        tvDownload.setText(R.string.set_pic);
                        tvDownload.setClickable(false);
                        tvDownload.setBackgroundResource(R.color.color_66FFFFFF);
                    }
                }
                break;
            case R.id.fiv_back:
                finish();
                break;
            case R.id.iv_wall_sound:
                WallpaperPreferenceHelper.putBoolean(WallpaperPreferenceHelper.PREF_WALL_IS_MUTE_WHEN_PREVIEW, !mIsMute);
                if (wallpaper.format == WallpaperFormat.FORMAT_VIDEO) {
                    setSound();
                } else {
                    LogUtil.e("暂不支持格式","暂不支持格式");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWallpaperView.continuePlay();
        file = ThemeSyncManager.getInstance().getFileByUrl(WallpaperDetailActivity.this, wallpaper.url);
        String path = WallpaperPreferenceHelper.getString(WallpaperPreferenceHelper.FILE_NAME,"");
        if (file != null && file.exists() || (!TextUtils.isEmpty(wallpaper.path) && new File(wallpaper.path).exists())) {
            LogUtil.e("ahsfdafsdghf",wallpaper.path+"\t"+path);
            tvDownload.setText(R.string.set_pic);
            mIvWallSound.setVisibility(View.VISIBLE);
        } else {
            tvDownload.setText(R.string.download);
        }
    }

    private void setSound() {
        if (wallpaper == null) return;
        if ((file != null && file.exists()) || (!TextUtils.isEmpty(wallpaper.path) && new File(wallpaper.path).exists()) || CallFlashManager.CALL_FLASH_START_SKY_ID.equals(wallpaper.id)) {
            mIvWallSound.setVisibility(View.VISIBLE);
        } else {
            mIvWallSound.setVisibility(View.GONE);
        }
        mIsMute = WallpaperPreferenceHelper.getBoolean(WallpaperPreferenceHelper.PREF_WALL_IS_MUTE_WHEN_PREVIEW, true);
        if (mIsMute) {
            mIvWallSound.setImageDrawable(getResources().getDrawable(R.drawable.icon_mute));
            FlurryAgent.logEvent("CallFlashDetailActivity-click_mute");
        } else {
            mIvWallSound.setImageDrawable(getResources().getDrawable(R.drawable.icon_sound));
            FlurryAgent.logEvent("CallFlashDetailActivity-click_sound");
        }
        mWallpaperView.setVideoMute(mIsMute);
    }

    private void setWall() {
        if (wallpaper.format == WallpaperFormat.FORMAT_VIDEO) {
            WallpaperPreferenceHelper.putString(WallpaperPreferenceHelper.FILE_NAME,file.getAbsolutePath());
            LogUtil.e("daozheli",wallpaper.format+"");
            Intent intent = new Intent();
            intent.setAction(VideoLiveWallpaperService.ACTION_UPDATE_PATH);
            intent.putExtra(VideoLiveWallpaperService.VIDEO_LIVE_WALLPAPER_PATH,file.getAbsolutePath());
            if (isExist){
                intent.putExtra(VideoLiveWallpaperService.VIDEO_LIVE_WALLPAPER_START_OPEN,true);
                sendBroadcast(intent);
                WallpaperPreferenceHelper.putObject(WallpaperPreferenceHelper.SETED_WALLPAPERS, wallpaper);
                EventBus.getDefault().post(new EventBusIsSet());
                ActivityBuilder.toAppointActivity(WallpaperDetailActivity.this,CallFlashSetResultActivity.class);
            }else {
                Intent intent1 = new Intent(android.app.WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent1.putExtra(android.app.WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(WallpaperDetailActivity.this, VideoLiveWallpaperService.class));
                startActivityForResult(intent1,1);
                intent.putExtra(VideoLiveWallpaperService.VIDEO_LIVE_WALLPAPER_START_OPEN,false);
            }

//            ActivityBuilder.toAppointActivity(WallpaperDetailActivity.this,CallFlashSetResultActivity.class);
//            setVideoToWallPaper();
        } else if (wallpaper.format == WallpaperFormat.FORMAT_IMAGE) {
            Bitmap bitmap = BitmapFactory.decodeFile(wallpaper.path);//filePath
            try {
                android.app.WallpaperManager wpm = (android.app.WallpaperManager) this.getSystemService(
                        Context.WALLPAPER_SERVICE);

                wpm.setBitmap(bitmap);
                Log.i("xzy", "wallpaper not null");

            } catch (IOException e) {
                Log.e("tgyhuj", "Failed to set wallpaper: " + e);
            }
            ActivityBuilder.toAppointActivity(WallpaperDetailActivity.this,CallFlashSetResultActivity.class);
        }else {
            LogUtil.e("暂不支持格式","暂不支持格式");
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("onActivityResult: ", requestCode+"\t"+requestCode);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mMajorityOfHandler.sendEmptyMessageDelayed(1, 1000);
            } else {
                EventBus.getDefault().post(new MiPhoneResult());
                mMajorityOfHandler.sendEmptyMessageDelayed(2, 1000);
            }
        }
    }


    public void setVideoToWallPaper() {
        LiveWallpaperService.setToWallPaper(this);
        LiveWallpaperService.voiceNormal(this);
    }

    public void onEventMainThread(EventPostIsExist eventPostIsExist){
        LogUtil.e("asdgauysd222222","asduyasuyduaysg");
        isExist = true;
    }

    public void onEventMainThread(MajorityOfResult majorityOfResult){
        LogUtil.e("asdgauysd4444444","asduyasuyduaysg");
        isDead = false;
    }


    @Override
    public void onPermissionGranted(int requestCode) {
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            tvDownload.setText(R.string.resume);
            tvDownload.setBackgroundResource(R.color.color_transparent);
            getPicture();
        } else if (requestCode == REQUEST_CODE_SET_WALLPAPER) {
            setWall();
        }
    }

    @Override
    public void onPermissionNotGranted(int requestCode) {
        super.onPermissionNotGranted(requestCode);
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            tvDownload.setBackgroundResource(R.color.color_FF27BB56);
        }
    }
}
