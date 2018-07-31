package com.md.callring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.md.callring.Song;


import java.util.List;

public class AudioAdapter extends BaseAdapter {

    private Context context ;
    private List<Song> data ;


    public AudioAdapter(Context context, List<Song> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size() ;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if(convertView == null ) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_audio, null) ;
            viewHolder.tvAudio = (TextView) convertView.findViewById(R.id.tv_audio) ;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvAudio.setText(data.get(position).getTitle()) ;
        return convertView;
    }

    class ViewHolder{
        TextView tvAudio;

        public ViewHolder() {
        }
    }
}
