package com.md.flashset.View;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjinwei on 2017/4/2.
 */

public class FlashLed {

    public static int LED_WIDTH = 40;
    public static int LED_LENGTH = 64;

    public static int BULB_WIDTH = 56;
    public static int BULB_LENGTH = 56;

    // the overlay of same led.
    public static int LED_OVERLAY = 16;
    // the overlay in corner.
    public static int LED_CROSS = 12;

    // the overlay of same bulb
    public static int BULB_OVERLAY = 18;
    // the overlay in cover for bulb
    public static int BULB_CROSS = 18;

    public static int VERTICAL_PADDING = 8;

    public static int LEAST_LIGHT_SIZE = 24;

    private List<LedLight> mLeftLights = new ArrayList<>();
    private List<LedLight> mTopLights = new ArrayList<>();
    private List<LedLight> mRightLights = new ArrayList<>();
    private List<LedLight> mBottomLights = new ArrayList<>();

    private List<LedLight> mLeftTopLights = new ArrayList<>();
    private List<LedLight> mLeftBottomLights = new ArrayList<>();
    private List<LedLight> mRightTopLights = new ArrayList<>();
    private List<LedLight> mRightBottomLights = new ArrayList<>();

    List<LedLight> mAllLights = new ArrayList<>();

    public int mFlashType;

    public static final int FLASH_TYPE_STREAMER = 0;
    public static final int FLASH_TYPE_FESTIVAL = 1;
    public static final int FLASH_TYPE_LOVE = 2;
    public static final int FLASH_TYPE_KISS = 3;
    public static final int FLASH_TYPE_ROSE = 4;
    public static final int FLASH_TYPE_MONKEY = 5;

    public static final int FLASH_TYPE_CUSTOM = 6;
    public static final int FLASH_TYPE_DYNAMIC = 0x10001;

    public static int FLASH_TYPE_DEFAULT = FLASH_TYPE_ROSE;
    private int mViewWidth = 0;
    private int mViewHeight = 0;
    private int mVerticalCount = 0;
    private int mHorizonCount = 0;

    int mAnimStep;
    int mCurPos;

    public FlashLed(Context context) {
        LED_WIDTH = dpToPx(context, 40);
        LED_LENGTH = dpToPx(context, 64);
        BULB_WIDTH = dpToPx(context, 56);
        BULB_LENGTH = dpToPx(context, 56);
        LED_OVERLAY = dpToPx(context, 16);
        LED_CROSS = dpToPx(context, 12);
        BULB_OVERLAY = dpToPx(context, 18);
        BULB_CROSS = dpToPx(context, 18);
        VERTICAL_PADDING = dpToPx(context, 8);
    }

    enum ColorType {
        Dark,
        Purple, PurpleA, PurpleB, PurpleC, PurpleD,
        Yellow, YellowA, YellowB, YellowC, YellowD,
        Blue, BlueA, BlueB, BlueC, BlueD,
        Green,
        BulbDark,
        BulbPurple, BulbYellow, BulbBlue, BulbGreen
    }

    ColorType DefaultColorType = ColorType.Purple;

    enum GroupType {
        Left, Top, Right, Bottom, LeftTop, LeftBottom, RightTop, RightBottom
    }

    class LedLight {
        int x;
        int y;
        ColorType colorType;
        GroupType groupType;

        LedLight(int x, int y, ColorType colorType, GroupType groupType) {
            this.x = x;
            this.y = y;
            this.colorType = colorType;
            this.groupType = groupType;
        }
    }

    public void setFlashType(int flashType) {
        mFlashType = flashType;
    }

    public int getFlashType() {
        return mFlashType;
    }

    public List<LedLight> getAllLights() {
        return mAllLights;
    }

    public long getFlashInterval() {
        long interval = 500;
        switch (mFlashType) {
            case FLASH_TYPE_STREAMER:
                interval = 100;
                break;
//            case FLASH_TYPE_BLOOM:
//                interval = 200;
//                break;
//            case FLASH_TYPE_GRADIENT:
//                interval = 200;
//                break;
            case FLASH_TYPE_CUSTOM:
//            case FLASH_TYPE_NEON:
                interval = 300;
                break;
            case FLASH_TYPE_FESTIVAL:
                interval = 300;
                break;
            default:
                break;
        }
        return interval;
    }

