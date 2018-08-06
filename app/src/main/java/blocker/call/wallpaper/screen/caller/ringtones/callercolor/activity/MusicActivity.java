package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;

import com.md.callring.Constant;
import com.md.callring.LocalSong;
import com.md.callring.Setting;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

public class MusicActivity extends BaseActivity implements View.OnClickListener {

    private LocalSong localSong;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Bundle bundle = getIntent().getExtras();
        localSong = (LocalSong) bundle.getSerializable(Constant.MUSIC_BUNDLE);
        mediaPlayer = MediaPlayer.create(this,localSong.getMusic());
        mediaPlayer.start();

        findViewById(R.id.tv_music).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_music:
                mediaPlayer.pause();
                String uri = "android.resource://" + getPackageName() + "/" + localSong.getMusic();
                Setting.setRing(this, RingtoneManager.TYPE_RINGTONE,uri,false);
                openActivity(CallFlashSetResultActivity.class,0,0,true);
                break;
        }
    }
}
