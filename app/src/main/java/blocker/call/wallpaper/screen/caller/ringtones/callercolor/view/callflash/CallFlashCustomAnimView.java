package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.callflash;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.md.flashset.View.FlashLed;
import com.md.flashset.View.FlashLedView;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.InCallHeartAnimLayout;

public class CallFlashCustomAnimView extends RelativeLayout {
    private Context mContext;
    private FlashLedView mFlashLedView;
    private InCallHeartAnimLayout mHeartView;
    private int mFlashType;

    public CallFlashCustomAnimView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CallFlashCustomAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CallFlashCustomAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_call_flash_custom_anim_view, this);

        mFlashLedView = (FlashLedView) findViewById(R.id.flash_led_view);
        mHeartView = (InCallHeartAnimLayout) findViewById(R.id.flash_heart_view);
    }

    public void startAnim(int flashType) {
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
        }
    }

    public void update(boolean isStop, int flashType) {
        switch (flashType) {
            case FlashLed.FLASH_TYPE_STREAMER:
            case FlashLed.FLASH_TYPE_FESTIVAL:
                if (isStop) {
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
                if (isStop) {
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
}
