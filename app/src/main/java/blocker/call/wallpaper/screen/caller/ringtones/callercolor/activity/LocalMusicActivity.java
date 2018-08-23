package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;

public class LocalMusicActivity extends BaseActivity {

    private ListView listview;
    private List<Song> data;
    private AudioAdapter adapter ;
    private LinearLayout root;
    private Thread run;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            progressDialog.cancel();
            if (data !=null){
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
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = (LinearLayout) findViewById(R.id.ll_setting) ;
        listview = (ListView) findViewById(R.id.lv_setting) ;
        ActionBar actionBar = findViewById(R.id.actionbar);
        actionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        permission();
    }

    @Override
    protected void translucentStatusBar() {

    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_local_music;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 100:
                Log.e("onRequestPermissionsResult:",grantResults[0]+"" );;
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    run = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            init();
                        }
                    };
                    run.start();
                } else {
                    // 没有获取到权限，做特殊处理
                }
                break;
        }
    }

    private void permission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }else{
            run = new Thread(){
                @Override
                public void run() {
                    super.run();
                    init();
                }
            };
            run.start();
        }
    }

    private void init(){
        data = AudioUtil.readAudio(this) ;
        if(null == data || data.size() == 0) {
            Toast.makeText(this, getString(R.string.get_audio_result), Toast.LENGTH_LONG).show() ;
            return ;
        }
        Log.e( "init: ", "ghjkl;'"+"\t"+data.size());

        AudioUtil.readImage(this) ;

        handler.sendEmptyMessage(0);
    }
}
