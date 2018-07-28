package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.SystemInfoUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;

public class PermissionTipActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout btnOk;
    private FontIconView fivClose;
    private ImageView ivHand;
    private ImageView ivSwitch;
    private TextView mTvDes;
    private String mPremissionFor;
    private RelativeLayout mLayoutNormal;
    private RelativeLayout mLayoutXiaoMi;
    private ImageView mIvHandXiaoMi;
    private ImageView mIvSwitchXiaoMi;
    private TextView mTvXiaoMiPermissionTitle;
    private TextView mTvXiaoMiPermissionDes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.getWindow().setWindowAnimations(R.style.dialog_animation);
        setContentView(R.layout.activity_notification_listener_setting_tip);
        mPremissionFor = getIntent().getStringExtra("permission_for");
        boolean isAutostartboot = getIntent().getBooleanExtra("is_auto_start_boot", false);
        boolean isShowOnLock = getIntent().getBooleanExtra("is_show_on_lock", false);

        mLayoutNormal = findViewById(R.id.layout_normal);
        btnOk = (LinearLayout) findViewById(R.id.layout_get_permission);
        fivClose = (FontIconView) findViewById(R.id.fiv_permission_close);
        mTvDes = (TextView) findViewById(R.id.textView5);
        ivHand = (ImageView) findViewById(R.id.iv_hand);
        ivSwitch = (ImageView) findViewById(R.id.iv_switch);

        //小米(show on lock 和 overlay)
        mLayoutXiaoMi = findViewById(R.id.layout_xiaomi);
        mTvXiaoMiPermissionTitle = (TextView) findViewById(R.id.tv_xiao_mi_permission_title);
        mTvXiaoMiPermissionDes = (TextView) findViewById(R.id.tv_xiao_mi_permission_des);
        mIvHandXiaoMi = (ImageView) findViewById(R.id.iv_hand_xiao_mi);
        mIvSwitchXiaoMi = (ImageView) findViewById(R.id.iv_switch_xiao_mi);

        btnOk.setOnClickListener(this);
        fivClose.setOnClickListener(this);

        findViewById(R.id.layout_space1).setVisibility(View.GONE);
        findViewById(R.id.layout_space2).setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mPremissionFor)) {
            if (SystemInfoUtil.isMiui()) {
                mLayoutNormal.setVisibility(View.GONE);
                mLayoutXiaoMi.setVisibility(View.VISIBLE);
                mTvDes.setText(Html.fromHtml(getString(R.string.call_flash_grant_permission_tip, mPremissionFor)) + getString(R.string.permission_xiao_mi_tip));
                mTvXiaoMiPermissionTitle.setText(R.string.permission_xiao_mi_overlay_title);
                mTvXiaoMiPermissionDes.setText(R.string.permission_xiao_mi_overlay_des);
                mIvSwitchXiaoMi.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startXiaoMiAnim();
                    }
                }, 1000);
            } else {
                mLayoutNormal.setVisibility(View.VISIBLE);
                mLayoutXiaoMi.setVisibility(View.GONE);
                mTvDes.setText(Html.fromHtml(getString(R.string.call_flash_grant_permission_tip, mPremissionFor)));
                ivSwitch.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAnim();
                    }
                }, 1000);
            }
        } else if (isAutostartboot) {
            mLayoutNormal.setVisibility(View.VISIBLE);
            mLayoutXiaoMi.setVisibility(View.GONE);
            mTvDes.setText(getString(R.string.permission_set_auto_start));//xiaomi
            ivSwitch.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAnim();
                }
            }, 1000);
        } else if (isShowOnLock) {
            mLayoutNormal.setVisibility(View.GONE);
            mLayoutXiaoMi.setVisibility(View.VISIBLE);
            mTvDes.setText(getString(R.string.permission_sett_lock_screen) + getString(R.string.permission_xiao_mi_tip));//xiaomi
            mTvXiaoMiPermissionTitle.setText(R.string.permission_xiao_mi_show_on_lock_title);
            mTvXiaoMiPermissionDes.setText(R.string.permission_xiao_mi_show_on_lock_des);
            mIvSwitchXiaoMi.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startXiaoMiAnim();
                }
            }, 1000);
        }
    }

    private void startXiaoMiAnim() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mIvHandXiaoMi, "scaleX", 1.0f, 0.8f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mIvHandXiaoMi, "scaleY", 1.0f, 0.8f);

        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIvSwitchXiaoMi.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIvSwitchXiaoMi.setBackgroundResource(R.drawable.icon_xiao_mi_permission_deny);
                        startXiaoMiAnim();
                    }
                }, 1000);
                mIvSwitchXiaoMi.setBackgroundResource(R.drawable.icon_xiao_mi_permission_accept);
            }
        });
        animatorSet.start();
    }

    @Override
    public void onClick(View v) {
        finish();
        switch (v.getId()) {
            case R.id.layout_get_permission:
                FlurryAgent.logEvent("PermissionTipActiivty----okAction");
                break;
            case R.id.fiv_permission_close:
                FlurryAgent.logEvent("PermissionTipActiivty----cancelAction");
                break;
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
}
