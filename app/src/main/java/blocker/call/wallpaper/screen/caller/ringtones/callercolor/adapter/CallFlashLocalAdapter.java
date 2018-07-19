package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.flashset.manager.CallFlashManager;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;

/**
 * Created by ChenR on 2018/1/25.
 */

public class CallFlashLocalAdapter extends RecyclerView.Adapter<CallFlashLocalAdapter.ViewHolder> {

    private Context context = null;
    private List<CallFlashInfo> model = null;

    private boolean isFlashSwitchOn = false;
    private int mFlashType = -1;
    private String mCustomPath = "";
    private String mDynamicPath = "";
    private boolean mIsComeCallAfter;
    private boolean mIsComePhoneDetail;

    private ConcurrentHashMap<String, File> videoMap;

    public CallFlashLocalAdapter(Context context, List<CallFlashInfo> model) {
        this.context = context;
        this.model = model;
        videoMap = new ConcurrentHashMap<>();
        isFlashSwitchOn = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
        mFlashType = CallFlashPreferenceHelper.getInt(CallFlashPreferenceHelper.CALL_FLASH_TYPE, -1);
        mCustomPath = CallFlashPreferenceHelper.getString(CallFlashPreferenceHelper.CALL_FLASH_CUSTOM_BG_PATH, "");
        mDynamicPath = CallFlashPreferenceHelper.getString(CallFlashPreferenceHelper.CALL_FLASH_TYPE_DYNAMIC_PATH, "");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = View.inflate(context, R.layout.item_call_flash_online, null);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CallFlashInfo info = model.get(position);
        if (info != null) {
            holder.root.setTag(position);
            holder.iv_download.setTag(position);
            if (info.imgResId > 0) {
                int imgId = info.imgResId;
                if (info.flashType == FlashLed.FLASH_TYPE_FESTIVAL) {
                    imgId = R.drawable.icon_flash_festival_small;
                }
                holder.gv_bg.showImage(imgId);
            }
            if (!TextUtils.isEmpty(info.url)) {
                File file = videoMap.get(info.url);
                if (file == null) {
                    file = CallFlashManager.getInstance().getOnlineThemeSourcePath(info.url);
                    videoMap.put(info.url, file);
                }
                if (file.exists() && file.isFile()) {
                    holder.iv_download.setVisibility(View.GONE);
                    setSelectIcon(holder, info);
                } else {
                    holder.iv_download.setVisibility(View.VISIBLE);
                }
            } else {
                setSelectIcon(holder, info);
            }
        }
    }

    private void setSelectIcon(ViewHolder holder, CallFlashInfo info) {
        if (isFlashSwitchOn) {
            if (((info.flashType == FlashLed.FLASH_TYPE_CUSTOM && mCustomPath.equals(info.path))
                    || (info.flashType == FlashLed.FLASH_TYPE_DYNAMIC && mDynamicPath.equals(info.path))
                    || (info.flashType != FlashLed.FLASH_TYPE_CUSTOM && info.flashType != FlashLed.FLASH_TYPE_CUSTOM))
                    && info.flashType == mFlashType) {
                holder.iv_call_select.setVisibility(View.VISIBLE);
            } else {
                holder.iv_call_select.setVisibility(View.GONE);
            }
        } else {
            holder.iv_call_select.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return model == null ? 0 : model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View root;
        private GlideView gv_bg;
        private ImageView iv_call_select;
        private ImageView iv_call_photo;
        private ImageView iv_download;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.layout_root);
            root.setOnClickListener(mOnClickListener);
            gv_bg = itemView.findViewById(R.id.gv_bg);
            iv_call_photo = itemView.findViewById(R.id.iv_call_photo);
            iv_call_select = itemView.findViewById(R.id.iv_select);
            iv_download = itemView.findViewById(R.id.iv_download);
        }
    }

    public void setCome(boolean isComeCallAfter, boolean isComePhoneDetail) {
        mIsComeCallAfter = isComeCallAfter;
        mIsComePhoneDetail = isComePhoneDetail;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag();

            if (model != null && model.size() > 0) {
                CallFlashInfo info = model.get(pos);

                GlideView glideView = v.findViewById(R.id.gv_bg);
                //  jump to CallFlashPreviewActivity
                ActivityBuilder.toCallFlashPreview(context, info, glideView);
            }
        }
    };

    public void clearMap() {
        try {
            if (videoMap != null) {
                videoMap.clear();
            }
        } catch (Exception e) {

        }
    }

}
