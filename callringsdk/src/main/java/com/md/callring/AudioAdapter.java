package com.md.callring;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.md.callring.Song;


import java.util.List;

public class AudioAdapter extends BaseAdapter {

    private Context mContext ;
    private List<Song> data ;
    private LayoutInflater inflater;


    public AudioAdapter(Context context, List<Song> data) {
        this.mContext = context;
        this.data = data;
        inflater =LayoutInflater.from(mContext);
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
            convertView = inflater.inflate(R.layout.item_audio, null) ;
            viewHolder.tvAudio = (TextView) convertView.findViewById(R.id.tv_audio) ;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Log.e( "getView: ","ghjkljjia" );
        viewHolder.tvAudio.setText(data.get(position).getTitle()) ;
        return convertView;
    }

    class ViewHolder{
        TextView tvAudio;

        public ViewHolder() {
        }
    }
}
