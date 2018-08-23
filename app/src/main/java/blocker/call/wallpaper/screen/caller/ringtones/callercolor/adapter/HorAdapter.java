package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.md.callring.RecyclerClick;
import com.md.wallpaper.WallpaperModel;
import com.md.wallpaper.bean.WallpaperInfo;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;

public class HorAdapter extends RecyclerView.Adapter<HorAdapter.ViewHolder>{

    private List<WallpaperInfo> wallpaperInfos;
    private Context context;
    private RecyclerClick mRecyclerClick;

    public RecyclerClick getmRecyclerClick() {
        return mRecyclerClick;
    }

    public void setmRecyclerClick(RecyclerClick mRecyclerClick) {
        this.mRecyclerClick = mRecyclerClick;
    }

    public HorAdapter(List<WallpaperInfo> wallpaperModels, Context context) {
        this.wallpaperInfos = wallpaperModels;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_flash_horizontal,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mGvBg.showImage(wallpaperInfos.get(position).path);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerClick.normalClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wallpaperInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        GlideView mGvBg;
        public ViewHolder(View itemView) {
            super(itemView);

            mGvBg = itemView.findViewById(R.id.gv_bg);
        }
    }
}
