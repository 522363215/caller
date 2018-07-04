package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by js on 2016/2/15.
 * 可以禁止滑动的ViewPager
 */
public class NotScrollViewPager extends ViewPager {
    public NotScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private boolean isCanScroll = true;

    /**
     * 设置是否可以滑动
     * @param isCanScroll
     */
    public void setScrollAble(boolean isCanScroll){
        this.isCanScroll = isCanScroll;

    }
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (isCanScroll||arg0.getAction() == MotionEvent.ACTION_UP)
            return super.onTouchEvent(arg0);
        else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (isCanScroll||arg0.getAction() == MotionEvent.ACTION_UP)
            try {
                return super.onInterceptTouchEvent(arg0);
            }catch (Exception e){
                return false;
            }
        else
            return false;
    }
}
