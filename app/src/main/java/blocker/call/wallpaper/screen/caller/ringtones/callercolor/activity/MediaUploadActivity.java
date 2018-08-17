package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.md.serverflash.download.ThemeResourceHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.FileUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;

/**
 * Created by ChenR on 2018/7/23.
 */

public class MediaUploadActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_ALBUM = 12345;
    private static final int PERMISSION_REQUEST_CODE = 1711;

    private ImageView ivUpload = null;
    private TextView tvUpload = null;
    private EditText edtAuthor = null;
    private FontIconView fivCheck = null;

    private boolean isAgreePrivacyPolicy = false;

    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAgreePrivacyPolicy = PreferenceHelper.getBoolean(PreferenceHelper.PREF_KEY_MEDIA_UPLOAD_IS_AGREE_PRIVACY_POLICY, false);

        initView();
        listener();
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this);
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_media_upload;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("MediaUploadActivity----show_main");
    }

    private void initView() {
        ivUpload = (ImageView) findViewById(R.id.iv_upload);
        tvUpload = (TextView) findViewById(R.id.tv_upload);
        fivCheck = (FontIconView) findViewById(R.id.fiv_check);
        edtAuthor = findViewById(R.id.edt_author);

        TextView tvPrivacyPolicy = findViewById(R.id.tv_privacy_policy);
        tvPrivacyPolicy.setText(Html.fromHtml(getString(R.string.media_upload_request_privacy_policy)));

        setActionUploadState();
    }

    private void setActionUploadState() {
        if (isAgreePrivacyPolicy) {
            fivCheck.setText(R.string.icon_check);
            tvUpload.setBackgroundColor(getResources().getColor(R.color.color_FF0084FF));
        } else {
            fivCheck.setText("");
            tvUpload.setBackgroundColor(getResources().getColor(R.color.gray));
        }
    }

    private void listener() {
        findViewById(R.id.tv_upload).setOnClickListener(this);
        findViewById(R.id.iv_upload).setOnClickListener(this);
        findViewById(R.id.tv_privacy_policy).setOnClickListener(this);
        findViewById(R.id.layout_privacy_policy).setOnClickListener(this);

        ((ActionBar) findViewById(R.id.action_bar)).setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(isFinishing()){
            return;
        }
        switch (v.getId()) {
            case R.id.tv_upload:
                if (!isAgreePrivacyPolicy) {
                    ToastUtils.showToast(this, getString(R.string.media_upload_tip_privacy_policy));
                    return;
                }
                FlurryAgent.logEvent("MediaUploadActivity-----click----upload");
                try {
                    if (!TextUtils.isEmpty(imagePath)) {
                        File file = new File(imagePath);
                        if (file.exists() && file.length() < (1024 * 1024 * 5)) {
                            String author = edtAuthor.getText().toString();
                            if (TextUtils.isEmpty(author)) {
                                author = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            }
                            Map<String, String> params = new HashMap<>();
                            params.put("name", file.getName());
                            params.put("file", "");
                            params.put("username", author);
                            params.put("action", "up_picture");
                            ThemeResourceHelper.getInstance().uploadFile(params, file, null);
                        }
                        ToastUtils.showToast(this, getString(R.string.media_upload_uploading));
                        finish();
                    } else {
                        ToastUtils.showToast(this, getString(R.string.media_upload_tip_no_file));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.iv_upload:
                FlurryAgent.logEvent("MediaUploadActivity-----click----choice_upload_img");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(MediaUploadActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MediaUploadActivity.this, new String[]
                            {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    Intent toAlbum = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(toAlbum, REQUEST_CODE_ALBUM);
                }

                break;
            case R.id.tv_privacy_policy:
                FlurryAgent.logEvent("MediaUploadActivity-----click----privacy_policy");
                try {
                    Intent intent = new Intent(this, BrowserActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.layout_privacy_policy:
                isAgreePrivacyPolicy = !isAgreePrivacyPolicy;
                setActionUploadState();
                FlurryAgent.logEvent("MediaUploadActivity-----click----set_privacy_policy_state");

                PreferenceHelper.putBoolean(PreferenceHelper.PREF_KEY_MEDIA_UPLOAD_IS_AGREE_PRIVACY_POLICY, isAgreePrivacyPolicy);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent toAlbum = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(toAlbum, REQUEST_CODE_ALBUM);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ALBUM && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            ivUpload.setImageURI(uri);

            imagePath = FileUtil.getImagePath(this, uri);
        }
    }
}
