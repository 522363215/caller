package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.PermissionInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.PermissionUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;

public class PermissionShowAdapter extends RecyclerView.Adapter<PermissionShowAdapter.ViewHolder> {
    private static final int DEFAULT_VIEW_TYPE = 0;
    private static final int LAST_POSITION_VIEW_TYPE = 1;
    private boolean mIsLetsStart;
    private List<PermissionInfo> mPermissionInfos;
    private Context mContext;
    private SetClickListener mSetClickListener;

    public PermissionShowAdapter(Context context, List<PermissionInfo> permissionInfos, boolean isLetsStart) {
        mContext = context;
        mPermissionInfos = permissionInfos;
        mIsLetsStart = isLetsStart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_permssion, null);
        setViewLayout(view, viewType);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mPermissionInfos.size() - 1) {
            return LAST_POSITION_VIEW_TYPE;
        }
        return DEFAULT_VIEW_TYPE;
    }

    private void setViewLayout(View view, int viewType) {
        //每个item 的宽度
        RelativeLayout.LayoutParams itemParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (itemParams == null) {
            itemParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            itemParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        if (mIsLetsStart) {
            //线的高度，根据总数在变
            RelativeLayout layoutLine = view.findViewById(R.id.layout_line);
            ViewGroup.LayoutParams layoutLineParams = layoutLine.getLayoutParams();
            if (mPermissionInfos != null && mPermissionInfos.size() > 3) {
                layoutLineParams.height = Stringutil.dpToPx(mContext, 30);
            } else {
                layoutLineParams.height = Stringutil.dpToPx(mContext, 50);
            }
            layoutLine.setLayoutParams(layoutLineParams);


            //每个item
            itemParams.topMargin = Stringutil.dpToPx(mContext, 10);
            if (viewType != LAST_POSITION_VIEW_TYPE) {
                itemParams.bottomMargin = Stringutil.dpToPx(mContext, 0);
            } else {
                itemParams.bottomMargin = Stringutil.dpToPx(mContext, 10);
            }
        } else {
            //每个item
            itemParams.topMargin = Stringutil.dpToPx(mContext, 20);
            itemParams.bottomMargin = Stringutil.dpToPx(mContext, 20);
        }

        view.setLayoutParams(itemParams);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PermissionInfo info = getItem(position);
        if (info == null) return;
        holder.tvSet.setTag(position);

//        holder.ivIcon.setBackgroundResource(info.iconResId);
        holder.tvTitle.setText(info.title);
        holder.tvDes.setText(info.permissionDes);

        if (PermissionUtils.PERMISSION_SHOW_ON_LOCK.equals(info.permission) || PermissionUtils.PERMISSION_AUTO_START.equals(info.permission)) {
            holder.tvSet.setText(R.string.call_flash_view);
        } else {
            holder.tvSet.setText(R.string.permission_set);
        }

        info.isGet = PermissionUtils.hasPermission(mContext, info.permission);
        if (!PermissionUtils.PERMISSION_SHOW_ON_LOCK.equals(info.permission) && !PermissionUtils.PERMISSION_AUTO_START.equals(info.permission) && info.isGet) {
            holder.ivGet.setVisibility(View.VISIBLE);
            holder.tvSet.setVisibility(View.GONE);
        } else {
            holder.ivGet.setVisibility(View.GONE);
            holder.tvSet.setVisibility(View.VISIBLE);
        }

        if (!mIsLetsStart || position == mPermissionInfos.size() - 1) {
            holder.layoutLine.setVisibility(View.GONE);
        } else {
            holder.layoutLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mPermissionInfos != null) {
            return mPermissionInfos.size();
        }
        return 0;
    }

    public PermissionInfo getItem(int position) {
        if (mPermissionInfos != null) {
            return mPermissionInfos.get(position);
        }
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvDes;
        TextView tvSet;
        ImageView ivGet;
        View layoutLine;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDes = itemView.findViewById(R.id.tv_des);
            tvSet = itemView.findViewById(R.id.tv_set);
            ivGet = itemView.findViewById(R.id.iv_get);
            layoutLine = itemView.findViewById(R.id.layout_line);
            tvSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvSet.getTag() != null) {
                        int position = (int) tvSet.getTag();
                        if (mSetClickListener != null) {
                            mSetClickListener.onSetClickListener(position);
                        }
                    }
                }
            });
        }
    }

    public interface SetClickListener {
        void onSetClickListener(int position);
    }

    public void setOnItemClickListener(SetClickListener listener) {
        mSetClickListener = listener;
    }
}
