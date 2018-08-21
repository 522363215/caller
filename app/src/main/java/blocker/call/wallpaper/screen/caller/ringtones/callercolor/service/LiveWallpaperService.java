package blocker.call.wallpaper.screen.caller.ringtones.callercolor.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.app.WallpaperManager;
import android.net.Uri;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import com.md.wallpaper.WallpaperPreferenceHelper;

import java.io.File;
import java.io.IOException;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.WallpaperDetailActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;


/**
 * author: Coolspan
 * time: 2017/3/13 15:19
 * describe: 动态壁纸服务，此服务继承自系统服务，一般不出现异常，此服务不会退出
 */
public class LiveWallpaperService extends WallpaperService {

    private static VideoEngine engine;
    public Engine onCreateEngine() {
        engine = new VideoEngine();
        return engine;
    }

    public static final String VIDEO_PARAMS_CONTROL_ACTION = "com.example.admin.livewallpaper";
    public static final String CESHI = "CESHI";
    public static final String CESHI1 = "CESHI1";
    public static final String KEY_ACTION = "action";
    public static final int ACTION_VOICE_SILENCE = 110;
    public static final int ACTION_VOICE_NORMAL = 111;


    class VideoBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            engine.onStart(intent.getStringExtra(CESHI1));
        }
    }

    public static void voiceSilence(Context context) {
        Intent intent = new Intent(LiveWallpaperService.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(LiveWallpaperService.KEY_ACTION, LiveWallpaperService.ACTION_VOICE_SILENCE);
        context.sendBroadcast(intent);
    }

    public static void voiceNormal(Context context) {
        Intent intent = new Intent(LiveWallpaperService.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(LiveWallpaperService.KEY_ACTION, LiveWallpaperService.ACTION_VOICE_NORMAL);
        context.sendBroadcast(intent);
    }

    public static void setToWallPaper(Context context) {
        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, LiveWallpaperService.class));
        context.startActivity(intent);
    }


    class VideoEngine extends Engine {

        private MediaPlayer mMediaPlayer;

        private BroadcastReceiver mVideoParamsControlReceiver;


        private SurfaceHolder holder;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            holder = surfaceHolder;
            IntentFilter intentFilter = new IntentFilter(VIDEO_PARAMS_CONTROL_ACTION);

            registerReceiver(mVideoParamsControlReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int action = intent.getIntExtra(KEY_ACTION, -1);

                    switch (action) {
                        case ACTION_VOICE_NORMAL:
                            mMediaPlayer.setVolume(1.0f, 1.0f);
                            break;
                        case ACTION_VOICE_SILENCE:
                            mMediaPlayer.setVolume(0, 0);
                            break;

                    }
                }
            }, intentFilter);


        }



        @Override
        public void onDestroy() {
            unregisterReceiver(mVideoParamsControlReceiver);
            super.onDestroy();

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                mMediaPlayer.start();
            } else {
                mMediaPlayer.pause();
            }
        }


        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            onStart(WallpaperPreferenceHelper.getString(WallpaperPreferenceHelper.FILE_NAME,""));
//            mMediaPlayer = new MediaPlayer();
//            mMediaPlayer.setSurface(holder.getSurface());
//            Log.e("onSurfaceCreated1: ",WallpaperPreferenceHelper.getString(WallpaperPreferenceHelper.FILE_NAME,"") );
//            try {
//                mMediaPlayer.setDataSource(WallpaperPreferenceHelper.getString(WallpaperPreferenceHelper.FILE_NAME,""));
////                mMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(WallpaperPreferenceHelper.getString(WallpaperPreferenceHelper.FILE_NAME,""))));
////                mMediaPlayer.setSurface(holder.getSurface());
//                mMediaPlayer.setLooping(true);
//                mMediaPlayer.prepare();
//                mMediaPlayer.start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                AssetManager assetMg = getApplicationContext().getAssets();
//                AssetFileDescriptor fileDescriptor = assetMg.openFd("test1.mp4");
//                mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
//                        fileDescriptor.getStartOffset(), fileDescriptor.getLength());
//                mMediaPlayer.setLooping(true);
//                mMediaPlayer.setVolume(0, 0);
//                mMediaPlayer.prepare();
//                mMediaPlayer.start();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }

        private void onStart(String mPath) {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            mMediaPlayer = new MediaPlayer();
            if (getApplicationContext() != null) {
                mPath = WallpaperPreferenceHelper.getString(WallpaperPreferenceHelper.FILE_NAME,"");
            }
            if (!TextUtils.isEmpty(mPath) && holder != null) {
                try {
//                  AssetManager assetMg = getApplicationContext().getAssets();
//                  AssetFileDescriptor fileDescriptor = assetMg.openFd("test1.mp4");
//                  mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
//                          fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                    mMediaPlayer.setDataSource(mPath);
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.seekTo(0);
                        }
                    });
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.setSurface(holder.getSurface());
                    mMediaPlayer.setVolume(0, 0);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mMediaPlayer.release();
            mMediaPlayer = null;

        }
    }


}
