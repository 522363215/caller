package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.md.callring.Constant;
import com.md.callring.RecyclerClick;
import com.md.serverflash.beans.Category;
import com.md.serverflash.beans.Theme;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.glide.GlideHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.CircleImageView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.OKCancelDialog;

public class AssortmentAdapter extends RecyclerView.Adapter<AssortmentAdapter.ViewHolder> {

    private Context context;
    private List<Category> themes;
    private int type;
    private RecyclerClick mRecyclerClick;

    public RecyclerClick getmRecyclerClick() {
        return mRecyclerClick;
    }

    public void setmRecyclerClick(RecyclerClick mRecyclerClick) {
        this.mRecyclerClick = mRecyclerClick;
    }

    public AssortmentAdapter(Context context, List<Category> themes, int type) {
        this.context = context;
        this.themes = themes;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (type == Constant.HOT){
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_hot_wallpaper,parent,false));
        }else if (type == Constant.HISTORY){
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_history_wallpaper,parent,false));
        }else if (type == Constant.ASSORTMENT){
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_assortment_wallpaper,parent,false));
        }else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (type == Constant.HOT){
            holder.ivHotAssort.showImage(themes.get(position).getIcon_url());
            holder.tvHotAssort.setText(themes.get(position).getTitle());
        }else if (type == Constant.HISTORY){
            GlideHelper.with(context).load(themes.get(position).getIcon_url()).into(holder.clrHistoryAssort);
        }else if (type == Constant.ASSORTMENT){
            holder.ivAssort.showImage(themes.get(position).getIcon_url());
            holder.tvAssort.setText(themes.get(position).getTitle());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerClick.normalClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (type == Constant.HOT){
            return 8;
        }else if (type == Constant.HISTORY){
            return 8;
        }else if (type == Constant.ASSORTMENT){
            return themes.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHotAssort,tvAssort;
        GlideView ivHotAssort,ivAssort;
        CircleImageView clrHistoryAssort;
        public ViewHolder(View itemView) {
            super(itemView);

            tvHotAssort = itemView.findViewById(R.id.tv_hot_assort);
            ivHotAssort = itemView.findViewById(R.id.iv_hot_assort);

            clrHistoryAssort = itemView.findViewById(R.id.clr_history_assort);

            tvAssort = itemView.findViewById(R.id.tv_assort);
            ivAssort = itemView.findViewById(R.id.iv_assort);
        }
    }
}
