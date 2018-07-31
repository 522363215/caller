package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.md.callring.AudioAdapter;
import com.md.callring.AudioUtil;
import com.md.callring.Song;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

public class LocalMusicActivity extends BaseActivity {

    private ListView listview;
    private List<Song> data;
    private AudioAdapter adapter ;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);

        root = (LinearLayout) findViewById(R.id.ll_setting) ;
        data = AudioUtil.readAudio(this) ;
        if(null == data || data.size() == 0) {
            Toast.makeText(this, "没有扫描到音频文件!", Toast.LENGTH_LONG).show() ;
            return ;
        }
        listview = (ListView) findViewById(R.id.lv_setting) ;
        adapter = new AudioAdapter(LocalMusicActivity.this, data) ;
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
}
