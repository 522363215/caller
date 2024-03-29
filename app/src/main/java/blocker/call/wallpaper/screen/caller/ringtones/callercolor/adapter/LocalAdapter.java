package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.md.callring.LocalSong;
import com.md.callring.RecyclerClick;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.ViewHolder> {
    private Context context;
    private List<LocalSong> localSongs;
    private RecyclerClick mRecyclerClick;

    public RecyclerClick getmRecyclerClick() {
        return mRecyclerClick;
    }

    public void setmRecyclerClick(RecyclerClick mRecyclerClick) {
        this.mRecyclerClick = mRecyclerClick;
    }

    public LocalAdapter(Context context, List<LocalSong> localSongs) {
        this.context = context;
        this.localSongs = localSongs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_wallpaper_list,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.ivBackground.showImage(localSongs.get(position).getDrawableRes());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == localSongs.size()-1){
                    mRecyclerClick.footClick(v,position);
                }else {
                    mRecyclerClick.normalClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return localSongs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        GlideView ivBackground;
        public ViewHolder(View itemView) {
            super(itemView);

            ivBackground = itemView.findViewById(R.id.iv_background);
        }
    }
}
