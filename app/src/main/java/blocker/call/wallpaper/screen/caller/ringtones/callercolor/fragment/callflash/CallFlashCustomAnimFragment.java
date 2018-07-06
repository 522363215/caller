package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.callflash;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import com.md.flashset.View.FlashLed;
import com.md.flashset.View.FlashLedView;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.InCallHeartAnimLayout;
import event.EventBus;

/**
 * Created by ChenR on 2017/12/21.
 */

public class CallFlashCustomAnimFragment extends Fragment {
    private static final String TAG = "CallFlashGifFragment";
    private View root = null;
    private FlashLedView mFlashLedView;
    private InCallHeartAnimLayout mHeartView;
    private int mFlashType = -1;
    private boolean mIsHideHangAndAnswerButton;
    private View mIvCallAnswer;
    private View mIvCallHang;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_call_flash_custom_view, container, false);
            mFlashLedView = (FlashLedView) root.findViewById(R.id.flash_led_view);
            mHeartView = (InCallHeartAnimLayout) root.findViewById(R.id.flash_heart_view);
            mIvCallAnswer = root.findViewById(R.id.iv_call_answer);
            mIvCallHang = root.findViewById(R.id.iv_call_hang);

            startAnswerAnim();

            setAmin(mFlashType);

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

    private void startAnswerAnim() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.answer_button_anim);
        root.findViewById(R.id.iv_call_answer).startAnimation(animation);
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


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        update(hidden, mFlashType);
    }

    private void update(boolean hidden, int flashType) {
        switch (flashType) {
            case FlashLed.FLASH_TYPE_STREAMER:
            case FlashLed.FLASH_TYPE_FESTIVAL:
                if (hidden) {
                    if (mFlashLedView != null) {
                        mFlashLedView.stopAnim();
                    }
                } else {
                    if (mFlashLedView != null) {
                        mFlashLedView.startAnim();
                    }
                }
                break;
            case FlashLed.FLASH_TYPE_LOVE:
            case FlashLed.FLASH_TYPE_KISS:
            case FlashLed.FLASH_TYPE_ROSE:
                if (hidden) {
                    if (mHeartView != null) {
                        mHeartView.stopAnim();
                    }
                } else {
                    if (mHeartView != null) {
                        mHeartView.startAnim();
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setAmin(int flashType) {
        mFlashType = flashType;
        if (mFlashLedView == null || mHeartView == null) {
            return;
        }
        mFlashLedView.setVisibility(View.GONE);
        mHeartView.setVisibility(View.GONE);
        mFlashLedView.stopAnim();
        mHeartView.stopAnim();
        switch (mFlashType) {
            case FlashLed.FLASH_TYPE_STREAMER:
            case FlashLed.FLASH_TYPE_FESTIVAL:
                mFlashLedView.setFlashType(mFlashType);
                mFlashLedView.setVisibility(View.VISIBLE);
                mFlashLedView.startAnim();
                break;
            case FlashLed.FLASH_TYPE_LOVE:
                mHeartView.setAnimView(R.drawable.ico_anim_heart);
                mHeartView.setVisibility(View.VISIBLE);
                mHeartView.startAnim();
                break;
            case FlashLed.FLASH_TYPE_KISS:
                mHeartView.setAnimView(R.drawable.ic_anim_kiss);
                mHeartView.setVisibility(View.VISIBLE);
                mHeartView.startAnim();
                break;
            case FlashLed.FLASH_TYPE_ROSE:
                mHeartView.setAnimView(R.drawable.ic_anim_rose);
                mHeartView.setVisibility(View.VISIBLE);
                mHeartView.startAnim();
                break;
//            case FlashLed.FLASH_TYPE_CHRISTMAS_SNOWMAN:
//                mSnowingSurfaceView.setVisibility(View.VISIBLE);
//                mSnowingSurfaceView.startFall();
//                RingManager.playMusicInAsset(ApplicationEx.getInstance().getApplicationContext(), "snowing.mp3");
//                break;
        }
    }

}
