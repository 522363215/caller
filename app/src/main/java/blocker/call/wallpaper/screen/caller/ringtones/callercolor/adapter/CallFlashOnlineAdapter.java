package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
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
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.CallFlashPreviewActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventCallFlashOnlineAdLoaded;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.CircleProgressBar;
import event.EventBus;

/**
 * Created by ChenR on 2018/1/31.
 */

public class CallFlashOnlineAdapter extends RecyclerView.Adapter<CallFlashOnlineAdapter.ViewHolder> {

    private static final String TAG = "CallFlashOnlineAdapter";
    private BitmapRequestBuilder<GlideUrl, Bitmap> mGlideBuilder;
    private Context context = null;
    private List<CallFlashInfo> model = null;

    private boolean isFlashSwitchOn = false;
    private int mFlashType = -1;
    private String mCustomPath = "";
    private String mDynamicPath = "";
    private boolean misInitAd;
    private Advertisement mAdvertisement;
    private boolean mIsAdloaded;
    private int mAdShowPosition = -1;
    private boolean mIsComeCallAfter;
    private boolean mIsComePhoneDetail;
    private ConcurrentHashMap<String, File> videoMap;
    private List<OnOnlineDownloadListener> mDownloadListenerList = null;

    private int childViewWidth, childViewHeight;

    public void setFragmentTag(int fragmentTag) {
        this.fragmentTag = fragmentTag;
    }

    private int fragmentTag = -99;

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

