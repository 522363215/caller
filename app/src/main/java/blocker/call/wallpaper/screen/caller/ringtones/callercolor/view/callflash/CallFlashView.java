package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.callflash;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashFormat;
import com.md.flashset.bean.CallFlashInfo;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;

public class CallFlashView extends RelativeLayout {
    private static final String TAG = "CallFlashView";
    private Context mContext;
    private GlideView mGlideView;
    private FullScreenVideoView mVideoView;
    private CallFlashInfo mCallFlashInfo;
    private int mCallFlashFormat;
    private CallFlashCustomAnimView mCustomAnimView;
    private int mVideoPlayProgress;
    private boolean misStart;
    private GlideView mGlideViewPreview;
    private boolean isVideoMute;

    private AtomicBoolean isStop = new AtomicBoolean(false);
    private AtomicBoolean isPause = new AtomicBoolean(false);

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

    public CallFlashInfo getCallFlashInfo () {
        return mCallFlashInfo;
    }

    public boolean isStopVideo () {
        return isStop.get();
    }

    public boolean isPauseVideo () {
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
            mVideoView.setVisibility(INVISIBLE);
            isStop.set(true);
        }
    }

    public void pause() {
        misStart = false;
        if (mCallFlashFormat == CallFlashFormat.FORMAT_VIDEO && mVideoView != null) {
            isPause.set(true);
            //记录播放的progress,避免黑屏
            mVideoView.pause();
            mVideoPlayProgress = mVideoView.getCurrentPosition();
        } else if (mCallFlashFormat == CallFlashFormat.FORMAT_CUSTOM_ANIM && mCustomAnimView != null && mCallFlashInfo != null) {
            mCustomAnimView.update(true, mCallFlashInfo.flashType);
        }
    }

    public void continuePlay() {
        if (misStart) return;
        if (mCallFlashFormat == CallFlashFormat.FORMAT_VIDEO && mVideoView != null) {
            LogUtil.d(TAG, "continuePlay mVideoPlayProgress:" + mVideoPlayProgress);
//            if (mGlideView != null && mCallFlashInfo != null) {
//                //显示视频第一帧防止黑屏
//                mGlideView.setVisibility(VISIBLE);
//                mGlideView.showVideoFirstFrame(mCallFlashInfo.path);
//            }
            mVideoView.seekTo(mVideoPlayProgress);
            mVideoView.start();
            misStart = true;
            isPause.set(false);
        } else if (mCallFlashFormat == CallFlashFormat.FORMAT_CUSTOM_ANIM && mCustomAnimView != null && mCallFlashInfo != null) {
            mCustomAnimView.update(false, mCallFlashInfo.flashType);
            misStart = true;
        }
    }

    private void setCustomAnim(CallFlashInfo info) {
        if (info == null) return;
        if (mCustomAnimView != null) {
            mCustomAnimView.setVisibility(VISIBLE);
            int flashType = info.flashType;
            mCustomAnimView.startAnim(flashType);
        }
    }

    private void setVideo(CallFlashInfo info) {
        if (info == null) return;
        final String path = info.path;
        int flashType = info.flashType;
        if (mVideoView != null && !TextUtils.isEmpty(path) && (flashType == FlashLed.FLASH_TYPE_MONKEY || new File(path).exists())) {
            if (mGlideView != null) {
                //显示视频第一帧防止黑屏
                mGlideView.setVisibility(VISIBLE);
                mGlideView.showVideoFirstFrame(path);
            }
            misStart = true;
            mVideoView.setVisibility(VISIBLE);
            mVideoView.setVideoPath(path);
            mVideoView.start();
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
                return true;
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
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
                            mVideoView.setBackgroundColor(Color.TRANSPARENT);
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
    }
}
