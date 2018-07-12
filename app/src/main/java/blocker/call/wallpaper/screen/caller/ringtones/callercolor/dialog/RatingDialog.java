package blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.rate.UriHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;

/**
 * Created by Admin on 2016/2/16.
 */
public class RatingDialog extends Dialog implements View.OnClickListener, DialogInterface.OnCancelListener {

    private TextView ok_button;
    private TextView cancel_button;
    private RatingBar rb_stars;
    private Context mContext;

    public RatingDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mContext = context;
    }

    public RatingDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = View.inflate(mContext, R.layout.dialog_rating, null);

        int width = DeviceUtil.getScreenWidth() - Stringutil.dpToPx(mContext, 32);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);

        setContentView(root, params);

        initView();
        listener();
        FlurryAgent.onStartSession(mContext);
    }


    private void initView() {
        ok_button = (TextView) findViewById(R.id.ok_button);
        cancel_button = (TextView) findViewById(R.id.cancel_button);
        rb_stars = (RatingBar) findViewById(R.id.rb_stars);
        ok_button.setEnabled(false);

        if (rb_stars.getRating() > 4.0) {
            ok_button.setBackgroundColor(mContext.getResources().getColor(R.color.button_green_color));
            ok_button.setEnabled(true);
        }

    }

    private void listener() {
        ok_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);
        setOnCancelListener(this);

        rb_stars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int iRating = (int) (rating * 10);
                if (iRating % 10 > 0) {
                    rb_stars.setRating(Math.round(rating));
                }
                if (rating > 0) {
                    ok_button.setBackgroundColor(mContext.getResources().getColor(R.color.button_green_color));
                    ok_button.setEnabled(true);
                } else {
                    ok_button.setBackgroundColor(mContext.getResources().getColor(R.color.color_FF99A2B0));
                    ok_button.setEnabled(false);
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                FlurryAgent.logEvent("RatingDialog----OkClick");
                float rating = rb_stars.getRating();
                if (rating > 4) {
                    try {
                        String packageName = getContext().getPackageName();
                        Intent intent = new Intent(Intent.ACTION_VIEW, UriHelper.getGooglePlay(packageName));
                        intent.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity");
                        getContext().startActivity(intent);
                        System.gc();
                    } catch (Exception e) {
                        String packageName = getContext().getPackageName();
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                UriHelper.getGooglePlay(packageName));
                        getContext().startActivity(intent);
                    }
                } else {
                    long feedbackTime = PreferenceHelper.getLong(PreferenceHelper.PREF_KEY_LAST_FEEDBACK_TIME, -1);
                    if (feedbackTime == -1 || !Stringutil.isToday(feedbackTime)) {
                        RatingOneDialog dialog = new RatingOneDialog(mContext);
                        dialog.setScore(Math.round(rating));
                        dialog.show();
                    }
                }

                //跳转google play
//                try {
//                    String packageName = getContext().getPackageName();
//                    Intent intent = new Intent(Intent.ACTION_VIEW, UriHelper.getGooglePlay(packageName));
//                    intent.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity");
//                    getContext().startActivity(intent);
//                    System.gc();
//                } catch (Exception e) {
//                    String packageName = getContext().getPackageName();
//                    Intent intent = new Intent(Intent.ACTION_VIEW,
//                            UriHelper.getGooglePlay(packageName));
//                    getContext().startActivity(intent);
//                }

//                PreferenceHelper.setAgreeShowDialog(getContext(), false);
                dismiss();

                break;
            case R.id.cancel_button:
                FlurryAgent.logEvent("RatingDialog----CancelClick");
                dismiss();
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        FlurryAgent.onEndSession(mContext);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        LogUtil.e("RatingDialog", "onCancel");
    }
}
