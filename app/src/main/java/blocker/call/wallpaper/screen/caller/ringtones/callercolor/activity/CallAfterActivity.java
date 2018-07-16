package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.md.block.beans.BlockInfo;
import com.md.block.core.BlockManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.CallLogInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.NumberInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ContactManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CallUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DateUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.NumberUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.AvatarView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.OKCancelDialog;

public class CallAfterActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "CallAfterActivity";
    private TextView tv_number;
    private TextView tv_location;
    private TextView tv_time;
    private TextView tv_tag;
    private AvatarView av_photo;
    private FontIconView fiv_type;

    private CallLogInfo mInfo;

    private RelativeLayout rl_menu_root;

    private boolean mIsShowPermissionTipDialog;
    private TextView tvBlock;
    private long mStartTime;
    private RelativeLayout layout_number_infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_after);
//        CommonUtils.translucentStatusBar(this, true);
        initView();

        onNewIntent(getIntent());
        initData();
        listener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("CallAfterActivity----ShowCallAfter");
        mStartTime = System.currentTimeMillis();
        statistic();
        if (mInfo != null) {
            layout_number_infos.setVisibility(View.VISIBLE);
            findViewById(R.id.rootview).setBackgroundColor(getResources().getColor(R.color.color_FF0A2134));
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mInfo = (CallLogInfo) intent.getSerializableExtra("lm_call_after_info");
        initData();
    }

    private void listener() {
        findViewById(R.id.fiv_back).setOnClickListener(this);
        findViewById(R.id.fiv_menu).setOnClickListener(this);
        findViewById(R.id.rl_call_back).setOnClickListener(this);
        findViewById(R.id.rl_call_block).setOnClickListener(this);
        findViewById(R.id.rl_call_sms).setOnClickListener(this);
        findViewById(R.id.tv_close).setOnClickListener(this);
        findViewById(R.id.tv_disable).setOnClickListener(this);
        findViewById(R.id.layout_menu_root).setOnClickListener(this);
        findViewById(R.id.rl_call_flase_personal).setOnClickListener(this);
    }

    private void initView() {
        tv_number = (TextView) findViewById(R.id.tv_call_number);
        tv_location = (TextView) findViewById(R.id.tv_call_location);
        tv_time = (TextView) findViewById(R.id.tv_call_time);
        av_photo = (AvatarView) findViewById(R.id.av_photo);
        fiv_type = (FontIconView) findViewById(R.id.fiv_call_type);
        tv_tag = (TextView) findViewById(R.id.tv_tag);
        tvBlock = (TextView) findViewById(R.id.tv_block);
        rl_menu_root = (RelativeLayout) findViewById(R.id.layout_menu_root);
        layout_number_infos = (RelativeLayout) findViewById(R.id.layout_number_infos);
    }

    private void initData() {
        if (mInfo != null) {
            String callDate = "";
            String name = mInfo.callName;
            String number = mInfo.callNumber;

            tv_location.setText(mInfo.callLoction);
            mInfo.callType = CallUtils.getCallTypeForEndCall(mInfo.callNumber);
            setCallType(mInfo.callType);
            if (mInfo.callType == CallLog.Calls.MISSED_TYPE) {
                if (CallUtils.getCallLogType(mInfo.callNumber) == CallLog.Calls.MISSED_TYPE) {
                    callDate = mInfo.callDate + "  " + getString(R.string.push_tools_missed);
                } else {
                    callDate = mInfo.callDate;
                }
                FlurryAgent.logEvent("CallAfterActivity----new_missed_call");
            } else {
                callDate = mInfo.callDate;
            }
            tv_time.setText(callDate);
            setCallPhoto();
        } else {
            finish();
        }
    }

    private void setCallPhoto() {
        Resources res = getResources();
        String photoId = mInfo.callIconId;

        if (!TextUtils.isEmpty(photoId)) {
            // 有头像的正常号码;
            av_photo.setPhotoId(photoId);
        } else {
            // 正常电话;
            av_photo.setText(res.getString(R.string.icon_user));
        }
    }

    private void setCallType(int type) {
        Resources res = getResources();
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                fiv_type.setTextColor(0xffffffff);
                fiv_type.setText(res.getString(R.string.icon_incoming));
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                fiv_type.setTextColor(0xffffffff);
                fiv_type.setText(res.getString(R.string.icon_out));
                break;
            case CallLog.Calls.MISSED_TYPE:
                fiv_type.setTextColor(res.getColor(R.color.color_FFF26158));
                fiv_type.setText(res.getString(R.string.icon_in_miss));
                break;
            case CallLog.Calls.VOICEMAIL_TYPE:
                fiv_type.setTextColor(0xffffffff);
                fiv_type.setText(res.getString(R.string.icon_incoming));
                break;
            case CallLog.Calls.REJECTED_TYPE:
                fiv_type.setTextColor(res.getColor(R.color.color_FFF26158));
                fiv_type.setText(res.getString(R.string.icon_outmiss));
                break;
            case CallLog.Calls.BLOCKED_TYPE:
                fiv_type.setTextColor(0xffffffff);
                fiv_type.setText(res.getString(R.string.icon_block));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fiv_back:
                FlurryAgent.logEvent("CallAfterActivity----toMain");
                toMain();
                break;
            case R.id.fiv_menu:
                // do nothing;
                FlurryAgent.logEvent("CallAfterActivity----menu");
                rl_menu_root.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_close:
                FlurryAgent.logEvent("CallAfterActivity----close");
                rl_menu_root.setVisibility(View.GONE);
                onBackPressed();
                break;
            case R.id.layout_menu_root:
                rl_menu_root.setVisibility(View.GONE);
                break;
            case R.id.rl_call_block:
                if (isFinishing() || mInfo == null) {
                    return;
                }

                OKCancelDialog dialog = new OKCancelDialog(CallAfterActivity.this, true);
                dialog.show();
                dialog.setContent(getString(R.string.phone_detail_block_dialog_title), true, false);
                dialog.setOKCancel(R.string.ok_string, R.string.no_string);
                dialog.setOkClickListener(new OKCancelDialog.OKClickListener() {
                    @Override
                    public void Ok() {
                        Async.run(new Runnable() {
                            @Override
                            public void run() {
                                if (mInfo == null) {
                                    return;
                                }
                                BlockInfo info = new BlockInfo();
                                info.setNumber(NumberUtil.getFormatNumberForDb(mInfo.callNumber));
                                info.setName(ContactManager.getInstance().getContactNameForNumber(mInfo.callNumber));
                                final boolean isSuc = BlockManager.getInstance().setBlockContact(info);
                                Async.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int content = isSuc ? R.string.phone_detail_have_no_tag : R.string.block_contacts_added_failed;
                                        ToastUtils.showToast(CallAfterActivity.this, getString(content));
                                    }
                                });

                            }
                        });
                    }
                });

                break;
            case R.id.tv_disable:
                try {
                    FlurryAgent.logEvent("CallAfterActivity----disable");
                    rl_menu_root.setVisibility(View.GONE);
                    onBackPressed();
                    startActivity(new Intent(this, SettingActivity.class));
                } catch (Exception e) {
                    LogUtil.e("callafterActiivty", "tv_disable e:" + e.getMessage());
                }

                break;
            case R.id.rl_call_back:
                FlurryAgent.logEvent("CallAfterActivity----CallBack");
                if (TextUtils.isEmpty(mInfo.callNumber)) {
                    ToastUtils.showToast(this, getResources().getString(R.string.call_after_click_toast));
                    break;
                }
                /*Intent callbackIntent = new Intent();
                callbackIntent.setAction(Intent.ACTION_CALL);
                callbackIntent.setData(Uri.parse("tel:" + mInfo.callNumber));
                startActivity(callbackIntent);*/
                DeviceUtil.callOut(Uri.parse("tel:" + mInfo.callNumber), this);
