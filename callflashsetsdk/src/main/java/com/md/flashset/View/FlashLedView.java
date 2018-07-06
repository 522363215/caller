package com.md.flashset.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.md.flashset.R;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangjinwei on 2017/3/31.
 */

public class FlashLedView extends View {

    private final String TAG = "led-test";

    private int mViewWidth = 0;
    private int mContentWidth = mViewWidth;
    private int mViewHeight = 0;
    private int mContentHeight = 16;

    // bar led
    Bitmap mLedDark;
    Bitmap mLedPurple;
    Bitmap mLedPurpleA;
    Bitmap mLedPurpleB;
    Bitmap mLedPurpleC;
    Bitmap mLedPurpleD;
    Bitmap mLedYellow;
    Bitmap mLedYellowA;
    Bitmap mLedYellowB;
    Bitmap mLedYellowC;
    Bitmap mLedYellowD;
    Bitmap mLedBlue;
    Bitmap mLedBlueA;
    Bitmap mLedBlueB;
    Bitmap mLedBlueC;
    Bitmap mLedBlueD;
    Bitmap mLedGreen;
    // round led
    Bitmap mBulbDark;
    Bitmap mBulbPurple;
    Bitmap mBulbYellow;
    Bitmap mBulbBlue;
    Bitmap mBulbGreen;

    private int mHorizonCount = 0;
    private int mVerticalCount = 0;

    private AtomicBoolean mStopAnim = new AtomicBoolean(false);
    private long mFlashInterval = 500;
    private FlashLed mFlashLed;

    private ViewTreeObserver mTreeObserver;

    Runnable mAnimator = new Runnable() {
        @Override
        public void run() {
            mFlashLed.calcNextFrame(mVerticalCount);
            if (!mStopAnim.get()) {
                postDelayed(this, mFlashInterval);
            }
            postInvalidate();
        }
    };

    public void setFlashType(int flashType) {
        if (mFlashLed != null) {
            mFlashLed.setFlashType(flashType);
        }
        mViewWidth = getWidth();
        mViewHeight = getHeight();
        mContentWidth = mViewWidth;
        mContentHeight = mViewHeight - FlashLed.VERTICAL_PADDING * 2;
        calcLedCount();
        mFlashLed.initLedLights(mContentWidth, mContentHeight, mHorizonCount, mVerticalCount);

        if (flashType == FlashLed.FLASH_TYPE_FESTIVAL) {
            setBackgroundResource(R.drawable.ic_bg_festival);
        }/* else if (flashType == FlashLed.FLASH_TYPE_CUSTOM) {
            String path = ApplicationEx.getInstance().getGlobalSettingPreference()
                    .getString(ConstantUtils.CALL_FLASH_CUSTOM_BG_PATH, "");
//            setBackground(TextUtils.isEmpty(path) ? new ColorDrawable(getResources().getColor(R.color.color_FF191B1E)) :
//            new BitmapDrawable(BitmapFactory.decodeFile(path)));

            if (TextUtils.isEmpty(path)) {
                setBackgroundResource(R.color.color_FF191B1E);
            } else {
                Bitmap bitmap = FileUtil.getBitmapFromPath(path);
                if (bitmap != null) {
                    Drawable drawable = new BitmapDrawable(bitmap);
                    setBackgroundDrawable(drawable);
                } else {
                    setBackgroundResource(R.color.color_FF191B1E);
                }
            }

        } */ else {
            setBackgroundResource(R.color.color_FF191B1E);
        }
    }

    public void startAnim() {
        mStopAnim.set(false);
        mFlashInterval = mFlashLed.getFlashInterval();
        post(mAnimator);
    }

    public void stopAnim() {
        mStopAnim.set(true);
        removeCallbacks(mAnimator);
    }