    public void calcNextFrame(int verticalCount) {
        /*if (mFlashType == FLASH_TYPE_BLOOM) {
            mAnimStep = ++mAnimStep % 4;
            ColorType colorType = ColorType.Blue;
            switch (mAnimStep) {
                case 0:
                    colorType = ColorType.Blue;
                    break;
                case 1:
                    colorType = ColorType.Yellow;
                    break;
                case 2:
                    colorType = ColorType.Green;
                    break;
                case 3:
                    colorType = ColorType.Purple;
                    break;
                default:
                    break;
            }
            for (LedLight led : mAllLights) {
                led.colorType = colorType;
            }
        } else if (mFlashType == FLASH_TYPE_NEON) {
            mAnimStep = ++mAnimStep % 2;
            for (LedLight led : mAllLights) {
                led.colorType = mAnimStep == 0 ? ColorType.Blue : ColorType.Purple;
                if (mAllLights.indexOf(led) % 2 == 0) {
                    led.colorType = mAnimStep == 0 ? ColorType.Purple : ColorType.Blue;
                }
            }
        } else if (mFlashType == FLASH_TYPE_GRADIENT) {
            mAnimStep = ++mAnimStep % 2;
            if (mAnimStep == 0) {
                setLightsColor(GroupType.Top, ColorType.Purple);
                setLightsColor(GroupType.LeftBottom, ColorType.Blue);
                setLightsColor(GroupType.RightBottom, ColorType.Blue);
                setLightsColor(GroupType.LeftTop, ColorType.Dark);
                setLightsColor(GroupType.RightTop, ColorType.Dark);
                setLightsColor(GroupType.Bottom, ColorType.Dark);
            } else {
                setLightsColor(GroupType.Top, ColorType.Dark);
                setLightsColor(GroupType.LeftBottom, ColorType.Dark);
                setLightsColor(GroupType.RightBottom, ColorType.Dark);
                setLightsColor(GroupType.LeftTop, ColorType.Yellow);
                setLightsColor(GroupType.RightTop, ColorType.Green);
                setLightsColor(GroupType.Bottom, ColorType.Purple);
            }

        } else */
        if (mFlashType == FLASH_TYPE_STREAMER) {
            int allLightsSize = mAllLights.size() == 0 ? LEAST_LIGHT_SIZE : mAllLights.size();

            if (mAnimStep == 0) {
                int midVCount = verticalCount / 2;
                if (mLeftLights.isEmpty() && mViewHeight != 0 && mVerticalCount != 0) {
                    initLeftLights(mViewHeight, mVerticalCount);
                }
                if (mLeftLights.isEmpty()) return;
                LedLight led = mLeftLights.get(midVCount);
                mCurPos = mAllLights.indexOf(led);
            } else {
                mCurPos = ++mCurPos % allLightsSize;
            }

            for (LedLight led : mAllLights) {
                led.colorType = ColorType.Dark;
            }

            int posBlueD = mCurPos % allLightsSize;
            int posBlueC = (posBlueD + 1) % allLightsSize;
            int posBlueB = (posBlueD + 2) % allLightsSize;
            int posBlueA = (posBlueD + 3) % allLightsSize;
            int posBlue = (posBlueD + 4) % allLightsSize;

            mAllLights.get(posBlue).colorType = ColorType.Blue;
            mAllLights.get(posBlueA).colorType = ColorType.BlueA;
            mAllLights.get(posBlueB).colorType = ColorType.BlueB;
            mAllLights.get(posBlueC).colorType = ColorType.BlueC;
            mAllLights.get(posBlueD).colorType = ColorType.BlueD;

            int posPurpleD = (mCurPos + allLightsSize / 3) % allLightsSize;
            int posPurpleC = (posPurpleD + 1) % allLightsSize;
            int posPurpleB = (posPurpleD + 2) % allLightsSize;
            int posPurpleA = (posPurpleD + 3) % allLightsSize;
            int posPurple = (posPurpleD + 4) % allLightsSize;

            mAllLights.get(posPurpleD).colorType = ColorType.PurpleD;
            mAllLights.get(posPurpleC).colorType = ColorType.PurpleC;
            mAllLights.get(posPurpleB).colorType = ColorType.PurpleB;
            mAllLights.get(posPurpleA).colorType = ColorType.PurpleA;
            mAllLights.get(posPurple).colorType = ColorType.Purple;

            int posYellowD = (mCurPos + allLightsSize * 2 / 3) % allLightsSize;
            int posYellowC = (posYellowD + 1) % allLightsSize;
            int posYellowB = (posYellowD + 2) % allLightsSize;
            int posYellowA = (posYellowD + 3) % allLightsSize;
            int posYellow = (posYellowD + 4) % allLightsSize;

            mAllLights.get(posYellowD).colorType = ColorType.YellowD;
            mAllLights.get(posYellowC).colorType = ColorType.YellowC;
            mAllLights.get(posYellowB).colorType = ColorType.YellowB;
            mAllLights.get(posYellowA).colorType = ColorType.YellowA;
            mAllLights.get(posYellow).colorType = ColorType.Yellow;

            mAnimStep = ++mAnimStep % allLightsSize;

        } else if (mFlashType == FLASH_TYPE_FESTIVAL) {
            mAnimStep = ++mAnimStep % 2;
            for (int i = 0; i < mAllLights.size(); ++i) {
                mAllLights.get(i).colorType = ColorType.BulbDark;
                if (i % 8 == (1 - mAnimStep)) {
                    mAllLights.get(i).colorType = ColorType.BulbPurple;
                } else if (i % 8 == (3 - mAnimStep)) {
                    mAllLights.get(i).colorType = ColorType.BulbYellow;
                } else if (i % 8 == (5 - mAnimStep)) {
                    mAllLights.get(i).colorType = ColorType.BulbBlue;
                } else if (i % 8 == (7 - mAnimStep)) {
                    mAllLights.get(i).colorType = ColorType.BulbGreen;
                }
            }
        }/* else if (mFlashType == FLASH_TYPE_CUSTOM) {
            // 边缘动画使用霓虹灯;
            mAnimStep = ++mAnimStep % 2;
            for (LedLight led : mAllLights) {
                led.colorType = mAnimStep == 0 ? ColorType.Blue : ColorType.Purple;
                if (mAllLights.indexOf(led) % 2 == 0) {
                    led.colorType = mAnimStep == 0 ? ColorType.Purple : ColorType.Blue;
                }
            }
        }*/
    }

