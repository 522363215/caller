package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.md.block.beans.BlockInfo;
import com.md.block.core.BlockManager;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.BlockAdapter;

public class BlockListFragment extends Fragment {
    public static final int BLOCK_LIST_SHOW_CONTACT = 0;
    public static final int BLOCK_LIST_SHOW_HISTORY = 1;

    private TextView tvEmpty;

    private ListView lvBlockContact;
    private BlockAdapter mAdapter;
    List<BlockInfo> model = new ArrayList<>();

    private int mCurrentShowType = BLOCK_LIST_SHOW_CONTACT;

    public static BlockListFragment newInstance (int showType) {
        BlockListFragment fragment = new BlockListFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("block_list_show_type", showType);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCurrentShowType = bundle.getInt("block_list_show_type");
        }

        initData();
    }

    private void initData() {
        List<BlockInfo> list = null;
        if (mCurrentShowType == BLOCK_LIST_SHOW_CONTACT) {
            list = BlockManager.getInstance().getBlockContacts();
        } else {
            list = BlockManager.getInstance().getBlockHistory();
        }
        if (list != null && list.size() > 0) {
            model.addAll(list);
        }
    }

    public void updateData() {
        model.clear();
        initData();
        mAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_block_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            tvEmpty = view.findViewById(R.id.tv_empty);
            lvBlockContact = view.findViewById(R.id.lv_block_contact);

            mAdapter = new BlockAdapter(getActivity(), model);
            mAdapter.setShowType(mCurrentShowType);
            lvBlockContact.setAdapter(mAdapter);

            lvBlockContact.setVisibility(model.isEmpty() ? View.GONE : View.VISIBLE);
            tvEmpty.setVisibility(model.isEmpty() ? View.VISIBLE : View.GONE);
        }

    }

}