    public FlashLedView(Context context) {
        super(context);
        init(context, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    }

    public FlashLedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public int dpToPx(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp == 0 ? 0 : (int) (dp * scale + 0.5f);
    }

    private void init(Context context, AttributeSet attrs) {
        mViewWidth = getScreenWidth(context);
        mContentWidth = mViewWidth;
        mViewHeight = getScreenHeight(context);
        mContentHeight = mViewHeight - dpToPx(context, 16);

        try {
            loadBitmaps();
        } catch (Throwable e) {

        }
        mFlashLed = new FlashLed(context);
        mTreeObserver = getViewTreeObserver();
        if (mTreeObserver.isAlive()) {
            mTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mViewWidth = getWidth();
                    mViewHeight = getHeight();
                    mContentWidth = mViewWidth;
                    mContentHeight = mViewHeight - FlashLed.VERTICAL_PADDING * 2;
                    calcLedCount();
                    mFlashLed.initLedLights(mContentWidth, mContentHeight, mHorizonCount, mVerticalCount);
                }
            });
        }
    }

    private void loadBitmaps() {
        mLedDark = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_dark), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);

        mLedPurple = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_purple), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedPurpleA = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_purple_a), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedPurpleB = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_purple_b), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedPurpleC = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_purple_c), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedPurpleD = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_purple_d), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);

        mLedYellow = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_yellow), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedYellowA = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_yellow_a), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedYellowB = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_yellow_b), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedYellowC = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_yellow_c), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedYellowD = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_yellow_d), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);

        mLedBlue = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_blue), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedBlueA = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_blue_a), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedBlueB = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_blue_b), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedBlueC = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_blue_c), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);
        mLedBlueD = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_blue_d), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);

        mLedGreen = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_led_green), FlashLed.LED_WIDTH, FlashLed.LED_LENGTH, true);

        mBulbDark = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bulb_dark), FlashLed.BULB_WIDTH, FlashLed.BULB_LENGTH, true);
        mBulbPurple = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bulb_purple), FlashLed.BULB_WIDTH, FlashLed.BULB_LENGTH, true);
        mBulbYellow = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bulb_yellow), FlashLed.BULB_WIDTH, FlashLed.BULB_LENGTH, true);
        mBulbBlue = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bulb_blue), FlashLed.BULB_WIDTH, FlashLed.BULB_LENGTH, true);
        mBulbGreen = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bulb_green), FlashLed.BULB_WIDTH, FlashLed.BULB_LENGTH, true);
    }

    private int calcVerticalCount() {
        if (mFlashLed.getFlashType() == FlashLed.FLASH_TYPE_FESTIVAL) {
            return calcBulbVerticalCount();
        } else {
            return calcLedVerticalCount();
        }
    }

    //  n * LED_LENGTH - overlay * ( n - 1) <= ViewHeight
    //  n  <= (ViewHeight - overlay) / (LED_LENGTH - overlay)
    private int calcLedVerticalCount() {
        return (mContentHeight - mFlashLed.getLightOverlay()) / (mFlashLed.getLightLength() - mFlashLed.getLightOverlay());
    }

    //  n * BULB_LENGTH - overlay * ( n - 1) <= ViewHeight - 2 * BULB_LENGTH + 2 * BULK_CROSS
    //  n  <= (ViewHeight - overlay - 2 * BULB_LENGTH + 2 * BULK_CROSS) / (BULB_LENGTH - overlay)
    private int calcBulbVerticalCount() {
        return (mContentHeight - mFlashLed.getLightOverlay() - 2 * mFlashLed.getLightLength() + 2 * mFlashLed.getLightCross()) / (mFlashLed.getLightLength() - mFlashLed.getLightOverlay());
    }


    private int calcHorizonCount() {
        if (mFlashLed.getFlashType() == FlashLed.FLASH_TYPE_FESTIVAL) {
            return calcBulbHorizonCount();
        } else {
            return calcLedHorizonCount();
        }
    }

    //  n * LED_LENGTH - overlay * ( n - 1) <= ViewWidth - 2 * cross
    //  n  <= (ViewWidth - 2 * cross - overlay) / (LED_LENGTH - overlay)
    private int calcLedHorizonCount() {
        return (mContentWidth - 2 * mFlashLed.getLightCross() - mFlashLed.getLightOverlay()) / (mFlashLed.getLightLength() - mFlashLed.getLightOverlay());
    }

    //  n * LED_LENGTH - overlay * ( n - 1) <= ViewWidth
    //  n  <= (ViewWidth - overlay) / (LED_LENGTH - overlay)
    private int calcBulbHorizonCount() {
        return (mContentWidth - mFlashLed.getLightOverlay()) / (mFlashLed.getLightLength() - mFlashLed.getLightOverlay());
    }

    private void calcLedCount() {
        mHorizonCount = calcHorizonCount();
        mVerticalCount = calcVerticalCount();
    }

    private Bitmap getBitmap(FlashLed.LedLight led) {
        Bitmap bitmap = mLedDark;
        switch (led.colorType) {
            case Dark:
                bitmap = mLedDark;
                break;
            case Purple:
                bitmap = mLedPurple;
                break;
            case PurpleA:
                bitmap = mLedPurpleA;
                break;
            case PurpleB:
                bitmap = mLedPurpleB;
                break;
            case PurpleC:
                bitmap = mLedPurpleC;
                break;
            case PurpleD:
                bitmap = mLedPurpleD;
                break;
            case Yellow:
                bitmap = mLedYellow;
                break;
            case YellowA:
                bitmap = mLedYellowA;
                break;
            case YellowB:
                bitmap = mLedYellowB;
                break;
            case YellowC:
                bitmap = mLedYellowC;
                break;
            case YellowD:
                bitmap = mLedYellowD;
                break;
            case Blue:
                bitmap = mLedBlue;
                break;
            case BlueA:
                bitmap = mLedBlueA;
                break;
            case BlueB:
                bitmap = mLedBlueB;
                break;
            case BlueC:
                bitmap = mLedBlueC;
                break;
            case BlueD:
                bitmap = mLedBlueD;
                break;
            case Green:
                bitmap = mLedGreen;
                break;
            case BulbDark:
                bitmap = mBulbDark;
                break;
            case BulbPurple:
                bitmap = mBulbPurple;
                break;
            case BulbYellow:
                bitmap = mBulbYellow;
                break;
            case BulbBlue:
                bitmap = mBulbBlue;
                break;
            case BulbGreen:
                bitmap = mBulbGreen;
                break;
            default:
                break;
        }

        if (led.groupType == FlashLed.GroupType.Top || led.groupType == FlashLed.GroupType.Bottom) {
            bitmap = rotateBitmap(bitmap, 90);
        }
        return bitmap;
    }

    public Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void drawAllLights(Canvas canvas) {
        List<FlashLed.LedLight> allLights = mFlashLed.getAllLights();
        for (FlashLed.LedLight led : allLights) {
            canvas.drawBitmap(getBitmap(led), led.x, led.y, null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mStopAnim.get()) {
            canvas.drawColor(Color.BLACK);
        } else if (mFlashLed.mFlashType != FlashLed.FLASH_TYPE_CUSTOM) {
            drawAllLights(canvas);
        }
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return Math.max(metrics.widthPixels, metrics.heightPixels);
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return Math.min(metrics.widthPixels, metrics.heightPixels);
    }
}
