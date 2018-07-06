package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;


public class DrawHookView extends View implements ValueAnimator.AnimatorUpdateListener {
    public static final int TYPE_DRAW_HOOK = 0;
    public static final int TYPE_DRAW_CROSS = 1;
    //线1的x轴
    private int line1_x = 0;
    //线1的y轴
    private int line1_y = 0;
    //线2的x轴
    private int line2_x = 0;
    //线2的y轴
    private int line2_y = 0;
    private Paint mPaint;
    private int mStrokeWidth = 10;
    private PathMeasure mTickPathMeasure;
    private boolean mIsStart;
    private Path mPathRight;
    private Path mPathRightDst;

    private Path mPathLine1;
    private Path mPathLine2;
    private Path mPathLine1Dst;
    private Path mPathLine2Dst;
    private PathMeasure mPathMeasure;
    private PathMeasure mPathMeasure2;
    private ValueAnimator mRightAnimator;
    private float mRightPercent;
    private int mType;
    private AnimListener mAnimListener;

    public DrawHookView(Context context) {
        super(context);
        init();
    }

    public DrawHookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawHookView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setDrawType(int type) {
        this.mType = type;
    }

    private void init() {
        mPaint = new Paint();
        //设置画笔颜色
        mPaint.setColor(getResources().getColor(R.color.white));
        mPaint.setStrokeWidth(10);
        //消除锯齿
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);


        mPathRight = new Path();
        mPathRightDst = new Path();

        mPathLine1 = new Path();
        mPathLine2 = new Path();
        mPathLine1Dst = new Path();
        mPathLine2Dst = new Path();

        mPathMeasure = new PathMeasure();
        mPathMeasure2 = new PathMeasure();

        mRightAnimator = ValueAnimator.ofFloat(0, 1);
        mRightAnimator.setDuration(1000);
        mRightAnimator.addUpdateListener(this);
    }

    public void Start() {
        if (mRightAnimator != null) {
            mRightAnimator.start();
        }

    }

    //绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//            //获取圆心的x坐标
//            int center = getWidth() / 2;
//            int center1 = center - getWidth() / 5;
//            //圆弧半径
//            int radius = getWidth() / 2 - 10;
//
//            /**
//             * 绘制对勾
//             */
//            if (line1_x < radius / 3) {
//                line1_x++;
//                line1_y++;
//            }
//
//            //画第一根线
//            canvas.drawLine(center1, center, center1 + line1_x, center + line1_y, mPaint);
//
//            if (line1_x == radius / 3) {
//                line2_x = line1_x;
//                line2_y = line1_y;
//                line1_x++;
//                line1_y++;
//            }
//
//            if (line1_x >= radius / 3 && line2_x <= radius) {
//                line2_x += 4;
//                line2_y -= 4;
//            }
//
//            //画第二根线
//            canvas.drawLine(center1 + line1_x - 5, center + line1_y, center1 + line2_x, center + line2_y, mPaint);
//            //每隔10毫秒界面刷新
//            postInvalidateDelayed(10);
        LogUtil.d("DrawHookView", "onDraw");
        if (mType == TYPE_DRAW_HOOK) {
            mPathRight.moveTo(getWidth() / 4, getWidth() / 2);
            mPathRight.lineTo(5 * getWidth() / 12, 2 * getWidth() / 3);
            mPathRight.lineTo(5 * getWidth() / 6, 3 * getWidth() / 12);
            mPathMeasure.nextContour();
            mPathMeasure.setPath(mPathRight, false);
            mPathMeasure.getSegment(0, mRightPercent * mPathMeasure.getLength(), mPathRightDst, true);
            canvas.drawPath(mPathRightDst, mPaint);
        } else {
            mPathLine1.moveTo(getWidth() / 3, getWidth() / 4);
            mPathLine1.lineTo(2 * getWidth() / 3, 3 * getWidth() / 4);
            mPathMeasure.nextContour();
            mPathMeasure.setPath(mPathLine1, false);
//            float pathLine1StopD = 0;
//            if (mRightPercent > 0 && mRightPercent <= 0.5) {
//                pathLine1StopD = mRightPercent * 2 * mPathMeasure.getLength();
//            } else {
//                pathLine1StopD = mPathMeasure.getLength();
//            }
//            mPathMeasure.getSegment(0, pathLine1StopD, mPathLine1Dst, true);
            mPathMeasure.getSegment(0, mRightPercent * mPathMeasure.getLength(), mPathLine1Dst, true);
            canvas.drawPath(mPathLine1Dst, mPaint);


//            if (mRightPercent > 0.5) {
            mPathLine2.moveTo(2 * getWidth() / 3, getWidth() / 4);
            mPathLine2.lineTo(getWidth() / 3, 3 * getWidth() / 4);
            mPathMeasure2.nextContour();
            mPathMeasure2.setPath(mPathLine2, false);
//                mPathMeasure2.getSegment(0, (0.5f * mRightPercent + 0.5f) * mPathMeasure2.getLength(), mPathLine2Dst, true);
            mPathMeasure2.getSegment(0, mRightPercent * mPathMeasure2.getLength(), mPathLine2Dst, true);
            canvas.drawPath(mPathLine2Dst, mPaint);
//            }
        }
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        LogUtil.d("DrawHookView", "onAnimationUpdate animation:" + animation.getAnimatedValue());
        mRightPercent = (float) animation.getAnimatedValue();
        postInvalidateDelayed(0);
        if (mRightPercent >= 1.0) {
            if (mAnimListener != null) {
                mAnimListener.onAnimFinish();
            }
        }
    }

    public interface AnimListener {
        void onAnimFinish();
    }

    public void setAnimListener(AnimListener listener) {
        this.mAnimListener = listener;
    }
}