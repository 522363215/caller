package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

/**
 * Created by ChenR on 2018/1/25.
 */

public class HorizontalCallFlashMarginDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "HorizontalCallFlashMarginDecoration";

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        Resources res = ApplicationEx.getInstance().getApplicationContext().getResources();
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.left = 0;
            outRect.right = res.getDimensionPixelOffset(R.dimen.dp4);
        } else if (position == layoutManager.getItemCount() - 1) {
            outRect.right = 0;
            outRect.left = res.getDimensionPixelOffset(R.dimen.dp4);
        } else {
            outRect.right = res.getDimensionPixelOffset(R.dimen.dp4);
            outRect.left = res.getDimensionPixelOffset(R.dimen.dp4);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }
}
