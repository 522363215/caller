package blocker.call.wallpaper.screen.caller.ringtones.callercolor.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

public class GlideHelper {
    private Context mContext;

    private GlideHelper(Context context) {
        mContext = context;
    }

    public static GlideHelper with(Context context) {
        return new GlideHelper(context);
    }

    public BitmapRequestBuilder<String, Bitmap> load(String url) {
        return Glide.with(mContext).load(url).asBitmap()
                .centerCrop()
                .placeholder(R.drawable.glide_loading_bg)//加载中图片
                .error(R.drawable.glide_load_failed_bg)//加载失败图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate();
    }

    public DrawableRequestBuilder<Drawable> load(Drawable drawable) {
        return Glide.with(mContext).load(drawable)
                .placeholder(R.drawable.glide_loading_bg)//加载中图片
                .error(R.drawable.glide_load_failed_bg)//加载失败图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate();
    }

    /**
     * 加载本地gif
     */
    public GifRequestBuilder<String> loadGif(String path) {
        return Glide.with(mContext).load(path).asGif()
                .placeholder(R.drawable.glide_loading_bg)//加载中图片
                .error(R.drawable.glide_load_failed_bg)//加载失败图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate();
    }

    /**
     * 加载资源gif
     */
    public GifRequestBuilder<Integer> loadGif(int gifResourceId) {
        return Glide.with(mContext).load(gifResourceId).asGif()
                .placeholder(R.drawable.glide_loading_bg)//加载中图片
                .error(R.drawable.glide_load_failed_bg)//加载失败图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate();
    }

}
