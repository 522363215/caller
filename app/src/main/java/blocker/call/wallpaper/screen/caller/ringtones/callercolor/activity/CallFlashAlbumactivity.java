package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.md.flashset.manager.CallFlashManager;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.FacebookConstant;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.FileUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import event.EventBus;
import okhttp3.Call;

/**
 * Created by ChenR on 2017/9/19.
 */

public class CallFlashAlbumactivity extends BaseActivity {
    private final int AlbumRequestCode = 1077;
    private final int PERMISSION_REQUEST_CODE = 1711;
    private Thread downloadThread = null;
    private Advertisement mAdvertisement;
    private Call mOkhttpCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_flash_album);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
//        initAds();
        init();
        listener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("CallFlashAlbumActivity-----ShowMain");
    }

    private void listener() {
        ((ActionBar) findViewById(R.id.actionbar)).setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.ll_local_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(CallFlashAlbumactivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CallFlashAlbumactivity.this, new String[]
                            {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    Intent toAlbum = new Intent();
                    toAlbum.setAction(Intent.ACTION_GET_CONTENT);
                    toAlbum.setType("image/*");
                    startActivityForResult(toAlbum, AlbumRequestCode);
                }
            }
        });
    }

    private void init() {
        initData();
    }

    private void initData() {
        initModel();
    }

    private void initModel() {
        initLocal();
    }

    private void initLocal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(CallFlashAlbumactivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CallFlashAlbumactivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            setLocalGallery();
        }
    }

    private void setLocalGallery() {
        TextView tv_image_count = (TextView) findViewById(R.id.tv_count);
        ImageView iv_icon = (ImageView) findViewById(R.id.iv_icon);

        Cursor query = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media.SIZE + ">?",
                new String[]{
                        String.valueOf(50 * 1024)
                },
                MediaStore.Images.Media.DATE_ADDED + " DESC");

        if (query != null && query.moveToFirst()) {
            findViewById(R.id.fiv_icon).setVisibility(View.GONE);
            iv_icon.setVisibility(View.VISIBLE);

            String url = query.getString(query.getColumnIndex(MediaStore.Images.Media.DATA));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(url, options);
            options.inSampleSize = FileUtil.calculateInSampleSize(options,
                    100, 100);
            options.inJustDecodeBounds = false;
            iv_icon.setImageBitmap(BitmapFactory.decodeFile(url, options));
            tv_image_count.setText(getString(R.string.call_flash_album_gallery_desc_2, query.getCount()));

            query.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Intent toAlbum = new Intent();
//            toAlbum.setAction(Intent.ACTION_GET_CONTENT);
//            toAlbum.setType("image/*");
//            startActivityForResult(toAlbum, AlbumRequestCode);
            setLocalGallery();
        } else {
            findViewById(R.id.iv_icon).setVisibility(View.GONE);
            findViewById(R.id.fiv_icon).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AlbumRequestCode && resultCode == RESULT_OK && data != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            String path = FileUtil.getImagePath(this, data.getData());
            if (TextUtils.isEmpty(path)) return;
            ActivityBuilder.toCallFlashDetail(this, CallFlashManager.getInstance().getCustomCallFlash(path), false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadThread != null && downloadThread.isAlive()) {
            downloadThread.interrupt();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (mOkhttpCall != null) mOkhttpCall.cancel();
    }


    private void initAds() {
        String fb_id = CallerAdManager.getCommonFbID(CallerAdManager.POSITION_FB_ADS_CALLGIFCUSTOM);
        mAdvertisement = new Advertisement(new MyAdvertisementAdapter(getWindow().getDecorView(),
                fb_id,
                ConstantUtils.ADMOB_ADV_FAKE_HOME_ID,
                Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,
                CallerAdManager.MOPUB_NATIVE_ADV_BIG_CALL_AFTER_ID,
                Advertisement.MOPUB_TYPE_NATIVE,
                CallerAdManager.BAIDU_ADV_BANNER_CALL_FLASH_ID,
                "",
                true));
        mAdvertisement.setRefreshWhenClicked(false);
        mAdvertisement.refreshAD(true);
    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String mopubKey, int moPubType, int baiduKey, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baiduKey, eventKey, AdvertisementSwitcher.SERVER_KEY_QUICK_DEFAULT, isBanner);
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
        }

        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.facebook_native_ads_banner_call_flash : FacebookConstant.FB_AD_BIG_VIEW_RES_ID;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_flash : R.layout.layout_admob_advanced_content_ad_flash;
        }

        @Override
        public int getBaiDuViewRes() {
            return mIsBanner ? R.layout.layout_du_ad_banner_falsh_show : R.layout.layout_du_ad_big;
        }

        @Override
        public int getMoPubViewRes() {
            return mIsBanner ? R.layout.layout_mopub_ad_banner_flash_show : R.layout.layout_mopub_no_icon_native_ads;
        }
    }
}
