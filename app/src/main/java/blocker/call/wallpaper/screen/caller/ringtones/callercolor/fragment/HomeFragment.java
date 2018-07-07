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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Category;
import com.md.serverflash.beans.Theme;
import com.md.serverflash.callback.CategoryCallback;
import com.md.serverflash.callback.SingleTopicThemeCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.CallFlashOnlineAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventCallFlashOnlineAdLoaded;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CallFlashMarginDecoration;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;
import event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private static final int ONLINE_THEME_PAGE_LOAD_LENGTH = 6;
    private static final int RECYCLER_ONLINE_CALL_FLASH_AD_SHOW_POSITION = 2;

    private static final long MAX_LOAD_THEME_FROM_NET_TIME = DateUtils.SECOND_IN_MILLIS * 5;
    private static final String TAG = "HomeFragment";

    private View mRoot;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTvRefreshFailed;

    private CallFlashOnlineAdapter mAdapter = null;
    private List<CallFlashInfo> model = new ArrayList<>();

    private boolean isRefreshed = false;
    private boolean isEmptyData;
    private int mOnlineFlashType = 0;
    private CallFlashMarginDecoration mCallFlashMarginDecoration;

    private int mPageNumber = 1;
    private String topic = "";

    private AtomicBoolean isRefreshData = new AtomicBoolean(false);

    public static HomeFragment newInstance(int onlineFlashType) {
        HomeFragment fragment = new HomeFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("online_flash_type", onlineFlashType);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            mOnlineFlashType = (int) bundle.get("online_flash_type");
        }

    }

    private void initOnlineData() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        isRefreshData.set(true);

        if (mOnlineFlashType == -1) {
            topic = CallFlashManager.ONLINE_THEME_TOPIC_NAME_NON_FEATURED;
        } else {
            topic = CallFlashManager.ONLINE_THEME_TOPIC_NAME_FEATURED;
        }
        List<Theme> data = ThemeSyncManager.getInstance().getCacheTopicData(topic, mPageNumber, ONLINE_THEME_PAGE_LOAD_LENGTH + 1);
        if (data != null /*&& data.size() > CallFlashManager.SMALL_TOPIC_THEME_REQUEST_COUNT*/) {
//            LogUtil.d(TAG, "initOnlineData data:" + data.size());
            model.clear();
            if (data.size() > ONLINE_THEME_PAGE_LOAD_LENGTH) {
                model.addAll(CallFlashManager.getInstance().themeToCallFlashInfo(data.subList(0, ONLINE_THEME_PAGE_LOAD_LENGTH)));
            } else {
                model.addAll(CallFlashManager.getInstance().themeToCallFlashInfo(data));
            }

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            } else {
                if (mTvRefreshFailed != null) {
                    mTvRefreshFailed.setVisibility(View.VISIBLE);
                }

                if (mRecyclerView != null) {
                    mRecyclerView.setVisibility(View.GONE);
                }
            }
            stopRefresh();
        } else {

            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null && !getActivity().isFinishing() && isRefreshData.get()) {
                        ToastUtils.showToast(getActivity(), getString(R.string.call_flash_more_refresh_online_themes_failed_tip));

                        stopRefresh();
                    }
                }
            }, MAX_LOAD_THEME_FROM_NET_TIME);

            ThemeSyncManager.getInstance().syncTopicData(topic, CallFlashManager.getMaxReqCount(),
                    new SingleTopicThemeCallback() {
                        @Override
                        public void onSuccess(int code, List<Theme> data) {
                            if (getActivity() == null || getActivity().isFinishing() || isRefreshData.get()) {
                                return;
                            }
                            model.clear();
                            if (data.size() > 6) {
                                model.addAll(CallFlashManager.getInstance().themeToCallFlashInfo(data.subList(0, CallFlashManager.SMALL_TOPIC_THEME_REQUEST_COUNT)));
                            } else {
                                model.addAll(CallFlashManager.getInstance().themeToCallFlashInfo(data));
                            }

                            if (mAdapter != null) {
                                mAdapter.notifyDataSetChanged();
                            } else {
                                if (mTvRefreshFailed != null) {
                                    mTvRefreshFailed.setVisibility(View.VISIBLE);
                                }

                                if (mRecyclerView != null) {
                                    mRecyclerView.setVisibility(View.GONE);
                                }
                            }
                            stopRefresh();
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            if (getActivity() == null || getActivity().isFinishing() || isRefreshData.get()) {
                                return;
                            }

                            stopRefresh();

                            if (mTvRefreshFailed != null) {
                                mTvRefreshFailed.setVisibility(View.VISIBLE);
                            }

                            if (mRecyclerView != null) {
                                mRecyclerView.setVisibility(View.GONE);
                            }

                            if (getActivity() != null) {
                                ToastUtils.showToast(getActivity(), getActivity().getString(R.string.call_flash_more_refresh_online_themes_failed_tip));
                            }
                        }
                    });
        }
    }

    private void stopRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        isRefreshData.set(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call_flash_classic, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();

        ThemeSyncManager.getInstance().syncCategoryList(new CategoryCallback() {
            @Override
            public void onSuccess(int code, List<Category> data) {

            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRoot = view;
        if (view != null) {
            mSwipeRefreshLayout = view.findViewById(R.id.flash_swipe_refresh);
            mTvRefreshFailed = view.findViewById(R.id.tv_refresh_failed);
            mRecyclerView = view.findViewById(R.id.rv_flash_list);

            mSwipeRefreshLayout.setRefreshing(true);
            GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 2, GridLayoutManager.VERTICAL, false);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mOnlineFlashType == CallFlashManager.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode()) {
//                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                    @Override
//                    public int getSpanSize(int position) {
//                        return position == RECYCLER_ONLINE_CALL_FLASH_AD_SHOW_POSITION ? 2 : 1;
//                    }
//                });
//            }
            mRecyclerView.setLayoutManager(layoutManager);
            mCallFlashMarginDecoration = new CallFlashMarginDecoration();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mOnlineFlashType == CallFlashManager.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode()) {
//                mCallFlashMarginDecoration.isHaveAd(true);
//            }
//            mCallFlashMarginDecoration.setAdShowPosition(RECYCLER_ONLINE_CALL_FLASH_AD_SHOW_POSITION);
            mRecyclerView.addItemDecoration(mCallFlashMarginDecoration);
            mAdapter = new CallFlashOnlineAdapter(view.getContext(), model);
            mAdapter.setCome(getActivity().getIntent().getBooleanExtra(ConstantUtils.COME_FROM_CALLAFTER, false), getActivity().getIntent().getBooleanExtra(ConstantUtils.COME_FROM_PHONEDETAIL, false));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mOnlineFlashType == CallFlashManager.ONLINE_THEME_TOPIC_NAME_FEATURED.hashCode()) {
//                mAdapter.setAdShowPosition(RECYCLER_ONLINE_CALL_FLASH_AD_SHOW_POSITION);
//            }
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setFragmentTag(mOnlineFlashType);

            listener();
            try {
                initOnlineData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    private void listener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    mPageNumber = 1;
                    initOnlineData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isPositionChanged = false;
            private int mLastPosition = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        Glide.with(HomeFragment.this).resumeRequests();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        Glide.with(HomeFragment.this).pauseRequests();
                        break;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                if (dy > 0) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    int position = layoutManager.findLastVisibleItemPosition();
                    isPositionChanged = (mLastPosition != position);
                    boolean isLast = position == model.size() - 1;
                    //有广告打开
//                    if (!topic.equals(CallFlashManager.ONLINE_THEME_TOPIC_NAME_NON_FEATURED)) {
//                        isLast = position == model.size();
//                    }
                    if (isPositionChanged && isLast) {
                        mLastPosition = position;
                        List<Theme> data = ThemeSyncManager.getInstance().getCacheTopicData(topic, ++mPageNumber, ONLINE_THEME_PAGE_LOAD_LENGTH);
                        if (data != null && data.size() > 0) {
                            model.addAll(CallFlashManager.getInstance().themeToCallFlashInfo(data));
                            mAdapter.notifyItemInserted((mPageNumber - 1) * ONLINE_THEME_PAGE_LOAD_LENGTH + 1);
                        }

                    }
                }
            }
        });
    }

    public void onEventMainThread(EventCallFlashOnlineAdLoaded event) {
        if (mCallFlashMarginDecoration != null) {
            mCallFlashMarginDecoration.setAdLoaded(true);
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
