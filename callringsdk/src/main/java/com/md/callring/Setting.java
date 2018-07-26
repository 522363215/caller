package com.md.callring;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

import static android.content.ContentValues.TAG;

public class Setting {
    private static String mUrl;
    private static Context mContext;

    public static void setmUrl(String url) {
        mUrl = url;
    }

    public static void setting(Context context) {
        // 外部调用者传来的context
        mContext = context;
        // 设置歌曲路径
        File filePath = new File(mUrl);
        ContentValues values = new ContentValues();
        // The data stream for the file
        values.put(MediaStore.MediaColumns.DATA, filePath.getAbsolutePath());
        // The title of the content
        values.put(MediaStore.MediaColumns.TITLE, filePath.getName());
        // The MIME type of the file
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        // values.put(MediaStore.AudioUtil.Media.ARTIST, "Madonna");
        // values.put(MediaStore.AudioUtil.Media.DURATION, 230);
        // 来电铃声
        // 第二个参数若是true则会在铃音库中显示
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        // 通知/短信铃声
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        // 闹钟铃声
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        // 系统铃声
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        // Insert it into the database
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(filePath
                .getAbsolutePath());
        // 下面这一句很重要
        mContext.getContentResolver().delete(
                uri,
                MediaStore.MediaColumns.DATA + "=\""
                        + filePath.getAbsolutePath() + "\"", null);
        Uri newUri = mContext.getContentResolver().insert(uri, values);
        RingtoneManager.setActualDefaultRingtoneUri(mContext,
                RingtoneManager.TYPE_RINGTONE, newUri);
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
               Log.d(TAG, "setRing deleteId:" + deleteId + ",path:" + sdfile.getAbsolutePath());
                newUri = context.getContentResolver().insert(uri, values);
                Log.d(TAG, "setRing newUri:" + newUri);
            } else {
//                int update = context.getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=\"" + getSystemRingtonePath(context) + "\"", null);
//                newUri = ContentUris.withAppendedId(uri, Long.valueOf(deleteId));
//                Log.d(TAG, "setRing update:" + update + ",newUri:" + newUri);
            }

        } catch (Exception e) {
            Log.e(TAG, "setRing e:" + e.getMessage());
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
//
        }
    }
}
