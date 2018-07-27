package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.glide.GlideHelper;

public class GlideView extends RelativeLayout {
    private Context mContext;
    private ImageView mImageView;
    private ImageView mIvShape;
    private ImageView mIvShape2;

    public GlideView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public GlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public GlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public class ScaleType {
        public static final int MATRIX = 0;
        public static final int FIT_XY = 1;
        public static final int FIT_START = 2;
        public static final int FIT_CENTER = 3;
        public static final int FIT_END = 4;
        public static final int CENTER = 5;
        public static final int CENTER_CROP = 6;
        public static final int CENTER_INSIDE = 7;
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.GlideView, 0, 0);
        try {
            int scaleType = attributes.getInt(R.styleable.GlideView_ScaleType, ScaleType.FIT_XY);
            LayoutInflater.from(context).inflate(R.layout.layout_glide_view, this);
            mImageView = findViewById(R.id.imageView);
            mIvShape = findViewById(R.id.iv_shape);
            mIvShape2 = findViewById(R.id.iv_shape2);
            setScaleType(scaleType);
        } finally {
            attributes.recycle();
        }
    }

    public void setScaleType(int scaleType) {
        if (mImageView == null) return;
        switch (scaleType) {
            case ScaleType.MATRIX:
                mImageView.setScaleType(ImageView.ScaleType.MATRIX);
                break;
            case ScaleType.FIT_XY:
                mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case ScaleType.FIT_START:
                mImageView.setScaleType(ImageView.ScaleType.FIT_START);
                break;
            case ScaleType.FIT_CENTER:
                mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
            case ScaleType.FIT_END:
                mImageView.setScaleType(ImageView.ScaleType.FIT_END);
                break;
            case ScaleType.CENTER:
                mImageView.setScaleType(ImageView.ScaleType.CENTER);
                break;
            case ScaleType.CENTER_CROP:
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                break;
            case ScaleType.CENTER_INSIDE:
                mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                break;
        }
    }

    /**
     * @param urlOrPath url 或者 本地资源路径
     */
    public void showImage(String urlOrPath) {
        if (mImageView != null) {
            GlideHelper.with(mContext).load(urlOrPath).into(mImageView);
        }
    }

    public void showImage(String urlOrPath, RequestListener<String, Bitmap> requestListener) {
        if (mImageView != null) {
            GlideHelper.with(mContext).load(urlOrPath).listener(requestListener).into(mImageView);
        }
    }

    /**
     * 加载图片时显示缩略图
     *
     * @param urlOrPath    实际资源的url 或者路径
     * @param thumbnailUrl 缩略图的额url
     */
    public void showImageWithThumbnail(String urlOrPath, String thumbnailUrl) {
        if (mImageView != null) {
            if (!TextUtils.isEmpty(thumbnailUrl) && !thumbnailUrl.equals(urlOrPath)) {
                BitmapRequestBuilder<String, Bitmap> load = GlideHelper.with(mContext).load(thumbnailUrl);
                GlideHelper.with(mContext).load(urlOrPath).thumbnail(load).into(mImageView);
            } else {
                showImage(urlOrPath);
            }
        }
    }

    /**
     * 加载图片时显示缩略图，并且伴随共享动画
     *
     * @param urlOrPath    实际资源的url 或者路径
     * @param thumbnailUrl 缩略图的额url
     */
    public void showImageWithThumbnailAndShareAnim(String urlOrPath, String thumbnailUrl, RequestListener<String, Bitmap> requestListener) {
        if (mImageView != null) {
            if (!TextUtils.isEmpty(thumbnailUrl) && !thumbnailUrl.equals(urlOrPath)) {
                BitmapRequestBuilder<String, Bitmap> load = GlideHelper.with(mContext).load(thumbnailUrl).listener(requestListener);
                GlideHelper.with(mContext).load(urlOrPath).thumbnail(load).into(mImageView);
            } else {
                GlideHelper.with(mContext).load(urlOrPath).listener(requestListener).into(mImageView);
            }
        }
    }

    /**
     * 高斯模糊显示图片
     *
     * @param urlOrPath 实际资源的url 或者路径
     */
    public void showImageWithBlur(String urlOrPath) {
        if (mIvShape2 != null) {
            mIvShape2.setVisibility(VISIBLE);
        }
        if (mIvShape != null) {
            mIvShape.setVisibility(VISIBLE);
            GlideHelper.with(mContext).load(urlOrPath).into(mIvShape);
        }
        if (mImageView != null) {
            GlideHelper.with(mContext).loadForBlur(urlOrPath).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (mIvShape != null) {
                        mIvShape.setVisibility(GONE);
                    }
                    return false;
                }
            }).into(mImageView);
        }
    }

    public void showImageWithBlur(int resId) {
        if (mIvShape != null) {
            mIvShape.setVisibility(VISIBLE);
            GlideHelper.with(mContext).load(resId).into(mIvShape);
        }
        if (mImageView != null) {
            GlideHelper.with(mContext).loadForBlur(resId).listener(new RequestListener<Integer, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (mIvShape != null) {
                        mIvShape.setVisibility(GONE);
                    }
                    return false;
                }
            }).into(mImageView);
        }
    }


    public void showImage(int resId) {
        if (mImageView != null) {
            mImageView.setBackgroundResource(resId);
        }
    }

    public void showImage(Drawable drawable) {
        if (mImageView != null) {
            GlideHelper.with(mContext).load(drawable).into(mImageView);
        }
    }

    public void showGif(String urlOrPath) {
        if (mImageView != null) {
            GlideHelper.with(mContext).loadGif(urlOrPath).into(mImageView);
        }
    }

    public void showGif(int resId) {
        if (mImageView != null) {
            GlideHelper.with(mContext).loadGif(resId).into(mImageView);
        }
    }

    public void showVideoFirstFrame(String videoPath) {
        if (mImageView != null) {
            Glide.with(mContext).load(videoPath).into(mImageView);
        }
    }
}
