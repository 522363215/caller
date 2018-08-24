package blocker.call.wallpaper.screen.caller.ringtones.callercolor.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.md.wallpaper.WallpaperPreferenceHelper;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventPostIsExist;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventPostIsExit;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.MajorityOfResult;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.MiPhoneResult;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import event.EventBus;


public class VideoLiveWallpaperService extends WallpaperService {
    public static String mPath = null;
    public static String ACTION_UPDATE_PATH = "screen.lock.VideoLiveWallpaperService.path_action";
    public static String ACTION_UPDATE_VOICE = "screen.lock.VideoLiveWallpaperService.voice_action";
    public static String VIDEO_LIVE_WALLPAPER_PATH = "Video_Live_WallPaper_Path";
    public static String VIDEO_LIVE_WALLPAPER_START_OPEN = "Video_Live_WallPaper_start_open";
    public static int a = 0;
    private MediaPlayer mMediaPlayer;
    public static VideoEngine engine;
    public static boolean isStartService = true;
    private boolean voiceStat = false;

    public Engine onCreateEngine() {
        voiceStat = WallpaperPreferenceHelper.getBoolean(WallpaperPreferenceHelper.PREF_WALL_IS_MUTE_WHEN_PREVIEW,true);
        engine = new VideoEngine();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        return engine;
    }

    public static class mBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_UPDATE_PATH)){
                mPath = intent.getStringExtra(VIDEO_LIVE_WALLPAPER_PATH);

                isStartService = intent.getBooleanExtra(VIDEO_LIVE_WALLPAPER_START_OPEN, true);
                if (!isStartService) {
                    if (engine != null) {
                        engine.onStart();
                    }
                }
            }

        }
    }

    class VideoEngine extends Engine {

        private SurfaceHolder holder;

        @Override
        public void onTouchEvent(MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(mMediaPlayer != null) {
                    if(!mMediaPlayer.isPlaying()){
                        mMediaPlayer.start();
                        mMediaPlayer.setVolume(0, 0);
//                        if(!voiceStat){
//                            mMediaPlayer.setVolume(1f, 1f);
//                        } else {
//                            mMediaPlayer.setVolume(0, 0);
//                        }
                    }
                }
            }
        }

        private void onStart() {
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
//                    boolean loop = LiveWallpaperUtil.getMainScreenLoopState(VideoLiveWallpaperService.this);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.setSurface(holder.getSurface());
                    mMediaPlayer.setVolume(0, 0);
//                    if(!voiceStat){
//                        mMediaPlayer.setVolume(1f, 1f);
//                    } else {
//                        mMediaPlayer.setVolume(0, 0);
//                    }
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            this.holder = surfaceHolder;
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                onStart();
            } else {
                if (mMediaPlayer != null) {
                    mMediaPlayer.pause();
                }
            }
        }


        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            onStart();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    public void onEventMainThread(EventPostIsExit eventPostIsExit){
        LogUtil.e("asdgauysd1111111","asduyasuyduaysg");
        EventBus.getDefault().post(new EventPostIsExist());
    }

    public void onEventMainThread(MiPhoneResult miPhoneResult){
        LogUtil.e("asdgauysd3333333","asduyasuyduaysg");
        EventBus.getDefault().post(new MajorityOfResult());
    }
}