    private void setLightsColor(GroupType groupType, ColorType colorType) {
        List<LedLight> list = null;
        switch (groupType) {
            case Top:
                list = mTopLights;
                break;
            case Left:
                list = mLeftLights;
                break;
            case Bottom:
                list = mBottomLights;
                break;
            case LeftTop:
                list = mLeftTopLights;
                break;
            case LeftBottom:
                list = mLeftBottomLights;
                break;
            case RightTop:
                list = mRightTopLights;
                break;
            case RightBottom:
                list = mRightBottomLights;
                break;
        }

        if (list != null) {
            for (LedLight led : list) {
                led.colorType = colorType;
            }
        }
    }

    public int getLightWidth() {
        if (mFlashType == FLASH_TYPE_FESTIVAL || mFlashType == FLASH_TYPE_KISS || mFlashType == FLASH_TYPE_ROSE) {
            return BULB_WIDTH;
        } else {
            return LED_WIDTH;
        }
    }

    public int getLightLength() {
        if (mFlashType == FLASH_TYPE_FESTIVAL || mFlashType == FLASH_TYPE_KISS || mFlashType == FLASH_TYPE_ROSE) {
            return BULB_LENGTH;
        } else {
            return LED_LENGTH;
        }
    }

    public int getLightOverlay() {
        if (mFlashType == FLASH_TYPE_FESTIVAL || mFlashType == FLASH_TYPE_KISS || mFlashType == FLASH_TYPE_ROSE) {
            return BULB_OVERLAY;
        } else {
            return LED_OVERLAY;
        }
    }

    public int getLightCross() {
        if (mFlashType == FLASH_TYPE_FESTIVAL || mFlashType == FLASH_TYPE_KISS || mFlashType == FLASH_TYPE_ROSE) {
            return BULB_CROSS;
        } else {
            return LED_CROSS;
        }
    }

