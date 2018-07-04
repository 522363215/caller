package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

/**
 * 字体图标
 * Created by John on 2016/1/22.
 */
public class FontIconView extends TextView {

    public FontIconView(Context context, String font) {
        super(context);
        init(context, font);
    }

    public FontIconView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.FontIconView, 0, 0);

        try {
            String font = attributes.getString(R.styleable.FontIconView_fontcid);
            init(context, font);
        } finally {
            attributes.recycle();
        }
    }

    private void init(Context context, String font) {
        if (font == null) {
            font = "icomoon";
        }

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), font + ".ttf");
        this.setTypeface(typeface);
    }

    public static Bitmap viewToBitmap(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();
        view.requestLayout();
        view.layout(0, 0, viewWidth, viewHeight);

        Bitmap bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(bitmap);
        view.draw(c);
        //bitmap.recycle();
        return bitmap;
    }

    public static Bitmap viewToBitmap(View view, int viewWidth, int viewHeight) {
        view.measure(MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.UNSPECIFIED));
        view.requestLayout();
        view.layout(0, 0, viewWidth, viewHeight);
        Bitmap bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(bitmap);
        view.draw(c);
        //bitmap.recycle();
        return bitmap;
    }

    private static Map<String, Bitmap> iconCache = new HashMap<>();

    public static synchronized Bitmap toBitmap(Context context, String font, String icon, int size, int color) {
        StringBuilder sb = new StringBuilder(16);
        sb.append(font);
        sb.append("@");
        sb.append(icon);
        sb.append("@");
        sb.append(size);
        sb.append("@");
        sb.append(color);

        String key = sb.toString();

        Bitmap i = iconCache.get(key);

        if (i == null) {
            FontIconView view = new FontIconView(context, font);

            view.setText(icon);
            view.setTextSize(size);
            view.setTextColor(color);

            i = viewToBitmap(view);

            iconCache.put(key, i);
        }

        return i;

    }

    public static synchronized Bitmap toBitmap(Context context, String font, String icon, int txtSize, int imgWidth, int imgHeight, int color) {
        StringBuilder sb = new StringBuilder(16);
        sb.append(font);
        sb.append("@");
        sb.append(icon);
        sb.append("@");
        sb.append(txtSize);
        sb.append("@");
        sb.append(color);
        sb.append("@");
        sb.append(imgWidth);
        sb.append("x");
        sb.append(imgHeight);

        String key = sb.toString();

        Bitmap i = iconCache.get(key);

        if (i == null) {
            FontIconView view = new FontIconView(context, font);
            view.setText(icon);
            view.setTextSize(txtSize);
            view.setTextColor(color);
            view.setGravity(Gravity.CENTER);

            view.setHeight(imgHeight);
            view.setWidth(imgWidth);
            i = viewToBitmap(view, imgWidth, imgHeight);

            iconCache.put(key, i);
        }

        return i;

    }

}
