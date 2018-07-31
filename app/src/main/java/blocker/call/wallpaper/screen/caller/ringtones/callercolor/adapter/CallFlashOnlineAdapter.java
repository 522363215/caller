package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.callback.OnDownloadListener;
import com.md.serverflash.download.ThemeResourceHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashDownloadCount;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.CircleProgressBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.callflash.CallFlashView;
import event.EventBus;

/**
 * Created by ChenR on 2018/1/31.
 */

public class CallFlashOnlineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CallFlashOnlineAdapter";
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

        videoMap = new ConcurrentHashMap<>();
        mDownloadListenerList = new ArrayList<>();

        if (context != null) {
            int dp8 = context.getResources().getDimensionPixelOffset(R.dimen.dp8);
            childViewWidth = (DeviceUtil.getScreenWidth() - dp8 * 3) / 2;
            childViewHeight = Stringutil.dpToPx(context, 252);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = View.inflate(context, R.layout.item_call_flash_online, null);
        return new NormalViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (context == null) return;
        isFlashSwitchOn = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
        mCurrentFlash = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, CallFlashInfo.class);
        setNormalHolder((NormalViewHolder) holder, position);
    }

    private void setNormalHolder(NormalViewHolder holder, int pos) {
        if (fragmentTag == CallFlashManager.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode() && pos == mAdShowPosition) {
            setAdItem(holder);
        } else {
            setItem(holder, pos);
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

    private void setItem(NormalViewHolder holder, int pos) {
        CallFlashInfo info = getItem(pos);
        holder.itemView.setVisibility(View.VISIBLE);
        holder.layoutCallFlash.setVisibility(View.VISIBLE);
        holder.layout_ad_view.setVisibility(View.GONE);
        if (info != null) {
            holder.root.setTag(pos);
            holder.layoutCallFlash.setTag(pos);
            holder.iv_download.setTag(pos);
            if (mCurrentFlash != null && mCurrentFlash.equals(info)) {
                holder.gv_bg.setVisibility(View.INVISIBLE);
                holder.callFlashView.setVisibility(View.VISIBLE);
                if (holder.callFlashView.isStopVideo()) {
                    holder.callFlashView.showCallFlashView(info);
                } else {
                    if (holder.callFlashView.isPauseVideo()) {
                        holder.callFlashView.continuePlay();
                    } else {
                        holder.callFlashView.showCallFlashView(info);
                    }
                }
            } else {
                holder.gv_bg.setVisibility(View.VISIBLE);
                holder.callFlashView.setVisibility(View.INVISIBLE);
                holder.callFlashView.stop();
            }

            if (info.isOnlionCallFlash) {
                String imgUrl = info.img_vUrl;
                if (childViewHeight != 0 && childViewWidth != 0) {
                    imgUrl += "_" + childViewWidth + "x" + childViewHeight;
                }
                info.thumbnail_imgUrl = imgUrl;
                holder.gv_bg.showImage(imgUrl);
            } else {
                if (info.imgResId > 0) {
                    int imgId = info.imgResId;
                    if (info.flashType == FlashLed.FLASH_TYPE_FESTIVAL) {
                        imgId = R.drawable.icon_flash_festival_small;
                    }
                    holder.gv_bg.showImage(imgId);
                } else {
                    holder.gv_bg.showImage(R.drawable.loaded_failed);
                }
            }

            File videoFile = videoMap.get(info.url);
            if (videoFile == null) {
                videoFile = CallFlashManager.getInstance().getOnlineThemeSourcePath(info.url);
                if (videoMap != null) {
                    videoMap.put(info.url, videoFile);
                }
            }
            if (videoFile != null) {
                if (videoFile.exists()) {
                    holder.iv_download.setVisibility(View.GONE);
                    holder.iv_call_select.setVisibility((mCurrentFlash != null && mCurrentFlash.equals(info))
                            ? View.VISIBLE : View.GONE);
                } else {
                    holder.iv_download.setVisibility(View.VISIBLE);
                    holder.iv_call_select.setVisibility(View.GONE);
                }
            }

            holder.mOnDownloadListener.setDownloadParams(holder, info);
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
//            callFlashView.setVideoMute(true);

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
        public void onSuccess(String url, File file) {
            if (info != null && info.url != null && info.url.equals(url)) {
                holder.iv_download.setVisibility(View.GONE);
                holder.iv_call_select.setVisibility(View.GONE);
                holder.pb_loading.setVisibility(View.GONE);

                CallFlashManager.getInstance().saveCallFlashDownloadCount(info);
                CallFlashManager.getInstance().saveDownloadedCallFlash(info);
                EventBus.getDefault().post(new EventRefreshCallFlashDownloadCount());
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
