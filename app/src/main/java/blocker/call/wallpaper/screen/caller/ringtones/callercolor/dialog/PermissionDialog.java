package blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.SystemInfoUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;


/**
 * Created by admin on 2017/3/28.
 */

public class PermissionDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private LinearLayout btnOk;
    private OkListener okListrner;
    private FontIconView fivClose;
    private ImageView ivHand;
    private ImageView ivSwitch;
    private CancelListener cancelListrner;
    private View mLayoutNormal;
    private View mLayoutXiaoMi;
    private ImageView ivHandXiaoMi;
    private ImageView ivSwitchXiaoMi;

    public PermissionDialog(@NonNull Context context) {
        super(context);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.getWindow().setWindowAnimations(R.style.dialog_animation);
        this.context = context;
        FlurryAgent.onStartSession(ApplicationEx.getInstance());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_perrmission_hint_dialog);
        btnOk = (LinearLayout) findViewById(R.id.layout_get_permission);
        fivClose = (FontIconView) findViewById(R.id.fiv_permission_close);

        mLayoutNormal = findViewById(R.id.layout_normal);
        ivHand = (ImageView) findViewById(R.id.iv_hand);
        ivSwitch = (ImageView) findViewById(R.id.iv_switch);

        mLayoutXiaoMi = findViewById(R.id.layout_xiaomi);
        ivHandXiaoMi = (ImageView) findViewById(R.id.iv_hand_xiao_mi);
        ivSwitchXiaoMi = (ImageView) findViewById(R.id.iv_switch_xiao_mi);


        btnOk.setOnClickListener(this);
        fivClose.setOnClickListener(this);


        if (SystemInfoUtil.isMiui()) {
            mLayoutNormal.setVisibility(View.GONE);
            mLayoutXiaoMi.setVisibility(View.VISIBLE);
            ivSwitchXiaoMi.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startXiaoMiAnim();
                }
            }, 1000);
        } else {
            mLayoutNormal.setVisibility(View.VISIBLE);
            mLayoutXiaoMi.setVisibility(View.GONE);
            ivSwitch.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAnim();
                }
            }, 1000);
        }
    }

    private void startAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(ivHand, "translationX", ivSwitch.getTranslationX(), ivSwitch.getTranslationX() + ivSwitch.getWidth() * 2 / 3);
        animator.setDuration(1000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ivSwitch.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivSwitch.setBackgroundResource(R.drawable.ic_switch_off);
                        startAnim();
                    }
                }, 1000);
                ivSwitch.setBackgroundResource(R.drawable.ic_switch_on);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void startXiaoMiAnim() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivHandXiaoMi, "scaleX", 1.0f, 0.8f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivHandXiaoMi, "scaleY", 1.0f, 0.8f);

        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ivSwitchXiaoMi.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivSwitchXiaoMi.setBackgroundResource(R.drawable.icon_xiao_mi_permission_deny);
                        startXiaoMiAnim();
                    }
                }, 1000);
                ivSwitchXiaoMi.setBackgroundResource(R.drawable.icon_xiao_mi_permission_accept);
            }
        });
        animatorSet.start();
    }

    @Override
    public void show() {
        super.show();
        //WindowManager windowManager = ((Activity) mContext).getWindowManager();
        //Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        //lp.width = (int) (display.getWidth()); //设置宽度
        lp.width = metrics.widthPixels;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_get_permission:
                dismiss();
                if (okListrner != null) {
                    okListrner.okAction();
                    FlurryAgent.logEvent("PermissionDialog----okAction");
                }
                break;
            case R.id.fiv_permission_close:
                dismiss();
                if (cancelListrner != null) {
                    cancelListrner.cancelAction();
                    FlurryAgent.logEvent("PermissionDialog----cancelAction");
                }
                break;
        }

        FlurryAgent.onEndSession(ApplicationEx.getInstance());

    }

    public interface OkListener {
        void okAction();
    }

    public void setOkListener(OkListener okListener) {
        this.okListrner = okListener;
    }

    public interface CancelListener {
        void cancelAction();
    }

    public void setCancelListener(CancelListener listener) {
        this.cancelListrner = listener;
    }

}
