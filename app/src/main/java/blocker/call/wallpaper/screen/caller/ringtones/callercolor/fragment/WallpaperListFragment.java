package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.md.callring.Constant;
import com.md.callring.RecyclerClick;
import com.md.flashset.bean.CallFlashDataType;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Theme;
import com.md.serverflash.callback.SingleTopicThemeCallback;
import com.md.serverflash.callback.ThemeSyncCallback;
import com.md.serverflash.callback.TopicThemeCallback;
import com.md.wallpaper.WallpaperPreferenceHelper;
import com.md.wallpaper.bean.WallpaperDataType;
import com.md.wallpaper.bean.WallpaperFormat;
import com.md.wallpaper.bean.WallpaperInfo;
import com.md.wallpaper.manager.WallpaperUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.WallpaperDetailActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.WallpaperListAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CallFlashMarginDecoration;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Utils;

public class WallpaperListFragment extends Fragment implements View.OnClickListener{
    private static final long MAX_LOAD_THEME_FROM_NET_TIME = DateUtils.SECOND_IN_MILLIS * 5;
    private List<WallpaperInfo> wallpaperInfos;
    private RecyclerView rvWallpaper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static final int PAGE_NUMBER_MAX_COUNT = 150;
    private int mCategoryId = -1;
    private TextView mTvRefreshFailed;
    private GridLayoutManager mLayoutManager;
    private AtomicBoolean isRefreshData = new AtomicBoolean(false);
    private View mLayoutPermissionTip;
    private LinearLayout mLayoutNoWalpaper;
    private CallFlashMarginDecoration mWallpaperMarginDecoration;
    private TextView mTvView;
    private Runnable mLoadMaxRunable;
    private static final String CATEGORY_ID = "category_id";
    private static final String DATA_TYPE = "data_type";
    private int mDataType = 0;
    private String topic = "";
    private WallpaperListAdapter wallpaperListAdapter;

    public static WallpaperListFragment newInstance(int dataType) {
        WallpaperListFragment fragment = new WallpaperListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, dataType);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initRecycleView(View view) {
        mLayoutManager = new GridLayoutManager(view.getContext(), 2, GridLayoutManager.VERTICAL, false);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mOnlineFlashType == WallpaperUtil.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode()) {
//                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                    @Override
//                    public int getSpanSize(int position) {
//                        return position == RECYCLER_ONLINE_CALL_FLASH_AD_SHOW_POSITION ? 2 : 1;
//                    }
//                });
//            }
        rvWallpaper.setLayoutManager(mLayoutManager);
        mWallpaperMarginDecoration = new CallFlashMarginDecoration();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mOnlineFlashType == WallpaperUtil.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode()) {
//                mWallpaperMarginDecoration.isHaveAd(true);
//            }
//            mWallpaperMarginDecoration.setAdShowPosition(RECYCLER_ONLINE_CALL_FLASH_AD_SHOW_POSITION);
        rvWallpaper.addItemDecoration(mWallpaperMarginDecoration);
        wallpaperListAdapter = new WallpaperListAdapter(view.getContext(), wallpaperInfos);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mOnlineFlashType == WallpaperUtil.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode()) {
//                mAdapter.setAdShowPosition(RECYCLER_ONLINE_CALL_FLASH_AD_SHOW_POSITION);
//            }
        wallpaperListAdapter.setDataType(mDataType);
        wallpaperListAdapter.setRecycleView(rvWallpaper);
        rvWallpaper.setAdapter(wallpaperListAdapter);
        wallpaperListAdapter.setmRecyclerClick(recyclerClick);
    }

    RecyclerClick recyclerClick = new RecyclerClick() {
        @Override
        public void normalClick(View view, int position) {
            Intent intent = new Intent();
            intent.setClass(getContext(),WallpaperDetailActivity.class);
            intent.putExtra(Constant.WALLPAPER_BUNDLE,wallpaperInfos.get(position));
            startActivity(intent);
        }

        @Override
        public void footClick(View view, int position) {

        }
    };

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

