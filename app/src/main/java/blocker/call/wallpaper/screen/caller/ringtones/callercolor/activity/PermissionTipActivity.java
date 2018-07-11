package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;

public class PermissionTipActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout btnOk;
    private FontIconView fivClose;
    private ImageView ivHand;
    private ImageView ivSwitch;
    private TextView mTvDes;
    private String mPremissionFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.getWindow().setWindowAnimations(R.style.dialog_animation);
        setContentView(R.layout.activity_notification_listener_setting_tip);
        mPremissionFor = getIntent().getStringExtra("permission_for");

        btnOk = (LinearLayout) findViewById(R.id.layout_get_permission);
        fivClose = (FontIconView) findViewById(R.id.fiv_permission_close);
        mTvDes = (TextView) findViewById(R.id.textView5);

        ivHand = (ImageView) findViewById(R.id.iv_hand);
        ivSwitch = (ImageView) findViewById(R.id.iv_switch);

        btnOk.setOnClickListener(this);
        fivClose.setOnClickListener(this);

        ivSwitch.postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnim();
            }
        }, 1000);

        mTvDes.setText(Html.fromHtml(getString(R.string.call_flash_grant_permission_tip, mPremissionFor)));
        findViewById(R.id.layout_space1).setVisibility(View.GONE);
        findViewById(R.id.layout_space2).setVisibility(View.GONE);
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
