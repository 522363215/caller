package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flurry.android.FlurryAgent;
import com.md.flashset.bean.CallFlashDataType;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Theme;
import com.md.serverflash.callback.ThemeSyncCallback;
import com.md.serverflash.callback.TopicThemeCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.CallFlashOnlineAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventCallFlashOnlineAdLoaded;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashList;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshWhenNetConnected;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CallFlashMarginDecoration;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.PermissionUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Utils;
import event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallFlashListFragment extends Fragment implements View.OnClickListener {
    private static final long MAX_LOAD_THEME_FROM_NET_TIME = DateUtils.SECOND_IN_MILLIS * 5;
    private static final String TAG = "CallFlashListFragment";
    private static final String DATA_TYPE = "data_type";
    private static final int PAGE_NUMBER_MAX_COUNT = 150;
    private static final String CATEGORY_ID = "category_id";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTvRefreshFailed;
    private CallFlashOnlineAdapter mAdapter = null;
    private List<CallFlashInfo> model = new ArrayList<>();
    private int mDataType = 0;
    private CallFlashMarginDecoration mCallFlashMarginDecoration;
    private String topic = "";
    private AtomicBoolean isRefreshData = new AtomicBoolean(false);
    private int mCategoryId = -1;
    private ProgressBar mPbLoading;
    private Runnable mLoadMaxRunable;
    private View mLayoutPermissionTip;
    private LinearLayout mLayoutNoCallFlash;
    private TextView mTvView;

    private int mCurrentFlashIndex = -1;
    private GridLayoutManager mLayoutManager;
    private int mLastCurrentFlashIndex;

    public static CallFlashListFragment newInstance(int dataType) {
        CallFlashListFragment fragment = new CallFlashListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, dataType);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 分类详情界面使用
     */
    public static CallFlashListFragment newInstance(int dataType, int categoryId) {
        CallFlashListFragment fragment = new CallFlashListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, dataType);
        bundle.putInt(CATEGORY_ID, categoryId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.logEvent("CallFlashListFragment------show_main");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            mDataType = bundle.get(DATA_TYPE) == null ? 0 : (int) bundle.get(DATA_TYPE);
            mCategoryId = bundle.get(CATEGORY_ID) == null ? -1 : (int) bundle.get(CATEGORY_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call_flash_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            mSwipeRefreshLayout = view.findViewById(R.id.flash_swipe_refresh);
            mRecyclerView = view.findViewById(R.id.rv_flash_list);
            mPbLoading = view.findViewById(R.id.pb_loading);

            mLayoutPermissionTip = view.findViewById(R.id.layout_permission_tip);

            mLayoutNoCallFlash = view.findViewById(R.id.layout_no_call_flash);
            mTvRefreshFailed = view.findViewById(R.id.tv_refresh_failed);
            mTvView = view.findViewById(R.id.tv_view);

            mPbLoading.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);

            initRecycleView(view);
            listener();
            initData(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showPermissionTip();
        CallFlashInfo info = CallFlashPreferenceHelper.getObject(
                CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, CallFlashInfo.class);
        if (info != null) {
            mCurrentFlashIndex = model.indexOf(info);
        }
        pauseOrContinuePlayVideo(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseOrContinuePlayVideo(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (model != null) {
            model.clear();
        }
        if (mAdapter != null) {
            mAdapter.clearMap();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.layout_permission_tip:
                ActivityBuilder.toPermissionActivity(getActivity(), false);
                break;
            case R.id.tv_view:
                ActivityBuilder.toMain(getActivity(), ActivityBuilder.FRAGMENT_HOME);
                break;
        }
    }

    private void initRecycleView(View view) {
        mLayoutManager = new GridLayoutManager(view.getContext(), 2, GridLayoutManager.VERTICAL, false);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mOnlineFlashType == CallFlashManager.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode()) {
//                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                    @Override
//                    public int getSpanSize(int position) {
//                        return position == RECYCLER_ONLINE_CALL_FLASH_AD_SHOW_POSITION ? 2 : 1;
//                    }
//                });
//            }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mCallFlashMarginDecoration = new CallFlashMarginDecoration();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mOnlineFlashType == CallFlashManager.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode()) {
//                mCallFlashMarginDecoration.isHaveAd(true);
//            }
//            mCallFlashMarginDecoration.setAdShowPosition(RECYCLER_ONLINE_CALL_FLASH_AD_SHOW_POSITION);
        mRecyclerView.addItemDecoration(mCallFlashMarginDecoration);
        mAdapter = new CallFlashOnlineAdapter(view.getContext(), model);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mOnlineFlashType == CallFlashManager.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode()) {
//                mAdapter.setAdShowPosition(RECYCLER_ONLINE_CALL_FLASH_AD_SHOW_POSITION);
//            }
        mAdapter.setDataType(mDataType);
        mAdapter.setRecycleView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void initData(boolean isGetCacheData) {
        try {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            isRefreshData.set(true);
            switch (mDataType) {
                case CallFlashDataType.CALL_FLASH_DATA_HOME:
                    initHomeData(isGetCacheData);
                    break;
                case CallFlashDataType.CALL_FLASH_DATA_CATEGORY:
                    initCategoryData(isGetCacheData);
                    break;
                case CallFlashDataType.CALL_FLASH_DATA_COLLECTION:
                    initCollectionData();
                    break;
                case CallFlashDataType.CALL_FLASH_DATA_DOWNLOADED:
                    updateUI(CallFlashManager.getInstance().getDownloadedCallFlash(), true);
                    break;
                case CallFlashDataType.CALL_FLASH_DATA_SET_RECORD:
                    updateUI(CallFlashManager.getInstance().getSetRecordCallFlash(), true);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI(List<CallFlashInfo> data, boolean isSuccess) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (isSuccess && data != null && data.size() > 0 && mAdapter != null) {
            //数据不同的时才更新界面
            if (!Utils.isSameList(data, model)) {
                model.clear();
                model.addAll(data);
                mAdapter.notifyDataSetChanged();
                if (mLayoutNoCallFlash != null) {
                    mLayoutNoCallFlash.setVisibility(View.GONE);
                }
                if (mRecyclerView != null) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            //当数据为0时只有收藏界面才刷新，其他界面保持不变
            if (mDataType == CallFlashDataType.CALL_FLASH_DATA_COLLECTION && data != null && data.size() == 0) {
                model.clear();
                mAdapter.notifyDataSetChanged();
            }
            updateUIForNoData(isSuccess);
        }
        mPbLoading.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        stopRefresh();
    }

    private void updateUIForNoData(boolean isSuccess) {
        if (getActivity() == null || getActivity().isFinishing() || !isRefreshData.get()) {
            return;
        }
        if (isSuccess) {
            if (mDataType == CallFlashDataType.CALL_FLASH_DATA_COLLECTION) {
                mTvRefreshFailed.setText(R.string.no_collection_call_flash_list_tip);
//                ToastUtils.showToast(getActivity(), getActivity().getString(R.string.no_collection_call_flash_list_tip));
            } else {
                mTvRefreshFailed.setText(R.string.no_online_call_flash_list_tip);
                ToastUtils.showToast(getActivity(), getActivity().getString(R.string.no_online_call_flash_list_tip));
            }
        } else {
            mTvRefreshFailed.setText(R.string.call_flash_more_refresh_online_themes_load_failed);
            ToastUtils.showToast(getActivity(), getActivity().getString(R.string.call_flash_more_refresh_online_themes_failed_tip));
        }

        if (model == null || model.size() <= 0) {
            if (mLayoutNoCallFlash != null) {
                mLayoutNoCallFlash.setVisibility(View.VISIBLE);
                if (mDataType == CallFlashDataType.CALL_FLASH_DATA_HOME) {
                    mTvView.setVisibility(View.GONE);
                } else {
                    mTvView.setVisibility(View.VISIBLE);
                }
            }
            if (mRecyclerView != null) {
                mRecyclerView.setVisibility(View.GONE);
            }
        }
    }

    private void initHomeData(boolean isGetCacheData) {
//        if (mOnlineFlashType == -1) {
//            topic = CallFlashManager.ONLINE_THEME_TOPIC_NAME_NON_FEATURED;
//        } else {
        topic = ConstantUtils.HOME_DATA_TYPE;
//        }
        List<Theme> cacheTopicDataList = ThemeSyncManager.getInstance().getCacheTopicDataList(topic);
        if (CommonUtils.isOldForFlash()) {
            List<Theme> cacheNewFlash = ThemeSyncManager.getInstance().getCacheTopicData(
                    CallFlashManager.ONLINE_THEME_TOPIC_NAME_NEW_FLASH, 1, 6);
            if (cacheNewFlash != null) {
                cacheTopicDataList.addAll(0, cacheNewFlash);
            }
        }
        if (isGetCacheData && cacheTopicDataList != null && cacheTopicDataList.size() > 0) {
            //缓存数据存在的时候相当于每次进来优先显示缓存然后再下拉刷新
            updateUI(CallFlashManager.getInstance().themeToCallFlashInfo(cacheTopicDataList), true);
            mSwipeRefreshLayout.setRefreshing(true);
            initData(false);
        } else {
            String[] themeTopic = null;
            if (CommonUtils.isOldForFlash()) {
                themeTopic = new String[2];
                themeTopic[0] = CallFlashManager.ONLINE_THEME_TOPIC_NAME_NEW_FLASH;
                themeTopic[1] = ConstantUtils.HOME_DATA_TYPE;
            } else {
                themeTopic = new String[1];
                themeTopic[0] = ConstantUtils.HOME_DATA_TYPE;
            }

            ThemeSyncManager.getInstance().syncTopicData(themeTopic, PAGE_NUMBER_MAX_COUNT, new TopicThemeCallback() {
                @Override
                public void onSuccess(int code, Map<String, List<Theme>> data) {
                    if (data == null || data.isEmpty()) {
                        return;
                    }
                    List<Theme> list = new ArrayList<>();
                    if (CommonUtils.isOldForFlash()) {
                        List<Theme> newFlash = data.get(CallFlashManager.ONLINE_THEME_TOPIC_NAME_NEW_FLASH);

                        if (newFlash.size() > 6) {
                            list.addAll(0, newFlash.subList(0, 6));
                        } else
                            list.addAll(0, newFlash);
                    }
                    List<Theme> feature = data.get(topic);
                    list.addAll(feature);

                    List<CallFlashInfo> infos = CallFlashManager.getInstance().themeToCallFlashInfo(list);
                    updateUI(infos, true);
                }

                @Override
                public void onFailure(int code, String msg) {
                    updateUI(null, false);
                }
            });
        }
    }

    private void initCategoryData(boolean isGetCacheData) {
        List<Theme> cacheCategoryDataList = ThemeSyncManager.getInstance().getCacheCategoryDataList(mCategoryId);
        if (isGetCacheData && cacheCategoryDataList != null && cacheCategoryDataList.size() > 0) {
            //缓存数据存在的时候相当于每次进来优先显示缓存然后再下拉刷新
            updateUI(CallFlashManager.getInstance().themeToCallFlashInfo(cacheCategoryDataList), true);
            mSwipeRefreshLayout.setRefreshing(true);
            initData(false);
        } else {
            ThemeSyncManager.getInstance().syncPageData(mCategoryId, new ThemeSyncCallback() {
                @Override
                public void onSuccess(List<Theme> data) {
                    updateUI(CallFlashManager.getInstance().themeToCallFlashInfo(data), true);
                }

                @Override
                public void onFailure(int code, String msg) {
                    updateUI(null, false);
                }
            });
        }
    }

    private void initCollectionData() {
        updateUI(CallFlashManager.getInstance().getCollectionCallFlash(), true);
    }

    private void stopRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        isRefreshData.set(false);
        Async.removeScheduledTaskOnUiThread(mLoadMaxRunable);
    }


    /**
     * @param isContinuePlay true:继续播放，false：暂停播放
     */
    public void pauseOrContinuePlayVideo(boolean isContinuePlay) {
        boolean enableCallFlash = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, PreferenceHelper.DEFAULT_VALUE_FOR_CALL_FLASH);
        if (getActivity() == null || getActivity().isFinishing() || mAdapter == null || !enableCallFlash) {
            return;
        }
        int firstItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastItemPosition = mLayoutManager.findLastVisibleItemPosition();
//        LogUtil.d(TAG, "pauseOrContinuePlayVideo firstItemPosition:" + firstItemPosition + ",lastItemPosition:" + lastItemPosition + ",mCurrentFlashIndex:" + mCurrentFlashIndex
//                + ",mLastCurrentFlashIndex:" + mLastCurrentFlashIndex + ",isContinuePlay:" + isContinuePlay);
        if (mLastCurrentFlashIndex != mCurrentFlashIndex && mLastCurrentFlashIndex >= firstItemPosition && mLastCurrentFlashIndex <= lastItemPosition) {
            //第二参数可以为任意对象，在adapter中payloads.get(0)可获得此值，此处写为int
            mAdapter.notifyItemChanged(mLastCurrentFlashIndex, CallFlashOnlineAdapter.ITEM_REFRESH_TYPE_PAUSE);
        }

        if (mCurrentFlashIndex >= firstItemPosition && mCurrentFlashIndex <= lastItemPosition) {
            if (isContinuePlay) {
                mAdapter.notifyItemChanged(mCurrentFlashIndex, CallFlashOnlineAdapter.ITEM_REFRESH_TYPE_PLAY);
            } else {
                mAdapter.notifyItemChanged(mCurrentFlashIndex, CallFlashOnlineAdapter.ITEM_REFRESH_TYPE_PAUSE);
            }
            mLastCurrentFlashIndex = mCurrentFlashIndex;
        }
    }

    private void listener() {
        mTvView.setOnClickListener(this);
        mLayoutPermissionTip.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Async.scheduleTaskOnUiThread(MAX_LOAD_THEME_FROM_NET_TIME, mLoadMaxRunable = new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null && !getActivity().isFinishing() && isRefreshData.get()) {
                            updateUIForNoData(false);
                            stopRefresh();
                        }
                    }
                });
                initData(false);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public int newState;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
//                LogUtil.d(TAG, "onScrollStateChanged newState:" + newState);
                this.newState = newState;
                mAdapter.setScrollState(newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        Glide.with(CallFlashListFragment.this).resumeRequests();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        Glide.with(CallFlashListFragment.this).pauseRequests();
                        break;
                }
                //滑动过程中控制设置的callflash的播放和暂停
                setCallFlashViewPlay();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                //滑动过程中控制设置的callflash的播放和暂停
                setCallFlashViewPlay();
            }
        });
    }

    private void setCallFlashViewPlay() {
        boolean enableCallFlash = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, PreferenceHelper.DEFAULT_VALUE_FOR_CALL_FLASH);
        if (!enableCallFlash || mLayoutManager == null || mAdapter == null) return;
        if (mDataType == CallFlashDataType.CALL_FLASH_DATA_HOME || mDataType == CallFlashDataType.CALL_FLASH_DATA_COLLECTION
                || mDataType == CallFlashDataType.CALL_FLASH_DATA_DOWNLOADED || mDataType == CallFlashDataType.CALL_FLASH_DATA_SET_RECORD) {
            //获取当前recycleView屏幕可见的第一项和最后一项的Position
            int firstItemPosition = mLayoutManager.findFirstVisibleItemPosition();
            int lastItemPosition = mLayoutManager.findLastVisibleItemPosition();
            if (mCurrentFlashIndex != -1 && mCurrentFlashIndex >= firstItemPosition && mCurrentFlashIndex <= lastItemPosition) {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemChanged(mCurrentFlashIndex, CallFlashOnlineAdapter.ITEM_REFRESH_TYPE_SCROLL);
                    }
                });
            }
        }
    }

    public void onEventMainThread(EventCallFlashOnlineAdLoaded event) {
        if (mCallFlashMarginDecoration != null) {
            mCallFlashMarginDecoration.setAdLoaded(true);
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onEventMainThread(EventRefreshCallFlashList event) {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onEventMainThread(EventRefreshWhenNetConnected event) {
        if (model == null || model.size() <= 0) {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
            initData(false);
        }
    }

    public void showPermissionTip() {
        if (mLayoutPermissionTip != null) {
            boolean isHaveAllPermission = PermissionUtils.isHaveAllPermission(getActivity());
            if (!isHaveAllPermission) {
                mLayoutPermissionTip.setVisibility(View.VISIBLE);
            } else {
                mLayoutPermissionTip.setVisibility(View.GONE);
            }
        }
    }
}
