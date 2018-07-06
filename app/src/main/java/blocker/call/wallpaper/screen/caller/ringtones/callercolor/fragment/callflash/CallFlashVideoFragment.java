package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.callflash;

import android.app.Fragment;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashFormat;
import com.md.flashset.bean.CallFlashInfo;

import java.io.File;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import event.EventBus;

/**
 * Created by ChenR on 2017/12/21.
 */

public class CallFlashVideoFragment extends Fragment {
    private View root = null;
    private VideoView mVideoView;

    private int mFlashType;
    private String mVideoPath = "";
    private CallFlashInfo mFlashInfo;
    private ImageView mImageView;
    private boolean mIsHideHangAndAnswerButton;
    private View mIvCallAnswer;
    private View mIvCallHang;

    public void setVideoResource(int flashType) {
        String res = "";
        switch (flashType) {
            case FlashLed.FLASH_TYPE_DANCE_PIG:
                res = ApplicationEx.getInstance().getExternalFilesDir(null).getAbsolutePath()
                        + File.separator + "theme" + File.separator + "gif" + File.separator + "pig.mp4";
                break;
            case FlashLed.FLASH_TYPE_RAP:
                res = ApplicationEx.getInstance().getExternalFilesDir(null).getAbsolutePath()
                        + File.separator + "theme" + File.separator + "gif" + File.separator + "rap.mp4";
                break;
            case FlashLed.FLASH_TYPE_MONKEY:
                res = "android.resource://" + ApplicationEx.getInstance().getPackageName() + "/" + R.raw.monkey;
                break;
            case FlashLed.FLASH_TYPE_BEAR:
                res = ApplicationEx.getInstance().getExternalFilesDir(null).getAbsolutePath()
                        + File.separator + "theme" + File.separator + "gif" + File.separator + "video_bear.mp4";
                break;
        }

        if (mVideoView != null && !TextUtils.isEmpty(res)) {
            mVideoView.setVideoPath(res);
            mVideoView.start();
        } else {
            mFlashType = flashType;
        }
    }

    public void setVideoPath(int flashType, String path) {
        if (mVideoView != null && !TextUtils.isEmpty(path)) {
            mVideoView.setVideoPath(path);
            mVideoView.start();
        }
        mVideoPath = path;
        mFlashType = flashType;
    }

    public void setCallFlashInfo(CallFlashInfo info) {
        if (info != null) {
            this.mFlashInfo = info;
            this.mFlashType = info.flashType;
            this.mVideoPath = info.path;

            if (mImageView != null && info.format == CallFlashFormat.FORMAT_VIDEO &&
                    (TextUtils.isEmpty(info.path) || (info.flashType != FlashLed.FLASH_TYPE_MONKEY && !(new File(info.path).exists())))) {
                mImageView.setVisibility(View.VISIBLE);
                mVideoView.stopPlayback();
                if (info.imgResId > 0) {
                    mImageView.setImageResource(info.imgResId);
                } else /*if (!TextUtils.isEmpty(info.imgPath) && new File(info.imgPath).exists()) */ {
//                    mImageView.setImageURI(Uri.parse(info.imgPath));
                    mImageView.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
                    if (!TextUtils.isEmpty(info.img_vUrl)) {
                        Glide.with(this)
                                .load(info.img_vUrl)
                                .placeholder(R.drawable.icon_unloaded_bg)//加载中图片
                                .error(R.drawable.icon_unloaded_bg)//加载失败图片
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate()
                                .into(mImageView);
                    }
                }
            } else if (mVideoView != null && ((info.flashType == FlashLed.FLASH_TYPE_MONKEY)
                    || (!TextUtils.isEmpty(info.path) && new File(info.path).exists()))) {
                if (mImageView != null) {
                    mImageView.setVisibility(View.GONE);
                }
                mVideoView.setVideoPath(info.path);
                mVideoView.start();
            }
        } else {
            LogUtil.e("chenr", "CallFlashVideoFragment method with setCallFlashInfo: CallFlashInfo is null.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_call_flash_video, container, false);

            mImageView = root.findViewById(R.id.iv_call_flash_video_bg);
            mVideoView = (VideoView) root.findViewById(R.id.vv_call_flash_video);
            mVideoView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            mIvCallAnswer = root.findViewById(R.id.iv_call_answer);
            mIvCallHang = root.findViewById(R.id.iv_call_hang);

            listener();
            startAnim();

            if (mFlashInfo == null) {
                if (mFlashType != FlashLed.FLASH_TYPE_DYNAMIC) {
                    setVideoResource(mFlashType);
                } else {
                    setVideoPath(FlashLed.FLASH_TYPE_DYNAMIC, mVideoPath);
                }
            } else {
                setCallFlashInfo(mFlashInfo);
            }

            if (mIsHideHangAndAnswerButton) {
                mIvCallAnswer.setVisibility(View.GONE);
                mIvCallHang.setVisibility(View.GONE);
            } else {
                mIvCallAnswer.setVisibility(View.VISIBLE);
                mIvCallHang.setVisibility(View.VISIBLE);
            }

        }

        return root;
    }

    public void setShowOrHideAnswerAndHangButton(Boolean isHide) {
        mIsHideHangAndAnswerButton = isHide;
        if (mIvCallAnswer != null && mIvCallHang != null) {
            if (isHide) {
                mIvCallAnswer.setVisibility(View.GONE);
                mIvCallHang.setVisibility(View.GONE);
            } else {
                mIvCallAnswer.setVisibility(View.VISIBLE);
                mIvCallHang.setVisibility(View.VISIBLE);
            }
        }
    }

    public void hideVideo() {
        if (mVideoView != null) {
            mVideoView.setZOrderOnTop(false);
        }
    }

    private void listener() {
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
                LogUtil.d("chenr", "video play error, what: " + what + ", extra: " + extra + ", flashType: " + mFlashType);
                return true;
            }
        });

//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                LogUtil.d("chenr", "設置屏幕顯示模式");
//                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
//            }
//        });

    }

    private void startAnim() {
        startAnswerAnim();
    }

    private void startAnswerAnim() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.answer_button_anim);
        root.findViewById(R.id.iv_call_answer).startAnimation(animation);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden() && mVideoView != null) {
            setVideoPath(mFlashType, mVideoPath);
//            mVideoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (mVideoView != null) {
                mVideoView.stopPlayback();
//                mPosition = mVideoView.getCurrentPosition();
            }
        } else {
            if (mVideoView != null) {
//                mVideoView.seekTo(mPosition);
                mVideoView.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }
}
