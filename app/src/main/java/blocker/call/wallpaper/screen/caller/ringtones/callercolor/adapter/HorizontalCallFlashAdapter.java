package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashInfo;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;

/**
 * Created by zhq on 2018/7/30.
 */

public class HorizontalCallFlashAdapter extends RecyclerView.Adapter<HorizontalCallFlashAdapter.ViewHolder> {
    private static final String TAG = "HorizontalCallFlashAdapter";
    private Context mContext;
    private List<CallFlashInfo> mData;

    public HorizontalCallFlashAdapter(Context context, List<CallFlashInfo> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = View.inflate(mContext, R.layout.item_call_flash_horizontal, null);
        ViewGroup.LayoutParams layoutParams = item.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(Stringutil.dpToPx(mContext, 110), Stringutil.dpToPx(mContext, 110));
        } else {
            layoutParams.width = Stringutil.dpToPx(mContext, 110);
            layoutParams.height = Stringutil.dpToPx(mContext, 110);
        }
        item.setLayoutParams(layoutParams);
        return new HorizontalCallFlashAdapter.ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        CallFlashInfo info = getItem(position);
//        LogUtil.d(TAG, "onBindViewHolder info:" + info);
        if (info == null) return;
        holder.mGvBg.setTag(position);
        if (mData.size() == 6) {
            LogUtil.d(TAG, "onBindViewHolder mdata:" + getItemCount() + ",flashType:" + info.flashType + ",isOnlionCallFlash:" + info.isOnlionCallFlash + ",position:" + position);
        }
        if (info.isOnlionCallFlash) {
            if (!TextUtils.isEmpty(info.thumbnail_imgUrl)) {
                holder.mGvBg.showImage(info.thumbnail_imgUrl);
            } else {
                holder.mGvBg.showImage(info.img_vUrl);
            }
        } else {
            if (info.imgResId > 0) {
                int imgId = info.imgResId;
                if (info.flashType == FlashLed.FLASH_TYPE_FESTIVAL) {
                    imgId = R.drawable.icon_flash_festival_small;
                }
                holder.mGvBg.showImage(imgId);
            } else {
                holder.mGvBg.showImage(R.drawable.loaded_failed);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public CallFlashInfo getItem(int position) {
        CallFlashInfo info = null;
        try {
            if (mData != null && mData.size() > 0) {
                info = mData.get(position);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "getItem exception: " + e.getMessage());
        }
        return info;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        GlideView mGvBg;

        public ViewHolder(View itemView) {
            super(itemView);
            mGvBg = itemView.findViewById(R.id.gv_bg);
            mGvBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGvBg.getTag() == null) return;
                    int position = (int) mGvBg.getTag();
                    CallFlashInfo info = getItem(position);
                    if (info == null) return;
                    ActivityBuilder.toCallFlashDetail(mContext, info, false);
                }
            });
        }
    }

}
