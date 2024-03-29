/*
 * Copyright (C) 2014 Balys Valentukevicius
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;


import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

import static android.view.GestureDetector.SimpleOnGestureListener;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


/**
 * Changed by Licheng.
 * <br/>{@code mrl_rippleHoverDuration} is added in attributes.xml to support the setting of hover speed.
 * <br/>{@code private int     rippleHoverDuration;} is added to support the setting of hover speed.
 */
public class RippleFrameLayout extends FrameLayout {

    private static final int     DEFAULT_DURATION        = 350;
    private static final int     DEFAULT_FADE_DURATION   = 75;
    private static final float   DEFAULT_DIAMETER_DP     = 35;
    private static final float   DEFAULT_ALPHA           = 0.2f;
    private static final int     DEFAULT_COLOR           = Color.BLACK;
    private static final int     DEFAULT_BACKGROUND      = Color.TRANSPARENT;
    private static final boolean DEFAULT_HOVER           = true;
    private static final boolean DEFAULT_DELAY_CLICK     = true;
    private static final boolean DEFAULT_PERSISTENT      = false;
    private static final boolean DEFAULT_SEARCH_ADAPTER  = false;
    private static final boolean DEFAULT_RIPPLE_OVERLAY  = false;
    private static final int     DEFAULT_ROUNDED_CORNERS = 0;
    private static final int   DEFAULT_HOVER_DURATION   = 350;//150;//2500;//850

    private static final int  FADE_EXTRA_DELAY = 50;

    private final Paint paint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect  bounds = new Rect();

    private int      rippleColor;
    private boolean  rippleOverlay;
    private boolean  rippleHover;
    private int      rippleDiameter;
    private int      rippleDuration;
    private int      rippleAlpha;
    private boolean  rippleDelayClick;
    private int      rippleFadeDuration;
    private boolean  ripplePersistent;
    private Drawable rippleBackground;
    private boolean  rippleInAdapter;
    private float    rippleRoundedCorners;
    private int     rippleHoverDuration;

    private float radius;

    private AdapterView parentAdapter;
    private View        childView;

    private AnimatorSet    rippleAnimator;
    private ObjectAnimator hoverAnimator;

    private Point currentCoords  = new Point();
    private Point previousCoords = new Point();

    private int layerType;

    private boolean eventCancelled;
    private boolean prepressed;
    private int     positionInAdapter;

    private GestureDetector   gestureDetector;
    private PerformClickEvent pendingClickEvent;
    private PressedEvent      pendingPressEvent;

    private double downX = 0.0;
    private double downY = 0.0;
    private double moveX = 0.0;
    private double moveY = 0.0;
    private long downTime = 0;

    public static RippleBuilder on(View view) {
        return new RippleBuilder(view);
    }

    public RippleFrameLayout(Context context) {
        this(context, null, 0);
    }

