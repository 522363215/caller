package com.md.flashset.volume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import java.lang.ref.WeakReference;

public class VolumeChangeObserver {
    private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    public interface VolumeChangeListener {
        /**
         * 系统媒体音量变化
         *
         * @param musicVolume
         */
        void onMusicVolumeChanged(int musicVolume);

        /**
         * 系统铃声音量变化
         *
         * @param ringVolume
         */
        void onRingVolumeChanged(int ringVolume);
    }

    private VolumeChangeListener mVolumeChangeListener;
    private VolumeBroadcastReceiver mVolumeBroadcastReceiver;
    private Context mContext;
    private AudioManager mAudioManager;
    private boolean mRegistered = false;

    public VolumeChangeObserver(Context context) {
        mContext = context;
        mAudioManager = (AudioManager) context.getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 获取当前音量
     *
     * @return
     */
    public int getStreamVolume(int streamType) {
        return mAudioManager != null ? mAudioManager.getStreamVolume(streamType) : -1;
    }

    /**
     * 获取系统最大音量
     *
     * @return
     */
    public int getMaxStreamVolume(int streamType) {
        return mAudioManager != null ? mAudioManager.getStreamMaxVolume(streamType) : 15;
    }

    /**
     * 修改音量
     *
     * @return
     */
    public void setStreamVolume(int streamType, int volume) {
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(streamType, volume, 0);
        }
    }

    public int getRingerMode() {
        return mAudioManager != null ? mAudioManager.getRingerMode() : AudioManager.RINGER_MODE_NORMAL;
    }

    public void setRingerMode(int ringerMode) {
        if (mAudioManager != null) {
            mAudioManager.setRingerMode(ringerMode);
        }
    }

    public AudioManager getAudioManager() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) mContext.getApplicationContext()
                    .getSystemService(Context.AUDIO_SERVICE);
        }
        return mAudioManager;
    }

    public VolumeChangeListener getVolumeChangeListener() {
        return mVolumeChangeListener;
    }

    public void setVolumeChangeListener(VolumeChangeListener volumeChangeListener) {
        this.mVolumeChangeListener = volumeChangeListener;
    }

    /**
     * 注册音量广播接收器
     *
     * @return
     */
    public void registerReceiver() {
        mVolumeBroadcastReceiver = new VolumeBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(VOLUME_CHANGED_ACTION);
        mContext.registerReceiver(mVolumeBroadcastReceiver, filter);
        mRegistered = true;
    }

    /**
     * 解注册音量广播监听器，需要与 registerReceiver 成对使用
     */
    public void unregisterReceiver() {
        if (mRegistered) {
            try {
                mContext.unregisterReceiver(mVolumeBroadcastReceiver);
                mVolumeChangeListener = null;
                mRegistered = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class VolumeBroadcastReceiver extends BroadcastReceiver {
        private WeakReference<VolumeChangeObserver> mObserverWeakReference;

        public VolumeBroadcastReceiver(VolumeChangeObserver volumeChangeObserver) {
            mObserverWeakReference = new WeakReference<>(volumeChangeObserver);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (VOLUME_CHANGED_ACTION.equals(intent.getAction())) {
                VolumeChangeObserver observer = mObserverWeakReference.get();
                if (observer != null) {
                    VolumeChangeListener listener = observer.getVolumeChangeListener();
                    int type = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1);
                    int volume = observer.getStreamVolume(type);
                    switch (type) {
                        case AudioManager.STREAM_MUSIC:
                            if (listener != null) {
                                if (volume >= 0) {
                                    listener.onMusicVolumeChanged(volume);
                                }
                            }
                            break;
                        case AudioManager.STREAM_RING:
                            if (listener != null) {
                                if (volume >= 0) {
                                    listener.onRingVolumeChanged(volume);
                                }
                            }
                            break;
                    }
                }
            }
        }
    }
}