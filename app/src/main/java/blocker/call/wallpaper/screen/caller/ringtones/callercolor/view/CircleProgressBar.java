package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;


public class CircleProgressBar extends View {
    private final static String LOG_TAG = "CircleProgressBar";

    public enum BAR_TYPE {
        STORAGE, RAM
    }

    private int maxProgress = 100;
    private int mCurrentProgress = 0;
    private float mProgressStrokeWidth = 16;
    private float mProgressBGStrokeWidth = 12;

    private int CircleBgColor = Color.TRANSPARENT;
    private int CirclePaintColor = Color.rgb(40, 130, 250);
    private int CircleProgressBgColor = Color.argb(48, 214, 214, 214);
    private int CircleInsideBgColor = Color.TRANSPARENT;

    private boolean mDrawCircleInsideBG = true;
    private float mBarTextSize = 1.01f;
    private int mStartAngle = 140;
    private int mSweepAngle = 260;
    private float mCircleMargin = 0;

    private BarAnimation mBarAnimation;
    private int mLastProgress = 0;
    private int mProgressDelta = 0;
    private float mProgressAni;
    private boolean mIsClockwise  = true;//顺时针

    //画圆�?在的距形区域
    RectF mOval;
    Paint mPaint;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mOval = new RectF();
        mPaint = new Paint();
        TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);

        maxProgress = styled.getInt(R.styleable.CircleProgressBar_maxProgress, 100);
        mCurrentProgress = styled.getInt(R.styleable.CircleProgressBar_progress, 0);
        mProgressStrokeWidth = styled.getDimension(R.styleable.CircleProgressBar_progressStrokeWidth, 16);
        mProgressBGStrokeWidth = styled.getDimension(R.styleable.CircleProgressBar_progressBGStrokeWidth, 12);
        CircleBgColor = styled.getColor(R.styleable.CircleProgressBar_CircleBgColor, Color.TRANSPARENT);
        CirclePaintColor = styled.getColor(R.styleable.CircleProgressBar_CirclePaintColor, Color.rgb(45, 105, 155));
        CircleProgressBgColor = styled.getColor(R.styleable.CircleProgressBar_CircleProgressBgColor, Color.rgb(220, 220, 220));
        CircleInsideBgColor = styled.getColor(R.styleable.CircleProgressBar_CircleInsideBgColor, getContext().getResources().getColor(R.color.whiteSmoke));
        mDrawCircleInsideBG = styled.getBoolean(R.styleable.CircleProgressBar_drawCircleInsideBG, false);
        mStartAngle = styled.getInt(R.styleable.CircleProgressBar_startAngle, 140);
        mSweepAngle = styled.getInt(R.styleable.CircleProgressBar_sweepAngle, 260);
        mCircleMargin = styled.getDimension(R.styleable.CircleProgressBar_circleMargin, 0);
        styled.recycle();
        mBarAnimation = new BarAnimation();
        mBarAnimation.setDuration(1000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = this.getWidth();
        float height = this.getHeight();
        float margin;

        if (height > width) {
            margin = (height - width) * 2 / 5;
        } else {
            margin = 0;
        }

        if (width != height) {
            height = width;
        }

//		LogUtil.d(LOG_TAG, "2 width: "+width+", height: "+height);

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG)); //抗锯齿
        mPaint.setAntiAlias(true); // 设置画笔为抗锯齿
        mPaint.setFilterBitmap(true); //抗锯齿
        mPaint.setDither(true);     //抗锯齿
        canvas.drawColor(CircleBgColor); // 白色背景

        mOval.left = mProgressStrokeWidth / 2; // 左上角x
        mOval.top = mProgressStrokeWidth / 2 + margin; // 左上角y
        mOval.right = width - mProgressStrokeWidth / 2; // 左下角x
        mOval.bottom = height - mProgressStrokeWidth / 2 + margin; // 右下角y

//		LogUtil.d(LOG_TAG, "3 oval: "+oval.left+", "+oval.top+", "+oval.right+", "+oval.bottom);
//		oval.left=5;                              //左边  
//	    oval.top=5;                                   //上边  
//	    oval.right = 63;                             //右边  
//	    oval.bottom = 63;                                //下边  

        //-90, 12 o'clock
        //0, 3 o'clock
        if (mDrawCircleInsideBG) {
            float cx = (mOval.right - mOval.left + mProgressStrokeWidth) / 2;
            float cy = (mOval.bottom - mOval.top + mProgressStrokeWidth) / 2 + margin;
            float radius = (mOval.right - mOval.left
                    + mProgressStrokeWidth
                    + mCircleMargin) / 2;
            mPaint.setStrokeWidth(0); //线宽
            mPaint.setStyle(Style.FILL_AND_STROKE);
//			paint.setStrokeCap(Cap.ROUND);
            mPaint.setColor(CircleInsideBgColor);
            canvas.drawCircle(cx, cy, radius, mPaint);
        }

        mPaint.setStrokeWidth(mProgressStrokeWidth); //线宽
        mPaint.setStyle(Style.STROKE);