        //初始化glide,列表中优化
        mGlideBuilder = Glide.with(context).from(GlideUrl.class)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.glide_loading_bg)//加载中图片
                .error(R.drawable.glide_load_failed_bg)//加载失败图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = View.inflate(context, R.layout.item_call_flash_online, null);
        return new CallFlashOnlineAdapter.ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (context == null) return;
        isFlashSwitchOn = CallFlashPreferenceHelper.getBoolean(ConstantUtils.CALL_FLASH_ON, false);
        mFlashType = CallFlashPreferenceHelper.getInt(ConstantUtils.CALL_FLASH_TYPE, -1);
        mCustomPath = CallFlashPreferenceHelper.getString(ConstantUtils.CALL_FLASH_CUSTOM_BG_PATH, "");
        mDynamicPath = CallFlashPreferenceHelper.getString(ConstantUtils.CALL_FLASH_TYPE_DYNAMIC_PATH, "");

        if (fragmentTag == CallFlashManager.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode() && position == mAdShowPosition) {
            // 广告相关.
            holder.layoutCallFlash.setVisibility(View.GONE);
            holder.layout_ad_view.setVisibility(View.VISIBLE);

            if (!mIsAdloaded) {
                if (CallerAdManager.isShowAd(CallerAdManager.POSITION_FB_ADS_CALLFLASH_HOT)) {
//                    initAd(holder.itemView, holder);
                }
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
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.layoutCallFlash.setVisibility(View.VISIBLE);
            holder.layout_ad_view.setVisibility(View.GONE);
            final CallFlashInfo info = getItem(position);
            if (info != null) {
                holder.root.setTag(position);
                holder.layoutCallFlash.setTag(position);
                holder.iv_download.setTag(position);
//            holder.tv_call_name.setText(info.title);

                String imgUrl = info.img_vUrl;
                if (childViewHeight != 0 && childViewWidth != 0) {
                    imgUrl += "_" + childViewWidth + "x" + childViewHeight;
                }
                if (fragmentTag != -1 && position < 6) {
                    imgUrl = info.img_vUrl;
                }


                mGlideBuilder.load(new GlideUrl(imgUrl)).into(holder.coverImg);

                File videoFile = videoMap.get(info.url);
                if (videoFile == null) {
                    videoFile = CallFlashManager.getInstance().getOnlineThemeSourcePath(info.url);
                    if (videoMap != null) {
                        videoMap.put(info.url, videoFile);
                    }
                }

                if (videoFile.exists()) {
                    holder.iv_download.setVisibility(View.GONE);
                    if (isFlashSwitchOn) {
                        LogUtil.d("chenr", "current flash type: " + mFlashType + ", info flash type: " + info.flashType +
                                ", \ninfo path: " + info.path + ", dynamic path: " + mDynamicPath + ", custom path: " + mCustomPath);
                        if (((info.flashType == FlashLed.FLASH_TYPE_CUSTOM && mCustomPath.equals(info.path))
                                || (info.flashType == FlashLed.FLASH_TYPE_DYNAMIC && mDynamicPath.equals(info.path))
                                || (info.flashType != FlashLed.FLASH_TYPE_CUSTOM && info.flashType != FlashLed.FLASH_TYPE_DYNAMIC))
                                && info.flashType == mFlashType) {
                            holder.iv_call_select.setVisibility(View.VISIBLE);
                        } else {
                            holder.iv_call_select.setVisibility(View.GONE);
                        }
                    } else {
                        holder.iv_call_select.setVisibility(View.GONE);
                    }
                } else {
                    holder.iv_download.setVisibility(View.VISIBLE);
                }
                holder.mOnDownloadListener.setDownloadParams(holder, info);
            }
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

    public void setCome(boolean isComeCallAfter, boolean isComePhoneDetail) {
        mIsComeCallAfter = isComeCallAfter;
        mIsComePhoneDetail = isComePhoneDetail;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View root;
        private RelativeLayout layoutCallFlash;
        private LinearLayout layout_ad_view;
        private ImageView coverImg;
        private ImageView iv_call_select;
        private ImageView iv_call_photo;
        private ImageView iv_download;
        private CircleProgressBar pb_loading;
        private TextView tv_call_name;

        FrameLayout layout_ad_admob;
        LinearLayout fb_native_ad_container;
        LinearLayout layout_mopub;

        private OnOnlineDownloadListener mOnDownloadListener;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.layout_root);
//            ViewGroup.LayoutParams params = root.getLayoutParams();
//            if (params == null)
//                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.height = Stringutil.dpToPx(ApplicationEx.getInstance().getApplicationContext(), 252);
//            root.setLayoutParams(params);


            layoutCallFlash = itemView.findViewById(R.id.layout_call_flash);
            layoutCallFlash.setOnClickListener(mOnItemClickListener);
            layout_ad_view = itemView.findViewById(R.id.layout_ad_view);
            layout_ad_admob = (FrameLayout) layout_ad_view.findViewById(R.id.layout_admob);
            fb_native_ad_container = (LinearLayout) itemView.findViewById(R.id.fb_native_ad_container);
            layout_mopub = (LinearLayout) itemView.findViewById(R.id.layout_mopub_native_view);

            coverImg = itemView.findViewById(R.id.coverImg);
            iv_call_photo = itemView.findViewById(R.id.iv_call_photo);
            iv_call_select = itemView.findViewById(R.id.iv_select);
            iv_download = itemView.findViewById(R.id.iv_download);
            pb_loading = itemView.findViewById(R.id.pb_loading);
            tv_call_name = itemView.findViewById(R.id.tv_call_name);

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

                // TODO: 2018/7/5  jump to CallFlashPreviewActivity
                ActivityBuilder.toCallFlashPreview(context, info, true);
            }
        }
    };

    private View.OnClickListener mOnDownloadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            int pos = (int) v.getTag();

            if (model != null && model.size() > 0) {
                CallFlashInfo info = getItem(pos);
                final View view = (View) v.getParent();
                final CircleProgressBar pb_loading = view.findViewById(R.id.pb_loading);
                final View iv_download = view.findViewById(R.id.iv_download);

                pb_loading.setVisibility(View.VISIBLE);
                iv_download.setVisibility(View.GONE);
                ThemeResourceHelper.getInstance().isCanWriteInStorage(ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
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
        private ViewHolder holder;
        private CallFlashInfo info;

        public void setDownloadParams(ViewHolder holder, CallFlashInfo info) {
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

//    private class OnOnlineDownloadListener implements ThemeDownloadListener {
//        private ViewHolder holder;
//        private CallFlashInfo info;
//
//        public void setDownloadParams(ViewHolder holder, CallFlashInfo info) {
//            this.holder = holder;
//            this.info = info;
//
//            if (holder != null && info != null) {
//                holder.pb_loading.setProgress(info.progress);
//            }
//        }
//
//        @Override
//        public void onDownloadSuccess(String s, File file) {
//            if (info != null && info.url != null && info.url.equals(s)) {
//                holder.iv_download.setVisibility(View.GONE);
//                holder.iv_call_select.setVisibility(View.GONE);
//                holder.pb_loading.setVisibility(View.GONE);
//            }
//        }
//
//        @Override
//        public void onDownloading(String s, int i) {
//            if (info != null && info.url != null && info.url.equals(s)) {
//                holder.iv_download.setVisibility(View.GONE);
//                holder.iv_call_select.setVisibility(View.GONE);
//                holder.pb_loading.setVisibility(View.VISIBLE);
//
//                info.progress = i;
//
//                holder.pb_loading.setProgress(i);
//            }
//        }
//
//        @Override
//        public void onDownloadFailed(String s) {
//            if (info != null && info.url != null && info.url.equals(s)) {
//                holder.iv_download.setVisibility(View.VISIBLE);
//                holder.iv_call_select.setVisibility(View.GONE);
//                holder.pb_loading.setVisibility(View.GONE);
//
////                ToastUtils.showToast(context, context.getString(R.string.call_flash_gif_show_load_failed));
//            }
//        }
//    }

    private void initAd(View view, ViewHolder holder) {
        if (misInitAd) {
            return;
        }
        LogUtil.d(TAG, "initAds contactRecycler");
        misInitAd = true;
//        MyAdvertisementAdapter adapter = null;
//            adapter = new MyAdvertisementAdapter(viewSelected,
//                    "",//ConstantUtils.FB_FAKE_CALL_TIME_ID
//                    ConstantUtils.ADMOB_ADV_FAKE_RESULT_ID,//ConstantUtils.ADMOB_AFTER_CALL_NATIVE_ID
//                    Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,//Advertisement.ADMOB_TYPE_NATIVE, Advertisement.ADMOB_TYPE_NONE
//                    "",
//                    false
//                    , holder);
//
//        mAdvertisement = new Advertisement(adapter);
//        mAdvertisement.setShowType(true);
//        mAdvertisement.setRefreshWhenClicked(true);
//        mAdvertisement.refreshAD(true);
        MyAdvertisementAdapter adapter = new MyAdvertisementAdapter(view,
                CallerAdManager.getContactBigFbId(),//ConstantUtils.FB_AFTER_CALL_ID
                ConstantUtils.ADMOB_ADV_CONTACT_ACTIVITY_ID,//ConstantUtils.ADMOB_AFTER_CALL_NATIVE_ID
                Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,//Advertisement.ADMOB_TYPE_NATIVE, Advertisement.ADMOB_TYPE_NONE
                CallerAdManager.MOPUB_NATIVE_ADV_BANNER_CONTACT_ID,
                Advertisement.MOPUB_TYPE_NATIVE,
                CallerAdManager.BAIDU_ADV_BANNER_CONTACT_ID,
                "",
                false,
                holder);
        if (context != null) {
            adapter.setAdMobBannerKey(CallerAdManager.ADMOB_BIG_BANNER_CONTACT_BIG_ID);
        }
        mAdvertisement = new Advertisement(adapter);
        mAdvertisement.setRefreshWhenClicked(false);
        mAdvertisement.refreshAD(true);

    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {

        private ViewHolder holder;

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType,
                                      String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, eventKey, isBanner,
                    AdvertisementSwitcher.SERVER_KEY_CONTACT_BIG_NEW);
        }

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType,
                                      String mopubKey, int moPubType, int baiduKey, String eventKey, boolean isBanner, ViewHolder holder) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baiduKey,
                    eventKey, AdvertisementSwitcher.SERVER_KEY_CONTACT_BIG_NEW, isBanner);
            this.holder = holder;
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            mIsAdloaded = true;
            holder.itemView.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new EventCallFlashOnlineAdLoaded());
            LogUtil.d("adver", "CallFlashOnlineAdapter onAdLoaded");
        }

        //        @Override
