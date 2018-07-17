package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.SmsComeAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.Message;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import event.EventBus;


public class SmsComeActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SmsComeActivity";
    private ListView mLvSms;
    private ActionBar mActionBar;
    private List<Message> messages = new ArrayList<>();
    private SmsComeAdapter mSmsComeAdapter;
    public static boolean isAlive;
    private static String mClassName;
    private RelativeLayout mLayoutMenuRoot;
    private FontIconView mFivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAlive = true;
        setContentView(R.layout.activity_sms_come);
        mClassName = getClass().getName();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        onNewIntent(getIntent());
    }

    private void initData(Intent intent) {
        Message message = (Message) intent.getSerializableExtra(ActivityBuilder.SMS_COME_MESSAGE);
        if (message != null) {
            messages.add(message);
            Collections.sort(messages, new SortComparator());
            mSmsComeAdapter.notifyDataSetChanged();
            if (messages.size() >= 1)
                mLvSms.setSelection(messages.size() - 1);
            mActionBar.setTitle(getString(R.string.call_after_message) + "(" + messages.size() + ")");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("SmsComeActivity--showMain");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlive = false;
        messages.clear();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    private void initView() {
        setActionbar();
        mLvSms = (ListView) findViewById(R.id.lv_sms);
        mSmsComeAdapter = new SmsComeAdapter(this, messages);
        mLvSms.setAdapter(mSmsComeAdapter);
        mLayoutMenuRoot = (RelativeLayout) findViewById(R.id.layout_menu_root);
        mLvSms.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (messages.size() >= 1)
                    mLvSms.smoothScrollToPosition(messages.size() - 1);
            }
        });
    }

    private void setActionbar() {
        mActionBar = (ActionBar) findViewById(R.id.action_bar);
        mFivBack = (FontIconView) findViewById(R.id.imgReturn);
        mActionBar.setBackgroundColor(getResources().getColor(R.color.color_FF0A2134));
        mActionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent("SmsComeActivity--clickBack");
                toMain();
            }
        });

        mFivBack.setTextColor(getResources().getColor(R.color.color_FF7594B2));
        ((FontIconView) mActionBar.findViewById(R.id.fiv_close)).setTextColor(getResources().getColor(R.color.color_FF7594B2));
        ((TextView) mActionBar.findViewById(R.id.txtTitle)).setTextColor(getResources().getColor(R.color.color_FF7594B2));
        mActionBar.findViewById(R.id.layout_message_actionbar).setVisibility(View.VISIBLE);
        mActionBar.findViewById(R.id.fiv_phone).setVisibility(View.GONE);
        ((FontIconView) mActionBar.findViewById(R.id.fiv_menu)).setTextColor(getResources().getColor(R.color.color_FF7594B2));
        mActionBar.findViewById(R.id.fiv_menu).setOnClickListener(this);
        findViewById(R.id.tv_close).setOnClickListener(this);
        findViewById(R.id.tv_disable).setOnClickListener(this);
        findViewById(R.id.layout_menu_root).setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fiv_menu:
                // do nothing;
                FlurryAgent.logEvent("SmsComeActivity----menu");
                mLayoutMenuRoot.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_close:
                FlurryAgent.logEvent("SmsComeActivity----close");
                mLayoutMenuRoot.setVisibility(View.GONE);
                onBackPressed();
                break;
            case R.id.layout_menu_root:
                mLayoutMenuRoot.setVisibility(View.GONE);
                break;
            case R.id.tv_disable:
                try {
                    FlurryAgent.logEvent("SmsComeActivity----disable");
                    mLayoutMenuRoot.setVisibility(View.GONE);
                    onBackPressed();
//                    startActivity(new Intent(this, BlockSettingActivity.class));
                } catch (Exception e) {
                    LogUtil.e("smscomeactivity", "tv_disable e:" + e.getMessage());
                }
                break;
        }
    }

    class SortComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            Message info1 = (Message) o1;
            Message info2 = (Message) o2;
            if (info1.date > info2.date) {
                return 1;
            }
            return -1;
        }
    }

    /* 判断某个界面是否在前台
     *
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context) {
        return DeviceUtil.isForeground(context, mClassName);
    }

    private void toMain() {
        ActivityBuilder.toMain(this, ActivityBuilder.FRAGMENT_HOME);
        if (!isFinishing()) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        FlurryAgent.logEvent("SmsComeActivity--onBackPressed");
        if (mLayoutMenuRoot.getVisibility() == View.VISIBLE) {
            mLayoutMenuRoot.setVisibility(View.GONE);
        } else {
            if (!isFinishing()) {
                finish();
            }
        }
    }
}
