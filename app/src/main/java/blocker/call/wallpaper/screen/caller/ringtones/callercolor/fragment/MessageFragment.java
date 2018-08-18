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
import com.md.wallpaper.FileType;
import com.md.wallpaper.Wallpaper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.WallpaperDetailActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.MessageAdapter;

public class MessageFragment extends Fragment {

    private List<Wallpaper> localSongs;
    private RecyclerView rvMessage;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<Wallpaper> list = (List<Wallpaper>) msg.obj;
            MessageAdapter messageAdapter = new MessageAdapter(getContext(), list);
            rvMessage.setAdapter(messageAdapter);
            messageAdapter.setmRecyclerClick(recyclerClick);
        }
    };
    RecyclerClick recyclerClick = new RecyclerClick() {
        @Override
        public void normalClick(View view, int position) {
            Intent intent = new Intent();
            intent.setClass(getContext(), WallpaperDetailActivity.class);
            intent.putExtra(Constant.MESSAGE_BUNDLE, localSongs.get(position));
            startActivity(intent);
        }

        @Override
        public void footClick(View view, int position) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        init(view);
        return view;
    }

    private void getSongList() {
        localSongs = new ArrayList<>();
        final String[] topic = new String[1];
        topic[0] = "Wall_img";
        ThemeSyncManager.getInstance().syncTopicData(topic, 150, new TopicThemeCallback() {
            @Override
            public void onSuccess(int code, Map<String, List<Theme>> data) {
                if (data != null && data.size() > 0) {
                    List<Theme> wallImg = data.get(topic[0]);
                    if (wallImg != null) {
                        for (Theme theme : wallImg) {
                            if (theme.getUrl().endsWith("mp4") || theme.getUrl().endsWith("MP4")) {
                                theme.setType(FileType.VIDEO_TYPE);
                            } else if (theme.getUrl().endsWith("gif") || theme.getUrl().endsWith("GIF")) {
                                theme.setType(FileType.GIF_TYPE);
                            } else {
                                theme.setType(FileType.PICTURE_TYPE);
                            }
                            localSongs.add(new Wallpaper(theme.getId() + "", theme.getTitle(), theme.getUrl(), theme.getImg_v(), theme.getType()));
                        }
                        Message message = new Message();
                        message.obj = localSongs;
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
        rvMessage = view.findViewById(R.id.rv_message);
        rvMessage.setLayoutManager(new GridLayoutManager(getContext(), 2));
        getSongList();
    }
}