//        public void adjustAdmobView(FrameLayout layoutAdmob) {
//            isShowAd = true;
//            View layout_ad_view = root.findViewById(R.id.layout_contact_list_ad_view);
//            FrameLayout layout_admob = (FrameLayout) layout_ad_view.findViewById(R.id.layout_admob);
//
//            layout_ad_view.setVisibility(View.VISIBLE);
//            layout_admob.setVisibility(View.VISIBLE);
//        }
//
        @Override
        public void adjustFbContainerView(LinearLayout layoutFacebook) {
            ViewGroup.LayoutParams params = layoutFacebook.getLayoutParams();
            params.width = DeviceUtil.getScreenWidth() - Stringutil.dpToPx(context, 16);
            layoutFacebook.setLayoutParams(params);
        }

        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.layout_facebook_ad_banner_contact_white_font : R.layout.facebook_ads_hot_callflash_recycleview;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_messageflash :
                    R.layout.layout_admob_advanced_content_ad_messageflash;
        }

        @Override
        public int getMoPubViewRes() {
            return mIsBanner ? R.layout.layout_mopub_ad_banner_contact_white_font : R.layout.layout_mopub_no_icon_native_ads;
        }

        @Override
        public int getBaiDuViewRes() {
            return mIsBanner ? R.layout.layout_du_ad_banner_contact_list_white_font : R.layout.layout_du_ad_big;
        }

        @Override
        public void adjustMoPubContainerView(LinearLayout layoutMoPub) {
            int offset = context.getResources().getDimensionPixelOffset(R.dimen.dp16) + Stringutil.dpToPx(context, 24);
            int screenWidth = DeviceUtil.getScreenWidth();

            ViewGroup.LayoutParams params = layoutMoPub.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams((screenWidth - offset), ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                params.width = screenWidth - offset;
            }
            layoutMoPub.setLayoutParams(params);
        }
    }
}