//                finish();
                break;
            case R.id.rl_call_sms:
                FlurryAgent.logEvent("CallAfterActivity----toSms");
                if (TextUtils.isEmpty(mInfo.callNumber)) {
                    ToastUtils.showToast(this, getResources().getString(R.string.call_after_click_toast));
                    break;
                } else {
//                    ActivityBuilder.toMessageActivity(this, mInfo.callNumber, true);
                }
//                finish();
                break;
        }
    }

    private void toMain() {
        ActivityBuilder.toMain(this, ConstantUtils.FRAGMENT_HOME);
        if (!isFinishing()) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mStartTime <= 2000) return;
        FlurryAgent.logEvent("CallAfterActivity----onBackPressed");
        if (rl_menu_root.getVisibility() == View.VISIBLE) {
            rl_menu_root.setVisibility(View.GONE);
        } else {
            if (isToMain()) {
                ActivityBuilder.toMain(this, ConstantUtils.FRAGMENT_HOME);
            }
            if (!isFinishing()) {
                finish();
            }
        }
    }

    public boolean isToMain() {
        long install_time = PreferenceHelper.getInt(ConstantUtils.PREF_KEY_INSTALL_TIME, 0);
//        long lastShowSplashTime = PreferenceHelper.getLong(PreferenceHelper.PREF_LAST_TO_SPALSH_TIME, 0);
        if (!DateUtils.isToday(install_time)/* && !DateUtils.isToday(lastShowSplashTime)*/) {
            Calendar cal = Calendar.getInstance();
            int h = 0;
            h = cal.get(Calendar.HOUR_OF_DAY);
            if (h >= 19 && h <= 23) {
                return true;
            }
        }
        return false;
    }

    private void statistic() {
        Calendar cal = Calendar.getInstance();
        int h = 0;
        h = cal.get(Calendar.HOUR_OF_DAY);

        Map<String, String> eventParams = new HashMap<String, String>();
        String strStaySec;
        if (h <= 4)
            strStaySec = "0-4";
        else if (h > 4 && h <= 8)
            strStaySec = "5-8";
        else if (h > 8 && h <= 12)
            strStaySec = "9-12";
        else if (h > 12 && h <= 16)
            strStaySec = "13-16";
        else if (h > 16 && h <= 20)
            strStaySec = "17-20";
        else
            strStaySec = "21-24";
        eventParams.put("param_user_stay_end_call_show", strStaySec);
        FlurryAgent.logEvent("user_stay_end_call_show", eventParams);
    }
}