//		paint.setStrokeCap(Cap.ROUND);

        mPaint.setColor(CircleProgressBgColor);// 设置画笔颜色, 圈圈背景
        canvas.drawArc(mOval, mStartAngle, mSweepAngle, false, mPaint); // 绘制白色圆圈，即进度条背�?
//		paint.setColor(Color.rgb(0x57, 0x87, 0xb6));
//		paint.setColor(Color.WHITE);  //圈圈颜色， 画画时
        mPaint.setColor(CirclePaintColor);
        canvas.drawArc(mOval, mStartAngle, (mIsClockwise ? 1 : -1) * ((float) mProgressAni / maxProgress) * mSweepAngle, false, mPaint); // 绘制进度圆弧，这里是蓝色


//		paint.setStrokeWidth(3);
//		String text = progress + "%";
//		String text = "" + progress;
//		int textHeight = height / 4;
//		
//		
//		paint.setTextSize(textHeight*barTextSize);
//		
//		int textWidth = (int) paint.measureText(text, 0, text.length());
//		paint.setStyle(Style.FILL);
//		canvas.drawText(text, width / 2 - textWidth / 2, height / 2 +textHeight/2, paint);

    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setProgressAnim(int progress) {
        this.mProgressDelta = progress - mCurrentProgress;
        this.mLastProgress = mCurrentProgress;
        this.mCurrentProgress = progress;
        this.invalidate();
    }

    public void setProgress(int progress) {
        this.mProgressDelta = progress - mCurrentProgress;
        this.mLastProgress = mCurrentProgress;
        this.mCurrentProgress = progress;
        this.mProgressAni = progress;
        this.invalidate();
    }

    public int getProgress() {
        return this.mCurrentProgress;
    }

    public void setCircleWidth(float value) {
        this.mProgressStrokeWidth = value;
        this.invalidate();
    }

    public void setBGCircleWidth(int value) {
        this.mProgressBGStrokeWidth = value;
        this.invalidate();
    }

    public void setClockwise(boolean value) {
        this.mIsClockwise = value;
    }

    public void setProgressNotInUiThread(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > maxProgress) {
            progress = maxProgress;
        }
        if (progress <= maxProgress) {
            this.mCurrentProgress = progress;
            this.postInvalidate();
        }
    }

    public float getBarTextSize() {
        return mBarTextSize;
    }

    public void setBarTextSize(float barTextSize) {
        this.mBarTextSize = barTextSize;
    }

    public int getCircleBgColor() {
        return CircleBgColor;
    }

    public void setCircleBgColor(int circleBgColor) {
        CircleBgColor = circleBgColor;
    }

    public int getCirclePaintColor() {
        return CirclePaintColor;
    }

    public void setCirclePaintColor(int circlePaintColor) {
        CirclePaintColor = circlePaintColor;
    }

    public int getCircleProgressBgColor() {
        return CircleProgressBgColor;
    }

    public void setCircleProgressBgColor(int circleProgressBgColor) {
        CircleProgressBgColor = circleProgressBgColor;
    }

    public int getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(int startAngle) {
        this.mStartAngle = startAngle;
    }

    public int getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        this.mSweepAngle = sweepAngle;
    }

    public boolean isDrawCircleBG() {
        return mDrawCircleInsideBG;
    }

    public void setDrawCircleBG(boolean drawCircleBG) {
        this.mDrawCircleInsideBG = drawCircleBG;
    }


    //anim
    public void setAnimationDuration(long time) {
        mBarAnimation.setDuration(time);
    }

    public void startCustomAnimation() {
        this.startAnimation(mBarAnimation);
    }

    public class BarAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                mProgressAni = interpolatedTime * (mProgressDelta) + mLastProgress;
            } else {
                mProgressAni = Float.valueOf(mCurrentProgress);
            }
            postInvalidate();
        }

    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        if(splashAds != null){
            splashAds.onSplashAdsEnd();
        }
    }

    protected SplashAds splashAds;

    public interface SplashAds {

        void onSplashAdsEnd();
    }

    public void setSplashAds(SplashAds splashAds) {
        this.splashAds = splashAds;
    }
}
