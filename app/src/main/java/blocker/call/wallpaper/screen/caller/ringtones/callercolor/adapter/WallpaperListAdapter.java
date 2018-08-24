package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.md.callring.RecyclerClick;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.callback.OnDownloadListener;
import com.md.serverflash.download.ThemeResourceHelper;
import com.md.wallpaper.WallpaperPreferenceHelper;
import com.md.wallpaper.WallpaperUtil;
import com.md.wallpaper.bean.WallpaperFormat;
import com.md.wallpaper.bean.WallpaperInfo;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.WallpaperDetailActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventBusIsSet;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventPostIsExist;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashDownloadCount;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.CircleProgressBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import event.EventBus;

public class WallpaperListAdapter extends RecyclerView.Adapter<WallpaperListAdapter.ViewHolder> {

    private Context context;
    private List<WallpaperInfo> wallpaperInfos;
    private RecyclerClick mRecyclerClick;
    private int mScrollState;
    private RecyclerView mRecyclerView;
    private int childViewWidth, childViewHeight;
    private List<OnOnlineDownloadListener> mDownloadListenerList = null;

    public RecyclerClick getmRecyclerClick() {
        return mRecyclerClick;
    }

    public void clearMap() {
        try {
            if (mDownloadListenerList != null && mDownloadListenerList.size() > 0) {
                for (OnOnlineDownloadListener listener : mDownloadListenerList) {
                    ThemeResourceHelper.getInstance().removeGeneralListener(listener);
                }
            }
        } catch (Exception e) {
        }
    }

    public void setmRecyclerClick(RecyclerClick mRecyclerClick) {
        this.mRecyclerClick = mRecyclerClick;
    }

    public WallpaperListAdapter(Context context, List<WallpaperInfo> wallpaperInfos) {
        this.context = context;
        this.wallpaperInfos = wallpaperInfos;
        mDownloadListenerList = new ArrayList<>();
        if (context != null) {
            int dp4 = context.getResources().getDimensionPixelOffset(R.dimen.dp4);
            childViewWidth = (DeviceUtil.getScreenWidth() - dp4 * 3) / 2;
            childViewHeight = 4 * childViewWidth / 3;
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(context).inflate(R.layout.item_wallpaper_list, parent, false);
        ViewGroup.LayoutParams layoutParams = item.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(childViewWidth, childViewHeight);
        } else {
            layoutParams.width = childViewWidth;
            layoutParams.height = childViewHeight;
        }
        item.setLayoutParams(layoutParams);

        return new ViewHolder(item);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mOnDownloadListener.setDownloadParams(holder, wallpaperInfos.get(position));
        File file = ThemeSyncManager.getInstance().getFileByUrl(context, wallpaperInfos.get(position).url);
        WallpaperInfo path = WallpaperPreferenceHelper.getObject(WallpaperPreferenceHelper.SETED_WALLPAPERS,WallpaperInfo.class);
        holder.ivBackground.showImage(wallpaperInfos.get(position).img_vUrl);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerClick.normalClick(v, position);
            }
        });
        if (file != null && file.exists() || (!TextUtils.isEmpty(wallpaperInfos.get(position).path) && new File(wallpaperInfos.get(position).path).exists())) {
            holder.ivDownload.setVisibility(View.INVISIBLE);
            if (path.path.equals(wallpaperInfos.get(position).path))
                holder.ivSelect.setVisibility(View.VISIBLE);
            else
                holder.ivSelect.setVisibility(View.INVISIBLE);
        } else {
            holder.ivDownload.setVisibility(View.VISIBLE);
        }
        if (wallpaperInfos.get(position).format == WallpaperFormat.FORMAT_VIDEO)
            holder.ivVedio.setVisibility(View.VISIBLE);
        else
            holder.ivVedio.setVisibility(View.INVISIBLE);

    }

    private class OnOnlineDownloadListener implements OnDownloadListener {
        private ViewHolder holder;
        private WallpaperInfo info;

        public void setDownloadParams(ViewHolder holder, WallpaperInfo info) {
            this.holder = holder;
            this.info = info;

            if (holder != null && info != null) {
                holder.pbLoading.setProgress(info.progress);
            }
        }

        @Override
        public void onFailureForIOException(String url) {
            onFailure(url);
        }

        @Override
        public void onSuccess(String url, File file) {
            if (info != null && info.url != null && info.url.equals(url)) {
                holder.ivDownload.setVisibility(View.GONE);
                holder.ivSelect.setVisibility(View.GONE);
                holder.pbLoading.setVisibility(View.GONE);

                WallpaperUtil.getInstance().saveWallpaperDownloadCount(info);
                WallpaperUtil.getInstance().saveDownloadedWallPaper(info);
                EventBus.getDefault().post(new EventRefreshCallFlashDownloadCount());
            }
        }

        @Override
        public void onProgress(String url, int progress) {
            if (info != null && info.url != null && info.url.equals(url)) {
                holder.ivDownload.setVisibility(View.GONE);
                holder.ivSelect.setVisibility(View.GONE);
                holder.pbLoading.setVisibility(View.VISIBLE);

                info.progress = progress;

                holder.pbLoading.setProgress(progress);
            }
        }

        @Override
        public void onFailure(String url) {
            if (info != null && info.url != null && info.url.equals(url)) {
                holder.ivDownload.setVisibility(View.VISIBLE);
                holder.ivSelect.setVisibility(View.GONE);
                holder.pbLoading.setVisibility(View.GONE);

//                ToastUtils.showToast(context, context.getString(R.string.call_flash_gif_show_load_failed));
            }
        }
    }

    public void setScrollState(int scrollState) {
        mScrollState = scrollState;
    }

    private int mDataType = 0;
    public void setDataType(int dataType) {
        this.mDataType = dataType;
    }

    public void setRecycleView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return wallpaperInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        GlideView ivBackground;
        ImageView ivSelect;
        ImageView ivDownload;
        CircleProgressBar pbLoading;
        ImageView ivVedio;
        OnOnlineDownloadListener mOnDownloadListener;

        public ViewHolder(View itemView) {
            super(itemView);

            ivBackground = itemView.findViewById(R.id.iv_background);
            ivSelect = itemView.findViewById(R.id.iv_select);
            ivDownload = itemView.findViewById(R.id.iv_download);
            pbLoading = itemView.findViewById(R.id.pb_loading);
            ivVedio = itemView.findViewById(R.id.iv_video);
            mOnDownloadListener = new OnOnlineDownloadListener();
            ThemeResourceHelper.getInstance().addGeneralListener(mOnDownloadListener);
            mDownloadListenerList.add(mOnDownloadListener);
        }
    }
}
