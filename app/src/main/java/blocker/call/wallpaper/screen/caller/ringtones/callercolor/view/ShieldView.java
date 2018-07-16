package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

public class ShieldView extends FrameLayout {
    private float mWidth;
    private float mHight;
    private float mDensity;
    private Paint mPaint;
    private Paint mPaint2;
    private Paint mPaint3;
    private Paint mPaint4;
    private Paint mPaint5;
    private Paint mPaint6;
    private ShieldScanView mShieldScanView;
    private Context mContext;
    private Shader mShader;
    private Shader mShader2;
    private ObjectAnimator mAnimator;

    private boolean isVisible = true;


    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public ShieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        mDensity = displayMetrics.density;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xffffffff);
        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setStyle(Paint.Style.STROKE);
        mPaint2.setStrokeWidth(7 * mDensity);
        mPaint3 = new Paint();
        mPaint3.setAntiAlias(true);
        mPaint3.setStyle(Paint.Style.FILL);
        mPaint4 = new Paint();
        mPaint4.setAntiAlias(true);
        mPaint4.setStyle(Paint.Style.FILL);
        mPaint4.setColor(Color.WHITE);
        mPaint5 = new Paint();
        mPaint5.setAntiAlias(true);
        mPaint5.setStyle(Paint.Style.FILL);
        mPaint5.setColor(0xFFE3FF00);
        mPaint6 = new Paint();
        mPaint6.setAntiAlias(true);
        mPaint6.setStyle(Paint.Style.FILL);
        mPaint6.setColor(0xFE3FF00);
        mPaint2.setStrokeWidth(6 * mDensity);
        mShieldScanView = new ShieldScanView(context, attrs);
        addView(mShieldScanView);
        this.mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void startScanAnimation(int time) {
        mAnimator = ObjectAnimator.ofFloat(mShieldScanView, "rotation", 0f, 360f);
        mAnimator.setDuration(time);
        mAnimator.setRepeatCount(-1);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.start();
    }

    public void duang() {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    float f = 300;
                    for (int i = 0; i <= f; i++) {
                        Thread.sleep(10);
                        mShader = new SweepGradient(mWidth / 2, mHight / 2,
                                new int[]{0x0000C858, 0x8000C858, 0xff00C858, 0xff00C858, 0x0000C858, 0x0000C858}, new float[]{i / f / 4, i / f / 2, 3 * i / f / 4, i / f, i / f, 1f});
                        mShader2 = new SweepGradient(mWidth / 2, mHight / 2,
                                new int[]{0x00E3FF00, 0x40E3FF00, 0x8FE3FF00, 0x80E3FF00, 0x00E3FF00, 0x00E3FF00}, new float[]{i / f / 4, i / f / 2, 3 * i / f / 4, i / f, i / f, 1f});

                        //设置渲染开始的角度
                        Matrix matrix = new Matrix();
                        matrix.preRotate(-90f, mWidth / 2, mHight / 2);
//                        mShader.setLocalMatrix(matrix);
                        mShader2.setLocalMatrix(matrix);

//                        mPaint2.setShader(mShader);
                        mPaint3.setShader(mShader2);
                        mShieldScanView.postInvalidate();
                    }
                    mShieldScanView.post(new Runnable() {
                        @Override
                        public void run() {
                            startScanAnimation(3000);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        t.start();
    }

    public void stop() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHight = getMeasuredHeight();
        /*Shader shieldShader = new SweepGradient(width/2,height/2,new int[]{0xffffffff,0xffffffff,0xff000000,0xffffffff},new float[]{0f,0.25f,0.5f,0.75f});
        p.setShader(shieldShader);*/
    }

    private class ShieldScanView extends View {

        public ShieldScanView(Context context, AttributeSet attrs) {
            super(context, attrs);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (/*mShader != null &&*/ mShader2 != null && isVisible) {
//                canvas.drawCircle(mWidth / 2, mHight / 2, 96 * mDensity, mPaint2);
                canvas.drawCircle(mWidth / 2, mHight / 2, 160 * mDensity, mPaint3);
                canvas.drawLine(mWidth / 2, mHight / 2, mWidth / 2, 0, mPaint6);
                canvas.drawCircle(mWidth / 2, mHight / 2, 8 * mDensity, mPaint5);
                canvas.drawCircle(mWidth / 2, mHight / 2, 4 * mDensity, mPaint4);
            }
        }
    }
}