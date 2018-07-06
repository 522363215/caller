package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.md.flashset.View.FlashLed;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.CallFlashAlbumactivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.NumberUtil;

/**
 * Created by Zhq on 2018/1/31.
 * 包括头像，号码，名字，地址等信息
 */
public class CallFlashAvatarInfoView extends RelativeLayout {
    private Context mContext;
    private AvatarView mAvatarCall;
    private TextView mTvNumber;
    private TextView mTvName;
    private TextView mTvLocation;
    private FontIconView mFivCustom;

    public CallFlashAvatarInfoView(Context context) {
        super(context);
        init(context, null);
    }

    public CallFlashAvatarInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CallFlashAvatarInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CallFlashAvatarInfoView, 0, 0);
        boolean layoutSmall = attributes.getBoolean(R.styleable.CallFlashAvatarInfoView_layoutSmall, false);
        if (layoutSmall) {
            LayoutInflater.from(context).inflate(R.layout.layout_call_flash_avatar_info_small, this);
        } else {
            LayoutInflater.from(context).inflate(R.layout.layout_call_flash_avatar_info, this);
        }
        mAvatarCall = (AvatarView) findViewById(R.id.av_call);
        mTvNumber = (TextView) findViewById(R.id.tv_call_number);
        mTvName = (TextView) findViewById(R.id.tv_call_name);
        mTvLocation = (TextView) findViewById(R.id.tv_call_location);
        mFivCustom = findViewById(R.id.fiv_avatar_add_custom);

        mTvNumber.setShadowLayer(10f, 0, 0, 0xff000000);
        mTvName.setShadowLayer(10f, 0, 0, 0xff000000);
        mTvLocation.setShadowLayer(5f, 0, 0, 0xff000000);

        mFivCustom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext != null) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, CallFlashAlbumactivity.class);
                    if (getContext() != null && getContext() instanceof Activity) {
                        intent.putExtra(ConstantUtils.COME_FROM_CALLAFTER, ((Activity) getContext()).getIntent()
                                .getBooleanExtra(ConstantUtils.COME_FROM_CALLAFTER, false));
                        intent.putExtra(ConstantUtils.COME_FROM_PHONEDETAIL, ((Activity) getContext()).getIntent()
                                .getBooleanExtra(ConstantUtils.COME_FROM_PHONEDETAIL, false));
                    }
//                intent.putStringArrayListExtra(ConstantUtils.NUMBER_FOR_CALL_FLASH, mNumbersForCallFlash);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    public void setAvatar(String uri) {
        setAvatar(0);//還原
        mAvatarCall.setAvatarUri(uri);
    }

    public void setAvatar(Bitmap bitmap) {
        setAvatar(0);//還原
        mAvatarCall.setAvatarBitmap(bitmap);
    }

    public void setAvatar(int drawableResId) {
        mAvatarCall.setAvatarDrawable(drawableResId);
    }

    public void setDefaultAvatar(int flashType) {
        if (flashType == FlashLed.FLASH_TYPE_PANDA) {
            mAvatarCall.setAvatarDrawable(R.drawable.ico_panda_head);
        } else {
            mAvatarCall.setAvatarDrawable(0);
        }
    }

    public void setDefaultAvatar(String name) {
        if (ConstantUtils.CALL_FLASH_THEME_GIF_NAME_PANDA.equals(name)) {
            ((CallFlashAvatarInfoView) findViewById(R.id.callFlashAvatarInfoView)).setAvatar(R.drawable.ico_panda_head);
        } else {
            ((CallFlashAvatarInfoView) findViewById(R.id.callFlashAvatarInfoView)).setAvatar(0);
        }
    }

    public void setNumber(String number) {
        mTvNumber.setVisibility(VISIBLE);
        mTvNumber.setText(NumberUtil.getLocalizationNumber(number));
    }

    public void setName(String name) {
        mTvName.setVisibility(VISIBLE);
        mTvName.setText(name);
    }

    public void setLocation(String location) {
        mTvLocation.setVisibility(VISIBLE);
        mTvLocation.setText(location);
    }

    public void isCustomFlash(boolean isCustom) {
        if (mFivCustom != null) {
            mFivCustom.setVisibility(isCustom ? View.VISIBLE : View.GONE);
        }
    }
}