    private int getHorizonTotalLength(int horizonCount) {
        return getLightLength() * horizonCount - getLightOverlay() * (horizonCount - 1);
    }

    private int getVerticalTotalLength(int verticalCount) {
        return getLightLength() * verticalCount - getLightOverlay() * (verticalCount - 1);
    }

    private void initTopLights(int viewWidth, int horizonCount) {
        mTopLights.clear();
        int hLength = getHorizonTotalLength(horizonCount);
        int leftX = (viewWidth - hLength) / 2;
        int y = VERTICAL_PADDING;
        for (int i = 0; i < horizonCount; ++i) {
            int n = i + 1;
            int x = leftX + (n - 1) * getLightLength() - (n - 1) * getLightOverlay();
            LedLight led = new LedLight(x, y, DefaultColorType, GroupType.Top);
            mTopLights.add(led);
        }
    }

    private void initBottomLights(int viewWidth, int viewHeight, int horizonCount) {
        mBottomLights.clear();
        int hLength = getHorizonTotalLength(horizonCount);
        int leftX = (viewWidth - hLength) / 2;
        int y = viewHeight - getLightWidth();
        for (int i = 0; i < horizonCount; ++i) {
            int n = i + 1;
            int x = leftX + (n - 1) * getLightLength() - (n - 1) * getLightOverlay();
            LedLight led = new LedLight(x, y, DefaultColorType, GroupType.Bottom);
            mBottomLights.add(led);
        }
    }

    private void initLeftLights(int viewHeight, int verticalCount) {
        mLeftLights.clear();
        int vLength = getVerticalTotalLength(verticalCount);
        int topY = (viewHeight - vLength) / 2;
        int x = 0;
        for (int i = 0; i < verticalCount; ++i) {
            int n = i + 1;
            int y = topY + (n - 1) * getLightLength() - (n - 1) * getLightOverlay();
            LedLight led = new LedLight(x, y, DefaultColorType, GroupType.Left);
            mLeftLights.add(led);
        }
    }

    private void initRightLights(int viewWidth, int viewHeight, int verticalCount) {
        mRightLights.clear();
        int vLength = getVerticalTotalLength(verticalCount);
        int topY = (viewHeight - vLength) / 2;
        int x = viewWidth - getLightWidth();
        for (int i = 0; i < verticalCount; ++i) {
            int n = i + 1;
            int y = topY + (n - 1) * getLightLength() - (n - 1) * getLightOverlay();
            LedLight led = new LedLight(x, y, DefaultColorType, GroupType.Right);
            mRightLights.add(led);
        }
    }

    private void initAllLights() {
        synchronized (mAllLights) {
            mAllLights.clear();
            mAllLights.addAll(mTopLights);
            mAllLights.addAll(mRightLights);

            for (int i = mBottomLights.size() - 1; i >= 0; --i) {
                mAllLights.add(mBottomLights.get(i));
            }

            for (int i = mLeftLights.size() - 1; i >= 0; --i) {
                mAllLights.add(mLeftLights.get(i));
            }
        }
    }

    private void initOtherLights(int verticalCount) {
        mLeftBottomLights.clear();
        mLeftTopLights.clear();
        mRightBottomLights.clear();
        mRightTopLights.clear();

        int midVCount = verticalCount / 2;
        for (int i = 0; i < verticalCount; ++i) {
            if (i < midVCount) {
                mLeftTopLights.add(mLeftLights.get(i));
                mRightTopLights.add(mRightLights.get(i));
            } else {
                mLeftBottomLights.add(mLeftLights.get(i));
                mRightBottomLights.add(mRightLights.get(i));
            }
        }
    }

    public void initLedLights(int viewWidth, int viewHeight, int horizonCount, int verticalCount) {
        this.mViewHeight = viewWidth;
        this.mViewWidth = viewHeight;
        this.mHorizonCount = horizonCount;
        this.mVerticalCount = verticalCount;
        initTopLights(viewWidth, horizonCount);
        initBottomLights(viewWidth, viewHeight, horizonCount);
        initLeftLights(viewHeight, verticalCount);
        initRightLights(viewWidth, viewHeight, verticalCount);
        initAllLights();
        initOtherLights(verticalCount);
    }

    public int dpToPx(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp == 0 ? 0 : (int) (dp * scale + 0.5f);
    }
}
