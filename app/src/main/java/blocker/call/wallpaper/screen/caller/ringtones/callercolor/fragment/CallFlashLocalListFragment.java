package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flurry.android.FlurryAgent;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.manager.CallFlashManager;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.CallFlashLocalAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CallFlashMarginDecoration;

/**
 * Created by ChenR on 2018/1/25.
 */

public class CallFlashLocalListFragment extends Fragment {
    private SwipeRefreshLayout flash_swipe_refresh;
    private RecyclerView rv_flash_list;
    private List<CallFlashInfo> mFlashInfoList = new ArrayList<>();
    private CallFlashLocalAdapter mAdapter = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.logEvent("CallFlashLocalListFragment------show_main");
        initFlashData();
        initField();
    }

    private void initField() {
        mAdapter = new CallFlashLocalAdapter(getActivity(), mFlashInfoList);
    }

    private void initFlashData() {
        List<CallFlashInfo> flashList = CallFlashManager.getInstance().getAllLocalFlashList();
        if (flashList != null) {
            mFlashInfoList.addAll(flashList);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call_flash_list, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFlashInfoList != null) {
            mFlashInfoList.clear();
        }
        if (mAdapter != null) {
            mAdapter.clearMap();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            flash_swipe_refresh = view.findViewById(R.id.flash_swipe_refresh);
            rv_flash_list = view.findViewById(R.id.rv_flash_list);

            GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 2, GridLayoutManager.VERTICAL, false);
            rv_flash_list.setLayoutManager(layoutManager);
            rv_flash_list.setAdapter(mAdapter);
            rv_flash_list.addItemDecoration(new CallFlashMarginDecoration());

            flash_swipe_refresh.setRefreshing(false);
            flash_swipe_refresh.setEnabled(false); // TODO: 2018/1/25 经典来电秀一般读取本地预制数据, 所以将刷新控件禁用. 可配置;
        }
    }
}