    public RippleFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setWillNotDraw(false);
        gestureDetector = new GestureDetector(context, longClickListener);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RippleFrameLayout);
        rippleColor = a.getColor(R.styleable.RippleFrameLayout_mrl_rippleColor, DEFAULT_COLOR);
        rippleDiameter = a.getDimensionPixelSize(
            R.styleable.RippleFrameLayout_mrl_rippleDimension,
            (int) dpToPx(getResources(), DEFAULT_DIAMETER_DP)
        );
        rippleOverlay = a.getBoolean(R.styleable.RippleFrameLayout_mrl_rippleOverlay, DEFAULT_RIPPLE_OVERLAY);
        rippleHover = a.getBoolean(R.styleable.RippleFrameLayout_mrl_rippleHover, DEFAULT_HOVER);
        rippleDuration = a.getInt(R.styleable.RippleFrameLayout_mrl_rippleDuration, DEFAULT_DURATION);
        rippleAlpha = (int) (255 * a.getFloat(R.styleable.RippleFrameLayout_mrl_rippleAlpha, DEFAULT_ALPHA));
        rippleDelayClick = a.getBoolean(R.styleable.RippleFrameLayout_mrl_rippleDelayClick, DEFAULT_DELAY_CLICK);
        rippleFadeDuration = a.getInteger(R.styleable.RippleFrameLayout_mrl_rippleFadeDuration, DEFAULT_FADE_DURATION);
        rippleBackground = new ColorDrawable(a.getColor(R.styleable.RippleFrameLayout_mrl_rippleBackground, DEFAULT_BACKGROUND));
        ripplePersistent = a.getBoolean(R.styleable.RippleFrameLayout_mrl_ripplePersistent, DEFAULT_PERSISTENT);
        rippleInAdapter = a.getBoolean(R.styleable.RippleFrameLayout_mrl_rippleInAdapter, DEFAULT_SEARCH_ADAPTER);
        rippleRoundedCorners = a.getDimensionPixelSize(R.styleable.RippleFrameLayout_mrl_rippleRoundedCorners, DEFAULT_ROUNDED_CORNERS);
        rippleHoverDuration = a.getInt(R.styleable.RippleFrameLayout_mrl_rippleHoverDuration, DEFAULT_HOVER_DURATION);

        a.recycle();

        paint.setColor(rippleColor);
        paint.setAlpha(rippleAlpha);

        enableClipPathSupportIfNecessary();
    }


    @SuppressWarnings("unchecked")
    public <T extends View> T getChildView() {
        return (T) childView;
    }

    @Override
    public final void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("RippleFrameLayout can host only one child");
        }
        //noinspection unchecked
        childView = child;
        super.addView(child, index, params);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        if (BuildConfig.DEBUG)  LogUtil.d("ripple_move", "click.");
        if (childView == null) {
            throw new IllegalStateException("RippleFrameLayout must have a child viewSelected to handle clicks");
        }
        childView.setOnClickListener(onClickListener);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener onClickListener) {
        if (childView == null) {
            throw new IllegalStateException("RippleFrameLayout must have a child viewSelected to handle clicks");
        }
        childView.setOnLongClickListener(onClickListener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return !findClickableViewInChild(childView, (int) event.getX(), (int) event.getY());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean superOnTouchEvent = super.onTouchEvent(event);

        if (!isEnabled() || !childView.isEnabled()) return superOnTouchEvent;

        boolean isEventInBounds = bounds.contains((int) event.getX(), (int) event.getY());

        if (isEventInBounds) {
            previousCoords.set(currentCoords.x, currentCoords.y);
            currentCoords.set((int) event.getX(), (int) event.getY());
        }

        boolean gestureResult = gestureDetector.onTouchEvent(event);
        if (gestureResult || hasPerformedLongPress) {
            return true;
        } else {
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    if (BuildConfig.DEBUG)  LogUtil.d("ripple_move", "up");
                    pendingClickEvent = new PerformClickEvent();
                    if (prepressed) {
                        setChildPressed(childView);
                        postDelayed(
                            new Runnable() {
                                @Override public void run() {
                                    childView.setPressed(false);
                                }
                            }, ViewConfiguration.getPressedStateDuration());
                    }

                    if (isEventInBounds) {
                        startRipple(pendingClickEvent);
                    } else if (!rippleHover) {
                        setRadius(0);
                    }
                    if (!rippleDelayClick && isEventInBounds) {
                        long moveTime = System.currentTimeMillis() - downTime;
                        if (moveTime > 200 && (moveX > 20 || moveY > 20)){
                        }else {
                            pendingClickEvent.run();
                        }
                    }
                    cancelPressedEvent();
                    break;
                case MotionEvent.ACTION_DOWN:
                    if (BuildConfig.DEBUG)  LogUtil.d("ripple_move", "down");
                    downX = event.getX();
                    downY = event.getY();
                    moveX = 0.0;
                    moveY = 0.0;
                    downTime = System.currentTimeMillis();
                    setPositionInAdapter();
                    eventCancelled = false;
                    pendingPressEvent = new PressedEvent(event);
                    if (isInScrollingContainer()) {
                        cancelPressedEvent();
                        prepressed = true;
                        postDelayed(pendingPressEvent, ViewConfiguration.getTapTimeout());
                    } else {
                        pendingPressEvent.run();
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (BuildConfig.DEBUG)  LogUtil.d("ripple_move", "cancel");
                    if (rippleInAdapter) {
                        // dont use current coords in adapter since they tend to jump drastically on scroll
                        currentCoords.set(previousCoords.x, previousCoords.y);
                        previousCoords = new Point();
                    }
                    childView.onTouchEvent(event);
                    if (rippleHover) {
                        if (!prepressed) {
                            startRipple(null);
                        }
                    } else {
                        childView.setPressed(false);
                    }
                    cancelPressedEvent();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (BuildConfig.DEBUG)  LogUtil.d("ripple_move", "move");
                    moveX += Math.abs(event.getX() - downX);
                    moveY += Math.abs(event.getY() - downY);
                    downX = event.getX();
                    downY = event.getY();
                    if (rippleHover) {
                        if (isEventInBounds && !eventCancelled) {
                            invalidate();
                        } else if (!isEventInBounds) {
                            startRipple(null);
                        }
                    }

                    if (!isEventInBounds) {
                        cancelPressedEvent();
                        if (hoverAnimator != null) {
                            hoverAnimator.cancel();
                        }
                        childView.onTouchEvent(event);
                        eventCancelled = true;
                    }
                    break;
            }
            return true;
        }
    }

    private void cancelPressedEvent() {
        if (pendingPressEvent != null) {
            removeCallbacks(pendingPressEvent);
            prepressed = false;
        }
    }

    private boolean hasPerformedLongPress;
    private SimpleOnGestureListener longClickListener = new SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            hasPerformedLongPress = childView.performLongClick();
            if (hasPerformedLongPress) {
                if (rippleHover) {
                    startRipple(null);
                }
                cancelPressedEvent();
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            hasPerformedLongPress = false;
            return super.onDown(e);
        }
    };

    private void startHover() {
        if (eventCancelled) return;

        if (hoverAnimator != null) {
            hoverAnimator.cancel();
        }
        final float radius = (float) (Math.sqrt(Math.pow(getWidth(), 2) + Math.pow(getHeight(), 2)) * 1.2f);
        hoverAnimator = ObjectAnimator.ofFloat(this, radiusProperty, rippleDiameter, radius).setDuration(rippleHoverDuration);
        hoverAnimator.setInterpolator(new LinearInterpolator());
        hoverAnimator.start();
    }

    private void startRipple(final Runnable animationEndRunnable) {
        if (eventCancelled) return;

        float endRadius = getEndRadius();

        cancelAnimations();

        rippleAnimator = new AnimatorSet();
        rippleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                if (!ripplePersistent) {
                    setRadius(0);
                    setRippleAlpha(rippleAlpha);
                }
                if (animationEndRunnable != null && rippleDelayClick) {
                    animationEndRunnable.run();
                }
                childView.setPressed(false);
            }
        });

        ObjectAnimator ripple = ObjectAnimator.ofFloat(this, radiusProperty, radius, endRadius);
        ripple.setDuration(rippleDuration);
        ripple.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator fade = ObjectAnimator.ofInt(this, circleAlphaProperty, rippleAlpha, 0);
        fade.setDuration(rippleFadeDuration);
        fade.setInterpolator(new AccelerateInterpolator());
        fade.setStartDelay(rippleDuration - rippleFadeDuration - FADE_EXTRA_DELAY);

        if (ripplePersistent) {
            rippleAnimator.play(ripple);
        } else if (getRadius() > endRadius) {
            fade.setStartDelay(0);
            rippleAnimator.play(fade);
        } else {
            rippleAnimator.playTogether(ripple, fade);
        }
        rippleAnimator.start();
    }

    private void cancelAnimations() {
        if (rippleAnimator != null) {
            rippleAnimator.cancel();
            rippleAnimator.removeAllListeners();
        }

        if (hoverAnimator != null) {
            hoverAnimator.cancel();
        }
    }

    private float getEndRadius() {
        final int width = getWidth();
        final int height = getHeight();

        final int halfWidth = width / 2;
        final int halfHeight = height / 2;

        final float radiusX = halfWidth > currentCoords.x ? width - currentCoords.x : currentCoords.x;
        final float radiusY = halfHeight > currentCoords.y ? height - currentCoords.y : currentCoords.y;

        return (float) Math.sqrt(Math.pow(radiusX, 2) + Math.pow(radiusY, 2)) * 1.2f;
    }

    private boolean isInScrollingContainer() {
        ViewParent p = getParent();
        while (p != null && p instanceof ViewGroup) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    private AdapterView findParentAdapterView() {
        if (parentAdapter != null) {
            return parentAdapter;
        }
        ViewParent current = getParent();
        while (true) {
            if (current instanceof AdapterView) {
                parentAdapter = (AdapterView) current;
                return parentAdapter;
            } else {
                try {
                    current = current.getParent();
                } catch (NullPointerException npe) {
                    throw new RuntimeException("Could not find a parent AdapterView");
                }
            }
        }
    }

    private void setPositionInAdapter() {
        if (rippleInAdapter) {
            positionInAdapter = findParentAdapterView().getPositionForView(RippleFrameLayout.this);
        }
    }

    private boolean adapterPositionChanged() {
        if (rippleInAdapter) {
            int newPosition = findParentAdapterView().getPositionForView(RippleFrameLayout.this);
            final boolean changed = newPosition != positionInAdapter;
            positionInAdapter = newPosition;
            if (changed) {
                cancelPressedEvent();
                cancelAnimations();
                childView.setPressed(false);
                setRadius(0);
            }
            return changed;
        }
        return false;
    }

    private boolean findClickableViewInChild(View view, int x, int y) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                final Rect rect = new Rect();
                child.getHitRect(rect);

                final boolean contains = rect.contains(x, y);
                if (contains) {
                    return findClickableViewInChild(child, x - rect.left, y - rect.top);
                }
            }
        } else if (view != childView) {
            return (view.isEnabled() && (view.isClickable() || view.isLongClickable() || view.isFocusableInTouchMode()));
        }

        return view.isFocusableInTouchMode();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bounds.set(0, 0, w, h);
        rippleBackground.setBounds(bounds);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    /**
     * Changed by Licheng.<br/>
     * There is a <strong>logic bug:</strong><br/>
     * {@code RectF(0, 0, 109,109)} should call getWidth or getHeight , not canvas.getWidth or canvas.getHeight.
     * <br/><strong>Before changed:</strong>{@code RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());}<br/>
     * <strong>After changed: </strong>{@code RectF rect = new RectF(0, 0, getWidth(),getHeight());}
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        final boolean positionChanged = adapterPositionChanged();
        if (rippleOverlay) {
            if (!positionChanged) {
                rippleBackground.draw(canvas);
            }
            super.draw(canvas);
            if (!positionChanged) {
                if (rippleRoundedCorners != 0) {
                    Path clipPath = new Path();
                    RectF rect = new RectF(0, 0, getWidth(),getHeight());//canvas.getWidth(), canvas.getHeight()); // Changed by Licheng
//                    if (BuildConfig.DEBUG) LogUtil.d("ripple","wrap ripple rect's width = "+getWidth()+" height: "+getHeight()+"canvas.getWidth = "+canvas.getWidth()+" canvas.getHeight: "+canvas.getWidth());
                    clipPath.addRoundRect(rect, rippleRoundedCorners, rippleRoundedCorners, Path.Direction.CW);
                    try {
                        canvas.clipPath(clipPath);
                    }catch (Exception e){

                    }
                }
                canvas.drawCircle(currentCoords.x, currentCoords.y, radius, paint);
            }
        } else {
            if (!positionChanged) {
                rippleBackground.draw(canvas);
                canvas.drawCircle(currentCoords.x, currentCoords.y, radius, paint);
            }
            super.draw(canvas);
        }
    }

    /*
     * Animations
     */
    private Property<RippleFrameLayout, Float> radiusProperty
        = new Property<RippleFrameLayout, Float>(Float.class, "radius") {
        @Override
        public Float get(RippleFrameLayout object) {
            return object.getRadius();
        }

        @Override
        public void set(RippleFrameLayout object, Float value) {
            object.setRadius(value);
        }
    };

    private float getRadius() {
        return radius;
    }


    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    private Property<RippleFrameLayout, Integer> circleAlphaProperty
        = new Property<RippleFrameLayout, Integer>(Integer.class, "rippleAlpha") {
        @Override
        public Integer get(RippleFrameLayout object) {
            return object.getRippleAlpha();
        }

        @Override
        public void set(RippleFrameLayout object, Integer value) {
            object.setRippleAlpha(value);
        }
    };

    public int getRippleAlpha() {
        return paint.getAlpha();
    }

    public void setRippleAlpha(Integer rippleAlpha) {
        paint.setAlpha(rippleAlpha);
        invalidate();
    }

    /*
    * Accessor
     */
    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
        paint.setColor(rippleColor);
        paint.setAlpha(rippleAlpha);
        invalidate();
    }

    public void setRippleOverlay(boolean rippleOverlay) {
        this.rippleOverlay = rippleOverlay;
    }

    public void setRippleDiameter(int rippleDiameter) {
        this.rippleDiameter = rippleDiameter;
    }

    public void setRippleDuration(int rippleDuration) {
        this.rippleDuration = rippleDuration;
    }

    public void setRippleBackground(int color) {
        rippleBackground = new ColorDrawable(color);
        rippleBackground.setBounds(bounds);
        invalidate();
    }

    public void setRippleHover(boolean rippleHover) {
        this.rippleHover = rippleHover;
    }

    public void setRippleDelayClick(boolean rippleDelayClick) {
        this.rippleDelayClick = rippleDelayClick;
    }

    public void setRippleFadeDuration(int rippleFadeDuration) {
        this.rippleFadeDuration = rippleFadeDuration;
    }

    public void setRipplePersistent(boolean ripplePersistent) {
        this.ripplePersistent = ripplePersistent;
    }

    public void setRippleInAdapter(boolean rippleInAdapter) {
        this.rippleInAdapter = rippleInAdapter;
    }

    public void setRippleRoundedCorners(int rippleRoundedCorner) {
        this.rippleRoundedCorners = rippleRoundedCorner;
        enableClipPathSupportIfNecessary();
    }

    public void setDefaultRippleAlpha(int alpha) {
        this.rippleAlpha = alpha;
        paint.setAlpha(alpha);
        invalidate();
    }

    public void performRipple() {
        currentCoords = new Point(getWidth() / 2, getHeight() / 2);
        startRipple(null);
    }

    public void performRipple(Point anchor) {
        currentCoords = new Point(anchor.x, anchor.y);
        startRipple(null);
    }

    /**
     * {@link Canvas#clipPath(Path)} is not supported in hardware accelerated layers
     * before API 18. Use software layer instead
     * <p/>
     * https://developer.android.com/guide/topics/graphics/hardware-accel.html#unsupported
     */
    private void enableClipPathSupportIfNecessary() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (rippleRoundedCorners != 0) {
                layerType = getLayerType();
                setLayerType(LAYER_TYPE_SOFTWARE, null);
            } else {
                setLayerType(layerType, null);
            }
        }
    }

    /*
     * Helper
     */
    private class PerformClickEvent implements Runnable {

        @Override public void run() {
            if (hasPerformedLongPress) return;

            // if parent is an AdapterView, try to call its ItemClickListener
            if (getParent() instanceof AdapterView) {
                clickAdapterView((AdapterView) getParent());
            } else if (rippleInAdapter) {
                // find adapter viewSelected
                clickAdapterView(findParentAdapterView());
            } else {
                // otherwise, just perform click on child
                childView.performClick();
            }
        }

        private void clickAdapterView(AdapterView parent) {
            final int position = parent.getPositionForView(RippleFrameLayout.this);
            final long itemId = parent.getAdapter() != null
                ? parent.getAdapter().getItemId(position)
                : 0;
            if (position != AdapterView.INVALID_POSITION) {
                parent.performItemClick(RippleFrameLayout.this, position, itemId);
            }
        }
    }

    private final class PressedEvent implements Runnable {

        private final MotionEvent event;

        public PressedEvent(MotionEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            prepressed = false;
            childView.setLongClickable(false);//prevent the child's long click,let's the ripple layout call it's performLongClick
            childView.onTouchEvent(event);
            setChildPressed(childView);
            if (rippleHover) {
                startHover();
            }
        }
    }

    private void setChildPressed(View view) {
        if (view != null) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View child = viewGroup.getChildAt(i);
                    child.setPressed(child.isEnabled());
                    setChildPressed(child);
                }
            } else {
                view.setPressed(view.isEnabled());
            }
        }
    }

    static float dpToPx(Resources resources, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    /*
     * Builder
     */

    public static class RippleBuilder {

        private final Context context;
        private final View    child;

        private int     rippleColor         = DEFAULT_COLOR;
        private boolean rippleOverlay       = DEFAULT_RIPPLE_OVERLAY;
        private boolean rippleHover         = DEFAULT_HOVER;
        private float   rippleDiameter      = DEFAULT_DIAMETER_DP;
        private int     rippleDuration      = DEFAULT_DURATION;
        private float   rippleAlpha         = DEFAULT_ALPHA;
        private boolean rippleDelayClick    = DEFAULT_DELAY_CLICK;
        private int     rippleFadeDuration  = DEFAULT_FADE_DURATION;
        private boolean ripplePersistent    = DEFAULT_PERSISTENT;
        private int     rippleBackground    = DEFAULT_BACKGROUND;
        private boolean rippleSearchAdapter = DEFAULT_SEARCH_ADAPTER;
        private float   rippleRoundedCorner = DEFAULT_ROUNDED_CORNERS;

        public RippleBuilder(View child) {
            this.child = child;
            this.context = child.getContext();
        }

        public RippleBuilder rippleColor(int color) {
            this.rippleColor = color;
            return this;
        }

        public RippleBuilder rippleOverlay(boolean overlay) {
            this.rippleOverlay = overlay;
            return this;
        }

        public RippleBuilder rippleHover(boolean hover) {
            this.rippleHover = hover;
            return this;
        }

        public RippleBuilder rippleDiameterDp(int diameterDp) {
            this.rippleDiameter = diameterDp;
            return this;
        }

        public RippleBuilder rippleDuration(int duration) {
            this.rippleDuration = duration;
            return this;
        }

        public RippleBuilder rippleAlpha(float alpha) {
            this.rippleAlpha = 255 * alpha;
            return this;
        }

        public RippleBuilder rippleDelayClick(boolean delayClick) {
            this.rippleDelayClick = delayClick;
            return this;
        }

        public RippleBuilder rippleFadeDuration(int fadeDuration) {
            this.rippleFadeDuration = fadeDuration;
            return this;
        }

        public RippleBuilder ripplePersistent(boolean persistent) {
            this.ripplePersistent = persistent;
            return this;
        }

        public RippleBuilder rippleBackground(int color) {
            this.rippleBackground = color;
            return this;
        }

        public RippleBuilder rippleInAdapter(boolean inAdapter) {
            this.rippleSearchAdapter = inAdapter;
            return this;
        }

        public RippleBuilder rippleRoundedCorners(int radiusDp) {
            this.rippleRoundedCorner = radiusDp;
            return this;
        }

        public RippleFrameLayout create() {
            RippleFrameLayout layout = new RippleFrameLayout(context);
            layout.setRippleColor(rippleColor);
            layout.setDefaultRippleAlpha((int) rippleAlpha);
            layout.setRippleDelayClick(rippleDelayClick);
            layout.setRippleDiameter((int) dpToPx(context.getResources(), rippleDiameter));
            layout.setRippleDuration(rippleDuration);
            layout.setRippleFadeDuration(rippleFadeDuration);
            layout.setRippleHover(rippleHover);
            layout.setRipplePersistent(ripplePersistent);
            layout.setRippleOverlay(rippleOverlay);
            layout.setRippleBackground(rippleBackground);
            layout.setRippleInAdapter(rippleSearchAdapter);
            layout.setRippleRoundedCorners((int) dpToPx(context.getResources(), rippleRoundedCorner));

            ViewGroup.LayoutParams params = child.getLayoutParams();
            ViewGroup parent = (ViewGroup) child.getParent();
            int index = 0;

            if (parent != null && parent instanceof RippleFrameLayout) {
                throw new IllegalStateException("RippleFrameLayout could not be created: parent of the viewSelected already is a RippleFrameLayout");
            }

            if (parent != null) {
                index = parent.indexOfChild(child);
                parent.removeView(child);
            }

            layout.addView(child, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

            if (parent != null) {
                parent.addView(layout, index, params);
            }

            return layout;
        }
    }
}
