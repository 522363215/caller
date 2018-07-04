package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog.PermissionDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventNoPermission;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import event.EventBus;

/**
 * Created by zhq on 2017/1/3.
 */
public class PermissionUtils {
    private static final String TAG = PermissionUtils.class.getSimpleName();
    //权限
    public static final String PERMISSION_WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";
    public static final String PERMISSION_READ_CALL_LOG = "android.permission.READ_CALL_LOG";
    public static final String PERMISSION_READ_CONTACTS = "android.permission.READ_CONTACTS";
    public static final String PERMISSION_WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";
    public static final String PERMISSION_CALL_PHONE = "android.permission.CALL_PHONE";
    public static final String PERMISSION_READ_SMS = "android.permission.READ_SMS";
    public static final String PERMISSION_READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    public static final String PERMISSION_INTERNET = "android.permission.INTERNET";


    //request code
    public static final int CODE_WRITE_CALL_LOG = 0;
    public static final int CODE_READ_CALL_LOG = 1;
    public static final int CODE_READ_CONTACTS = 2;
    public static final int CODE_WRITE_CONTACTS = 3;
    public static final int CODE_CALL_PHONE = 4;
    public static final int CODE_READ_SMS = 5;
    public static final int CODE_READ_PHONE_STATE = 6;
    public static final int CODE_INTERNET = 7;
    public static final int CODE_MULTI_PERMISSION = 200;//一次申请多个权限的requestcode
    public static final int CODE_REQUEST_PERMISSION_SETTING = 201;//带返回结果的跳转到系统权限设置界面的请求码
    public static final int CODE_CALLLOG_PERMISSION = 202;
    public static final int CODE_CONTACT_PERMISSION = 203;
    public static final int CODE_SMS_PERMISSION = 204;
    public static final int CODE_PHONE_PERMISSION = 205;
    public static final int CODE_EXTERNAL_STORAGE_PERMISSION = 206;
    public static final int CODE_CAMERA = 207;

    public static final int CODE_ALERT_WINDOW_PERMISSION = 301; // incoming call dialog

    private static PermissionDialog mPermissionDialog;

    public interface PermissionGrant {
        void onPermissionGranted(int requestCode);

        void onPermissionNotGranted(int requestCode);
    }

    //***********************************************请求权限 start***********************************************/

    /**
     * 一次申请多个不同组权限
     */
    public static void requestMultiPermissions(final Activity activity, String[] requestPermissions, PermissionGrant grant, int requestCode) {
        ArrayList<String> notGrandedPermissions = getNoGrantedPermission(activity, requestPermissions, requestCode);

        if (notGrandedPermissions == null) {
            LogUtil.d(TAG, "权限集合为空");
            return;
        }

        if (notGrandedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(activity, notGrandedPermissions.toArray(new String[notGrandedPermissions.size()]), requestCode);
        }

        if (notGrandedPermissions.size() == 0) {
            //代表全部权限都同意了
            grant.onPermissionGranted(requestCode);
        }
    }

    /**
     * @param activity
     * @param requestPermissions
     * @param requestCode
     */
    private static ArrayList<String> getNoGrantedPermission(Activity activity, String[] requestPermissions, int requestCode) {
        ArrayList<String> notGrantedPermissionsList = new ArrayList<>();
        for (int i = 0; i < requestPermissions.length; i++) {
            String requestPermission = requestPermissions[i];
            //TODO checkSelfPermission
            boolean checkSelfPermission = false;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission) == PackageManager.PERMISSION_GRANTED;
            } catch (RuntimeException e) {
                LogUtil.e(TAG, "RuntimeException:" + e.getMessage());
                return null;
            }