        rvWallpaper.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public int newState;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
//                LogUtil.d(TAG, "onScrollStateChanged newState:" + newState);
                this.newState = newState;
                wallpaperListAdapter.setScrollState(newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        Glide.with(WallpaperListFragment.this).resumeRequests();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        Glide.with(WallpaperListFragment.this).pauseRequests();
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.logEvent("WallpaperListFragment------show_main");
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            mDataType = bundle.get(DATA_TYPE) == null ? 0 : (int) bundle.get(DATA_TYPE);
            mCategoryId = bundle.get(CATEGORY_ID) == null ? -1 : (int) bundle.get(CATEGORY_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall_paper, container, false);
        init(view);
        initRecycleView(view);
        listener();
        initData(true);
        return view;
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

        if ( wallpaperInfos == null || wallpaperInfos.size() <= 0) {
            if (mLayoutNoWalpaper != null) {
                mLayoutNoWalpaper.setVisibility(View.VISIBLE);
                if (mDataType == CallFlashDataType.CALL_FLASH_DATA_HOME) {
                    mTvView.setVisibility(View.GONE);
                } else {
                    mTvView.setVisibility(View.VISIBLE);
                }
            }
            if (rvWallpaper != null) {
                rvWallpaper.setVisibility(View.GONE);
            }
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

    public void initData(boolean isGetCacheData) {
        try {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            isRefreshData.set(true);
            switch (mDataType) {
                case WallpaperDataType.WALLPAPER_HOME:
                    initHomeData(isGetCacheData);
                    break;
                case WallpaperDataType.WALLPAPER__DATA_COLLECTION:
                    initCollectionData();
                    break;
                case WallpaperDataType.WALLPAPER_DATA_DOWNLOADED:
                    updateUI(WallpaperUtil.getInstance().getDownloadedWallpaper(), true);
                    break;
                case WallpaperDataType.WALLPAPER_DATA_SET_RECORD:
                    updateUI(WallpaperUtil.getInstance().getSetRecordWallpaper(), true);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI(List<WallpaperInfo> data, boolean isSuccess) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (isSuccess && data != null && data.size() > 0 && wallpaperListAdapter != null) {
            //数据不同的时才更新界面
            if (!Utils.isSameList(data, wallpaperInfos)) {
                wallpaperInfos.clear();
                wallpaperInfos.addAll(data);
                wallpaperListAdapter.notifyDataSetChanged();
                if (mLayoutNoWalpaper != null) {
                    mLayoutNoWalpaper.setVisibility(View.GONE);
                }
                if (rvWallpaper != null) {
                    rvWallpaper.setVisibility(View.VISIBLE);
                }
            }
        } else {
            //当数据为0时只有收藏界面才刷新，其他界面保持不变
            if (mDataType == CallFlashDataType.CALL_FLASH_DATA_COLLECTION && data != null && data.size() == 0) {
                wallpaperInfos.clear();
                wallpaperListAdapter.notifyDataSetChanged();
            }
            updateUIForNoData(isSuccess);
        }
        mPbLoading.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        stopRefresh();
    }

    private void initHomeData(boolean isGetCacheData) {
//        if (mOnlineFlashType == -1) {
//            topic = WallpaperUtil.ONLINE_THEME_TOPIC_NAME_NON_FEATURED;
//        } else {
        topic = ConstantUtils.WALLPAPER_DATA_TYPE;
//        }
        List<Theme> cacheTopicDataList = ThemeSyncManager.getInstance().getCacheTopicDataList(topic);
        if (isGetCacheData && cacheTopicDataList != null && cacheTopicDataList.size() > 0) {
            //缓存数据存在的时候相当于每次进来优先显示缓存然后再下拉刷新
            updateUI(WallpaperUtil.getInstance().themeToWallpaperInfo(cacheTopicDataList), true);
            mSwipeRefreshLayout.setRefreshing(true);
            initData(false);
        } else {
            ThemeSyncManager.getInstance().syncTopicData(topic, PAGE_NUMBER_MAX_COUNT, new SingleTopicThemeCallback() {
                @Override
                public void onSuccess(int code, List<Theme> data) {
                    if (data == null || data.isEmpty()) {
                        return;
                    }

                    List<WallpaperInfo> infos = WallpaperUtil.getInstance().themeToWallpaperInfo(data);
                    updateUI(infos, true);
                }

                @Override
                public void onFailure(int code, String msg) {
                    updateUI(null, false);
                }
            });
        }
    }

    private void stopRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        isRefreshData.set(false);
        Async.removeScheduledTaskOnUiThread(mLoadMaxRunable);
    }

    private void initCollectionData() {
        updateUI(WallpaperUtil.getInstance().getCollectionCallFlash(), true);
    }


    private ProgressBar mPbLoading;
    private void init(View view) {
        wallpaperInfos = new ArrayList<>();
        mSwipeRefreshLayout = view.findViewById(R.id.flash_swipe_refresh);
        mPbLoading = view.findViewById(R.id.pb_loading);

        mLayoutPermissionTip = view.findViewById(R.id.layout_permission_tip);

        mLayoutNoWalpaper = view.findViewById(R.id.layout_no_call_flash);
        mTvRefreshFailed = view.findViewById(R.id.tv_refresh_failed);
        mTvView = view.findViewById(R.id.tv_view);
        rvWallpaper = view.findViewById(R.id.rv_message);
        mSwipeRefreshLayout.setVisibility(View.GONE);

    }

}
