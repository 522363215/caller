package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.support.v13.app.ActivityCompat;
import android.text.TextUtils;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;

/**
 * Created by ChenR on 2018/7/14.
 */

public class CallUtils {

    private static Context mAppContext = ApplicationEx.getInstance();

    @TargetApi(23)
    public static int getCallTypeForEndCall(String number){
        int re_call_type = -3;

        if (mAppContext == null) {
            mAppContext = ApplicationEx.getInstance();
        }

        if(mAppContext != null && !TextUtils.isEmpty(number)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(ApplicationEx.getInstance(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    return re_call_type;
                }
            }
            Context context = ApplicationEx.getInstance();
            final String[] projection = null;
            final String[] selectionArgs = new String[]{number};
            final String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";
            Cursor cursor = null;
            final String selection = " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(" + CallLog.Calls.NUMBER + ",')','')" + ",' ','')" + ",'(','')" + ",'-','') " + ",'–','') Like ?";
            long end_call_show_time = System.currentTimeMillis();
            try {
                cursor = context.getContentResolver().query(
                        Uri.parse("content://call_log/calls"),
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
                while (cursor.moveToNext()) {
                    String callLogID = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls._ID));
                    String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                    String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
                    String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
                    String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));

                    LogUtil.d("getCallTypeForEndCall", "getCallTypeForEndCall number: "+number+", new:"+isCallNew+", is calltype: "+callType+", date: "+ Stringutil.getTimeString(Long.valueOf(callDate)));
                    long call_time = Long.valueOf(callDate);

                    if(end_call_show_time - call_time < 60 * 1000){
                        re_call_type = Integer.valueOf(callType);//1 incomming, 2 outgoing, 3 missed, 4 voice, 5 reject
                        LogUtil.d("getCallTypeForEndCall", "number: "+number+", date: "+Stringutil.getTimeString(Long.valueOf(callDate))+", call_type"+callType);
                        break;
                    }

//                    if(Integer.parseInt(callType) == CallLog.Calls.MISSED_TYPE){
//                        if(end_call_show_time - call_time < 60 * 1000){
//                            LogUtil.d("isMissedCall", "isMissedCall number missed: "+number+", date: "+Stringutil.getTimeString(Long.valueOf(callDate)));
//                            break;
//                        }
//                    }
                }
            } catch (Exception ex) {
                LogUtil.e("getCallTypeForEndCall", "getCallTypeForEndCall exception: "+ ex.getMessage());
            } finally {
                if(cursor!=null) {
                    cursor.close();
                }
            }
        }
        return re_call_type;
    }

    public static int getCallLogType(String number) {
        int calllogType = -1;
        String nationalNumber = NumberUtil.getNumberByPattern(number);
        String[] project = null;
        Uri uri = CallLog.Calls.CONTENT_URI;
        project = new String[]{
                CallLog.Calls.DATE,//日期
                CallLog.Calls.DURATION,//通话时长
                CallLog.Calls.TYPE,//通话类型
        };
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(mAppContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return calllogType;
            }
        }
        Cursor cursor = null;
        String selection = " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(" + CallLog.Calls.NUMBER + ",')','')" + ",' ','')" + ",'(','')" + ",'-','') " + ",'–','') Like ?";
        try {
            cursor = mAppContext.getContentResolver().query(uri, project, selection, new String[]{"%" + nationalNumber}, CallLog.Calls.DEFAULT_SORT_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(2) == CallLog.Calls.INCOMING_TYPE) {
                        if (cursor.getInt(1) > 0) {
                            calllogType = CallLog.Calls.INCOMING_TYPE;
                        } else {
                            calllogType = CallLog.Calls.REJECTED_TYPE;
                        }
                    } else if (cursor.getInt(2) == CallLog.Calls.OUTGOING_TYPE) {
                        calllogType = CallLog.Calls.OUTGOING_TYPE;
                    } else if (cursor.getInt(2) == CallLog.Calls.MISSED_TYPE) {
                        calllogType = CallLog.Calls.MISSED_TYPE;
                    } else if (cursor.getInt(2) == CallLog.Calls.REJECTED_TYPE) {
                        calllogType = CallLog.Calls.REJECTED_TYPE;
                    }
                    break;
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return calllogType;
    }

}
