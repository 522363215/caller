package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.md.callring.Constant;
import com.md.callring.LocalSong;
import com.md.callring.RecyclerClick;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.LocalAdapter;

public class RingActivity extends BaseActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);

        final List<LocalSong> localSongs = getSongList();
        RecyclerView rvSetting = findViewById(R.id.rv_setting);
        rvSetting.setLayoutManager(new GridLayoutManager(this,2));
        LocalAdapter localAdapter = new LocalAdapter(RingActivity.this,localSongs);
        rvSetting.setAdapter(localAdapter);
        localAdapter.setmRecyclerClick(new RecyclerClick() {
            @Override
            public void normalClick(View view,int p) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.MUSIC_BUNDLE,localSongs.get(p));
                openActivity(MusicActivity.class,bundle,0,0,false);
            }

            @Override
            public void footClick(View view,int p) {
                openActivity(LocalMusicActivity.class,0,0,false);
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private List<LocalSong> getSongList(){
        List<LocalSong> localSongs = new ArrayList<>();
        localSongs.add(new LocalSong("铃声一",R.raw.youdiantian,"http://img4.duitang.com/uploads/item/201507/13/20150713071816_N58aC.jpeg"));
        localSongs.add(new LocalSong("铃声二",R.raw.xuemaojiao,"http://cdn2.image.apk.gfan.com/asdf/PImages/2014/9/15/960838_2a9b1753d-4d02-424f-ace5-15647f6fbdbc.jpg"));
        localSongs.add(new LocalSong("铃声三",R.raw.innocence,"http://s3.sinaimg.cn/mw690/001LVQHHty6TKndBTEe62&690"));
        localSongs.add(new LocalSong("加载本地文件",0,null));
        return localSongs;
    }
}

