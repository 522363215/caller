package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.md.block.beans.BlockInfo;
import com.md.block.core.BlockManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.CallLogInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ContactManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CallUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
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
    private boolean isAddedInBlockContacts;
    private Advertisement mAdvertisement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAds();
        initView();
        onNewIntent(getIntent());
        initData();
        listener();
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 全屏时增加StatusBar站位的留白
            ViewGroup group = findViewById(R.id.rl_caller);
            View view = new View(this);
            view.setBackgroundColor(getResources().getColor(R.color.color_FF0A2134));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.getStatusBarHeight());
            view.setLayoutParams(params);
            group.addView(view, 0);
        }
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_call_after;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("CallAfterActivity----ShowCallAfter");
        mStartTime = System.currentTimeMillis();
        statistic();
        if (mInfo != null) {
            layout_number_infos.setVisibility(View.VISIBLE);
            findViewById(R.id.root_view).setBackgroundColor(getResources().getColor(R.color.color_FF0A2134));
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
        findViewById(R.id.rl_call_flash).setOnClickListener(this);
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

            tv_number.setText(TextUtils.isEmpty(name) ? number : name);
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

            isAddedInBlockContacts = BlockManager.getInstance().existInBlockContacts(number);
            tvBlock.setText(isAddedInBlockContacts ? R.string.phone_detail_unblock : R.string.phone_detail_block);
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
                aboutBlock();
                break;
            case R.id.rl_call_flash:
                toMain();
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
                DeviceUtil.callOut(this, mInfo.callNumber);
//                finish();
                break;
            case R.id.rl_call_sms:
                FlurryAgent.logEvent("CallAfterActivity----toSms");
                if (TextUtils.isEmpty(mInfo.callNumber)) {
                    ToastUtils.showToast(this, getResources().getString(R.string.call_after_click_toast));
                    break;
                } else {
                    DeviceUtil.toSystemSms(this, mInfo.callNumber);
                }
//                finish();
                break;
        }
    }

    private void aboutBlock() {
        if (isFinishing() || mInfo == null) {
            return;
        }

        OKCancelDialog dialog = new OKCancelDialog(CallAfterActivity.this, true);
        dialog.show();
        int content = isAddedInBlockContacts ? R.string.phone_detail_cancel_block : R.string.phone_detail_block_dialog_title;
        dialog.setContent(getString(content), true, false);
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
                        if (isAddedInBlockContacts) {
                            final boolean isSuc = BlockManager.getInstance().removeBlockContact(info);
                            isAddedInBlockContacts = !isSuc;
                            Async.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isSuc) {
                                        tvBlock.setText(R.string.phone_detail_block);
                                    }
                                    int content = isSuc ? R.string.block_contacts_remove_success : R.string.block_contacts_remove_failed;
                                    ToastUtils.showToast(CallAfterActivity.this, getString(content));
                                }
                            });
                        } else {
                            final boolean isSuc = BlockManager.getInstance().setBlockContact(info);
                            isAddedInBlockContacts = isSuc;
                            if (isSuc) {
                                BlockManager.getInstance().setBlockSwitchState(true);
                            }
                            Async.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isSuc) {
                                        tvBlock.setText(R.string.phone_detail_unblock);
                                    }
                                    int content = isSuc ? R.string.phone_detail_have_no_tag : R.string.block_contacts_added_failed;
                                    ToastUtils.showToast(CallAfterActivity.this, getString(content));
                                }
                            });
                        }

                    }
                });
            }
        });
    }

    private void toMain() {
        ActivityBuilder.toMain(this, ActivityBuilder.FRAGMENT_HOME);
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
                ActivityBuilder.toMain(this, ActivityBuilder.FRAGMENT_HOME);
            }
            if (!isFinishing()) {
                finish();
            }
        }
    }

    public boolean isToMain() {
        long install_time = PreferenceHelper.getLong(PreferenceHelper.PREF_KEY_INSTALL_TIME, 0);
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

    //******************************************AD******************************************//
    private void initAds() {
        if(CallerAdManager.isShowAdOnEndCall()) {
            MyAdvertisementAdapter adapter = new MyAdvertisementAdapter(getWindow().getDecorView(),
                    "",//ConstantUtils.FB_AFTER_CALL_ID
                    CallerAdManager.ADMOB_ID_ADV_END_CALL_NORMAL,//ConstantUtils.ADMOB_AFTER_CALL_NATIVE_ID
                    Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,//Advertisement.ADMOB_TYPE_NATIVE, Advertisement.ADMOB_TYPE_NONE
                    "",
                    Advertisement.MOPUB_TYPE_NATIVE,
                    -1,
                    "",
                    false);
            mAdvertisement = new Advertisement(adapter);
            mAdvertisement.setRefreshWhenClicked(false);
            mAdvertisement.refreshAD(true);
            mAdvertisement.enableFullClickable();
        }
    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, eventKey, isBanner, AdvertisementSwitcher.SERVER_KEY_END_CALL);
        }

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String mopubKey, int moPubType, int baiduKey, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baiduKey, eventKey, AdvertisementSwitcher.SERVER_KEY_END_CALL, isBanner);
        }

        @Override
        public void onAdLoaded() {
        }

        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.facebook_native_ads_banner_50 : R.layout.facebook_no_icon_native_ads_call_after_big;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_callafter : R.layout.layout_admob_advanced_content_ad_callafter;
        }

        @Override
        public int getAdmobHeight() {
            return 180;
        }
    }
    //******************************************AD******************************************//

}
