package blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.md.flashset.bean.CallFlashDataType;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.flashset.manager.CallFlashManager;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.HorizontalCallFlashAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.HorizontalCallFlashMarginDecoration;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;

/**
 * @author zhq
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MineFragment";
    private GlideView mGvBgCurrent;
    private RecyclerView mRvSetRecord;
    private RecyclerView mRvCollect;
    private RecyclerView mRvDownload;
    private View mLayoutNoCallFlash;
    private View mTvView;
    private View mLayoutSetRecord;
    private View mLayoutCollect;
    private View mLayoutDownloaded;
    private HorizontalCallFlashAdapter mSetRecordAdapter;
    private HorizontalCallFlashAdapter mCollectAdapter;
    private HorizontalCallFlashAdapter mDownloadAdapter;
    private List<CallFlashInfo> mCollectData = new ArrayList<>();
    private List<CallFlashInfo> mSetRecordData = new ArrayList<>();
    private List<CallFlashInfo> mDownloadData = new ArrayList<>();
    private CallFlashInfo mCurrentCallFlashInfo;
    private View mLayoutCollectAllBtn;
    private View mLayoutSetRecordAllBtn;
    private View mLayoutDownloadedAllBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        listener();
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    private void setData() {
        //当前设置的call flash
        setCurrentCallFlashBg();
        //设置记录
        setCallFlashRecord();
        //收藏
        setCollect();
        //下载
        setDownload();
    }

    private void initView(View view) {
        //当前设置
        mGvBgCurrent = view.findViewById(R.id.gv_bg_current);
        mLayoutNoCallFlash = view.findViewById(R.id.layout_no_call_flash);
        mTvView = view.findViewById(R.id.tv_view);
        mGvBgCurrent.setOnClickListener(this);

        //设置记录
        mLayoutSetRecord = view.findViewById(R.id.layout_set_record);
        mRvSetRecord = view.findViewById(R.id.rv_set_record);
        mLayoutSetRecordAllBtn = view.findViewById(R.id.layout_set_record_all_btn);

        mSetRecordAdapter = new HorizontalCallFlashAdapter(getActivity(), mSetRecordData);
        mRvSetRecord.setLayoutManager(new LinearLayoutManager(getActivity(), GridLayoutManager.HORIZONTAL, false));
        mRvSetRecord.addItemDecoration(new HorizontalCallFlashMarginDecoration());
        mRvSetRecord.setHasFixedSize(true);
        mRvSetRecord.setNestedScrollingEnabled(false);
        mRvSetRecord.setAdapter(mSetRecordAdapter);

        //收藏
        mLayoutCollect = view.findViewById(R.id.layout_collect);
        mRvCollect = view.findViewById(R.id.rv_collect);
        mLayoutCollectAllBtn = view.findViewById(R.id.layout_collect_all_btn);

        mCollectAdapter = new HorizontalCallFlashAdapter(getActivity(), mCollectData);
        mRvCollect.setLayoutManager(new LinearLayoutManager(getActivity(), GridLayoutManager.HORIZONTAL, false));
        mRvCollect.addItemDecoration(new HorizontalCallFlashMarginDecoration());
        mRvCollect.setHasFixedSize(true);
        mRvCollect.setNestedScrollingEnabled(false);
        mRvCollect.setAdapter(mCollectAdapter);

        //已下载
        mLayoutDownloaded = view.findViewById(R.id.layout_download);
        mRvDownload = view.findViewById(R.id.rv_download);
        mLayoutDownloadedAllBtn = view.findViewById(R.id.layout_downloaded_all_btn);

        mDownloadAdapter = new HorizontalCallFlashAdapter(getActivity(), mDownloadData);
        mRvDownload.setLayoutManager(new LinearLayoutManager(getActivity(), GridLayoutManager.HORIZONTAL, false));
        mRvDownload.addItemDecoration(new HorizontalCallFlashMarginDecoration());
        mRvDownload.setHasFixedSize(true);
        mRvDownload.setNestedScrollingEnabled(false);
        mRvDownload.setAdapter(mDownloadAdapter);
    }

    private void listener() {
        mLayoutCollectAllBtn.setOnClickListener(this);
        mLayoutDownloadedAllBtn.setOnClickListener(this);
        mLayoutSetRecordAllBtn.setOnClickListener(this);
    }

    private void setCurrentCallFlashBg() {
        mCurrentCallFlashInfo = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, CallFlashInfo.class);
        if (mCurrentCallFlashInfo != null) {
            mGvBgCurrent.setVisibility(View.VISIBLE);
            mLayoutNoCallFlash.setVisibility(View.GONE);
            if (mCurrentCallFlashInfo.isOnlionCallFlash) {
                mGvBgCurrent.showImageWithThumbnail(mCurrentCallFlashInfo.img_vUrl, mCurrentCallFlashInfo.thumbnail_imgUrl);
            } else {
                mGvBgCurrent.showImage(mCurrentCallFlashInfo.imgResId);
            }

        } else {
            mGvBgCurrent.setVisibility(View.GONE);
            mLayoutNoCallFlash.setVisibility(View.VISIBLE);
        }
    }

    private void setDownload() {
        List<CallFlashInfo> dataList = CallFlashManager.getInstance().getDownloadedCallFlash();
        if (mDownloadData == null) {
            mDownloadData = new ArrayList<>();
        }
        if (dataList != null && dataList.size() > 0) {
            LogUtil.d(TAG, "setDownload dataList:" + dataList.size());
//            if (mCurrentCallFlashInfo != null && dataList.contains(mCurrentCallFlashInfo)) {
//                dataList.remove(mCurrentCallFlashInfo);
//            }
            mDownloadData.clear();
            mDownloadData.addAll(dataList);
        }

        if (mDownloadData.size() > 0) {
            mLayoutDownloaded.setVisibility(View.VISIBLE);
            mDownloadAdapter.notifyDataSetChanged();
        } else {
            mLayoutDownloaded.setVisibility(View.GONE);
        }
    }

    private void setCollect() {
        List<CallFlashInfo> dataList = CallFlashManager.getInstance().getCollectionCallFlash();
        if (mCollectData == null) {
            mCollectData = new ArrayList<>();
        }
        if (dataList != null && dataList.size() > 0) {
            LogUtil.d(TAG, "setCollect dataList:" + dataList.size());
//            if (mCurrentCallFlashInfo != null && dataList.contains(mCurrentCallFlashInfo)) {
//                dataList.remove(mCurrentCallFlashInfo);
//            }
            mCollectData.clear();
            mCollectData.addAll(dataList);
        }

        if (mCollectData.size() > 0) {
            mLayoutCollect.setVisibility(View.VISIBLE);
            mCollectAdapter.notifyDataSetChanged();
        } else {
            mLayoutCollect.setVisibility(View.GONE);
        }
    }

    private void setCallFlashRecord() {
        List<CallFlashInfo> dataList = CallFlashManager.getInstance().getSetRecordCallFlash();
        if (mSetRecordData == null) {
            mSetRecordData = new ArrayList<>();
        }
        if (dataList != null && dataList.size() > 0) {
            LogUtil.d(TAG, "setCallFlashRecord dataList:" + dataList.size());
//            if (mCurrentCallFlashInfo != null && dataList.contains(mCurrentCallFlashInfo)) {
//                dataList.remove(mCurrentCallFlashInfo);
//            }
            mSetRecordData.clear();
            mSetRecordData.addAll(dataList);
        }

        if (mSetRecordData.size() > 0) {
            mLayoutSetRecord.setVisibility(View.VISIBLE);
            mSetRecordAdapter.notifyDataSetChanged();
        } else {
            mLayoutSetRecord.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.gv_bg_current:
                if (mCurrentCallFlashInfo != null) {
                    ActivityBuilder.toCallFlashDetail(getActivity(), mCurrentCallFlashInfo, false);
                }
                break;
            case R.id.layout_collect_all_btn:
                ActivityBuilder.toCallFlashList(getActivity(), CallFlashDataType.CALL_FLASH_DATA_COLLECTION);
                break;
            case R.id.layout_downloaded_all_btn:
                ActivityBuilder.toCallFlashList(getActivity(), CallFlashDataType.CALL_FLASH_DATA_DOWNLOADED);
                break;
            case R.id.layout_set_record_all_btn:
                ActivityBuilder.toCallFlashList(getActivity(), CallFlashDataType.CALL_FLASH_DATA_SET_RECORD);
                break;
        }
    }
}
