package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

/**
 * Created by ChenR on 2018/1/25.
 */

public class CallFlashMarginDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "CallFlashMarginDecoration";
    private int mAdShowPosition;
    private boolean mIsAdLoaded;
    private boolean mIsHaveAd;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        Resources res = ApplicationEx.getInstance().getApplicationContext().getResources();
        int position = parent.getChildAdapterPosition(view);
        if (position == 0 || position == 1) {
            outRect.top = res.getDimensionPixelOffset(R.dimen.dp8);
        }

        if (!mIsHaveAd) {
            outRect.bottom = res.getDimensionPixelOffset(R.dimen.dp8);
            int pos = position % 2;
            if (pos != 0) {
                // right cul
                outRect.right = res.getDimensionPixelOffset(R.dimen.dp8);
                outRect.left = res.getDimensionPixelOffset(R.dimen.dp4);
            } else {
                outRect.right = res.getDimensionPixelOffset(R.dimen.dp4);
                outRect.left = res.getDimensionPixelOffset(R.dimen.dp8);
            }
        } else {
            if (position != mAdShowPosition) {
                outRect.bottom = res.getDimensionPixelOffset(R.dimen.dp8);
            } else {
                LogUtil.d(TAG, "position :" + position + ",mIsAdLoaded:" + mIsAdLoaded);
                if (mIsAdLoaded) {
                    outRect.bottom = res.getDimensionPixelOffset(R.dimen.dp8);
                } else {
                    outRect.bottom = 0;
                }
            }
            if (position < mAdShowPosition) {
                int pos = position % 2;
                if (pos != 0) {
                    // right cul
                    outRect.right = res.getDimensionPixelOffset(R.dimen.dp8);
                    outRect.left = res.getDimensionPixelOffset(R.dimen.dp4);
                } else {
                    outRect.right = res.getDimensionPixelOffset(R.dimen.dp4);
                    outRect.left = res.getDimensionPixelOffset(R.dimen.dp8);
                }
            } else if (position > mAdShowPosition) {
                int pos = position % 2;
                if (pos != 1) {
                    // right cul
                    outRect.right = res.getDimensionPixelOffset(R.dimen.dp8);
                    outRect.left = res.getDimensionPixelOffset(R.dimen.dp4);
                } else {
                    outRect.right = res.getDimensionPixelOffset(R.dimen.dp4);
                    outRect.left = res.getDimensionPixelOffset(R.dimen.dp8);
                }
            } else {
                outRect.right = res.getDimensionPixelOffset(R.dimen.dp8);
                outRect.left = res.getDimensionPixelOffset(R.dimen.dp8);
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    public void setAdShowPosition(int adShowPosition) {
        mAdShowPosition = adShowPosition;
    }

    public void setAdLoaded(boolean isAdloaded) {
        this.mIsAdLoaded = isAdloaded;
    }

    public void isHaveAd(boolean isHaveAd) {
        this.mIsHaveAd = isHaveAd;
    }
}
