package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.md.serverflash.beans.Category;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.CategoryDetailActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.glide.GlideHelper;

public class CallFlashCategoryAdapter extends RecyclerView.Adapter<CallFlashCategoryAdapter.ViewHolder> {

    private Context mContext;
    private List<Category> mData;

    public CallFlashCategoryAdapter(Context context, List<Category> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_category_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = getItem(position);
        if (category == null) return;
        holder.tvTitle.setTag(position);
        holder.tvTitle.setText(category.getTitle());
        GlideHelper.with(mContext).load(category.getIcon_url());
    }

    public Category getItem(int position) {
        if (mData != null) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBg;
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ivBg = itemView.findViewById(R.id.iv_bg);
            tvTitle = itemView.findViewById(R.id.tv_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) tvTitle.getTag();
                    Category category = getItem(pos);
                    if (category != null) {
                        Intent intent = new Intent(mContext, CategoryDetailActivity.class);
                        intent.putExtra("category_info", category);
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
