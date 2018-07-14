package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.callflash;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashFormat;
import com.md.flashset.bean.CallFlashInfo;

import java.io.File;

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
    private int mVidepPlayProgress;
    private boolean misStart;

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

        mGlideView = findViewById(R.id.glide_view);
        mVideoView = findViewById(R.id.video_view);
        mCustomAnimView = findViewById(R.id.custom_anim_view);
        setVideoListener();
    }

    public void showCallFlashView(CallFlashInfo info) {
        mCallFlashInfo = info;
        if (info == null) return;
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
            Async.scheduleTaskOnUiThread(300, new Runnable() {
                @Override
                public void run() {
                    mVideoView.stopPlayback();
                }
            });
        }
    }

    public void pause() {
        misStart = false;
        if (mCallFlashFormat == CallFlashFormat.FORMAT_VIDEO && mVideoView != null) {
            //记录播放的progress,避免黑屏
            mVideoView.pause();
            mVidepPlayProgress = mVideoView.getCurrentPosition();
        } else if (mCallFlashFormat == CallFlashFormat.FORMAT_CUSTOM_ANIM && mCustomAnimView != null && mCallFlashInfo != null) {
            mCustomAnimView.update(true, mCallFlashInfo.flashType);
        }
    }

    public void continuePlay() {
        if (misStart) return;
        if (mCallFlashFormat == CallFlashFormat.FORMAT_VIDEO && mVideoView != null) {
            mVideoView.seekTo(mVidepPlayProgress);
            mVideoView.start();
            misStart = true;
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
        if (mVideoView != null && !TextUtils.isEmpty(path) && (flashType == FlashLed.FLASH_TYPE_MONKEY || new File(info.path).exists())) {
            misStart = true;
            mVideoView.setVisibility(VISIBLE);
            mVideoView.setVideoPath(path);
            mVideoView.start();
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
    }

    /**
     * 如果没有下载，则显示预览图
     */
    private void showPreview(CallFlashInfo info) {
        if (info == null || mGlideView == null) return;
        mGlideView.setVisibility(VISIBLE);
        mGlideView.showImageWithThumbnail(info.img_vUrl, info.thumbnail_imgUrl);
    }

}
