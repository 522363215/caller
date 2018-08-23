package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.md.callring.Constant;
import com.md.callring.RecyclerClick;
import com.md.wallpaper.SetPic;
import com.md.wallpaper.WallpaperPreferenceHelper;
import com.md.wallpaper.bean.WallpaperInfo;

import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.WallpaperDetailActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.HorAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.HorizontalCallFlashMarginDecoration;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;

public class UsedPictureFragment extends Fragment {

    private SetPic setPic;
    private LinearLayout mLlSet,mLlGet;
    private RecyclerView mRvAllCookie,mRvAll;
    private RelativeLayout mRlCookie,mRlDownloaded;
    private GlideView mIvGet;
    private WallpaperInfo setWallpaper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_used_picture,container,false);

        intiView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    public SetPic getSetPic() {
        return setPic;
    }

    public void setSetPic(SetPic setPic) {
        this.setPic = setPic;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_set:
                    setPic.setPic();
                    break;
                case R.id.iv_get:
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WallpaperDetailActivity.class);
                    intent.putExtra(Constant.WALLPAPER_BUNDLE, setWallpaper);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void intiView(View view){
        mLlSet = view.findViewById(R.id.ll_set);
        mLlGet = view.findViewById(R.id.ll_get);
        mRvAllCookie = view.findViewById(R.id.rv_all_cookie);
        mRvAll = view.findViewById(R.id.rv_all);
        mIvGet = view.findViewById(R.id.iv_get);
        mRlCookie = view.findViewById(R.id.rl_cookie);
        mRlDownloaded = view.findViewById(R.id.rl_downloaded);
        view.findViewById(R.id.tv_set).setOnClickListener(mOnClickListener);
        mIvGet.setOnClickListener(mOnClickListener);
    }

    private void initData(){
        if (WallpaperPreferenceHelper.getString(WallpaperPreferenceHelper.FILE_NAME,"")!=null&&!WallpaperPreferenceHelper.getString(WallpaperPreferenceHelper.FILE_NAME,"").equals("")){
            mLlSet.setVisibility(View.GONE);
            mLlGet.setVisibility(View.VISIBLE);
            mIvGet.showImage(WallpaperPreferenceHelper.getString(WallpaperPreferenceHelper.FILE_NAME,""));
            setWallpaper = (WallpaperInfo) WallpaperPreferenceHelper.getObject(WallpaperPreferenceHelper.SETED_WALLPAPERS, WallpaperInfo.class);
        }else {
            mLlSet.setVisibility(View.VISIBLE);
            mLlGet.setVisibility(View.GONE);
        }
        List<WallpaperInfo> download = WallpaperPreferenceHelper.getDataList(WallpaperPreferenceHelper.DOWNLOADED_WALLPAPERS,WallpaperInfo[].class);
        final List<WallpaperInfo> cookie = WallpaperPreferenceHelper.getDataList(WallpaperPreferenceHelper.COOKIE,WallpaperInfo[].class);
        if (download!=null&&download.size()>0){
            mRlDownloaded.setVisibility(View.VISIBLE);
            mRvAllCookie.setLayoutManager(new LinearLayoutManager(getActivity(), GridLayoutManager.HORIZONTAL, false));
            mRvAllCookie.addItemDecoration(new HorizontalCallFlashMarginDecoration());
            HorAdapter horAdapter = new HorAdapter(download,getActivity());
            mRvAllCookie.setAdapter(horAdapter);
            horAdapter.setmRecyclerClick(new RecyclerClick() {
                @Override
                public void normalClick(View view, int position) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WallpaperDetailActivity.class);
                    intent.putExtra(Constant.WALLPAPER_BUNDLE, cookie.get(position));
                    startActivity(intent);
                }

                @Override
                public void footClick(View view, int position) {

                }
            });
        }else {
            mRlDownloaded.setVisibility(View.GONE);
        }

        if (cookie!=null&&cookie.size()>0){
            mRlCookie.setVisibility(View.VISIBLE);
            mRvAll.setLayoutManager(new LinearLayoutManager(getActivity(), GridLayoutManager.HORIZONTAL, false));
            mRvAll.addItemDecoration(new HorizontalCallFlashMarginDecoration());
            HorAdapter horAdapter = new HorAdapter(cookie,getActivity());
            mRvAll.setAdapter(horAdapter);

            horAdapter.setmRecyclerClick(new RecyclerClick() {
                @Override
                public void normalClick(View view, int position) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WallpaperDetailActivity.class);
                    intent.putExtra(Constant.WALLPAPER_BUNDLE, cookie.get(position));
                    startActivity(intent);
                }

                @Override
                public void footClick(View view, int position) {

                }
            });
        }else {
            mRlCookie.setVisibility(View.GONE);
        }
    }


    RecyclerClick recyclerClick = new RecyclerClick() {
        @Override
        public void normalClick(View view, int position) {

        }

        @Override
        public void footClick(View view, int position) {

        }
    };
}
