package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.flurry.android.FlurryAgent;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Category;
import com.md.serverflash.callback.CategoryCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.CallFlashCategoryAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshWhenNetConnected;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CallFlashMarginDecoration;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    private static final String TAG = "CategoryFragment";
    private RecyclerView mRvCategory;
    private CallFlashCategoryAdapter mCategoryAdapter;
    private List<Category> mData = new ArrayList<>();
    private ProgressBar mPbLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("CategroyFragment-------show_main");
        if (view != null) {
            initView(view);
            initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void initData() {
        ThemeSyncManager.getInstance().syncCategoryList(new CategoryCallback() {
            @Override
            public void onSuccess(int code, List<Category> data) {
                updateUI(data);
            }

            @Override
            public void onFailure(int code, String msg) {
                updateUI(null);
            }
        });
    }

    private void updateUI(List<Category> data) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (data != null) {
            LogUtil.d(TAG, "syncCategoryList data:" + data.size());
            Category localCategory = new Category();
            localCategory.setPx_id("-1024");
            localCategory.setTitle(getString(R.string.call_flash_album_local));

            data.add(localCategory);
            mData.clear();
            mData.addAll(data);
        } else {
            Category localCategory = new Category();
            localCategory.setPx_id("-1024");
            localCategory.setTitle(getString(R.string.call_flash_album_local));
            mData.clear();
            mData.add(localCategory);
        }
        mCategoryAdapter.notifyDataSetChanged();
        mPbLoading.setVisibility(View.GONE);
        mRvCategory.setVisibility(View.VISIBLE);
    }

    private void initView(View view) {
        mPbLoading = view.findViewById(R.id.pb_loading);
        mRvCategory = view.findViewById(R.id.rv_category_list);

        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 2, GridLayoutManager.VERTICAL, false);
        mRvCategory.setLayoutManager(layoutManager);
        mRvCategory.addItemDecoration(new CallFlashMarginDecoration());
        mCategoryAdapter = new CallFlashCategoryAdapter(getActivity(), mData);
        mRvCategory.setAdapter(mCategoryAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(EventRefreshWhenNetConnected event) {
        if (mData == null || mData.size() <= 1) {
            initData();
        }
    }
}
