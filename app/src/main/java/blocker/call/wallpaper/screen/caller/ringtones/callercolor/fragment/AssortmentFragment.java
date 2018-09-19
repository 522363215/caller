package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.md.callring.Constant;
import com.md.callring.RecyclerClick;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Category;
import com.md.serverflash.callback.CategoryCallback;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.AssortmentActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.AssortmentAdapter;


public class AssortmentFragment extends Fragment {

    private RecyclerView hotRvAssort,historyRvAssort,assortmentRvAssort;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assortment,container,false);

        LinearLayout hot = view.findViewById(R.id.hot);
        TextView hotTvTitleAssort = hot.findViewById(R.id.tv_title_assort);
        hotTvTitleAssort.setText(R.string.hot);
        hotRvAssort = hot.findViewById(R.id.rv_assort);
        LinearLayoutManager hotLayoutManager = new LinearLayoutManager(getActivity(),GridLayoutManager.HORIZONTAL,false);
        hotLayoutManager.setSmoothScrollbarEnabled(true);
        hotLayoutManager.setAutoMeasureEnabled(true);
        hotRvAssort.setLayoutManager(hotLayoutManager);
        hotRvAssort.setHasFixedSize(true);
        hotRvAssort.setNestedScrollingEnabled(false);

        LinearLayout history = view.findViewById(R.id.history);
        TextView historyTvTitleAssort = history.findViewById(R.id.tv_title_assort);
        historyTvTitleAssort.setText(R.string.history);
        historyRvAssort = history.findViewById(R.id.rv_assort);
        LinearLayoutManager historyLayoutManager = new LinearLayoutManager(getActivity(),GridLayoutManager.HORIZONTAL,false);
        historyLayoutManager.setSmoothScrollbarEnabled(true);
        historyLayoutManager.setAutoMeasureEnabled(true);
        historyRvAssort.setLayoutManager(historyLayoutManager);
        historyRvAssort.setHasFixedSize(true);
        historyRvAssort.setNestedScrollingEnabled(false);

        LinearLayout assortment = view.findViewById(R.id.assortment);
        TextView assortmentTvTitleAssort = assortment.findViewById(R.id.tv_title_assort);
        assortmentTvTitleAssort.setText(R.string.assortment);
        assortmentRvAssort = assortment.findViewById(R.id.rv_assort);
        GridLayoutManager assortmentLayoutManager = new GridLayoutManager(getActivity(),2);
        assortmentLayoutManager.setSmoothScrollbarEnabled(true);
        assortmentLayoutManager.setAutoMeasureEnabled(true);
        assortmentRvAssort.setLayoutManager(assortmentLayoutManager);
        assortmentRvAssort.setHasFixedSize(true);
        assortmentRvAssort.setNestedScrollingEnabled(false);

        ThemeSyncManager.getInstance().syncCategoryList(categoryCallback);

        return view;
    }

    CategoryCallback categoryCallback = new CategoryCallback() {
        @Override
        public void onSuccess(int code, final List<Category> data) {
            AssortmentAdapter hotAdapter = new AssortmentAdapter(getActivity(),data,0);
            hotRvAssort.setAdapter(hotAdapter);
            hotAdapter.setmRecyclerClick(new RecyclerClick() {
                @Override
                public void normalClick(View view, int position) {
                    Intent intent = new Intent(getActivity(),AssortmentActivity.class);
                    intent.putExtra(Constant.ISASSORTMENT,data.get(position).getPx_id());
                    intent.putExtra(Constant.NAME,data.get(position).getTitle());
                    startActivity(intent);
                }

                @Override
                public void footClick(View view, int position) {

                }
            });
            AssortmentAdapter historyAdapter = new AssortmentAdapter(getActivity(),data,1);
            historyRvAssort.setAdapter(historyAdapter);
            historyAdapter.setmRecyclerClick(new RecyclerClick() {
                @Override
                public void normalClick(View view, int position) {
                    Intent intent = new Intent(getActivity(),AssortmentActivity.class);
                    intent.putExtra(Constant.ISASSORTMENT,data.get(position).getPx_id());
                    intent.putExtra(Constant.NAME,data.get(position).getTitle());
                    startActivity(intent);
                }

                @Override
                public void footClick(View view, int position) {

                }
            });
            AssortmentAdapter assortmentAdapter = new AssortmentAdapter(getActivity(),data,2);
            assortmentRvAssort.setAdapter(assortmentAdapter);
            assortmentAdapter.setmRecyclerClick(new RecyclerClick() {
                @Override
                public void normalClick(View view, int position) {
                    Intent intent = new Intent(getActivity(),AssortmentActivity.class);
                    intent.putExtra(Constant.ISASSORTMENT,data.get(position).getPx_id());
                    intent.putExtra(Constant.NAME,data.get(position).getTitle());
                    startActivity(intent);
                }

                @Override
                public void footClick(View view, int position) {

                }
            });
        }

        @Override
        public void onFailure(int code, String msg) {

        }
    };
}
