package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import com.md.serverflash.ThemeSyncManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.LiveWallpaperService;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.md.callring.Constant;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.callback.OnDownloadListener;
import com.md.serverflash.download.ThemeResourceHelper;
import com.md.wallpaper.FileUtil;
import com.md.wallpaper.WallpaperPreferenceHelper;
import com.md.wallpaper.bean.WallpaperFormat;
import com.md.wallpaper.bean.WallpaperInfo;
import com.md.wallpaper.manager.WallpaperManager;

import java.io.File;
import java.io.IOException;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.BatteryProgressBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.WallpaperView;

public class WallpaperDetailActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvDownload;
    private WallpaperInfo wallpaper;
    private GlideView gvBg;
    private BatteryProgressBar mPbDownloading;
    private WallpaperInfo setWallpaper;
    private WallpaperView mWallpaperView;

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

        File file = ThemeSyncManager.getInstance().getFileByUrl(WallpaperDetailActivity.this, wallpaper.url);
        if (file != null && file.exists() || (!TextUtils.isEmpty(wallpaper.path) && new File(wallpaper.path).exists())) {
            if (wallpaper != null && !wallpaper.equals(setWallpaper)) {
                tvDownload.setText(R.string.set_pic);
            } else {
                tvDownload.setText(R.string.set_pic);
                tvDownload.setClickable(false);
                tvDownload.setBackgroundResource(R.color.color_66FFFFFF);
            }
        } else {
            tvDownload.setText(R.string.download);
        }

        mPbDownloading = findViewById(R.id.pb_downloading);
        mPbDownloading.setMaxProgress(100);

        FontIconView fiv_back = findViewById(R.id.fiv_back);
        fiv_back.setOnClickListener(this);

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
                tvDownload.setText(R.string.download);
            }

            @Override
            public void onFailureForIOException(String wallpaper) {
                LogUtil.e("askdjk", "文件存储失败");
            }

            @Override
            public void onProgress(String wallpaper, int progress) {
                tvDownload.setText(progress + "%");
                tvDownload.setVisibility(View.VISIBLE);
                mPbDownloading.setVisibility(View.VISIBLE);
                Log.e("onProgress: ", progress + "");
                mPbDownloading.setProgress(progress);
            }

            @Override
            public void onSuccess(String u, File file) {
                wallpaper.isDownloadSuccess = true;
                wallpaper.isDownloaded = false;
                wallpaper.path = file.getAbsolutePath();

                tvDownload.setBackgroundResource(R.color.progress_default_color);
                mWallpaperView.showWallpaper(wallpaper);
                WallpaperManager.saveDownloadedWallPaper(wallpaper);

                tvDownload.setText(R.string.set_pic);
                tvDownload.setVisibility(View.VISIBLE);
                mPbDownloading.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setWall();
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(this, "获取权限失败，请手动开启", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
                    int permission = ContextCompat.checkSelfPermission(this,
                            "android.permission.WRITE_EXTERNAL_STORAGE");
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // 没有写的权限，去申请写的权限，会弹出对话框
                        ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
                    } else {
                        tvDownload.setText(R.string.resume);
                        tvDownload.setBackgroundResource(R.color.color_half_transparent);
                        getPicture();
                    }

                } else if (tvDownload.getText().toString().equals(this.getString(R.string.set_pic))) {
                    WallpaperPreferenceHelper.putObject(WallpaperPreferenceHelper.SETED_WALLPAPERS, wallpaper);
                    String[] WALL = {
                            "android.permission.SET_WALLPAPER",};
                    int wall = ContextCompat.checkSelfPermission(this,
                            "android.permission.SET_WALLPAPER");
                    if (wall != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, WALL, 1);
                    } else {
                        setWall();
                    }
                }
                break;
            case R.id.fiv_back:
                finish();
                break;
        }
    }

    private void setWall() {
        if (wallpaper.format == WallpaperFormat.FORMAT_VIDEO) {
            LogUtil.e("daozheli",wallpaper.format+"");
            setVideoToWallPaper();
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
            openActivity(CallFlashSetResultActivity.class, 0, 0, true);
        }else {
            LogUtil.e("暂不支持格式","暂不支持格式");
        }
    }

    public void setVideoToWallPaper() {
        LiveWallpaperService.setToWallPaper(this);
        LiveWallpaperService.voiceNormal(this);
    }

}
