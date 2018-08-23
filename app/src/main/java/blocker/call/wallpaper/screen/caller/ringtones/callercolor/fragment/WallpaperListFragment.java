package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.md.callring.Constant;
import com.md.callring.RecyclerClick;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Theme;
import com.md.serverflash.callback.TopicThemeCallback;
import com.md.wallpaper.bean.WallpaperInfo;
import com.md.wallpaper.manager.WallpaperManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.WallpaperDetailActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.WallpaperListAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;

public class WallpaperListFragment extends Fragment {
    private RecyclerView rvWallpaper;
    private SwipeRefreshLayout flashSwipeReFresh;
    private View layoutNoWallpaper;

    private List<WallpaperInfo> wallpaperInfos = new ArrayList<>();
    private WallpaperListAdapter wallpaperListAdapter;

    RecyclerClick recyclerClick = new RecyclerClick() {
        @Override
        public void normalClick(View view, int position) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), WallpaperDetailActivity.class);
            intent.putExtra(Constant.WALLPAPER_BUNDLE, wallpaperInfos.get(position));
            startActivity(intent);
        }

        @Override
        public void footClick(View view, int position) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall_paper, container, false);

        if (view != null) {
            init(view);
            requestData();
            listener();
        }

        return view;
    }

    private void listener() {
        flashSwipeReFresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });
    }

    private void requestData() {
        setRefreshState(true);

        flashSwipeReFresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshState(false);
            }
        }, 3000);

        final String[] topic = new String[1];
        topic[0] = WallpaperManager.ONLINE_TOPIC_WALLPAPER_WALL_IMG;
        ThemeSyncManager.getInstance().syncTopicData(topic, 150, new TopicThemeCallback() {
            @Override
            public void onSuccess(int code, Map<String, List<Theme>> data) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                setRefreshState(false);

                if (data != null && data.size() > 0) {
                    rvWallpaper.setVisibility(View.VISIBLE);
                    layoutNoWallpaper.setVisibility(View.GONE);

                    List<Theme> wallImg = data.get(topic[0]);
                    List<WallpaperInfo> infos = WallpaperManager.themeToWallpaprInfo(getActivity(), wallImg);
                    if (infos != null && infos.size() > 0) {
                        wallpaperInfos.clear();
                        wallpaperInfos.addAll(infos);
                        wallpaperListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                rvWallpaper.setVisibility(View.GONE);
                layoutNoWallpaper.setVisibility(View.VISIBLE);
                setRefreshState(false);

                ToastUtils.showToast(getActivity(), getString(R.string.wallpaper_list_data_load_failed));
            }
        });
    }

    private void setRefreshState(boolean b) {
        flashSwipeReFresh.setRefreshing(b);
    }

    private void init(View view) {
        rvWallpaper = view.findViewById(R.id.rv_wallpaper);
        flashSwipeReFresh = view.findViewById(R.id.flash_swipe_refresh);
        layoutNoWallpaper = view.findViewById(R.id.layout_no_wallpaper);

        rvWallpaper.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        wallpaperListAdapter = new WallpaperListAdapter(getActivity(), wallpaperInfos);
        rvWallpaper.setAdapter(wallpaperListAdapter);
        wallpaperListAdapter.setmRecyclerClick(recyclerClick);
    }
}
