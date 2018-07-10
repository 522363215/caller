package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.CallLogInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;

/**
 * Created by ChenR on 2018/7/10.
 */

public class AddBlockContactAdapter extends BaseAdapter {

    private Context mContext;
    private List<CallLogInfo> model;

    public AddBlockContactAdapter (Context context, List<CallLogInfo> model) {
        this.mContext = context;
        this.model = model;
    }

    @Override
    public int getCount() {
        return model == null ? 0 : model.size();
    }

    @Override
    public CallLogInfo getItem(int position) {
        return model == null ? null : model.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_add_block_contact, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CallLogInfo item = getItem(position);
        if (item != null) {
            String name = item.callName;
            String number = item.callNumber;
            boolean isSelected = item.isSelected;

            holder.tvName.setText(TextUtils.isEmpty(name) ? number : name);
            holder.tvNumber.setText(number);
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView tvName;
        private TextView tvNumber;

        public ViewHolder(View itemRoot) {

            tvName = itemRoot.findViewById(R.id.tv_name);
            tvNumber = itemRoot.findViewById(R.id.tv_number);
        }

    }
}
