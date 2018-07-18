package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.PermissionInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.PermissionUtils;

public class PermissionShowAdapter extends RecyclerView.Adapter<PermissionShowAdapter.ViewHolder> {
    private List<PermissionInfo> mPermissionInfos;
    private Context mContext;
    private ItemClickListener mItemClickListener;

    public PermissionShowAdapter(Context context, List<PermissionInfo> permissionInfos) {
        mContext = context;
        mPermissionInfos = permissionInfos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_permssion, null);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PermissionInfo info = getItem(position);
        if (info == null) return;
        holder.tvEnable.setTag(position);

        holder.ivIcon.setBackgroundResource(info.iconResId);
        holder.tvTitle.setText(info.title);
        holder.tvDes.setText(info.permissionDes);

        info.isGet = PermissionUtils.hasPermission(mContext, info.permission);
        if (info.isGet) {
            holder.layoutEnabled.setVisibility(View.VISIBLE);
            holder.tvEnable.setVisibility(View.GONE);
        } else {
            holder.layoutEnabled.setVisibility(View.GONE);
            holder.tvEnable.setVisibility(View.VISIBLE);
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
        TextView tvEnable;
        LinearLayout layoutEnabled;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDes = itemView.findViewById(R.id.tv_des);
            tvEnable = itemView.findViewById(R.id.tv_enable);
            layoutEnabled = itemView.findViewById(R.id.layout_enabled);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvEnable.getTag() != null) {
                        int position = (int) tvEnable.getTag();
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClickListener(position);
                        }
                    }
                }
            });
        }
    }

    public interface ItemClickListener {
        void onItemClickListener(int position);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }
}
