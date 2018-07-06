package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;


public class BatteryProgressBar extends View {

    private final int progressBgColor;
    private final int progressColor;
    private float maxProgress = 10000;
    private float progress = 500;
    private float currentProgress = 0;

    private boolean isFirst = true;

    //画圆�?在的距形区域
    public BatteryProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.BatteryProgressBar);
        progressBgColor = styled.getColor(R.styleable.BatteryProgressBar_progressBgColor, getResources().getColor(R.color.progress_bg_default_color));
        progressColor = styled.getColor(R.styleable.BatteryProgressBar_progressColor, getResources().getColor(R.color.progress_default_color));
        styled.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = this.getWidth();
        float height = this.getHeight();

        RectF rect = new RectF(0, 0, width, height);
        Paint paint1 = new Paint();
//		paint1.setColor(getResources().getColor(R.color.lightGray));
//		paint1.setColor(Color.rgb(229, 229, 229));#E5E5E5
        paint1.setColor(progressBgColor);
        canvas.drawRect(rect, paint1);

//		LogUtil.d(LOG_TAG, "2 width: "+width+", height: "+height);
        Paint paint2 = new Paint();
//		paint2.setShader(gradient);
//		paint2.setColor(Color.rgb(60, 180, 40));#3CB428
        paint2.setColor(progressColor);
        RectF progressRect = null;
        progressRect = new RectF(0, 0, width * currentProgress / maxProgress, height);
        canvas.drawRect(progressRect, paint2);
    }


    public float getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setProgress(final float progress) {
        currentProgress = progress;
        invalidate();
    }

    public float getProgress() {
        return this.progress;
    }

    public float getCurrentProgress() {
        return this.currentProgress;
    }

}
