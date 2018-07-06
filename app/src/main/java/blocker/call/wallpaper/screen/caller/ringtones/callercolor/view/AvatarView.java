package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RelativeLayout;


import java.io.InputStream;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ContactManager;

/**
 * Created by ChenR on 2017/3/25.
 * 头像;
 */

public class AvatarView extends RelativeLayout {

    private Context mContext;
    private FontIconView fiv;
    private CircleImageView iv;
    private Resources mResources;

    private String photoId;
    private String strPhoto;
    private float textSize;

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        mResources = context.getResources();

        TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.AvatarView, 0, 0);
        photoId = typed.getString(R.styleable.AvatarView_photoId);
        textSize = typed.getFloat(R.styleable.AvatarView_fontSize, 1.0f);
        String defaultPhoto = typed.getString(R.styleable.AvatarView_defaultText);
        strPhoto = TextUtils.isEmpty(defaultPhoto) ? mContext.getString(R.string.icon_user) : defaultPhoto;
        typed.recycle();

        initView();
        showImage(false);
    }

    private void initView() {
        initFontIcon();
        initImageView();
        addView(fiv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(iv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    private void initImageView() {
        iv = new CircleImageView(mContext);
        //iv.setScaleType(ImageView.ScaleType.CENTER);
    }

    private void initFontIcon() {
        fiv = new FontIconView(mContext, "icomoon");
        //fiv.setText(mContext.getString(R.string.icon_user));
        fiv.setText(strPhoto);
        fiv.setTextColor(0x88003466);
        fiv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        //fiv.setAlpha(0.5f);
        fiv.setGravity(Gravity.CENTER);

//        fiv.setBackgroundDrawable(mResources.getDrawable(R.drawable.shape_circle_contact_bg));
        fiv.setBackgroundResource(R.drawable.shape_circle_contact_bg);
    }

    public void setPhotoId(String photoId) {
        boolean isShow = false;
        if (!TextUtils.isEmpty(photoId)) {
            this.photoId = photoId;
            Bitmap bitmap = ContactManager.getInstance().getContactPhoto(photoId);
            if (bitmap != null) {
                isShow = true;
//                Bitmap bmp = makeRoundBitmap(bitmap);
                iv.setImageBitmap(bitmap);
            }
        }
        showImage(isShow);
    }

    /*public void setFontIconAlpha (float alpha) {
        fiv.setAlpha(alpha);
        fiv.setTextColor(0x88003466);
    }*/

    private Bitmap makeRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height / 2;
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int color = 0xff888888;
        paint.setColor(color);
        Rect rect = new Rect(left, top, right, bottom);
        //RectF rectF = new RectF(rect);

        canvas.drawARGB(0, 0, 0, 0);
        //canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            text = strPhoto;
        }
        if (text.equals(mContext.getString(R.string.icon_his))) {
            fiv.setTextColor(0x88f26158);
//            fiv.setAlpha(1.0f);
            fiv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48);
        } else {
            fiv.setTextColor(0x88003466);
            //fiv.setAlpha(0.5f);
            fiv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
        fiv.setText(text);
        showImage(false);
    }

    public void setPhotoUri(String uri) {
        boolean isShow = false;
        if (!TextUtils.isEmpty(uri)) {
            Bitmap bitmap = resolveUri(uri);
            if (bitmap != null) {
//                Bitmap roundBitmap = makeRoundBitmap(bitmap);
                iv.setImageBitmap(bitmap);
                isShow = true;
            }
        }
        showImage(isShow);
    }

    private Bitmap resolveUri(String strUri) {
        Bitmap bitmap = null;
        Uri uri = Uri.parse(strUri);
        try {
            InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeStream(inputStream, null, options);
            options.inJustDecodeBounds = false;
            inputStream.close();

            int outWidth = options.outWidth;
            int outHeight = options.outHeight;
            float targetWidth = 120f;
            float targetHeight = 120f;
            int mSampleSize = 1;

            if (outHeight < outWidth && outWidth > targetWidth) {
                mSampleSize = (int) (outWidth / targetWidth);
            } else if (outHeight > outWidth && outHeight > targetHeight) {
                mSampleSize = (int) (outHeight / targetHeight);
            }
            if (mSampleSize < 0) {
                mSampleSize = 1;
            }
            options.inSampleSize = mSampleSize;

            InputStream is = mContext.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (Exception e) {

        }
        return bitmap;
    }

    private void showImage(boolean isImage) {
        if (isImage) {
            fiv.setVisibility(GONE);
            iv.setVisibility(VISIBLE);
        } else {
            iv.setVisibility(GONE);
            fiv.setVisibility(VISIBLE);
        }
    }

    public void setAvatarDrawable(int resDrawableId) {
        if (resDrawableId > 0) {
            iv.setImageResource(resDrawableId);
            showImage(true);
        } else {
            showImage(false);
        }
    }

    public void setAvatarBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            iv.setImageBitmap(bitmap);
            showImage(true);
        } else {
            showImage(false);
            setText("");
        }
    }

    public void setAvatarUri(String uri) {
        if (uri == null) {
            showImage(false);
            setText("");
        } else {
            showImage(true);
            setPhotoUri(uri);
        }
    }
}
