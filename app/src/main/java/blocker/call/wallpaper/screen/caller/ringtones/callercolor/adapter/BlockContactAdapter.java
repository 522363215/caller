package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;

/**
 * Created by ChenR on 2018/7/6.
 */

public class BlockContactAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<String> model = null;

    public BlockContactAdapter(Context context, List<String> model) {
        this.mContext = context;
        this.model = model;
    }

    @Override
    public int getCount() {
        return model == null ? 0 : model.size();
    }

    @Override
    public String getItem(int position) {
        return model == null ? null : model.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mContext == null) return null;
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_block_contact, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String item = getItem(position);
        if (!TextUtils.isEmpty(item)) {
            holder.textView.setText(item);
        }

        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
        private FontIconView fontIconView;
        private TextView textView;

        public ViewHolder(View root) {
            if (root == null) {
                return;
            }

            imageView = root.findViewById(R.id.iv_photo);
            fontIconView = root.findViewById(R.id.fiv_photo);
            textView = root.findViewById(R.id.tv_name);
        }

    }
}
