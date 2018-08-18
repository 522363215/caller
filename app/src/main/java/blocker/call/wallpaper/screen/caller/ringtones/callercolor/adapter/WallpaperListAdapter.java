package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.md.callring.RecyclerClick;
import com.md.wallpaper.bean.WallpaperInfo;


import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;

public class WallpaperListAdapter extends RecyclerView.Adapter<WallpaperListAdapter.ViewHolder> {

    private Context context;
    private List<WallpaperInfo> wallpaperInfos;
    private RecyclerClick mRecyclerClick;

    public RecyclerClick getmRecyclerClick() {
        return mRecyclerClick;
    }

    public void setmRecyclerClick(RecyclerClick mRecyclerClick) {
        this.mRecyclerClick = mRecyclerClick;
    }

    public WallpaperListAdapter(Context context, List<WallpaperInfo> wallpaperInfos) {
        this.context = context;
        this.wallpaperInfos = wallpaperInfos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(com.md.callring.R.layout.item_song, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvSong.setText(wallpaperInfos.get(position).title);
        holder.ivBackground.showImage(wallpaperInfos.get(position).img_vUrl);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerClick.normalClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wallpaperInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSong;
        RelativeLayout rlSong;
        GlideView ivBackground;

        public ViewHolder(View itemView) {
            super(itemView);

            tvSong = itemView.findViewById(com.md.callring.R.id.tv_song);
            rlSong = itemView.findViewById(com.md.callring.R.id.rl_song);
            ivBackground = itemView.findViewById(R.id.iv_background);
        }
    }
}
