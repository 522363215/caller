package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.md.wallpaper.bean.WallpaperFormat;
import com.md.wallpaper.bean.WallpaperInfo;

import java.io.File;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

public class WallpaperView extends RelativeLayout {
    private static final String TAG = "WallpaperView";
    private Context mContext;
    private GlideView mGlideView;
    private TextureVideoView mVideoView;
    private WallpaperInfo mWallpaperInfo;
    private int mWallpaperFormat;
    private int mVideoPlayProgress;
    private GlideView mGlideViewPreview;
    private boolean isVideoMute;
    private MediaPlayer mMediaPlayer;

    public WallpaperView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public WallpaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public WallpaperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_call_flash_view, this);

        mGlideView = findViewById(R.id.glide_view);//用于显示GIF或者图片
        mVideoView = findViewById(R.id.video_view);//用于显示视频

        mGlideViewPreview = findViewById(R.id.glide_view_preview);//在没下载的时候用于显示预览图
        setVideoListener();
    }


    public boolean isPlaying() {
        if (mWallpaperFormat == WallpaperFormat.FORMAT_VIDEO && mVideoView != null) {
            return mVideoView.isPlaying();
        }
        return false;
    }

    public void showWallpaper(WallpaperInfo info) {
        mWallpaperInfo = info;
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
        mWallpaperFormat = info.format;
        switch (mWallpaperFormat) {
            case WallpaperFormat.FORMAT_GIF:
                setGif(info);
                break;
            case WallpaperFormat.FORMAT_IMAGE:
                setImage(info);
                break;
            case WallpaperFormat.FORMAT_VIDEO:
                setVideo(info);
                break;
        }
    }

    /**
     * 该方法主要用于视频停止
     */
    public void stop() {
        if (mWallpaperFormat == WallpaperFormat.FORMAT_VIDEO && mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    public void pause() {
        if (mWallpaperFormat == WallpaperFormat.FORMAT_VIDEO && mVideoView != null) {
            //记录播放的progress,避免黑屏
            mVideoView.pause();
            mVideoView.setFocusable(false);
            mVideoPlayProgress = mVideoView.getCurrentPosition();
        }
    }

    public void continuePlay() {
        if (mWallpaperFormat == WallpaperFormat.FORMAT_VIDEO && mVideoView != null && !mVideoView.isPlaying()) {
            LogUtil.d(TAG, "continuePlay mVideoPlayProgress:" + mVideoPlayProgress);
            mVideoView.seekTo(mVideoPlayProgress);
            mVideoView.start();
            mVideoView.setFocusable(true);
        }
    }

    private void setVideo(WallpaperInfo info) {
        if (info == null) return;
        final String path = info.path;
        if (mVideoView != null && !TextUtils.isEmpty(path) && new File(path).exists()) {
            if (mGlideView != null) {
                //显示视频第一帧防止黑屏
                mGlideView.setVisibility(VISIBLE);
                mGlideView.showVideoFirstFrame(path);
            }
            mVideoView.setVisibility(VISIBLE);
            mVideoView.setVideoPath(path);
            mVideoView.start();
            mVideoView.setFocusable(true);
        } else {
            showPreview(info);
        }
    }

    private void setImage(WallpaperInfo info) {
        if (info == null) return;
        String path = info.path;
        if (mGlideView != null && !TextUtils.isEmpty(path) && new File(info.path).exists()) {
            mGlideView.setVisibility(VISIBLE);
            mGlideView.showImage(path);
        } else {
            showPreview(info);
        }
    }

    private void setGif(WallpaperInfo info) {
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
    private void showPreview(WallpaperInfo info) {
        if (info == null || mGlideViewPreview == null) return;
        mGlideViewPreview.setVisibility(VISIBLE);
        mGlideViewPreview.showImageWithThumbnail(info.img_vUrl, info.thumbnail_imgUrl);
    }

    public void setVideoMute(boolean mVideoMute) {
        this.isVideoMute = mVideoMute;
        if (mMediaPlayer != null) {
            if (isVideoMute) {
                mMediaPlayer.setVolume(0f, 0f);
            } else {
                mMediaPlayer.setVolume(1.0f, 1.0f);
            }
        }
    }
}
