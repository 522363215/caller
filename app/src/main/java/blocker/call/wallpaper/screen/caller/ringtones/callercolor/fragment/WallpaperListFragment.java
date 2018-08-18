package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class WallpaperListFragment extends Fragment {

    private List<WallpaperInfo> wallpaperInfos;
    private RecyclerView rvWallpaper;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<WallpaperInfo> list = (List<WallpaperInfo>) msg.obj;
            WallpaperListAdapter wallpaperListAdapter = new WallpaperListAdapter(getContext(), list);
            rvWallpaper.setAdapter(wallpaperListAdapter);
            wallpaperListAdapter.setmRecyclerClick(recyclerClick);
        }
    };
    RecyclerClick recyclerClick = new RecyclerClick() {
        @Override
        public void normalClick(View view, int position) {
            Intent intent = new Intent();
            intent.setClass(getContext(), WallpaperDetailActivity.class);
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
        init(view);
        return view;
    }

    private void getWallPaerList() {
        wallpaperInfos = new ArrayList<>();
        final String[] topic = new String[1];
        topic[0] = "Wall_img";
        ThemeSyncManager.getInstance().syncTopicData(topic, 150, new TopicThemeCallback() {
            @Override
            public void onSuccess(int code, Map<String, List<Theme>> data) {
                if (data != null && data.size() > 0) {
                    List<Theme> wallImg = data.get(topic[0]);
                    List<WallpaperInfo> infos = WallpaperManager.themeToWallpaprInfo(getActivity(), wallImg);
                    if (infos != null && infos.size() > 0) {
                        wallpaperInfos.clear();
                        wallpaperInfos.addAll(infos);
                        Message message = new Message();
                        message.obj = wallpaperInfos;
                        handler.sendMessage(message);
                    }
                }
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    private void init(View view) {
        rvWallpaper = view.findViewById(R.id.rv_message);
        rvWallpaper.setLayoutManager(new GridLayoutManager(getContext(), 2));
        getWallPaerList();
    }
}
