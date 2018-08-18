package blocker.call.wallpaper.screen.caller.ringtones.callercolor.service;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.md.callring.Constant;

import java.io.File;

public class VideoWallpaperService extends WallpaperService {

    private Context context;

    private VideoWallpaperEngine mVideoWallpaperEngine;

    private Uri userPickedUri;
    private String filePath;

    private final static long REFLESH_GAP_TIME = 1000L;//如果想播放的流畅，需满足1s 16帧   62ms切换间隔时间

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        filePath = intent.getStringExtra(Constant.VIDEO);
        return START_STICKY;
    }

    @Override
    public Engine onCreateEngine() {
        this.context = this;
        this.mVideoWallpaperEngine = new VideoWallpaperEngine();
        userPickedUri = Uri.fromFile(new File(filePath));
        return mVideoWallpaperEngine;
    }

    private class VideoWallpaperEngine extends Engine {

        MediaPlayer mediaPlayer;

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            initMediaPlayer(holder);
        }

        /**
         * 初始化MediaPlayer
         *
         * @param surfaceHolder
         */
        private void initMediaPlayer(SurfaceHolder surfaceHolder) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), userPickedUri);
            mediaPlayer.setSurface(surfaceHolder.getSurface());
            mediaPlayer.setLooping(true);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                //可见时播放
                mediaPlayer.start();
            } else {
                //不可见时暂停
                mediaPlayer.pause();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            //停止释放MediaPlayer
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }
}
