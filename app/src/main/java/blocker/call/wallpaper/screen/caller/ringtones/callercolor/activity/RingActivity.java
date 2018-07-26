package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.md.callring.AudioAdapter;
import com.md.callring.AudioUtil;
import com.md.callring.Setting;
import com.md.callring.Song;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

public class RingActivity extends BaseActivity {

    private Button setting;

    private ListView listview;
    private List<Song> data;
    private AudioAdapter adapter ;
    private LinearLayout root ;

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

        root = (LinearLayout) findViewById(R.id.ll_setting) ;
        data = AudioUtil.readAudio(this) ;
        if(null == data || data.size() == 0) {
            Toast.makeText(this, "没有扫描到音频文件!", Toast.LENGTH_LONG).show() ;
            return ;
        }
        listview = (ListView) findViewById(R.id.lv_setting) ;
        adapter = new AudioAdapter(this, data) ;
        listview.setAdapter(adapter) ;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // 获取专辑图片
                Bitmap bmp = data.get(arg2).getAlbumCover() ;
                if(null != bmp) {
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), bmp) ;
                    root.setBackgroundDrawable(drawable);
                }
                else {
                    root.setBackgroundColor(Color.GRAY) ;
                }
            }
        }) ;

        AudioUtil.readImage(this) ;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

