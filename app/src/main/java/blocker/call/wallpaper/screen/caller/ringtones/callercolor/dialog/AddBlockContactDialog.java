package blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.md.block.beans.BlockInfo;
import com.md.block.core.BlockManager;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.AddBlockContactAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.CallLogInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.NumberUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;

/**
 * Created by ChenR on 2018/7/10.
 */

public class AddBlockContactDialog extends Dialog {
    private Context mContext;
    private View root;

    private ListView lvCallLog;
    private View pbLoading;

    private OnAddBlockContactListener listener;

    private AddBlockContactAdapter mAdapter = null;
    private List<CallLogInfo> model = new ArrayList<>();

    public AddBlockContactDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int width = DeviceUtil.getScreenWidth() - DeviceUtil.dp2Px(32);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        root = View.inflate(mContext, R.layout.dialog_add_block_contact, null);
        setContentView(root, params);

        initView();
        Async.run(new LoadCallLogRunn());
    }

    @Override
    public void show() {
        super.show();
        FlurryAgent.logEvent("AddBlockContactDialog-----show");
    }

    private void initView() {
        lvCallLog = findViewById(R.id.lv_call_log);
        pbLoading = findViewById(R.id.pb_loading);

        lvCallLog.setVisibility(View.GONE);
        pbLoading.setVisibility(View.VISIBLE);

        mAdapter = new AddBlockContactAdapter(mContext, model);
        lvCallLog.setAdapter(mAdapter);

        lvCallLog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mContext == null || model.isEmpty()) {
                    return;
                }

                CallLogInfo logInfo = model.get(position);
                String saveNumber = NumberUtil.getFormatNumberForDb(logInfo.callNumber);

                BlockInfo block = new BlockInfo();
                block.setName(logInfo.callName);
                block.setNumber(saveNumber);
                block.setBlockTime(System.currentTimeMillis());
                boolean isSuc = BlockManager.getInstance().setBlockContact(block);

                FlurryAgent.logEvent("AddBlockContactDialog-----item_click------add_block_contact");

                if (listener != null) {
                    listener.onAddBlockContact(isSuc, logInfo.callNumber);
                }
                dismiss();
            }
        });
    }

    private void invalidView() {
        if (root != null) {
            root.post(new Runnable() {
                @Override
                public void run() {
                    if (model.size() > 8) {
                        ViewGroup.LayoutParams params = root.getLayoutParams();
                        if (params == null) {
                            int width = DeviceUtil.getScreenWidth() - DeviceUtil.dp2Px(32);
                            params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                        }
                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                        params.height = (int) (metrics.heightPixels - DeviceUtil.getStatusBarHeight() - 36 * metrics.density);
                        root.setLayoutParams(params);
                    }

                    mAdapter.notifyDataSetChanged();
                    pbLoading.setVisibility(View.GONE);
                    lvCallLog.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void setOnAddBlockContactListener (OnAddBlockContactListener listener) {
        this.listener = listener;
    }

    private class LoadCallLogRunn implements Runnable {

        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_CALL_LOG)
                            != PackageManager.PERMISSION_GRANTED) {
                Async.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(mContext, mContext.getString(R.string.permission_denied_txt));
                        dismiss();
                    }
                });
                return;
            }

            List<String> blockedNumber = null;
            List<BlockInfo> blockContacts = BlockManager.getInstance().getBlockContacts();
            if (blockContacts != null && blockContacts.size() > 0) {
                blockedNumber = new ArrayList<>();
                for (BlockInfo info : blockContacts) {
                    blockedNumber.add(info.getNumber());
                }
            }

            List<CallLogInfo> tempCallLog = null;
            List<String> addedNumberCache = null;

            Uri uri = CallLog.Calls.CONTENT_URI;
            String[] project = {
                    CallLog.Calls._ID,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.CACHED_NORMALIZED_NUMBER,
                    CallLog.Calls.DATE
            };

            String selection = CallLog.Calls.DATE + ">?";
            String [] selectionArgs = new String[1];
            selectionArgs[0] = String.valueOf(System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS * 30 * 3);

            Cursor query = mContext.getContentResolver().query(uri, project, selection, selectionArgs, CallLog.Calls.DEFAULT_SORT_ORDER);
            if (query != null) {
                if (query.getCount() <= 0) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(getContext(), getContext().getString(R.string.add_block_empty_call_log));
                            dismiss();
                        }
                    });
                    return;
                }

                tempCallLog = new ArrayList<>();
                addedNumberCache = new ArrayList<>();
                while (query.moveToNext()) {
                    long id = query.getLong(query.getColumnIndex(project[0]));
                    String number = query.getString(query.getColumnIndex(project[1]));
                    String cacheName = query.getString(query.getColumnIndex(project[2]));
                    String normalizedNumber = query.getString(query.getColumnIndex(project[3]));
                    long date = query.getLong(query.getColumnIndex(project[4]));

                    String saveNumber = NumberUtil.getFormatNumberForDb(number);
                    if (blockedNumber != null && blockedNumber.contains(saveNumber)) {
                        LogUtil.d("chenr", "已经添加到黑名单: " + number + ",  name: " + cacheName);
                        continue;
                    }

                    if (!addedNumberCache.contains(number)) {
                        CallLogInfo info = new CallLogInfo();
                        info.id = id;
                        info.callNumber = number;
                        info.callName = cacheName;
                        info.formatNum = normalizedNumber;
                        info.date = date;

                        addedNumberCache.add(number);
                        tempCallLog.add(info);
                    }
                }
                if (tempCallLog.size() > 0) {
                    model.addAll(tempCallLog);
                    invalidView();
                }
                query.close();
            }
        }

    }

    public interface OnAddBlockContactListener {
        void onAddBlockContact(boolean isSuccess, String blockNumber);
    }
}
