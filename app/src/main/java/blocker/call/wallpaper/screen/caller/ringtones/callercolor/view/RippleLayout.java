package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;

/**
 * Created by ChenR on 2017/6/16.
 */

public class RippleLayout extends RelativeLayout {

    private static final int SHOW_SPACING_TIME = 700;
    private static final int MSG_WAVE1_ANIMATION = 1;
    private static final int MSG_WAVE0_ANIMATION = 0;
    private static final int IMAMGEVIEW_SIZE = 80;
    /**
     * 三张波纹图片
     */
    private static final int SIZE = 2;

    /**
     * 动画默认循环播放时间
     */
    private int show_spacing_time = SHOW_SPACING_TIME;
    /**
     * 初始化动画集
     */
    private AnimationSet[] mAnimationSet = new AnimationSet[SIZE];
    /**
     * 水波纹图片
     */
    private ImageView[] imgs = new ImageView[SIZE];
    /**
     * 背景图片
     */
    private ImageView img_bg;
    /**
     * 水波纹和背景图片的大小
     */
    private float imageViewWidth = IMAMGEVIEW_SIZE;
    private float imageViewHeigth = IMAMGEVIEW_SIZE;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WAVE1_ANIMATION:
                    imgs[MSG_WAVE1_ANIMATION].startAnimation(mAnimationSet[MSG_WAVE1_ANIMATION]);
                    break;
//                case MSG_WAVE2_ANIMATION:
//                    imgs[MSG_WAVE2_ANIMATION].startAnimation(mAnimationSet[MSG_WAVE2_ANIMATION]);
//                    break;
                case MSG_WAVE0_ANIMATION:
                    imgs[MSG_WAVE0_ANIMATION].startAnimation(mAnimationSet[MSG_WAVE0_ANIMATION]);
                    break;
//                case MSG_WAVE3_ANIMATION:
//                    imgs[MSG_WAVE3_ANIMATION].startAnimation(mAnimationSet[MSG_WAVE3_ANIMATION]);
//                    break;
            }

        }
    };

    public RippleLayout(Context context) {
        this(context, null);
    }

    public RippleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributeSet(context, attrs);
        initView(context);
    }

    private void getAttributeSet(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleLayout);

        show_spacing_time = typedArray.getInt(R.styleable.RippleLayout_showSpacingTime, SHOW_SPACING_TIME);
        imageViewWidth = typedArray.getDimension(R.styleable.RippleLayout_imageViewWidth, IMAMGEVIEW_SIZE);
        imageViewHeigth = typedArray.getDimension(R.styleable.RippleLayout_imageViewHeight, IMAMGEVIEW_SIZE);
        LogUtil.d("TAG", "show_spacing_time=" + show_spacing_time + "mm imageViewWidth=" + imageViewWidth + "px  imageViewHeigth=" + imageViewHeigth + "px");
        typedArray.recycle();
    }

    private void initView(Context context) {
        setLayout(context);
        for (int i = 0; i < imgs.length; i++) {
            mAnimationSet[i] = initAnimationSet();
        }
    }

    private void setLayout(Context context) {
        LayoutParams params = new LayoutParams(Stringutil.dpToPx(context, 72), Stringutil.dpToPx(context, 72));
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        for (int i = 0; i < SIZE; i++) {
            imgs[i] = new ImageView(context);
            imgs[i].setImageResource(R.drawable.ring_ripple);
            addView(imgs[i], params);
        }
    }

    private AnimationSet initAnimationSet() {
        AnimationSet as = new AnimationSet(true);
        //缩放度：变大两倍
        ScaleAnimation sa = new ScaleAnimation(1f, 3f, 1f, 3f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(show_spacing_time * 3);
        sa.setRepeatCount(Animation.INFINITE);// 设置循环
        //透明度
        AlphaAnimation aa = new AlphaAnimation(1, 0.1f);
        aa.setDuration(show_spacing_time * 3);
        aa.setRepeatCount(Animation.INFINITE);//设置循环
        as.addAnimation(sa);
        as.addAnimation(aa);
        return as;
    }

    public void startWaveAnimation() {
        imgs[MSG_WAVE0_ANIMATION].setVisibility(VISIBLE);
        imgs[MSG_WAVE0_ANIMATION].startAnimation(mAnimationSet[0]);
//        mHandler.sendEmptyMessageDelayed(MSG_WAVE1_ANIMATION, show_spacing_time);
        imgs[MSG_WAVE1_ANIMATION].setVisibility(VISIBLE);
        mHandler.sendEmptyMessageDelayed(MSG_WAVE1_ANIMATION, show_spacing_time * 2);
//        mHandler.sendEmptyMessageDelayed(MSG_WAVE3_ANIMATION, show_spacing_time * 3);

    }

    /**
     * 停止水波纹动画
     */
    public void stopWaveAnimation() {
        for (int i = 0; i < imgs.length; i++) {
            imgs[i].clearAnimation();
            imgs[i].setVisibility(INVISIBLE);
        }
        mHandler.removeMessages(MSG_WAVE1_ANIMATION);
    }

    /**
     * 获取播放的速度
     */
    public int getShowSpacingTime () {
        return show_spacing_time;
    }

    /**
     * 设计播放的速度，默认是800毫秒
     */
    public void setShowSpacingTime(int showSpacingTime) {
        this.show_spacing_time = showSpacingTime;
    }
}
