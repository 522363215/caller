package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.md.flashset.bean.CallFlashFormat;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.callback.OnDownloadListener;
import com.md.serverflash.download.ThemeResourceHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashDownloadCount;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.EncryptionUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.CircleProgressBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.callflash.CallFlashView;

/**
 * Created by ChenR on 2018/1/31.
 */

public class CallFlashOnlineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CallFlashOnlineAdapter";

    public static final int ITEM_REFRESH_TYPE_PLAY = 0;
    public static final int ITEM_REFRESH_TYPE_PAUSE = 1;
    public static final int ITEM_REFRESH_TYPE_SCROLL = 2;

    private Context context = null;
    private List<CallFlashInfo> model = null;

    private boolean isFlashSwitchOn = false;
    private CallFlashInfo mCurrentFlash;
    private boolean misInitAd;
    private Advertisement mAdvertisement;
    private boolean mIsAdloaded;
    private int mAdShowPosition = -1;
    private ConcurrentHashMap<String, File> videoMap;
    private List<OnOnlineDownloadListener> mDownloadListenerList = null;

    private int childViewWidth, childViewHeight;
    private int mScrollState;
    private RecyclerView.ViewHolder holder;
    private List<Boolean> payloads;
    private RecyclerView mRecyclerView;
    private LruCache<String, Bitmap> mFirstFrameMemoryCache;

    public void setFragmentTag(int fragmentTag) {
        this.fragmentTag = fragmentTag;
    }

    private int fragmentTag = -99;
    private int mDataType = 0;

    public void setDataType(int dataType) {
        this.mDataType = dataType;
    }

    public void clearMap() {
        try {
            if (videoMap != null) {
                videoMap.clear();
            }
            if (mDownloadListenerList != null && mDownloadListenerList.size() > 0) {
                for (OnOnlineDownloadListener listener : mDownloadListenerList) {
                    ThemeResourceHelper.getInstance().removeGeneralListener(listener);
                }
            }
        } catch (Exception e) {
        }
    }

    public CallFlashOnlineAdapter(Context context, List<CallFlashInfo> model) {
        this.context = context;
        this.model = model;
        //设置第一帧图片缓存大小
        setFirstFrameCacheSize();

        videoMap = new ConcurrentHashMap<>();
        mDownloadListenerList = new ArrayList<>();
        if (context != null) {
            int dp4 = context.getResources().getDimensionPixelOffset(R.dimen.dp4);
            childViewWidth = (DeviceUtil.getScreenWidth() - dp4 * 3) / 2;
            childViewHeight = 4 * childViewWidth / 3;
        }
    }

    private void setFirstFrameCacheSize() {
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mFirstFrameMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    private void addFirstFrameBitmapToMemoryCache(String callFlashId, Bitmap bitmap) {
        if (getFirstFrameBitmapFromMemoryCache(callFlashId) == null) {
            mFirstFrameMemoryCache.put(callFlashId, bitmap);
        }
    }

    private Bitmap getFirstFrameBitmapFromMemoryCache(String callFlashId) {
        return mFirstFrameMemoryCache.get(callFlashId);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = View.inflate(context, R.layout.item_call_flash_online, null);
        ViewGroup.LayoutParams layoutParams = item.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(childViewWidth, childViewHeight);
        } else {
            layoutParams.width = childViewWidth;
            layoutParams.height = childViewHeight;
        }
        item.setLayoutParams(layoutParams);
        return new NormalViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //为空 不使用 ,使用public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads)，方便局部刷新
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (context == null) return;
        isFlashSwitchOn = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
        mCurrentFlash = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, CallFlashInfo.class);
        if (fragmentTag == CallFlashManager.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode() && position == mAdShowPosition) {
            setAdItem((NormalViewHolder) holder);
        } else {
            setItem((NormalViewHolder) holder, position, payloads);
        }
    }

    private void setAdItem(NormalViewHolder holder) {
        // 广告相关.
        holder.layoutCallFlash.setVisibility(View.GONE);
        holder.layout_ad_view.setVisibility(View.VISIBLE);

        if (!mIsAdloaded) {
//                if (CallerAdManager.isShowAd(CallerAdManager.POSITION_FB_ADS_CALLFLASH_HOT)) {
////                    initAd(holder.itemView, holder);
//                }
        } else {
            int childCount = holder.layout_ad_admob.getChildCount();
            if (childCount == 0) {
                View adView = mAdvertisement.showListViewFBAD();
                if (adView == null) {
                    adView = mAdvertisement.showListViewAdmobAd();
                }
                if (adView == null) {
                    adView = mAdvertisement.showListViewMopubAd();
                }

                if (adView == null) {
                    adView = mAdvertisement.showListViewBaiduAd();
                }


                if (adView != null) {
                    if (adView.getParent() != null) {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }
                    holder.layout_ad_admob.addView(adView);
                    holder.layout_ad_admob.setVisibility(View.VISIBLE);
                    holder.layout_ad_view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setItem(NormalViewHolder holder, int pos, List<Object> payloads) {
        CallFlashInfo info = getItem(pos);
        if (info == null) return;
        if (payloads.isEmpty()) {//payloads为空 即为正常情况下的执行或者调用notifyItemChanged(position,payloads)方法时payloads==null执行的,
            holder.itemView.setVisibility(View.VISIBLE);
            holder.layoutCallFlash.setVisibility(View.VISIBLE);
            holder.layout_ad_view.setVisibility(View.GONE);
            holder.root.setTag(pos);
            holder.layoutCallFlash.setTag(pos);
            holder.iv_download.setTag(pos);
            holder.mOnDownloadListener.setDownloadParams(holder, info);
            setSelectOrDownloadState(holder, info);
            setBg(holder, info);
            setCallFlashShow(holder, pos, payloads);
        } else {//payloads不为空 即调用notifyItemChanged(position,payloads)方法payloads!=null执行的
            setSelectOrDownloadState(holder, info);
            setCallFlashShow(holder, pos, payloads);
        }
    }

    private void setBg(NormalViewHolder holder, CallFlashInfo info) {
        holder.gv_bg.setVisibility(View.VISIBLE);
        holder.callFlashView.setVisibility(View.GONE);
        if (holder.iv_call_select.getVisibility() == View.VISIBLE && info.format == CallFlashFormat.FORMAT_VIDEO && CallFlashManager.getInstance().isCallFlashDownloaded(info)) {
            if (CallFlashManager.CALL_FLASH_START_SKY_ID.equals(info.id)) {
                holder.gv_bg.showImage(R.drawable.img_star_sky_v);
            } else {
                File firstFrameFile = ThemeSyncManager.getInstance().getVideoFirstFrameFileByUrl(context, info.url);
                if (firstFrameFile != null && firstFrameFile.exists()) {
                    Bitmap firstFrameBitmapFromMemoryCache = getFirstFrameBitmapFromMemoryCache(info.id);
                    if (firstFrameBitmapFromMemoryCache == null) {
                        firstFrameBitmapFromMemoryCache = BitmapFactory.decodeFile(firstFrameFile.getAbsolutePath());
                        addFirstFrameBitmapToMemoryCache(info.id, firstFrameBitmapFromMemoryCache);
                    }
                    holder.gv_bg.showImage(firstFrameBitmapFromMemoryCache);
                } else {
                    if (!TextUtils.isEmpty(info.thumbnail_imgUrl)) {
                        holder.gv_bg.showImage(info.thumbnail_imgUrl);
                    } else {
                        holder.gv_bg.showImage(info.img_vUrl);
                    }

                }
            }
        } else {
            if (info.isOnlionCallFlash) {
                String imgUrl = info.img_vUrl;
                if (childViewHeight != 0 && childViewWidth != 0) {
                    imgUrl += "_" + childViewWidth + "x" + childViewHeight;
                }
                info.thumbnail_imgUrl = imgUrl;
                holder.gv_bg.showImage(imgUrl);
            } else {
                int imgId = info.imgResId;
                if (CallFlashManager.CALL_FLASH_START_SKY_ID.equals(info.id)) {
                    imgId = R.drawable.img_star_sky_v;
                }
                if (info.imgResId > 0) {
                    holder.gv_bg.showImage(imgId);
                } else {
                    holder.gv_bg.showImage(R.drawable.loaded_failed);
                }
            }
        }
    }

    private void setSelectOrDownloadState(NormalViewHolder holder, CallFlashInfo info) {
        if (info.isOnlionCallFlash) {
            File videoFile = videoMap.get(info.url);
            if (videoFile == null) {
                videoFile = CallFlashManager.getInstance().getOnlineThemeSourcePath(info.url);
                if (videoMap != null && videoFile != null) {
                    videoMap.put(info.url, videoFile);
                }
            }
            if (videoFile != null) {
                if (videoFile.exists()) {
                    boolean enableCallFlash = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, PreferenceHelper.DEFAULT_VALUE_FOR_CALL_FLASH);
                    holder.iv_download.setVisibility(View.GONE);
                    holder.iv_call_select.setVisibility((mCurrentFlash != null && mCurrentFlash.equals(info) && enableCallFlash)
                            ? View.VISIBLE : View.GONE);
                } else {
                    holder.iv_download.setVisibility(View.VISIBLE);
                    holder.iv_call_select.setVisibility(View.GONE);
                }
            }
        } else {
            if (!TextUtils.isEmpty(info.path)) {
                boolean enableCallFlash = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, PreferenceHelper.DEFAULT_VALUE_FOR_CALL_FLASH);
                holder.iv_download.setVisibility(View.GONE);
                holder.iv_call_select.setVisibility((mCurrentFlash != null && mCurrentFlash.equals(info) && enableCallFlash)
                        ? View.VISIBLE : View.GONE);
            } else {
                holder.iv_download.setVisibility(View.VISIBLE);
                holder.iv_call_select.setVisibility(View.GONE);
            }
        }
    }

    private void setCallFlashShow(final NormalViewHolder holder, int pos, List<Object> payloads) {
        CallFlashInfo info = getItem(pos);
        boolean enableCallFlash = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, PreferenceHelper.DEFAULT_VALUE_FOR_CALL_FLASH);
        if (!enableCallFlash || mCurrentFlash == null || info == null || !mCurrentFlash.equals(info)) {
            holder.gv_bg.setVisibility(View.VISIBLE);
            holder.callFlashView.setVisibility(View.GONE);
            return;
        }
        float viewShowPercent = DeviceUtil.getItemShowPercentInRecycleView(mRecyclerView, holder.itemView);
        boolean canPlay = viewShowPercent >= ConstantUtils.CALL_FLASH_LIST_SHOW_VIDEO_PERCENT && (mScrollState == RecyclerView.SCROLL_STATE_IDLE || mScrollState == RecyclerView.SCROLL_STATE_DRAGGING) && !holder.callFlashView.isPlaying();
        if (payloads.isEmpty()) {//payloads为空 即为正常情况下的执行或者调用notifyItemChanged(position,payloads)方法时payloads==null执行的,
            LogUtil.d(TAG, "setCallFlashShow 正常刷新 canPlay:" + canPlay);
            if (canPlay) {
                playOrPause(holder, info, true, 300);
            } else if (viewShowPercent <= ConstantUtils.CALL_FLASH_LIST_STOP_VIDEO_PERCENT && holder.callFlashView.isPlaying()) {
                playOrPause(holder, info, false, 300);
            }
        } else {//payloads不为空 即调用notifyItemChanged(position,payloads)方法payloads!=null执行的
            int refreshType = payloads.get(0) != null ? (int) payloads.get(0) : -1;
            switch (refreshType) {
                case ITEM_REFRESH_TYPE_PLAY:
                    LogUtil.d(TAG, "setCallFlashShow 局部刷新 继续播放 viewShowPercent:" + viewShowPercent + ",position:" + pos + ",canPlay:" + canPlay);
                    if (canPlay) {
                        playOrPause(holder, info, true, 50);
                    } else {
                        if (!holder.callFlashView.isPlaying()) {
                            //防止黑屏
                            holder.gv_bg.setVisibility(View.VISIBLE);
                            holder.callFlashView.setVisibility(View.GONE);
                        }
                    }
                    break;
                case ITEM_REFRESH_TYPE_PAUSE:
                    LogUtil.d(TAG, "setCallFlashShow 局部刷新 暂停" + ",position:" + pos);
                    if (holder.callFlashView.isPlaying()) {
                        holder.callFlashView.pause();
                        //此处不隐藏，否则会闪
                    }
                    break;
                case ITEM_REFRESH_TYPE_SCROLL:
//                    LogUtil.d(TAG, "setCallFlashShow 局部刷新 滚动 viewShowPercent:" + viewShowPercent + ",position:" + pos + "canPlay:" + canPlay);
                    if (canPlay) {
                        playOrPause(holder, info, true, 300);
                    } else if (viewShowPercent <= ConstantUtils.CALL_FLASH_LIST_STOP_VIDEO_PERCENT && holder.callFlashView.isPlaying()) {
                        playOrPause(holder, info, false, 300);
                    }
                    break;
            }
        }
    }

    private void playOrPause(NormalViewHolder holder, CallFlashInfo info, boolean isPlay, long delayGoneBGTime) {
        if (isPlay) {
            holder.callFlashView.setVideoMute(true);
            if (holder.callFlashView.isStopVideo()) {
                holder.callFlashView.showCallFlashView(info);
            } else {
                if (holder.callFlashView.isPause()) {
                    holder.callFlashView.continuePlay();
                } else {
                    holder.callFlashView.showCallFlashView(info);
                }
            }
            callFlashVisibile(holder, true, delayGoneBGTime);
        } else {
            holder.callFlashView.pause();
            callFlashVisibile(holder, false, delayGoneBGTime);
        }

    }


    private void callFlashVisibile(final NormalViewHolder holder, boolean isShow, long delayGoneBGTime) {
        if (isShow) {
            if (holder.gv_bg.getVisibility() == View.VISIBLE) {
                Async.scheduleTaskOnUiThread(delayGoneBGTime, new Runnable() {
                    @Override
                    public void run() {
                        holder.gv_bg.setVisibility(View.GONE);
                    }
                });
            }
            holder.callFlashView.setVisibility(View.VISIBLE);
        } else {
            holder.gv_bg.setVisibility(View.VISIBLE);
            holder.callFlashView.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        int count = 0;
        if (model != null) {
            int size = model.size();
            //mAdShowPosition == -1表示不显示广告
            count = mAdShowPosition == -1 || size <= mAdShowPosition ? size : size + 1;
        }
        return count;
    }

    public void setAdShowPosition(int adShowPosition) {
        mAdShowPosition = adShowPosition;
    }

    public CallFlashInfo getItem(int position) {
        CallFlashInfo info = null;
        try {
            if (model != null && model.size() > 0) {
                //mAdShowPosition == -1表示不显示广告
                if (mAdShowPosition == -1 || position < mAdShowPosition) {
                    info = model.get(position);
                } else if (position > mAdShowPosition) {
                    info = model.get(position - 1);
                }
            }
        } catch (Exception e) {
            LogUtil.e("CallFlashOnlineAdapter", " ContactRecyclerviewAdapter getItem exception: " + e.getMessage());
        }

        return info;
    }

    public void setScrollState(int scrollState) {
        mScrollState = scrollState;
    }

    public void setRecycleView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    class NormalViewHolder extends RecyclerView.ViewHolder {
        private View root;
        private RelativeLayout layoutCallFlash;
        private LinearLayout layout_ad_view;
        private GlideView gv_bg;
        private ImageView iv_call_select;
        private ImageView iv_call_photo;
        private ImageView iv_download;
        private CircleProgressBar pb_loading;
        private TextView tv_call_name;
        private CallFlashView callFlashView;

        FrameLayout layout_ad_admob;
        LinearLayout layout_mopub;

        private OnOnlineDownloadListener mOnDownloadListener;

        public NormalViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.layout_root);
            layoutCallFlash = itemView.findViewById(R.id.layout_call_flash);
            layoutCallFlash.setOnClickListener(mOnItemClickListener);
            layout_ad_view = itemView.findViewById(R.id.layout_ad_view);
            layout_ad_admob = (FrameLayout) layout_ad_view.findViewById(R.id.layout_admob);
            layout_mopub = (LinearLayout) itemView.findViewById(R.id.layout_mopub_native_view);

            gv_bg = itemView.findViewById(R.id.gv_bg);
            iv_call_photo = itemView.findViewById(R.id.iv_call_photo);
            iv_call_select = itemView.findViewById(R.id.iv_select);
            iv_download = itemView.findViewById(R.id.iv_download);
            pb_loading = itemView.findViewById(R.id.pb_loading);
            tv_call_name = itemView.findViewById(R.id.tv_call_name);
            callFlashView = itemView.findViewById(R.id.layout_call_flash_view);
            callFlashView.setVideoMute(true);

            CardView cardView = callFlashView.findViewById(R.id.layout_card_view);
            cardView.setCardElevation(0);
            cardView.setPreventCornerOverlap(false);
            cardView.setUseCompatPadding(false);
//            iv_download.setOnClickListener(mOnDownloadClickListener);

            mOnDownloadListener = new OnOnlineDownloadListener();
//            ThemeDownloadApi.addGeneralListener(mOnDownloadListener);
            ThemeResourceHelper.getInstance().addGeneralListener(mOnDownloadListener);
            mDownloadListenerList.add(mOnDownloadListener);
        }
    }

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag();

            if (model != null && model.size() > 0) {
                CallFlashInfo info = getItem(pos);
                GlideView glideView = v.findViewById(R.id.gv_bg);
                // TODO: 2018/7/5  jump to CallFlashPreviewActivity
//                ActivityBuilder.toCallFlashPreview(context, info, glideView);

                ActivityBuilder.toCallFlashDetail(context, info, false);
            }
        }
    };

    private View.OnClickListener mOnDownloadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            int pos = (int) v.getTag();

            if (model != null && model.size() > 0) {
                final CallFlashInfo info = getItem(pos);
                final View view = (View) v.getParent();
                final CircleProgressBar pb_loading = view.findViewById(R.id.pb_loading);
                final View iv_download = view.findViewById(R.id.iv_download);

                pb_loading.setVisibility(View.VISIBLE);
                iv_download.setVisibility(View.GONE);
                ThemeResourceHelper.getInstance().isCanWriteInStorage(ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                ThemeResourceHelper.getInstance().downloadThemeResources(info.id, info.url, new OnDownloadListener() {
                    @Override
                    public void onFailure(String url) {
                        iv_download.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailureForIOException(String url) {
                        onFailure(url);
                    }

                    @Override
                    public void onProgress(String url, int progress) {
                        pb_loading.setProgress(progress);
                    }

                    @Override
                    public void onSuccess(String url, File file) {
                        iv_download.setVisibility(View.GONE);
                        pb_loading.setVisibility(View.GONE);
                        view.findViewById(R.id.iv_select).setVisibility(View.GONE);
                    }
                });

            }
        }
    };

    private class OnOnlineDownloadListener implements OnDownloadListener {
        private NormalViewHolder holder;
        private CallFlashInfo info;

        public void setDownloadParams(NormalViewHolder holder, CallFlashInfo info) {
            this.holder = holder;
            this.info = info;

            if (holder != null && info != null) {
                holder.pb_loading.setProgress(info.progress);
            }
        }

        @Override
        public void onFailureForIOException(String url) {
            onFailure(url);
        }

        @Override
        public void onSuccess(final String url, final File file) {
            if (info != null && info.url != null && info.url.equals(url)) {
                holder.iv_download.setVisibility(View.GONE);
                holder.iv_call_select.setVisibility(View.GONE);
                holder.pb_loading.setVisibility(View.GONE);

                CallFlashManager.getInstance().saveCallFlashDownloadCount(info);
                CallFlashManager.getInstance().saveDownloadedCallFlash(info);
                EventBus.getDefault().post(new EventRefreshCallFlashDownloadCount());

                if (!EncryptionUtil.isEncrypted(file.getAbsolutePath())) {
                    CallFlashManager.getInstance().saveVideoFirstFrame(url);
                }
                //加密
                if (file != null && !TextUtils.isEmpty(file.getAbsolutePath())) {
                    EncryptionUtil.encrypt(info);
                }
            }
        }

        @Override
        public void onProgress(String url, int progress) {
            if (info != null && info.url != null && info.url.equals(url)) {
                holder.iv_download.setVisibility(View.GONE);
                holder.iv_call_select.setVisibility(View.GONE);
                holder.pb_loading.setVisibility(View.VISIBLE);

                info.progress = progress;

                holder.pb_loading.setProgress(progress);
            }
        }

        @Override
        public void onFailure(String url) {
            if (info != null && info.url != null && info.url.equals(url)) {
                holder.iv_download.setVisibility(View.VISIBLE);
                holder.iv_call_select.setVisibility(View.GONE);
                holder.pb_loading.setVisibility(View.GONE);

//                ToastUtils.showToast(context, context.getString(R.string.call_flash_gif_show_load_failed));
            }
        }
    }
}
