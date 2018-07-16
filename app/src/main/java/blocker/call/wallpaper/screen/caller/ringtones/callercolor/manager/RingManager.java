package blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.md.flashset.View.FlashLed;
import com.md.flashset.helper.CallFlashPreferenceHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

/**
 * Created by Zhq on 2017/12/16.
 */

public class RingManager {
    private static final String TAG = "RingManager";
    private static AssetManager mAssetManager;
    private static MediaPlayer mPlayer;
    private static AudioManager mAudioManager;
    private static long mLastSaveTime;

    private static String getSystemRingtonePath(Context context) {
        Cursor cursor = null;
        String data = null;
        try {
            Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
            if (null == uri) return null;
            final String scheme = uri.getScheme();

            if (scheme == null)
                data = uri.getPath();
            else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                data = uri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                        if (index > -1) {
                            data = cursor.getString(index);
                        }
                    }
                    cursor.close();
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "getSystemRingtonePath exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {

                }

            }
        }
        LogUtil.d(TAG, "getSystemRingtonePath data:" + data);
        return data;
    }

    public static void saveCurrentRingPath(Context context) {
        //保存铃声的路径
        String ringPath = RingManager.getSystemRingtonePath(context);
        LogUtil.d(TAG, "saveCurrentRingPath ringPath:" + ringPath);
        if (isSaveRing(ringPath)) {
            PreferenceHelper.putString(PreferenceHelper.PREF_LAST_RING_PATH, ringPath);
        }
    }

    private static boolean isSaveRing(String ringPath) {
        if (!TextUtils.isEmpty(ringPath)) {
            if (ringPath.endsWith("monkey.mp3") || ringPath.endsWith("santa_show.mp3") || ringPath.endsWith("snowing.mp3")) {
                return false;
            }
        }
        return true;
    }

    private static void setRingNone(Context context) {
        try {
            String path = getSystemRingtonePath(context);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
            Uri newUri = null;
            String deleteId = "";
            try {
                Cursor cursor = context.getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    deleteId = cursor.getString(cursor.getColumnIndex("_id"));
                    cursor.close();
                }
                LogUtil.d(TAG, "setRingNone setRing deleteId:" + deleteId);

                context.getContentResolver().delete(uri,
                        MediaStore.MediaColumns.DATA + "=\"" + path + "\"", null);
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, null);
            } catch (Exception e) {
                LogUtil.e(TAG, "setRing e:" + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reductionRing(Context context) {
        try {
            //set_ring_tone_monkey true代表设置过铃声
            if (!PreferenceHelper.getBoolean("set_ring_tone_monkey", false)) {
                return;
            }
            String lastRingPath = PreferenceHelper.getString(PreferenceHelper.PREF_LAST_RING_PATH, "");
            LogUtil.d(TAG, "reductionRing lastRingPath:" + lastRingPath);
            if (TextUtils.isEmpty(lastRingPath)) {
                setRingNone(context);
            } else {
                if (!lastRingPath.endsWith("monkey.mp3")) {
                    setRing(context, RingtoneManager.TYPE_RINGTONE, lastRingPath, true);
                }
            }
            PreferenceHelper.putBoolean("set_ring_tone_monkey", false);
        } catch (Exception e) {
            LogUtil.d(TAG, "reductionRing e:" + e.getMessage());
        }
    }

    public static String getAssetsCacheFilePath(Context context, String fileName) {
        File cacheFile = new File(context.getExternalFilesDir(null), fileName);
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            LogUtil.e(TAG, "getAssetsCacheFilePath e:" + e.getMessage());
            e.printStackTrace();
        }
        return cacheFile.getAbsolutePath();
    }

    public static void playMusicInAsset(Context context, String musicName) {
        // 打开指定音乐文件,获取assets目录下指定文件的AssetFileDescriptor对象
        try {

            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
            } else {
                mPlayer.stop();
                mPlayer.reset();
            }

            if (mAssetManager == null) {
                mAssetManager = context.getAssets();
            }
            AssetFileDescriptor fileDescriptor = mAssetManager.openFd(musicName);
            mPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());


            mPlayer.prepare();
            mPlayer.setLooping(true);
            mPlayer.start();

            LogUtil.d("play_m", "musicName:" + musicName);


        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "playMusicInAsset e:" + e.getMessage());
        }
    }

    public static void stopMusicInAssetAll() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    public static void stopMusicInAsset() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    public static void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * 设置铃声
     *
     * @param type            RingtoneManager.TYPE_RINGTONE 来电铃声
     *                        RingtoneManager.TYPE_NOTIFICATION 通知铃声
     *                        RingtoneManager.TYPE_ALARM 闹钟铃声
     * @param path            下载下来的mp3全路径
     * @param isreductionRing true:还原铃声，false:设置铃声
     */
    public static void setRing(Context context, int type, String path, boolean isreductionRing) {
        RingManager.saveCurrentRingPath(context);
        Uri oldRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE); //系统当前  通知铃声
        Uri oldNotification = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION); //系统当前  通知铃声
        Uri oldAlarm = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM); //系统当前  闹钟铃声

        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = null;
        String deleteId = "";
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
            if (cursor.moveToFirst()) {
                deleteId = cursor.getString(cursor.getColumnIndex("_id"));
            }

            if (!isreductionRing) {
                context.getContentResolver().delete(uri,
                        MediaStore.MediaColumns.DATA + "=\"" + sdfile.getAbsolutePath() + "\"", null);
                LogUtil.d(TAG, "setRing deleteId:" + deleteId + ",path:" + sdfile.getAbsolutePath());
                newUri = context.getContentResolver().insert(uri, values);
                LogUtil.d(TAG, "setRing newUri:" + newUri);
            } else {
                int update = context.getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=\"" + getSystemRingtonePath(context) + "\"", null);
                newUri = ContentUris.withAppendedId(uri, Long.valueOf(deleteId));
                LogUtil.d(TAG, "setRing update:" + update + ",newUri:" + newUri);
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "setRing e:" + e.getMessage());
            e.printStackTrace();
        }

        if (newUri != null) {

            String ringStoneId = "";
            String notificationId = "";
            String alarmId = "";
            if (null != oldRingtoneUri) {
                ringStoneId = oldRingtoneUri.getLastPathSegment();
            }

            if (null != oldNotification) {
                notificationId = oldNotification.getLastPathSegment();
            }

            if (null != oldAlarm) {
                alarmId = oldAlarm.getLastPathSegment();
            }

            Uri setRingStoneUri;
            Uri setNotificationUri;
            Uri setAlarmUri;

            if (type == RingtoneManager.TYPE_RINGTONE || ringStoneId.equals(deleteId)) {
                setRingStoneUri = newUri;

            } else {
                setRingStoneUri = oldRingtoneUri;
            }

            if (type == RingtoneManager.TYPE_NOTIFICATION || notificationId.equals(deleteId)) {
                setNotificationUri = newUri;

            } else {
                setNotificationUri = oldNotification;
            }

            if (type == RingtoneManager.TYPE_ALARM || alarmId.equals(deleteId)) {
                setAlarmUri = newUri;

            } else {
                setAlarmUri = oldAlarm;
            }

            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, setRingStoneUri);
            PreferenceHelper.putBoolean("set_ring_tone_monkey", !isreductionRing);
            LogUtil.d(TAG, "setRing setActualDefaultRingtoneUri");
