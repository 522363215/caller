package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.md.block.beans.BlockInfo;
import com.md.block.core.BlockManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.BlockAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshBlockHistory;

public class BlockListFragment extends Fragment {
    public static final int BLOCK_LIST_SHOW_CONTACT = 0;
    public static final int BLOCK_LIST_SHOW_HISTORY = 1;

    private View layoutEmpty;

    private ListView lvBlockContact;
    private BlockAdapter mAdapter;
    List<BlockInfo> model = new ArrayList<>();

    private int mCurrentShowType = BLOCK_LIST_SHOW_CONTACT;

    public static BlockListFragment newInstance(int showType) {
        BlockListFragment fragment = new BlockListFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("block_list_show_type", showType);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCurrentShowType = bundle.getInt("block_list_show_type");
        }

        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
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
        lvBlockContact.setVisibility(model.isEmpty() ? View.GONE : View.VISIBLE);
        layoutEmpty.setVisibility(model.isEmpty() ? View.VISIBLE : View.GONE);
        if (!model.isEmpty()) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_block_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FlurryAgent.logEvent("BlockListFragment-------show_main");
        if (view != null) {
            layoutEmpty = view.findViewById(R.id.layout_empty);
            lvBlockContact = view.findViewById(R.id.lv_block_contact);

            mAdapter = new BlockAdapter(getActivity(), model);
            mAdapter.setShowType(mCurrentShowType);
            lvBlockContact.setAdapter(mAdapter);

            lvBlockContact.setVisibility(model.isEmpty() ? View.GONE : View.VISIBLE);
            layoutEmpty.setVisibility(model.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(EventRefreshBlockHistory event) {
        updateData();
    }
}
