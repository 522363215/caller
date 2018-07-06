package blocker.call.wallpaper.screen.caller.ringtones.callercolor.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;

public class GlideModelConfig implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //设置内存缓存大小
//        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取系统分配给应用的总内存大小
//        int memoryCacheSize = maxMemory / 8;//设置图片内存缓存占用八分之一
//        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
        // 20%大的内存缓存作为 Glide 的默认值
        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);
        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));

        //设置磁盘缓存大小
        int diskCacheSize = 1024 * 1024 * 100;//最多可以缓存多少字节的数据
//        File cacheDir = context.getExternalCacheDir();//指定的是数据的缓存地址
//        builder.setDiskCache(new DiskLruCacheFactory(cacheDir.getPath(), "glide", diskCacheSize));
        //存放在data/data/xxxx/cache/
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "glide", diskCacheSize));

        //设置图片解码格式
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
    }
}