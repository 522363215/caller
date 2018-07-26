package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.md.block.core.service.CallerNotificationListenerService;

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
    private static PermissionDialog mPermissionDialog;

    //request code
    public static final int REQUEST_CODE_ALL_PERMISSION_ = 1024;
    private static final int REQUEST_CODE_REQUEST_PERMISSION_SETTING = 201;//带返回结果的跳转到系统权限设置界面的请求码
    public static final int REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION = 206;
    public static final int REQUEST_CODE_PHONE_STATE_PERMISSION = 207;
    public static final int REQUEST_CODE_RECEIVE_SMS_PERMISSION = 208;
    public static final int REQUEST_CODE_READ_CONTACT_PERMISSION = 209;

    //特殊权限code
    public static final int REQUEST_CODE_OVERLAY_PERMISSION = 1712;
    public static final int REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS = 1713;
    public static final int REQUEST_CODE_WRITE_SETTINGS = 1715;
    public static final int REQUEST_CODE_APP_DETAILS_SETTINGS = 1716;


    public static final String PERMISSION_OVERLAY = "permission_overlay";//悬浮窗权限
    public static final String PERMISSION_NOTIFICATION_POLICY_ACCESS = "permission_notification_policy_acCess";//通知服务权限

    public static final String[] PERMISSION_GROUP_PHONE = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG};
    public static final String[] PERMISSION_GROUP_SMS = new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS};
    public static final String[] PERMISSION_GROUP_CONTACT = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    public static final String[] PERMISSION_GROUP_STORAGE = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

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
                            PreferenceHelper.putBoolean(PreferenceHelper.PREF_KEY_IS_RETURN_FROM_SETTING_ACTIIVTY, false);
                        }
                        openSettingActivity(activity);
                        return null;
                    }
                }
            } else {
                // TODO: 2017/4/5 授权失败
                notGrantedPermissionsList.add(requestPermission);
                if (requestPermission == Manifest.permission.READ_CALL_LOG) {
                    PreferenceHelper.putBoolean(PreferenceHelper.PREF_KEY_IS_RETURN_FROM_SETTING_ACTIIVTY, false);
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
                    activity.startActivityForResult(intent, REQUEST_CODE_REQUEST_PERMISSION_SETTING);
                } else {
                    Intent intent = new Intent();
                    intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
//                    ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//                    intent.setComponent(componentName);
                    intent.putExtra("extra_pkgname", activity.getPackageName());
                    activity.startActivityForResult(intent, REQUEST_CODE_REQUEST_PERMISSION_SETTING);
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

    public static boolean hasPermission(Context context, String permission) {
        if (PERMISSION_OVERLAY.equals(permission)) {
            return SpecialPermissionsUtil.canDrawOverlays(context);
        }

        if (PERMISSION_NOTIFICATION_POLICY_ACCESS.equals(permission)) {
            return SpecialPermissionsUtil.isHaveNotificationPolicyAccess(context);
        }

        if (Manifest.permission_group.PHONE.equals(permission)) {
            return hasPermissions(context, PermissionUtils.PERMISSION_GROUP_PHONE);
        }

        if (Manifest.permission_group.CONTACTS.equals(permission)) {
            return hasPermissions(context, PermissionUtils.PERMISSION_GROUP_CONTACT);
        }

        if (Manifest.permission_group.SMS.equals(permission)) {
            return hasPermissions(context, PermissionUtils.PERMISSION_GROUP_SMS);
        }

        if (Manifest.permission_group.STORAGE.equals(permission)) {
            return hasPermissions(context, PermissionUtils.PERMISSION_GROUP_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
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

    public static void toggleNotificationListenerService(Context context) {
        if (context != null) {
            try {
                ComponentName thisComponent = new ComponentName(context, CallerNotificationListenerService.class);
                PackageManager pm = context.getPackageManager();
                pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                LogUtil.d("notify_answer", "permissionUtil toggleNotificationListenerService() called");
                PreferenceHelper.putBoolean("is_start_NotifiListernerOnFirst", true);
            } catch (Exception e) {
                LogUtil.e("notify_answer", "permissionUtil toggleNotificationListenerService exception: " + e.getMessage());
            }

        }
    }


    public static boolean isHaveAllPermission(Context context) {
        boolean canDrawOverlays = SpecialPermissionsUtil.canDrawOverlays(context);
        boolean isHaveNotificationPolicyAccess = SpecialPermissionsUtil.isHaveNotificationPolicyAccess(context);
        boolean isHavePhone = hasPermissions(context, PERMISSION_GROUP_PHONE);
        boolean isHaveSMS = hasPermissions(context, PERMISSION_GROUP_SMS);
        boolean isHaveStorage = hasPermissions(context, PERMISSION_GROUP_STORAGE);
        boolean isHaveContact = hasPermissions(context, PERMISSION_GROUP_CONTACT);
        return canDrawOverlays && isHaveNotificationPolicyAccess && isHavePhone && isHaveSMS && isHaveStorage && isHaveContact;
    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }
}
