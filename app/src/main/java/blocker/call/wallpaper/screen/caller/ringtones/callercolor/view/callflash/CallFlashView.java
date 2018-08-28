package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.callflash;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.md.flashset.bean.CallFlashFormat;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.EncryptionUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.TextureVideoView;

public class CallFlashView extends RelativeLayout {
    private static final String TAG = "CallFlashView";
    private Context mContext;
    private GlideView mGlideView;
    private TextureVideoView mVideoView;
    private CallFlashInfo mCallFlashInfo;
    private int mCallFlashFormat;
    private CallFlashCustomAnimView mCustomAnimView;
    private int mVideoPlayProgress;
    private GlideView mGlideViewPreview;
    private boolean isVideoMute;

    private AtomicBoolean isStop = new AtomicBoolean(false);
    private AtomicBoolean isPause = new AtomicBoolean(false);
    private MediaPlayer mMediaPlayer;

    public CallFlashView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CallFlashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CallFlashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_call_flash_view, this);

        mGlideView = findViewById(R.id.glide_view);//用于显示GIF或者图片
        mVideoView = findViewById(R.id.video_view);//用于显示视频
        mCustomAnimView = findViewById(R.id.custom_anim_view);//用于显示自定义动画callflash

        mGlideViewPreview = findViewById(R.id.glide_view_preview);//在没下载的时候用于显示预览图
        setVideoListener();
    }

    public CallFlashInfo getCallFlashInfo() {
        return mCallFlashInfo;
    }

    public boolean isStopVideo() {
        return isStop.get();
    }

    public boolean isPlaying() {
        if (mCallFlashFormat == CallFlashFormat.FORMAT_VIDEO && mVideoView != null) {
            return mVideoView.isPlaying();
        } else if (mCallFlashFormat == CallFlashFormat.FORMAT_CUSTOM_ANIM && mCustomAnimView != null) {
            return mCustomAnimView.isPlaying();
        }
        return false;
    }

    public boolean isPause() {
        return isPause.get();
    }

    public void showCallFlashView(CallFlashInfo info) {
        mCallFlashInfo = info;
        if (info == null) return;
        if (mGlideViewPreview != null) {
            mGlideViewPreview.setVisibility(GONE);
        }
        if (mGlideView != null) {
            mGlideView.setVisibility(GONE);
        }
        if (mVideoView != null) {
            mVideoView.setVisibility(GONE);
        }
        if (mCustomAnimView != null) {
            mCustomAnimView.setVisibility(GONE);
        }
        mCallFlashFormat = info.format;
        switch (mCallFlashFormat) {
            case CallFlashFormat.FORMAT_GIF:
                setGif(info);
                break;
            case CallFlashFormat.FORMAT_IMAGE:
                setImage(info);
                break;
            case CallFlashFormat.FORMAT_VIDEO:
                setVideo(info);
                break;
            case CallFlashFormat.FORMAT_CUSTOM_ANIM:
                setCustomAnim(info);
                break;
        }
    }

    /**
     * 该方法主要用于视频停止
     */
    public void stop() {
        if (mCallFlashFormat == CallFlashFormat.FORMAT_VIDEO && mVideoView != null) {
            mVideoView.stopPlayback();
            isStop.set(true);
        }
    }

    public void pause() {
        if (mCallFlashFormat == CallFlashFormat.FORMAT_VIDEO && mVideoView != null) {
            //记录播放的progress,避免黑屏
            mVideoView.pause();
            mVideoView.setFocusable(false);
            mVideoPlayProgress = mVideoView.getCurrentPosition();
            isPause.set(true);
        } else if (mCallFlashFormat == CallFlashFormat.FORMAT_CUSTOM_ANIM && mCustomAnimView != null && mCallFlashInfo != null) {
            mCustomAnimView.update(true, mCallFlashInfo.flashType);
            isPause.set(true);
        }
    }

    public void continuePlay() {
        if (mCallFlashFormat == CallFlashFormat.FORMAT_VIDEO && mVideoView != null && !mVideoView.isPlaying()) {
            LogUtil.d(TAG, "continuePlay mVideoPlayProgress:" + mVideoPlayProgress);
//            if (mGlideView != null && mCallFlashInfo != null) {
//                //显示视频第一帧防止黑屏
//                mGlideView.setVisibility(VISIBLE);
//                mGlideView.showVideoFirstFrame(mCallFlashInfo.path);
//            }
            mVideoView.seekTo(mVideoPlayProgress);
            mVideoView.start();
            mVideoView.setFocusable(true);
            isPause.set(false);
        } else if (mCallFlashFormat == CallFlashFormat.FORMAT_CUSTOM_ANIM && mCustomAnimView != null && mCallFlashInfo != null && !mCustomAnimView.isPlaying()) {
            mCustomAnimView.update(false, mCallFlashInfo.flashType);
            isPause.set(false);
        }
    }

    private void setCustomAnim(CallFlashInfo info) {
        if (info == null) return;
        if (mCustomAnimView != null) {
            mCustomAnimView.setVisibility(VISIBLE);
            int flashType = info.flashType;
            mCustomAnimView.startAnim(flashType);
            isPause.set(false);
        }
    }

    private void setVideo(final CallFlashInfo info) {
        if (info == null) return;
        final String path = info.path;
        String flashID = info.id;
        if (mVideoView != null && !TextUtils.isEmpty(path) && (CallFlashManager.CALL_FLASH_START_SKY_ID.equals(flashID) || new File(path).exists())) {
            if (mGlideView != null) {
                //显示视频第一帧防止黑屏
                mGlideView.setVisibility(VISIBLE);
                if (CallFlashManager.CALL_FLASH_START_SKY_ID.equals(flashID)) {
                    mGlideView.showImage(info.imgResId);
                } else {
                    File firstFrameFile = ThemeSyncManager.getInstance().getVideoFirstFrameFileByUrl(mContext, info.url);
                    LogUtil.d(TAG, "setVideo firstFrameFile:" + (firstFrameFile == null ? "" : firstFrameFile.exists()));
                    if (firstFrameFile != null && firstFrameFile.exists()) {
                        mGlideView.showImageInSdCard(firstFrameFile.getAbsolutePath());
                    } else {
                        mGlideView.showImage(info.img_vUrl);
                        if (!EncryptionUtil.isEncrypted(path)) {
                            CallFlashManager.getInstance().saveVideoFirstFrame(info.url);
                        }
                    }
                }
            }
            //加密
            EncryptionUtil.encrypt(info);
            mVideoView.setVisibility(VISIBLE);
            mVideoView.setVideoPath(path);
            mVideoView.start();
            mVideoView.setFocusable(true);
            isStop.set(false);
        } else {
            showPreview(info);
        }
    }

    private void setImage(CallFlashInfo info) {
        if (info == null) return;
        String path = info.path;
        if (mGlideView != null && !TextUtils.isEmpty(path) && new File(info.path).exists()) {
            mGlideView.setVisibility(VISIBLE);
            mGlideView.showImage(path);
        } else {
            showPreview(info);
        }
    }

    private void setGif(CallFlashInfo info) {
        if (info == null) return;
        String path = info.path;
        if (mGlideView != null && !TextUtils.isEmpty(path) && new File(info.path).exists()) {
            mGlideView.setVisibility(VISIBLE);
            mGlideView.showGif(path);
        } else {
            showPreview(info);
        }
    }

    private void setVideoListener() {
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null) {
                    mp.start();
                }
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (mCallFlashInfo != null) {
                    LogUtil.e("chenr", "video play error, what: " + what + ", extra: " + extra + ", flashType: " + mCallFlashInfo.flashType);
                }
                isStop.set(true);
                return true;
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer = mp;
                //设置videoView 静音
                if (isVideoMute) {
                    mp.setVolume(0f, 0f);
                }
                mp.start();

                Async.scheduleTaskOnUiThread(500, new Runnable() {
                    @Override
                    public void run() {
                        if (mGlideView != null) {
                            mGlideView.setVisibility(GONE);
                        }
                    }
                });
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        LogUtil.d(TAG, "setOnInfoListener mp:" + mp + ",what:" + what);
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            if (mGlideView != null) {
                                mGlideView.setVisibility(GONE);
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    /**
     * 如果没有下载，则显示预览图
     */
    private void showPreview(CallFlashInfo info) {
        if (info == null || mGlideViewPreview == null) return;
        mGlideViewPreview.setVisibility(VISIBLE);
        mGlideViewPreview.showImageWithThumbnail(info.img_vUrl, info.thumbnail_imgUrl);
    }

    public void setVideoMute(boolean mVideoMute) {
        this.isVideoMute = mVideoMute;
        try {
            if (mMediaPlayer != null) {
                if (isVideoMute) {
                    mMediaPlayer.setVolume(0f, 0f);
                } else {
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "setVideoMute e:" + e.getMessage());
        }
    }
}
