package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import com.md.callring.Constant;
import com.md.callring.LocalAdapter;
import com.md.callring.LocalSong;
import com.md.callring.RecyclerClick;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

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
                openActivity(MusicActivity.class,bundle,0,0,true);
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
        localSongs.add(new LocalSong("有点甜",R.raw.youdiantian,R.color.white));
        localSongs.add(new LocalSong("有点甜",R.raw.youdiantian,R.color.white));
        localSongs.add(new LocalSong("有点甜",R.raw.youdiantian,R.color.white));
        localSongs.add(new LocalSong("加载本地文件",0,R.color.white));
        return localSongs;
    }
}

