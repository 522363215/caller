package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.PermissionShowAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.PermissionInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.PermissionUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;

public class PermissionActivity extends BaseActivity implements View.OnClickListener, PermissionShowAdapter.ItemClickListener {
    private boolean mIsShowPermission;
    private LinearLayout mLayoutRequestPermission;
    private TextView mTvPermissionPermission;
    private View mLayoutShowPermission;
    private ActionBar mActionBar;
    private RecyclerView mRvPermission;
    private List<PermissionInfo> mPermissionInfos = new ArrayList<>();
    private PermissionShowAdapter mPermissionShowAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        onNewIntent(getIntent());
        initView();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mIsShowPermission = intent.getBooleanExtra(ActivityBuilder.IS_SHOW_PERMISSIONS, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPermissionShowAdapter != null) {
            mPermissionShowAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        //请求权限
        mLayoutRequestPermission = findViewById(R.id.layout_request_permission);
        mTvPermissionPermission = findViewById(R.id.tv_request_permission);
        mTvPermissionPermission.setOnClickListener(this);

        //显示权限
        mLayoutShowPermission = findViewById(R.id.layout_show_permission);
        mActionBar = findViewById(R.id.actionbar);
        mRvPermission = findViewById(R.id.rv_permission);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvPermission.setLayoutManager(layoutManager);
        mRvPermission.setAdapter(mPermissionShowAdapter = new PermissionShowAdapter(this, mPermissionInfos));
        mPermissionShowAdapter.setOnItemClickListener(this);

        mActionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (mIsShowPermission) {
            mLayoutShowPermission.setVisibility(View.VISIBLE);
            mLayoutRequestPermission.setVisibility(View.GONE);
        } else {
            mLayoutShowPermission.setVisibility(View.GONE);
            mLayoutRequestPermission.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        if (!mIsShowPermission) return;
        getPermissions();
    }

    private void getPermissions() {
        if (mPermissionInfos == null) {
            mPermissionInfos = new ArrayList<>();
        } else {
            mPermissionInfos.clear();
        }
        PermissionInfo floatWindowPermission = new PermissionInfo();
        floatWindowPermission.iconResId = R.drawable.ic_launcher;
        floatWindowPermission.title = getString(R.string.permission_float_window_title);
        floatWindowPermission.permissionDes = getString(R.string.permission_float_window_des);
        floatWindowPermission.permission = PermissionUtils.PERMISSION_OVERLAY;
        mPermissionInfos.add(floatWindowPermission);

        PermissionInfo notificationPermission = new PermissionInfo();
        notificationPermission.iconResId = R.drawable.ic_launcher;
        notificationPermission.title = getString(R.string.permission_notification_title);
        notificationPermission.permissionDes = getString(R.string.permission_notification_des);
        notificationPermission.permission = PermissionUtils.PERMISSION_NOTIFICATION_POLICY_ACCESS;
        mPermissionInfos.add(notificationPermission);

        PermissionInfo storagePermission = new PermissionInfo();
        storagePermission.iconResId = R.drawable.ic_launcher;
        storagePermission.title = getString(R.string.permission_storage_title);
        storagePermission.permissionDes = getString(R.string.permission_storage_des);
        storagePermission.permission = Manifest.permission_group.STORAGE;
        mPermissionInfos.add(storagePermission);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionInfo phonePermission = new PermissionInfo();
            phonePermission.iconResId = R.drawable.ic_launcher;
            phonePermission.title = getString(R.string.permission_phone_title);
            phonePermission.permissionDes = getString(R.string.permission_phone_des);
            phonePermission.permission = Manifest.permission_group.PHONE;
            mPermissionInfos.add(phonePermission);

            PermissionInfo smsPermission = new PermissionInfo();
            smsPermission.iconResId = R.drawable.ic_launcher;
            smsPermission.title = getString(R.string.permission_sms_title);
            smsPermission.permissionDes = getString(R.string.permission_sms_des);
            smsPermission.permission = Manifest.permission_group.SMS;
            mPermissionInfos.add(smsPermission);

            PermissionInfo contactPermission = new PermissionInfo();
            contactPermission.iconResId = R.drawable.ic_launcher;
            contactPermission.title = getString(R.string.permission_contact_title);
            contactPermission.permissionDes = getString(R.string.permission_contact_des);
            contactPermission.permission = Manifest.permission_group.CONTACTS;
            mPermissionInfos.add(contactPermission);
        }

        mPermissionShowAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_request_permission:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission(Stringutil.concatAll(PermissionUtils.PERMISSION_GROUP_PHONE, PermissionUtils.PERMISSION_GROUP_SMS, PermissionUtils.PERMISSION_GROUP_CONTACT), PermissionUtils.REQUEST_CODE_ALL_PERMISSION_);
                }
                break;
        }
    }

    @Override
    public void onPermissionNotGranted(int requestCode) {
        super.onPermissionNotGranted(requestCode);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_ALL_PERMISSION_:
                finish();
                break;
            case PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION:
            case PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS:
            case PermissionUtils.REQUEST_CODE_PHONE_STATE_PERMISSION:
            case PermissionUtils.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION:
            case PermissionUtils.REQUEST_CODE_RECEIVE_SMS_PERMISSION:
            case PermissionUtils.REQUEST_CODE_READ_CONTACT_PERMISSION:
                break;
        }
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        super.onPermissionGranted(requestCode);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_ALL_PERMISSION_:
                finish();
                break;
            case PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION:
            case PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS:
            case PermissionUtils.REQUEST_CODE_PHONE_STATE_PERMISSION:
            case PermissionUtils.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION:
            case PermissionUtils.REQUEST_CODE_RECEIVE_SMS_PERMISSION:
            case PermissionUtils.REQUEST_CODE_READ_CONTACT_PERMISSION:
//                if (mPermissionShowAdapter != null) {
//                    mPermissionShowAdapter.notifyDataSetChanged();
//                }
                break;
        }
    }

    @Override
    public void onItemClickListener(int position) {
        PermissionInfo info = mPermissionInfos.get(position);
        if (info == null || info.isGet) return;
        String permission = info.permission;
        if (PermissionUtils.PERMISSION_OVERLAY.equals(permission)) {
            requestSpecialPermission(PermissionUtils.PERMISSION_OVERLAY);
        } else if (PermissionUtils.PERMISSION_NOTIFICATION_POLICY_ACCESS.equals(permission)) {
            requestSpecialPermission(PermissionUtils.PERMISSION_NOTIFICATION_POLICY_ACCESS);
        } else if (Manifest.permission_group.STORAGE.equals(permission)) {
            requestPermission(PermissionUtils.PERMISSION_GROUP_STORAGE, PermissionUtils.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION);
        } else if (Manifest.permission_group.PHONE.equals(permission)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission(PermissionUtils.PERMISSION_GROUP_PHONE, PermissionUtils.REQUEST_CODE_PHONE_STATE_PERMISSION);
            }
        } else if (Manifest.permission_group.SMS.equals(permission)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission(PermissionUtils.PERMISSION_GROUP_SMS, PermissionUtils.REQUEST_CODE_RECEIVE_SMS_PERMISSION);
            }
        } else if (Manifest.permission_group.CONTACTS.equals(permission)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission(PermissionUtils.PERMISSION_GROUP_CONTACT, PermissionUtils.REQUEST_CODE_READ_CONTACT_PERMISSION);
            }
        }
    }
}
