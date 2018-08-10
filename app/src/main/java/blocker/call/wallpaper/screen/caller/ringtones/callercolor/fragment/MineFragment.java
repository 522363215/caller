package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.md.flashset.bean.CallFlashDataType;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.flashset.manager.CallFlashManager;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.HorizontalCallFlashAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.HorizontalCallFlashMarginDecoration;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;

/**
 * @author zhq
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MineFragment";
    private GlideView mGvBgCurrent;
    private RecyclerView mRvSetRecord;
    private RecyclerView mRvCollect;
    private RecyclerView mRvDownload;
    private View mLayoutNoCallFlash;
    private View mTvView;
    private View mLayoutSetRecord;
    private View mLayoutCollect;
    private View mLayoutDownloaded;
    private HorizontalCallFlashAdapter mSetRecordAdapter;
    private HorizontalCallFlashAdapter mCollectAdapter;
    private HorizontalCallFlashAdapter mDownloadAdapter;
    private List<CallFlashInfo> mCollectData = new ArrayList<>();
    private List<CallFlashInfo> mSetRecordData = new ArrayList<>();
    private List<CallFlashInfo> mDownloadData = new ArrayList<>();
    private CallFlashInfo mCurrentCallFlashInfo;
    private View mLayoutCollectAllBtn;
    private View mLayoutSetRecordAllBtn;
    private View mLayoutDownloadedAllBtn;
    private TextView mTvCurrentCallFlashTitle;
    private Advertisement mAdvertisement;
    private View mLayoutAd;
    private LinearLayout mLayoutCurrentFlash;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("MineFragment-----show_main");
        initAds();
        initView(view);
        listener();
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    private void setData() {
        //当前设置的call flash
        setCurrentCallFlashBg();
        //设置记录
        setCallFlashRecord();
        //收藏
        setCollect();
        //下载
        setDownload();
    }

    private void initView(View view) {
        //ad
        mLayoutAd = view.findViewById(R.id.layout_ad_view);

        //当前设置
        mLayoutCurrentFlash = view.findViewById(R.id.layout_current_call_flash);
        mGvBgCurrent = view.findViewById(R.id.gv_bg_current);
        mLayoutNoCallFlash = view.findViewById(R.id.layout_no_call_flash);
        mTvCurrentCallFlashTitle = view.findViewById(R.id.tv_current_call_flash_title);
        mTvView = view.findViewById(R.id.tv_view);
        mGvBgCurrent.setOnClickListener(this);
        mTvView.setOnClickListener(this);

        //设置记录
        mLayoutSetRecord = view.findViewById(R.id.layout_set_record);
        mRvSetRecord = view.findViewById(R.id.rv_set_record);
        mLayoutSetRecordAllBtn = view.findViewById(R.id.layout_set_record_all_btn);

        mSetRecordAdapter = new HorizontalCallFlashAdapter(getActivity(), mSetRecordData);
        mRvSetRecord.setLayoutManager(new LinearLayoutManager(getActivity(), GridLayoutManager.HORIZONTAL, false));
        mRvSetRecord.addItemDecoration(new HorizontalCallFlashMarginDecoration());
        mRvSetRecord.setHasFixedSize(true);
        mRvSetRecord.setNestedScrollingEnabled(false);
        mRvSetRecord.setAdapter(mSetRecordAdapter);

        //收藏
        mLayoutCollect = view.findViewById(R.id.layout_collect);
        mRvCollect = view.findViewById(R.id.rv_collect);
        mLayoutCollectAllBtn = view.findViewById(R.id.layout_collect_all_btn);

        mCollectAdapter = new HorizontalCallFlashAdapter(getActivity(), mCollectData);
        mRvCollect.setLayoutManager(new LinearLayoutManager(getActivity(), GridLayoutManager.HORIZONTAL, false));
        mRvCollect.addItemDecoration(new HorizontalCallFlashMarginDecoration());
        mRvCollect.setHasFixedSize(true);
        mRvCollect.setNestedScrollingEnabled(false);
        mRvCollect.setAdapter(mCollectAdapter);

        //已下载
        mLayoutDownloaded = view.findViewById(R.id.layout_download);
        mRvDownload = view.findViewById(R.id.rv_download);
        mLayoutDownloadedAllBtn = view.findViewById(R.id.layout_downloaded_all_btn);

        mDownloadAdapter = new HorizontalCallFlashAdapter(getActivity(), mDownloadData);
        mRvDownload.setLayoutManager(new LinearLayoutManager(getActivity(), GridLayoutManager.HORIZONTAL, false));
        mRvDownload.addItemDecoration(new HorizontalCallFlashMarginDecoration());
        mRvDownload.setHasFixedSize(true);
        mRvDownload.setNestedScrollingEnabled(false);
        mRvDownload.setAdapter(mDownloadAdapter);

        setCurrentCallFlashHeight();
    }

    private void setCurrentCallFlashHeight() {
        if (DeviceUtil.getStatusBarHeight() + Stringutil.dpToPx(getActivity(), 2 * 56 + 3 * 4 + 250 + 32 + 252) > DeviceUtil.getScreenHeight()) {
            ViewGroup.LayoutParams layoutParams = mLayoutCurrentFlash.getLayoutParams();
            //250 为广告的高度
            layoutParams.height = DeviceUtil.getScreenHeight() - Stringutil.dpToPx(getActivity(), 2 * 56 + 3 * 4 + 250 + 32) - DeviceUtil.getStatusBarHeight();
            mLayoutCurrentFlash.setLayoutParams(layoutParams);
        }
    }

    private void listener() {
        mLayoutCollectAllBtn.setOnClickListener(this);
        mLayoutDownloadedAllBtn.setOnClickListener(this);
        mLayoutSetRecordAllBtn.setOnClickListener(this);
    }

    private void setCurrentCallFlashBg() {
        mCurrentCallFlashInfo = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, CallFlashInfo.class);
        if (mCurrentCallFlashInfo != null) {
            mGvBgCurrent.setVisibility(View.VISIBLE);
            mLayoutNoCallFlash.setVisibility(View.GONE);
            if (mCurrentCallFlashInfo.isOnlionCallFlash) {
                if (!TextUtils.isEmpty(mCurrentCallFlashInfo.img_hUrl)) {
                    mGvBgCurrent.showImageWithThumbnail(mCurrentCallFlashInfo.img_hUrl, mCurrentCallFlashInfo.thumbnail_imgUrl);
                } else {
                    mGvBgCurrent.showImageWithThumbnail(mCurrentCallFlashInfo.img_vUrl, mCurrentCallFlashInfo.thumbnail_imgUrl);
                }
            } else {
                if (mCurrentCallFlashInfo.img_hResId > 0) {
                    mGvBgCurrent.showImage(mCurrentCallFlashInfo.img_hResId);
                } else {
                    mGvBgCurrent.showImage(mCurrentCallFlashInfo.imgResId);
                }
            }
            boolean isCallFlashOn = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
            if (isCallFlashOn) {
                mTvCurrentCallFlashTitle.setText(R.string.mine_current_call_flash);
                mTvCurrentCallFlashTitle.setTextColor(getResources().getColor(R.color.white));
            } else {
                mTvCurrentCallFlashTitle.setText(R.string.mine_current_call_flash_is_close);
                mTvCurrentCallFlashTitle.setTextColor(getResources().getColor(R.color.color_FFE54646));
            }
        } else {
            mGvBgCurrent.setVisibility(View.GONE);
            mLayoutNoCallFlash.setVisibility(View.VISIBLE);
        }
    }

    private void setDownload() {
        List<CallFlashInfo> dataList = CallFlashManager.getInstance().getDownloadedCallFlash();
        if (mDownloadData == null) {
            mDownloadData = new ArrayList<>();
        }

        mDownloadData.clear();
        if (dataList != null && dataList.size() > 0) {
            LogUtil.d(TAG, "setDownload dataList:" + dataList.size());
            mDownloadData.addAll(dataList);
        }

        if (mDownloadData.size() > 0) {
            mLayoutDownloaded.setVisibility(View.VISIBLE);
            mDownloadAdapter.notifyDataSetChanged();
        } else {
            mLayoutDownloaded.setVisibility(View.GONE);
        }
    }

    private void setCollect() {
        List<CallFlashInfo> dataList = CallFlashManager.getInstance().getCollectionCallFlash();
        if (mCollectData == null) {
            mCollectData = new ArrayList<>();
        }
        mCollectData.clear();
        if (dataList != null && dataList.size() > 0) {
            LogUtil.d(TAG, "setCollect dataList:" + dataList.size());
            mCollectData.addAll(dataList);
        }

        if (mCollectData.size() > 0) {
            mLayoutCollect.setVisibility(View.VISIBLE);
            mCollectAdapter.notifyDataSetChanged();
        } else {
            mLayoutCollect.setVisibility(View.GONE);
        }
    }

    private void setCallFlashRecord() {
        List<CallFlashInfo> dataList = CallFlashManager.getInstance().getSetRecordCallFlash();
        if (mSetRecordData == null) {
            mSetRecordData = new ArrayList<>();
        }

        mSetRecordData.clear();
        if (dataList != null && dataList.size() > 0) {
            LogUtil.d(TAG, "setCallFlashRecord dataList:" + dataList.size());
            mSetRecordData.addAll(dataList);
        }

        if (mSetRecordData.size() > 0) {
            mLayoutSetRecord.setVisibility(View.VISIBLE);
            mSetRecordAdapter.notifyDataSetChanged();
        } else {
            mLayoutSetRecord.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.gv_bg_current:
                if (mCurrentCallFlashInfo != null) {
                    FlurryAgent.logEvent("MineFragment-----click---to_flash_detail--show_current_flash_detail");
                    ActivityBuilder.toCallFlashDetail(getActivity(), mCurrentCallFlashInfo, false);
                }
                break;
            case R.id.tv_view:
                FlurryAgent.logEvent("MineFragment------click---to_main---show_all_flash");
                ActivityBuilder.toMain(getActivity(), ActivityBuilder.FRAGMENT_HOME);
                break;
            case R.id.layout_collect_all_btn:
                FlurryAgent.logEvent("MineFragment------click---to_flash_list---show_all_collected_flash");
                ActivityBuilder.toCallFlashList(getActivity(), CallFlashDataType.CALL_FLASH_DATA_COLLECTION);
                break;
            case R.id.layout_downloaded_all_btn:
                FlurryAgent.logEvent("MineFragment------click---to_flash_list---show_all_downloaded_flash");
                ActivityBuilder.toCallFlashList(getActivity(), CallFlashDataType.CALL_FLASH_DATA_DOWNLOADED);
                break;
            case R.id.layout_set_record_all_btn:
                FlurryAgent.logEvent("MineFragment------click---to_flash_list---show_all_set_flash");
                ActivityBuilder.toCallFlashList(getActivity(), CallFlashDataType.CALL_FLASH_DATA_SET_RECORD);
                break;
        }
    }

    //******************************************AD******************************************//
    private void initAds() {
        MyAdvertisementAdapter adapter = new MyAdvertisementAdapter(getActivity().getWindow().getDecorView(),
                "",//ConstantUtils.FB_AFTER_CALL_ID
                CallerAdManager.ADMOB_ID_ADV_MINE_NORMAL,//ConstantUtils.ADMOB_AFTER_CALL_NATIVE_ID
                Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,//Advertisement.ADMOB_TYPE_NATIVE, Advertisement.ADMOB_TYPE_NONE
                "",
                Advertisement.MOPUB_TYPE_NATIVE,
                -1,
                "",
                false);
        mAdvertisement = new Advertisement(adapter);
        mAdvertisement.setRefreshWhenClicked(false);
        mAdvertisement.refreshAD(true);
        mAdvertisement.enableFullClickable();
    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, eventKey, isBanner, AdvertisementSwitcher.SERVER_KEY_FLASH_MINE);
        }

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String mopubKey, int moPubType, int baiduKey, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baiduKey, eventKey, AdvertisementSwitcher.SERVER_KEY_FLASH_MINE, isBanner);
        }

        @Override
        public void onAdLoaded() {
            mLayoutAd.setVisibility(View.VISIBLE);
        }

        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.facebook_native_ads_banner_50 : R.layout.facebook_no_icon_native_ads_call_after_big;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_mine : R.layout.layout_admob_advanced_content_ad_mine;
        }
    }
    //******************************************AD******************************************//
}
