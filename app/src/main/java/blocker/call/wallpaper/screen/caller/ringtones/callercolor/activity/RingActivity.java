package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.media.RingtoneManager;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.md.callring.Setting;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

public class RingActivity extends BaseActivity {

    private Button setting;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        setting = (Button) findViewById(R.id.btn_setting);
        setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = Environment
                        .getExternalStorageDirectory().toString()
                        + "/tencent/QQfile_recv/汪苏泷 By2 - 有点甜.mp3";
                // 外部调用传入一个url
//                Setting.setmUrl(url);
                Setting.setRing(RingActivity.this, RingtoneManager.TYPE_RINGTONE,url,false);
                // 开始设置
//                Setting.setting(RingActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

