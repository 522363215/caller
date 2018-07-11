package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.md.block.beans.BlockInfo;
import com.md.block.core.BlockManager;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ContactManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.NumberUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;

public class BlockAdapter extends BaseAdapter {
    private List<BlockInfo> model;
    private Context mContext;

    private View.OnClickListener mOnRemoveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag();
            BlockInfo blockInfo = model.get(pos);
            //BlockManager.getInstance().
        }
    };

    public BlockAdapter (Context context, List<BlockInfo> model) {
        this.mContext = context;
        this.model = model;
    }

    @Override
    public int getCount() {
        return model == null ? -1 : model.size();
    }

    @Override
    public BlockInfo getItem(int position) {
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
            convertView = View.inflate(mContext, R.layout.item_block_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.fivRemove.setTag(position);

        BlockInfo item = getItem(position);
        if (item != null) {
            Bitmap photo = ContactManager.getInstance().getContactPhoto(item.getPhotoID());
            if (photo != null) {
                holder.ivPhoto.setImageBitmap(photo);
            }

            if (TextUtils.isEmpty(item.getName())) {
                String number = NumberUtil.getFormatNumber(item.getNumber());
                holder.textView.setText(number);
                LogUtil.d("chenr", "BlockAdapter show format number by NumberUtil.getFormatNumber:  " + number);
            } else {
                holder.textView.setText(item.getName());
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private FontIconView fivPhoto;
        private ImageView ivPhoto;
        private TextView textView;
        private View fivRemove;

        public ViewHolder (View root) {
            if (root != null) {
                fivPhoto = root.findViewById(R.id.fiv_photo);
                ivPhoto = root.findViewById(R.id.iv_photo);
                textView = root.findViewById(R.id.tv_name);
                fivRemove = root.findViewById(R.id.fiv_remove);


            }
        }
    }
}