//            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, setNotificationUri);
//            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, setAlarmUri);

//            switch (type) {
//                case RingtoneManager.TYPE_RINGTONE:
//                    Toast.makeText(context, "设置来电铃声成功！", Toast.LENGTH_SHORT).show();
//                    break;
//                case RingtoneManager.TYPE_NOTIFICATION:
//                    Toast.makeText(context, "设置通知铃声成功！", Toast.LENGTH_SHORT).show();
//                    break;
//                case RingtoneManager.TYPE_ALARM:
//                    Toast.makeText(context, "设置闹钟铃声成功！", Toast.LENGTH_SHORT).show();
//                    break;
//            }
        }
    }

    public static boolean isTheRing(Context context, String musicName) {
        String path = getSystemRingtonePath(context);
        LogUtil.d(TAG, "isTheRing path:" + path);
        if (!TextUtils.isEmpty(path) && path.endsWith(musicName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否为Video的flash
     */
    public static boolean isVideoFlash(int flashType) {
        return flashType == FlashLed.FLASH_TYPE_MONKEY || flashType == FlashLed.FLASH_TYPE_DANCE_PIG
                || flashType == FlashLed.FLASH_TYPE_RAP;
    }

    /**
     * @param isSet      true:媒体音量大小设为当前铃声音量大小，false:还原媒体音量大小
     * @param isGradient true:声音渐变
     */
    public static void setVideoRingVolume(boolean isSet, boolean isGradient) {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) ApplicationEx.getInstance().getSystemService(Context.AUDIO_SERVICE);
        }
        assert mAudioManager != null;
        if (isSet) {
            //当前铃声音量大小
            int currentRingVoluem = PreferenceHelper.getInt(PreferenceHelper.PREF_CURRENT_RING_VOLUME, mAudioManager.getStreamVolume(AudioManager.STREAM_RING));
            int maxRingVoluem = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
            //当前媒体声音大小
            int currentMusicVoluem = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxMusicVoluem = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            //保存当前媒体音量大小
            PreferenceHelper.putInt(PreferenceHelper.PREF_CURRENT_MUSIC_VOLUEM, currentMusicVoluem);
            //将媒体音量设为铃声音量
            if (maxRingVoluem != 0) {
                if (isGradient) {
                    volumeGradient(0, (currentRingVoluem * maxMusicVoluem) / maxRingVoluem);
                } else {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (currentRingVoluem * maxMusicVoluem) / maxRingVoluem, 0);
                }
                LogUtil.d(TAG, "setVideoRingVolume set currentRingVoluem :" + currentRingVoluem + ",maxRingVoluem:" + maxRingVoluem +
                        ",currentMusicVoluem:" + currentMusicVoluem + ",maxMusicVoluem:" + maxMusicVoluem +
                        ",setAfterMusicVoluem:" + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        } else {
            int musicVoluem = PreferenceHelper.getInt(PreferenceHelper.PREF_CURRENT_MUSIC_VOLUEM, mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVoluem, 0);
            LogUtil.d(TAG, "setVideoRingVolume restore musicVoluem :" + musicVoluem +
                    "还原后MusicVoluem:" + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    }

    /**
     * @param isSet true:媒体音量大小设为0，false:还原媒体音量大小
     */
    public static void setVideoRingSilent(boolean isSet) {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) ApplicationEx.getInstance().getSystemService(Context.AUDIO_SERVICE);
        }
        assert mAudioManager != null;
        if (isSet) {
            //当前铃声音量大小
            int currentRingVoluem = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
            int maxRingVoluem = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
            //当前媒体声音大小
            int currentMusicVoluem = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxMusicVoluem = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            //保存当前媒体音量大小
            PreferenceHelper.putInt(PreferenceHelper.PREF_CURRENT_MUSIC_VOLUEM, currentMusicVoluem);
            //将媒体音量设为铃声音量
            if (maxRingVoluem != 0) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                LogUtil.d(TAG, "setVideoRingVolume set currentRingVoluem :" + currentRingVoluem + ",maxRingVoluem:" + maxRingVoluem +
                        ",currentMusicVoluem:" + currentMusicVoluem + ",maxMusicVoluem:" + maxMusicVoluem +
                        ",setAfterMusicVoluem:" + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        } else {
            int musicVoluem = PreferenceHelper.getInt(PreferenceHelper.PREF_CURRENT_MUSIC_VOLUEM, mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVoluem, 0);
            LogUtil.d(TAG, "setVideoRingVolume restore musicVoluem :" + musicVoluem +
                    "还原后MusicVoluem:" + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    }

    public static void volumeGradient(int start, final int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int volume = (int) animation.getAnimatedValue();
                try {
                    //此时可能 mediaPlayer 状态发生了改变,所以用try catch包裹,一旦发生错误,立马取消
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                } catch (Exception e) {
                    animation.cancel();
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, end, 0);
                }
            }
        });
        animator.start();
    }

    public static boolean isRingFlash(int mFlashType) {
        return false;
    }

    public static String getRingName(int flashType) {
        return "";
    }

    /**
     * 静音模式切换
     *
     * @param isSilent true:设为静音，false:恢复原状
     */
    public static void silentSwitch(boolean isSilent) {
        AudioManager audioManager = (AudioManager) ApplicationEx.getInstance().getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            if (isSilent) {
                //设置之前保存
                PreferenceHelper.putInt(PreferenceHelper.PREF_KEY_RING_MODE_BEFORE_SET_SILENT, audioManager.getRingerMode());
                LogUtil.d("RingManager:", "silentSwitch RINGING 已被静音 设置之前的RingMode:" + audioManager.getRingerMode());
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                audioManager.getStreamVolume(AudioManager.STREAM_RING);
            } else {
                int lastRingMode = PreferenceHelper.getInt(PreferenceHelper.PREF_KEY_RING_MODE_BEFORE_SET_SILENT, AudioManager.RINGER_MODE_NORMAL);
                audioManager.setRingerMode(lastRingMode);
                audioManager.getStreamVolume(AudioManager.STREAM_RING);
                Log.d("RingManager", "silentSwitch RINGING 取消静音 恢复lastRingMode:" + lastRingMode);
            }
        }
    }

    public static boolean isSilentMode() {
        AudioManager audioManager = (AudioManager) ApplicationEx.getInstance().getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        int mode = audioManager.getRingerMode();
        if (mode == AudioManager.RINGER_MODE_SILENT) {
            return true;
        }
        return false;
    }

    public static void playSystemRingtone(Context context, String number) {
        boolean isFlashSwitchOn = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
        if (isFlashSwitchOn) return;
        setVideoRingVolume(true, false);
        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.stop();
            mPlayer.reset();
        }
        try {
            mPlayer.setDataSource(context, uri);
            //准备一下(内存卡)
            mPlayer.prepare();
            mPlayer.setLooping(true);
            mPlayer.start();
        } catch (IOException e) {
            LogUtil.d(TAG, "playSystemRingtone e:" + e.getMessage());
        }

    }

    public static void stopSystemRingtone() {
        setVideoRingVolume(false, false);
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    public static void saveCurrentRingVolume() {
        //离上次保存超过5秒才保存，防止短时间内连续保存出错
        if (System.currentTimeMillis() - mLastSaveTime > 5 * 1000) {
            AudioManager audioManager = (AudioManager) ApplicationEx.getInstance().getBaseContext().getSystemService(Context.AUDIO_SERVICE);
            int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
            PreferenceHelper.putInt(PreferenceHelper.PREF_CURRENT_RING_VOLUME, currVolume);
            mLastSaveTime = System.currentTimeMillis();
            LogUtil.d("phoneStateReceiver", "save currVolume:" + currVolume);
        }
    }

    /**
     * 退出callflash设置界面后还原媒体铃声
     */
    public static void reStoreMusicVoluem() {
        AudioManager am = (AudioManager) ApplicationEx.getInstance().getBaseContext().getSystemService(Service.AUDIO_SERVICE);
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int lastMusicVolume = PreferenceHelper.getInt(PreferenceHelper.PREF_CURRENT_MUSIC_VOLUEM, currentVolume);
        if (lastMusicVolume != currentVolume) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, lastMusicVolume, 0);
        }
    }
}
