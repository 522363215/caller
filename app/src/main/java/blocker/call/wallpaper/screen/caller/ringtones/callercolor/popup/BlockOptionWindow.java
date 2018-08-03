package blocker.call.wallpaper.screen.caller.ringtones.callercolor.popup;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.md.block.beans.BlockInfo;
import com.md.block.core.BlockManager;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.BlockListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;

/**
 * Created by ChenR on 2018/7/12.
 */

public class BlockOptionWindow extends PopupWindow implements View.OnClickListener{

    private View root;
    private TextView tvBlockSwitch;
    private TextView tvClearAll;

    private int mCurrentFragmentIndex;

    private OnClearDataCallback callback;

    public BlockOptionWindow(Context context, int currentFragmentIndex) {
        super(context);

        this.mCurrentFragmentIndex = currentFragmentIndex;

        initView(context);
    }

    private void initView(Context context) {
        if (context == null) {
            return;
        }
        root = View.inflate(context, R.layout.popup_block_option, null);

        tvBlockSwitch = root.findViewById(R.id.tv_block_switch);
        tvClearAll = root.findViewById(R.id.tv_clear_all);

        View blockClear = root.findViewById(R.id.layout_block_clear);

        tvBlockSwitch.setText(BlockManager.getInstance().getBlockSwitchState() ?
                R.string.block_option_close_switch : R.string.block_option_open_switch);

        if (mCurrentFragmentIndex == BlockListFragment.BLOCK_LIST_SHOW_CONTACT) {
            List<BlockInfo> list = BlockManager.getInstance().getBlockContacts();
            if (list != null && list.size() > 0) {
                blockClear.setVisibility(View.VISIBLE);
                tvClearAll.setText(R.string.block_option_clear_all_contacts);
            } else {
                blockClear.setVisibility(View.GONE);
            }
        } else if (mCurrentFragmentIndex == BlockListFragment.BLOCK_LIST_SHOW_HISTORY) {
            List<BlockInfo> list = BlockManager.getInstance().getBlockHistory();
            if (list != null && list.size() > 0) {
                blockClear.setVisibility(View.VISIBLE);
                tvClearAll.setText(R.string.block_option_clear_all_history);
            } else {
                blockClear.setVisibility(View.GONE);
            }
        }

        tvBlockSwitch.setOnClickListener(this);
        tvClearAll.setOnClickListener(this);

        setContentView(root);
        setOutsideTouchable(true);
    }

    @Override
    public void onClick(View v) {
        if (root == null || root.getContext() == null) {
            return;
        }
        Context context = root.getContext();
        switch (v.getId()) {
            case R.id.tv_block_switch:
                boolean state = BlockManager.getInstance().getBlockSwitchState();
                BlockManager.getInstance().setBlockSwitchState(!state);

                tvBlockSwitch.setText(!state ? R.string.block_option_close_switch :
                        R.string.block_option_open_switch);
                String msg = context.getString(!state ? R.string.block_option_toast_open_success :
                        R.string.block_option_toast_close_success);
                ToastUtils.showToast(context, msg);
                dismiss();
                break;
            case R.id.tv_clear_all:
                if (mCurrentFragmentIndex == BlockListFragment.BLOCK_LIST_SHOW_CONTACT) {
                    BlockManager.getInstance().clearBlockContacts();
                    ToastUtils.showToast(context, context.getString(R.string.block_option_toast_clear_all_contacts_success));
                } else {
                    BlockManager.getInstance().clearBlockHistory();
                    ToastUtils.showToast(context, context.getString(R.string.block_option_toast_clear_all_history_success));
                }
                if (callback != null) {
                    callback.onClearData(mCurrentFragmentIndex);
                }
                dismiss();
                break;
        }
    }

    public void setClearCallback (OnClearDataCallback callback) {
        this.callback = callback;
    }

    public interface OnClearDataCallback {
        void onClearData (int clearIndex);
    }
}
