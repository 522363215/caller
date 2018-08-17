package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.message.MessagePictureDBProcess;
import com.example.message.Picture;
import com.example.message.SharedUtils;
import com.md.callring.Constant;
import com.md.serverflash.callback.OnDownloadListener;
import com.md.serverflash.download.ThemeResourceHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.BatteryProgressBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessagePictureActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView tvMessagePic;
    private Picture url;
    private GlideView gdMessage;
    private static final int SUCCESS = 1;
    private static final int FALL = 2;
    private Bitmap bitmap;
    private MessagePictureDBProcess messagePictureDBProcess;
    private String fileName ;
    private String dates;
    private SharedUtils sharedUtils;
    private OkHttpClient okHttpClient;
    private BatteryProgressBar mPbDownloadingBelowAd;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] picture = (byte[]) msg.obj;
            switch (msg.what) {
                //加载网络成功进行UI的更新,处理得到的图片资源
                case SUCCESS:
                    //通过message，拿到字节数组
                    bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                    SaveBitmapFromBit(bitmap);
                    tvMessagePic.setText(R.string.set_pic);
                    break;
                //当加载网络失败执行的逻辑代码
                case FALL:
                    tvMessagePic.setText(R.string.download);
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_picture);

        messagePictureDBProcess = new MessagePictureDBProcess(this);

        sharedUtils =SharedUtils.getSharedUtils(this);

        url = (Picture) getIntent().getSerializableExtra(Constant.MESSAGE_BUNDLE);
        gdMessage = findViewById(R.id.gd_message);
        gdMessage.showImageWithBlur(url.getThumbnail());

        imageView = findViewById(R.id.iv_message);
        Glide.with(this).load(url.getThumbnail()).into(imageView);

        tvMessagePic = findViewById(R.id.tv_message_pic);
        tvMessagePic.setOnClickListener(this);
        Picture picture = messagePictureDBProcess.findPicture(url.getDrawableRes());
        if (picture==null) {
            tvMessagePic.setText(R.string.download);
        }else if (picture!=null){
            Log.e("onCreate: ",picture.getPath());
            File file = new File(picture.getPath());
            if (file.exists()){
                if (!sharedUtils.readNews(Constant.PICTURE).equals(picture.getDrawableRes())) {
                    tvMessagePic.setText(R.string.set_pic);

                }else {
                    tvMessagePic.setText(R.string.set_pic);
                    tvMessagePic.setClickable(false);
                    tvMessagePic.setBackgroundResource(R.color.color_66FFFFFF);
                }
            }else {
                tvMessagePic.setText(R.string.download);
            }
        }

        mPbDownloadingBelowAd = findViewById(R.id.pb_downloading_below_ad);
        mPbDownloadingBelowAd.setMaxProgress(100);

    }


    private void getPicture(){
        ThemeResourceHelper.getInstance().downloadThemeResources(url.getId(), url.getDrawableRes(), new OnDownloadListener() {
            @Override
            public void onFailure(String url) {
                tvMessagePic.setText(R.string.download);
            }

            @Override
            public void onFailureForIOException(String url) {
                LogUtil.e("askdjk","文件存储失败");
            }

            @Override
            public void onProgress(String url, int progress) {
                tvMessagePic.setText(progress+"%");
                tvMessagePic.setVisibility(View.VISIBLE);
                mPbDownloadingBelowAd.setVisibility(View.VISIBLE);
                Log.e("onProgress: ",progress+"" );
                mPbDownloadingBelowAd.setProgress(progress);
            }

            @Override
            public void onSuccess(String u, File file) {
                tvMessagePic.setBackgroundResource(R.color.progress_default_color);
                messagePictureDBProcess.addPicture(new Picture(url.getId(),url.getName(),url.getDrawableRes(),file.getPath(),url.getThumbnail()));
                tvMessagePic.setText(R.string.set_pic);
                tvMessagePic.setVisibility(View.VISIBLE);
                mPbDownloadingBelowAd.setVisibility(View.INVISIBLE);
                //若该文件存在
                if (file.exists()) {
                    bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
                }
            }
        });
    }

    private void getPic(String path){
        //1.创建一个okhttpclient对象
        okHttpClient = new OkHttpClient();
        //2.创建Request.Builder对象，设置参数，请求方式如果是Get，就不用设置，默认就是Get
        Request request = new Request.Builder()
                .url(path)
                .build();
        //3.创建一个Call对象，参数是request对象，发送请求
        Call call = okHttpClient.newCall(request);
        //4.异步请求，请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(FALL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //得到从网上获取资源，转换成我们想要的类型
                byte[] Picture_bt = response.body().bytes();
                //通过handler更新UI
                Message message = handler.obtainMessage();
                message.obj = Picture_bt;
                message.what = SUCCESS;
                handler.sendMessage(message);
            }
        });
    }

    //保存文件的方法：
    public void SaveBitmapFromView(View view) {
        int w = view.getWidth();
        int h = view.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        view.layout(0, 0, w, h);
        view.draw(c);
        // 缩小图片
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f,0.5f); //长和宽放大缩小的比例
        bmp = Bitmap.createBitmap(bmp,0,0,               bmp.getWidth(),bmp.getHeight(),matrix,true);
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = String.valueOf(new Date());
        Date dates = null;
        try {
            dates = format.parse(date);
            saveBitmap(bmp,dates+".JPEG");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //保存文件的方法：
    public void SaveBitmapFromBit(Bitmap bmp) {
        // 缩小图片
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f,0.5f); //长和宽放大缩小的比例
        Calendar calendar = Calendar.getInstance();
        //获取系统的日期
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH)+1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //获取系统时间
        //小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //分钟
        int minute = calendar.get(Calendar.MINUTE);
        //秒
        int second = calendar.get(Calendar.SECOND);
        dates = year+""+month+""+day+""+hour+""+minute+""+second;
        Log.e("onCreate1: ",dates);
        saveBitmap(bmp,dates+".JPEG");
    }

    /*
     * 保存文件，文件名为当前日期
     */
    public void saveBitmap(Bitmap bitmap, String bitName){

        File file ;
        if(Build.BRAND .equals("Xiaomi") ){ // 小米手机
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/"+bitName ;
        }else{  // Meizu 、Oppo
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+bitName ;
        }
        file = new File(fileName);

        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out))
            {
                out.flush();
                out.close();
                // 插入图库
                MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), bitName, null);

            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
        // 发送广播，通知刷新图库的显示
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

        Log.e( "onCreate2: ",fileName );
        messagePictureDBProcess.addPicture(new Picture(url.getName(),url.getDrawableRes(),fileName,url.getThumbnail()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (sharedUtils.readNews(Constant.PICTURE)!=null||!sharedUtils.readNews(Constant.PICTURE).equals("")){
                        setWall();
                    }
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(this, "获取权限失败，请手动开启", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_message_pic:
                if (tvMessagePic.getText().toString().equals(this.getString(R.string.download))) {
                    String[] PERMISSIONS = {
                            "android.permission.READ_EXTERNAL_STORAGE",
                            "android.permission.WRITE_EXTERNAL_STORAGE"};
                    //检测是否有写的权限
                    int permission = ContextCompat.checkSelfPermission(this,
                            "android.permission.WRITE_EXTERNAL_STORAGE");
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // 没有写的权限，去申请写的权限，会弹出对话框
                        ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
                    } else {
                        tvMessagePic.setText(R.string.resume);
                        tvMessagePic.setBackgroundResource(R.color.color_half_transparent);
                        getPicture();
                    }

                }else if (tvMessagePic.getText().toString().equals(this.getString(R.string.set_pic))){
                    sharedUtils.saveNews(Constant.PICTURE,url.getDrawableRes());
                    String[] WALL = {
                            "android.permission.SET_WALLPAPER",};
                    int wall = ContextCompat.checkSelfPermission(this,
                            "android.permission.SET_WALLPAPER");
                    if (wall != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, WALL, 1);
                    } else {
                        setWall();
                    }
                }
                break;
        }
    }

    private void setWall(){
        try {
            WallpaperManager wpm = (WallpaperManager) this.getSystemService(
                    Context.WALLPAPER_SERVICE);

            wpm.setBitmap(bitmap);
            Log.i("xzy", "wallpaper not null");

        } catch (IOException e) {
            Log.e("tgyhuj", "Failed to set wallpaper: " + e);
        }
        openActivity(CallFlashSetResultActivity.class, 0, 0, true);
    }

}