            if (checkSelfPermission || Build.VERSION.SDK_INT < 23) {
                // TODO: 2017/4/5 授权成功
                if (SystemInfoUtil.isMiui() || RomUtils.checkIsMiuiRom()) {
                    boolean isMiAllowed = (PermissionUtils.checkOp(activity, AppOpsManager.OPSTR_READ_CONTACTS)
                            && PermissionUtils.checkOp(activity, AppOpsManager.OPSTR_READ_SMS)
                            && PermissionUtils.checkOp(activity, AppOpsManager.OPSTR_READ_CALL_LOG));
                    LogUtil.d("perm_check", "onPermissionGranted mi: " + isMiAllowed);
                    if (!isMiAllowed) {
                        notGrantedPermissionsList.add(requestPermission);
                        if (requestPermission == Manifest.permission.READ_CALL_LOG) {
                            PreferenceHelper.setBoolean(PreferenceHelper.PREF_KEY_IS_RETURN_FROM_SETTING_ACTIIVTY, false);
                        }
                        openSettingActivity(activity);
                        return null;
                    }
                }
            } else {
                // TODO: 2017/4/5 授权失败
                notGrantedPermissionsList.add(requestPermission);
                if (requestPermission == Manifest.permission.READ_CALL_LOG) {
                    PreferenceHelper.setBoolean(PreferenceHelper.PREF_KEY_IS_RETURN_FROM_SETTING_ACTIIVTY, false);
                }
            }
        }
        return notGrantedPermissionsList;
    }
    //***********************************************请求权限  end***********************************************/


    //***********************************************请求结果 start***********************************************/

    /**
     * @param activity
     * @param requestCode  Need consistent with requestPermission
     * @param permissions
     * @param grantResults
     */
    public static void requestPermissionsResult(final Activity activity, final int requestCode, @NonNull String[] permissions,
                                                @NonNull int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }

        requestMultiResult(activity, permissions, grantResults, permissionGrant, requestCode);
        LogUtil.d(TAG, "grantResults:" + grantResults.length + ",permissions:" + permissions);

    }

    private static void requestMultiResult(Activity activity, String[] permissions, int[] grantResults, PermissionGrant permissionGrant, int requestCode) {
        if (activity == null) {
            return;
        }
        ArrayList<String> notGranted = new ArrayList<>();
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {//授权成功

                    if (SystemInfoUtil.isMiui() || RomUtils.checkIsMiuiRom()) {
                        boolean isMiAllowed = (PermissionUtils.checkOp(activity, AppOpsManager.OPSTR_READ_CONTACTS)
                                && PermissionUtils.checkOp(activity, AppOpsManager.OPSTR_READ_SMS)
                                && PermissionUtils.checkOp(activity, AppOpsManager.OPSTR_READ_CALL_LOG));
                        LogUtil.d("perm_check", "onPermissionGranted mi: " + isMiAllowed);
                        if (!isMiAllowed) {
                            notGranted.add(permissions[i]);
                            openSettingActivity(activity);
                            return;
                        }
                    }

                } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {//点击拒绝授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {//点击拒绝，再次弹出
                        notGranted.add(permissions[i]);
                        LogUtil.d(TAG, "neveraskagain  one permission:" + permissions[i]);
                    } else { // 选择不再询问，并点击拒绝，弹出提示框
                        notGranted.add(permissions[i]);
                        LogUtil.d(TAG, "neveraskagain permission:" + permissions[i]);
                        LogUtil.d(TAG, "openSettingActivity");
                        openSettingActivity(activity);
                        return;
                    }
                }
            }
        }

        if (notGranted.size() == 0) {
            LogUtil.d(TAG, "所有权限获取成功");
            permissionGrant.onPermissionGranted(requestCode);
        } else {
            LogUtil.d(TAG, "权限没有获取成功");
            permissionGrant.onPermissionNotGranted(requestCode);
//            ActivityCompat.requestPermissions(
//                    activity,
//                    permissions,
//                    CODE_MULTI_PERMISSION);
        }

    }
    //***********************************************请求结果 end***********************************************/


    //***********************************************显示对话框 start***************************************************/
    public static void openSettingActivity(final Activity activity) {
        LogUtil.d(TAG, "跳转到设置界面");
        showMessageOKCancel(activity, new PermissionDialog.OkListener() {
            @Override
            public void okAction() {
                mPermissionDialog.dismiss();
                if (!SystemInfoUtil.isMiui()) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    LogUtil.d(TAG, "getPackageName(): " + activity.getPackageName());
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivityForResult(intent, CODE_REQUEST_PERMISSION_SETTING);
                } else {
                    Intent intent = new Intent();
                    intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
//                    ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//                    intent.setComponent(componentName);
                    intent.putExtra("extra_pkgname", activity.getPackageName());
                    activity.startActivityForResult(intent, CODE_REQUEST_PERMISSION_SETTING);
                }

            }
        });
    }

    private static void showMessageOKCancel(final Activity context, PermissionDialog.OkListener okListener) {
        /*new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();*/
        mPermissionDialog = new PermissionDialog(context);
        mPermissionDialog.setCanceledOnTouchOutside(true);
        mPermissionDialog.setOkListener(okListener);
        if (!mPermissionDialog.isShowing()) {
            mPermissionDialog.show();
        }
        mPermissionDialog.setCancelListener(new PermissionDialog.CancelListener() {
            @Override
            public void cancelAction() {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    EventBus.getDefault().post(new EventNoPermission());
                }
            }
        });
    }
    //***********************************************显示对话框 end**************************************************/

    public static boolean hasPermission(Activity activity, String permission) {
        if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * @param context
     * @param permission_opstr, AppOpsManager.OPSTR_READ_CONTACTS,
     * @return
     */
    public static boolean checkOp(Context context, String permission_opstr) {
        boolean is = true;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                is = false;
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int checkOp = appOpsManager.checkOp(permission_opstr, android.os.Process.myUid(), context.getPackageName());
                LogUtil.d("perm_check", "checkOp:" + checkOp);
                switch (checkOp) {
                    case AppOpsManager.MODE_ALLOWED:
                        is = true;
                        LogUtil.d("perm_check", "AppOpsManager.MODE_ALLOWED ：allowed.");
                        break;
                    case AppOpsManager.MODE_IGNORED:
                        LogUtil.d("perm_check", "AppOpsManager.MODE_IGNORED：not allowed.");
                        break;
                    case AppOpsManager.MODE_DEFAULT:
                        LogUtil.d("perm_check", "AppOpsManager.MODE_DEFAULT");
                        break;
                    case AppOpsManager.MODE_ERRORED:
                        LogUtil.d("perm_check", "AppOpsManager.MODE_ERRORED");
                        break;
                    case 4:
                        LogUtil.d("perm_check", "AppOpsManager.OTHER：need asked.");
                        break;
                }
            }
        } catch (Exception e) {
            LogUtil.e("perm_check", "checkOp exception: " + e.getMessage());
        }
        return is;
    }
}
