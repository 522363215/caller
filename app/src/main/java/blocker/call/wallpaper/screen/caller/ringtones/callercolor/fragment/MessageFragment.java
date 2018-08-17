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

import com.example.message.MessagePictureDB;
import com.example.message.Picture;
import com.md.callring.Constant;
import com.md.callring.RecyclerClick;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Theme;
import com.md.serverflash.callback.TopicThemeCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.MessagePictureActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.MessageAdapter;

public class MessageFragment extends Fragment{

    private List<Picture> localSongs;
    private RecyclerView rvMessage;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<Picture> list = (List<Picture>) msg.obj;
            MessageAdapter messageAdapter = new MessageAdapter(getContext(),list);
            rvMessage.setAdapter(messageAdapter);
            messageAdapter.setmRecyclerClick(recyclerClick);
        }
    };
    RecyclerClick recyclerClick = new RecyclerClick() {
        @Override
        public void normalClick(View view, int position) {
            Intent intent = new Intent();
            intent.setClass(getContext(), MessagePictureActivity.class);
            intent.putExtra(Constant.MESSAGE_BUNDLE,localSongs.get(position));
            startActivity(intent);
        }

        @Override
        public void footClick(View view, int position) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message,container,false);
        init(view);
        return view;
    }

    private void getSongList(){
        localSongs = new ArrayList<>();
        final String [] topic = new String[1];
        topic[0]= "Wall_img";
        ThemeSyncManager.getInstance().syncTopicData(topic, 150, new TopicThemeCallback() {
            @Override
            public void onSuccess(int code, Map<String, List<Theme>> data) {
                if (data != null && data.size() > 0) {
                    List<Theme> wallImg = data.get(topic[0]);
                    if (wallImg!=null) {
                        for (Theme theme:wallImg){
                            localSongs.add(new Picture(theme.getId()+"",theme.getTitle(),theme.getUrl(),theme.getImg_v()));
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

    private void init(View view){
        rvMessage = view.findViewById(R.id.rv_message);
        rvMessage.setLayoutManager(new GridLayoutManager(getContext(),2));
        getSongList();
    }
}
