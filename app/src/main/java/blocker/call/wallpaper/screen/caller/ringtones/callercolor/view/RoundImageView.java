package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;


import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;


/**
 * Created by luowp on 2016/8/5.
 */

public class RoundImageView extends ImageView {
    private static final float DEFAULT_CORNER_RADIUS = 6;

    private float mRoundPx = -1;
    private BitmapShader mShader;
    private Paint mPaint;
    private RectF mRectF;

    private int mMaxWidth;
    private int mMaxHeight;

    private boolean mShouldDrawRoundCorner;

    public RoundImageView(Context context) {
        super(context);
        mRoundPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, DEFAULT_CORNER_RADIUS,
                context.getResources().getDisplayMetrics());
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyle, 0);
        if (a != null) {
            mRoundPx = a.getDimensionPixelSize(R.styleable.RoundImageView_cornerRadius, -1);
            a.recycle();
        }

        if (mRoundPx < 0) {
            mRoundPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, DEFAULT_CORNER_RADIUS,
                    context.getResources().getDisplayMetrics());
        }
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    public void setMaxWidth(int maxWidth) {
        super.setMaxWidth(maxWidth);
        mMaxWidth = maxWidth;
    }

    @Override
    public void setMaxHeight(int maxHeight) {
        super.setMaxHeight(maxHeight);
        mMaxHeight = maxHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getDrawable() != null) {
            try {
                int width = Math.min(mMaxWidth, MeasureSpec.getSize(widthMeasureSpec));
                int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
                if (height > mMaxHeight) {
                    height = mMaxHeight;
                }
                setMeasuredDimension(width, height);
            }catch (Exception e){
                LogUtil.error(e);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    protected float getRoundPx() {
        return mRoundPx;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mRectF = null;
        mShouldDrawRoundCorner = false;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mRectF = null;
        mShouldDrawRoundCorner = false;
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mRectF = null;
        mShouldDrawRoundCorner = false;
    }

    protected void onDraw(Canvas canvas) {
        if (mRectF == null) {
            mRectF = new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            setupPaintWithShader();
        }

        if (mShouldDrawRoundCorner) {
            canvas.drawRoundRect(mRectF, mRoundPx, mRoundPx, mPaint);

        } else {
            super.onDraw(canvas);
        }
    }

    private void setupPaintWithShader() {
        Bitmap bitmap = null;
        Drawable drawable = getDrawable();

        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();

        } else if (drawable instanceof TransitionDrawable) {
            TransitionDrawable transitionDrawable = (TransitionDrawable) drawable;
            bitmap = ((BitmapDrawable) transitionDrawable.getDrawable(transitionDrawable.getNumberOfLayers() - 1)).getBitmap();
        }

        if (bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postScale(mRectF.right / bitmap.getWidth(), mRectF.bottom / bitmap.getHeight());
            mShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mShader.setLocalMatrix(matrix);
            mPaint.setShader(mShader);
            mShouldDrawRoundCorner = true;

        } else {
            mShouldDrawRoundCorner = false;
        }
    }
}