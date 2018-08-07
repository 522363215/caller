package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.PermissionShowAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.PermissionInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.PermissionUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.SpecialPermissionsUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;

public class PermissionActivity extends BaseActivity implements View.OnClickListener, PermissionShowAdapter.SetClickListener {
    private RecyclerView mRvPermission;
    private List<PermissionInfo> mPermissionInfos = new ArrayList<>();
    private PermissionShowAdapter mPermissionShowAdapter;
    private TextView mTvTitle;
    private TextView mTvButton;
    private View mTvSkip;
    private boolean mIsLetsStart;
    private ActionBar mActionBar;
    private View mLayoutStart;
    private View mLayoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
        initView();
        initData();
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this);
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_permission;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mIsLetsStart = intent.getBooleanExtra(ActivityBuilder.IS_LETS_START, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mIsLetsStart) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPermissionShowAdapter != null) {
            mPermissionShowAdapter.notifyDataSetChanged();
        }

        if (PermissionUtils.isHaveAllPermission(this)) {
            mTvButton.setText(R.string.permission_all_set);
        } else {
            mTvButton.setText(R.string.permission_next);
        }
    }

    private void initView() {
        mActionBar = findViewById(R.id.actionbar);

        mLayoutStart = findViewById(R.id.layout_start);
        mTvTitle = findViewById(R.id.tv_title);
        mRvPermission = findViewById(R.id.rv_permission);

        mLayoutBtn = findViewById(R.id.layout_btn);
        mTvButton = findViewById(R.id.tv_button);
        mTvSkip = findViewById(R.id.tv_skip);

        mTvButton.setOnClickListener(this);
        mTvSkip.setOnClickListener(this);

        if (mIsLetsStart) {
            mActionBar.setVisibility(View.GONE);
            mLayoutStart.setVisibility(View.VISIBLE);
            mLayoutBtn.setVisibility(View.VISIBLE);
            mTvTitle.setText(R.string.permission_title);
        } else {
            mActionBar.setVisibility(View.VISIBLE);
            mLayoutStart.setVisibility(View.GONE);
            mLayoutBtn.setVisibility(View.GONE);
            mActionBar.setTitle(R.string.side_slip_permission_check);
            mActionBar.setOnBackClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvPermission.setLayoutManager(layoutManager);
        mRvPermission.setAdapter(mPermissionShowAdapter = new PermissionShowAdapter(this, mPermissionInfos, mIsLetsStart));
        mPermissionShowAdapter.setOnItemClickListener(this);
    }

    private void initData() {
        List<PermissionInfo> requestPermissions = PermissionUtils.getRequestPermissions();
        if (mPermissionInfos == null) {
            mPermissionInfos = new ArrayList<>();
        } else {
            mPermissionInfos.clear();
        }
        mPermissionInfos.addAll(requestPermissions);
        mPermissionShowAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_button:
                onNext();
                break;
            case R.id.tv_skip:
                finish();
                break;
        }
    }

    private void onNext() {
        if (mPermissionInfos != null) {
            for (PermissionInfo permission : mPermissionInfos) {
                if (!PermissionUtils.hasPermission(this, permission.permission)) {
                    if (permission.isSpecialPermission) {
                        if (!TextUtils.isEmpty(permission.permission)) {
                            requestSpecialPermission(permission.permission);
                            return;
                        }
                    } else {
                        if (permission.permissions != null && permission.permissions.length > 0) {
                            requestPermission(permission.permissions, permission.requestCode);
                            return;
                        }
                    }
                }
            }
        }
        finish();
    }

    @Override
    public void onPermissionNotGranted(int requestCode) {
        super.onPermissionNotGranted(requestCode);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION:
                break;
            case PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS:
                break;
            case PermissionUtils.REQUEST_CODE_PHONE_AND_CONTACT_PERMISSION:
                break;
            case PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK:
                break;
            case PermissionUtils.REQUEST_CODE_READ_CALL_LOG_PERMISSION:
                break;
        }
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        super.onPermissionGranted(requestCode);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION:
                break;
            case PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS:
                break;
            case PermissionUtils.REQUEST_CODE_PHONE_AND_CONTACT_PERMISSION:
                break;
            case PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK:
                break;
            case PermissionUtils.REQUEST_CODE_READ_CALL_LOG_PERMISSION:
                break;
        }
    }

    @Override
    public void onSetClickListener(int position) {
        PermissionInfo info = mPermissionInfos.get(position);
        if (info == null) return;
        if (PermissionUtils.PERMISSION_AUTO_START.equals(info.permission) || PermissionUtils.PERMISSION_SHOW_ON_LOCK.equals(info.permission)) {
            //小米专用权限
            if (PermissionUtils.PERMISSION_AUTO_START.equals(info.permission)) {
                SpecialPermissionsUtil.toXiaomiAutoStartPermission(this, PermissionUtils.REQUEST_CODE_AUTO_START);
            } else if (PermissionUtils.PERMISSION_SHOW_ON_LOCK.equals(info.permission)) {
                SpecialPermissionsUtil.toXiaomiShowOnLockPermssion(this, PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK);
            }
        } else if (!info.isGet) {
            if (info.isSpecialPermission) {
                requestSpecialPermission(info.permission);
            } else {
                requestPermission(info.permissions, info.requestCode);
            }
        }
    }
}
