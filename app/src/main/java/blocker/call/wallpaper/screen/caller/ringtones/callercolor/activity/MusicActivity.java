package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.md.callring.Constant;
import com.md.callring.LocalSong;
import com.md.callring.Setting;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.CircleImageView;

public class MusicActivity extends BaseActivity implements View.OnClickListener {

    private LocalSong localSong;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        localSong = (LocalSong) bundle.getSerializable(Constant.MUSIC_BUNDLE);

        CircleImageView circleImageView = findViewById(R.id.ci_music);
        Glide.with(this).load(localSong.getDrawableRes()).into(circleImageView);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.music);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        circleImageView.startAnimation(animation);
        ActionBar actionBar = findViewById(R.id.actionbar);
        actionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mediaPlayer = MediaPlayer.create(this,localSong.getMusic());
        mediaPlayer.start();

        findViewById(R.id.tv_music).setOnClickListener(this);
    }

    @Override
    protected void translucentStatusBar() {

    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_music;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        mediaPlayer.pause();
    }
}
