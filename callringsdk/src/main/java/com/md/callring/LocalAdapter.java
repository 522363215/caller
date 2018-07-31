package com.md.callring;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

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
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_song,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvSong.setText(localSongs.get(position).getName());
        holder.rlSong.setBackgroundColor(localSongs.get(position).getDrawableRes());
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
        TextView tvSong;
        RelativeLayout rlSong;
        public ViewHolder(View itemView) {
            super(itemView);

            tvSong = itemView.findViewById(R.id.tv_song);
            rlSong = itemView.findViewById(R.id.rl_song);
        }
    }
}
