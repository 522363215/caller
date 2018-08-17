package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;


/**
 * Created by js on 2016/5/30.
 * 用来管理应用图标的类
 */
public class IconUtil {
    private static LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>((int) Runtime.getRuntime().maxMemory() / 16) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    public static void setImage(String packageName, PackageManager pm, ImageView icon) {
        if (icon == null) {
            return;
        }
        if ((TextUtils.isEmpty(packageName) || pm == null)) {
            icon.setImageResource(android.R.drawable.sym_def_app_icon);
            return;
        }
        Bitmap bitmap = imageCache.get(packageName);
        if (bitmap != null) {
            icon.setImageBitmap(bitmap);
        } else {
            try {
                Drawable drawable = pm.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES).loadIcon(pm);
                if (drawable instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    Bitmap bitmap2 = bitmapDrawable.getBitmap();
                    bitmap2 = Bitmap.createScaledBitmap(bitmap2, Utils.dp2Px(40), Utils.dp2Px(40), false);
                    icon.setImageBitmap(bitmap2);
                    imageCache.put(packageName, bitmap2);
                } else {
                    icon.setImageDrawable(drawable);
                }
            } catch (Exception e) {
                icon.setImageResource(android.R.drawable.sym_def_app_icon);
                e.printStackTrace();
            }
        }
    }

    public static void addToPhotoCache(String key, Bitmap photo) {
        if (!TextUtils.isEmpty(key) && photo != null) {
            imageCache.put(key, photo);
        }
    }

    public static Bitmap getPhoto(String key) {
        return imageCache.get(key);
    }

    public static void trimMemCache() {
        imageCache.evictAll();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap rsBlur(Context context, Bitmap source, float radius) {
        if (radius > 25) {
            radius = 25;
        }
        Bitmap inputBmp = source;
        //(1)
        RenderScript renderScript = RenderScript.create(context);

        LogUtil.d("rsBlur", "scale size:" + inputBmp.getWidth() + "*" + inputBmp.getHeight());

        // Allocate memory for Renderscript to work with
        //(2)
        final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        //(3)
        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        //(4)
        scriptIntrinsicBlur.setInput(input);
        //(5)
        // Set the blur radius
        scriptIntrinsicBlur.setRadius(radius);
        //(6)
        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output);
        //(7)
        // Copy the output to the blurred bitmap
        output.copyTo(inputBmp);
        //(8)
        renderScript.destroy();

        return inputBmp;
    }

    public static Bitmap getBitmap(Context context, int layoutId) {
        //加载xml布局文件
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(layoutId, null);
        //启用绘图缓存
        view.setDrawingCacheEnabled(true);
        //调用下面这个方法非常重要，如果没有调用这个方法，得到的bitmap为null
        view.measure(View.MeasureSpec.makeMeasureSpec(256, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(256, View.MeasureSpec.EXACTLY));
        //这个方法也非常重要，设置布局的尺寸和位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        //获得绘图缓存中的Bitmap
        view.buildDrawingCache();
        return view.getDrawingCache();
    }
}
