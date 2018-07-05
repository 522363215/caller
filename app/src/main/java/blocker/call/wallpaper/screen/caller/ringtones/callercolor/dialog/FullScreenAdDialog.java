package blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;


import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.RoundProgressBar;

public class FullScreenAdDialog extends BaseDialog {
    private static final long CLOSE_COUNTDOWN_TIME = 4000;
    private FullScreenAdListener mListener;
    private Context mContext;
    private FontIconView mFivBtnClose;
    private RoundProgressBar mPbCountDown;
    private TextView mTvCountDown;
    private int mLastAnimatedValue;
    private RelativeLayout mLayoutCountDown;
    private boolean mCanClose;

    public FullScreenAdDialog(Context context) {
        super(context, R.style.FullScreenAdDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtil.hideStatuBar(getWindow().getDecorView());
        setContentView(R.layout.dialog_full_screen_ad);
        initView();
        listener();
        setCountDown();
    }

    private void setCountDown() {
        mFivBtnClose.setVisibility(View.GONE);
        mLayoutCountDown.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(CLOSE_COUNTDOWN_TIME);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
//                setCountDownText(animatedValue);
                mPbCountDown.setProgress(animatedValue);
                mLastAnimatedValue = animatedValue;
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLayoutCountDown.setVisibility(View.GONE);
                mFivBtnClose.setVisibility(View.VISIBLE);
                mCanClose = true;
            }
        });
        valueAnimator.start();
    }

    private void setCountDownText(int animatedValue) {
        int count = (int) (CLOSE_COUNTDOWN_TIME / 1000);//4
        for (int i = 0; i < count; i++) {
            if (animatedValue == i * (100 / count)) {
                mTvCountDown.setText(String.valueOf(count - 1 - i));
            }
        }
    }

    private void initView() {
        mFivBtnClose = (FontIconView) findViewById(R.id.btn_close);
        mPbCountDown = (RoundProgressBar) findViewById(R.id.pb_countdown);
        mTvCountDown = (TextView) findViewById(R.id.tv_countdown);
        mLayoutCountDown = (RelativeLayout) findViewById(R.id.layout_countdown);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCanClose) {
                if (mListener != null) {
                    mListener.onAdClose();
                }
            } else {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void show(FullScreenAdListener listener) {
        super.show();
        mListener = listener;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
        if (mListener != null) {
            mListener.onAdShow();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void listener() {
        mFivBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onAdClose();
                }
            }
        });

//        setOnDismissListener(new OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                if (mListener != null) {
//                    mListener.onAdClose();
//                }
//            }
//        });
    }

    public interface FullScreenAdListener {
        void onAdClose();

        void onAdShow();
    }
}
