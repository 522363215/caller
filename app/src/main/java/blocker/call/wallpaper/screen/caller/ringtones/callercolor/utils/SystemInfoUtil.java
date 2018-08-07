package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by cattom on 12/5/2015.
 */
public class SystemInfoUtil {
    public static final String SYS_EMUI = "sys_emui";
    public static final String SYS_MIUI = "sys_miui";
    public static final String SYS_FLYME = "sys_flyme";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";
    private static final String MANUFACTURER_XIAOMI = "Xiaomi";//小米

    public static boolean isMiui() {
//        File file = new File("/system/build.prop");
//        try {
//            FileReader fis = new FileReader(file);
//            BufferedReader br = new BufferedReader(fis);
//            String tempString = "";
//            while ((tempString = br.readLine()) != null) {
//                if (tempString.toLowerCase().contains("ro.miui.ui.version.name")) {
//                    br.close();
//                    return true;
//                }
//            }
//            br.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String os_name = getSystem();
        if (!TextUtils.isEmpty(os_name) && os_name.equals(SYS_MIUI)) {
            return true;
        }
        return false;
    }

    public static String getSystem() {
        String SYS = "";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            if (!TextUtils.isEmpty(getSystemProperty(KEY_MIUI_VERSION_CODE, ""))
                    || !TextUtils.isEmpty(getSystemProperty(KEY_MIUI_VERSION_NAME, ""))
                    || !TextUtils.isEmpty(getSystemProperty(KEY_MIUI_INTERNAL_STORAGE, ""))) {
                SYS = SYS_MIUI;//小米
            } else if (!TextUtils.isEmpty(getSystemProperty(KEY_EMUI_API_LEVEL, ""))
                    || !TextUtils.isEmpty(getSystemProperty(KEY_EMUI_VERSION, ""))
                    || !TextUtils.isEmpty(getSystemProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, ""))) {
                SYS = SYS_EMUI;//华为
            }
//            else if (getMeizuFlymeOSFlag().toLowerCase().contains("flyme")) {
//                SYS = SYS_FLYME;//魅族
//            }
            LogUtil.d("miui_setting", "getSystem SYS above 25: " + SYS);
            return SYS;
        } else {
            try {
                Properties prop = new Properties();
                prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
                if (prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                        || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                        || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null) {
                    SYS = SYS_MIUI;//小米
                } else if (prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
                        || prop.getProperty(KEY_EMUI_VERSION, null) != null
                        || prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null) {
                    SYS = SYS_EMUI;//华为
                }
//                else if (getMeizuFlymeOSFlag().toLowerCase().contains("flyme")) {
//                    SYS = SYS_FLYME;//魅族
//                }
                LogUtil.d("miui_setting", "getSystem SYS under 25: " + SYS);
            } catch (Exception e) {
                LogUtil.e("miui_setting", "getSystem exception: " + e.getMessage());
                return SYS;
            } finally {
                return SYS;
            }
        }
    }

    private static int op(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                Object localObject = context.getSystemService("appops");
                Class localClass = localObject.getClass();
                Class[] arrayOfClass = new Class[3];
                arrayOfClass[0] = Integer.TYPE;
                arrayOfClass[1] = Integer.TYPE;
                arrayOfClass[2] = String.class;
                Method localMethod = localClass.getMethod("checkOp",
                        arrayOfClass);
                Object[] arrayOfObject = new Object[3];
                arrayOfObject[0] = Integer.valueOf(24);
                arrayOfObject[1] = Integer.valueOf(Binder.getCallingUid());
                arrayOfObject[2] = context.getPackageName();
                int j = ((Integer) localMethod.invoke(localObject,
                        arrayOfObject)).intValue();
//		        if (j == 0)
//		            return 1;
                return j;
            } catch (Throwable localThrowable2) {
                return -1;
            }
        } else {//19以下的版本的特殊处理
            ApplicationInfo localApplicationInfo1 = null;
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if (!"blocker.call.wallpaper.screen.caller.ringtones.callercolor".equalsIgnoreCase(cn.getPackageName())) {
                try {
                    ApplicationInfo localApplicationInfo2 = context.getPackageManager()
                            .getApplicationInfo(context.getPackageName(), 0);
                    localApplicationInfo1 = localApplicationInfo2;
                    if (localApplicationInfo1 != null) {
                        if ((0x8000000 & localApplicationInfo1.flags) == 0)
                            return 0;
                    }
                } catch (Throwable localThrowable1) {
                    localApplicationInfo1 = null;
                }
            }
            return -1;
        }
    }

    public static boolean isSamsungPhone() {
        String manufacturer;
        try {
            manufacturer = Build.MANUFACTURER.toLowerCase();
        } catch (Exception e) {
            manufacturer = "";
        }
        return manufacturer.equals("samsung");
    }

    private static boolean checkOp(Context context) {
        try {
            return AppOpsManager.MODE_ALLOWED == op(context);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMiuiPopupAllowed(Context context) {
        if (isMiui()) {
            return checkOp(context);
        } else {
            return false;
        }
    }

    public static boolean isMiPad() {
        File file = new File("/system/build.prop");
        try {
            FileReader fis = new FileReader(file);
            BufferedReader br = new BufferedReader(fis);
            String tempString = "";
            while ((tempString = br.readLine()) != null) {
                if (tempString.toLowerCase().contains("mi pad")) {
                    br.close();
                    return true;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (intent == null) return false;
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_ACTIVITIES).size() > 0;
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static String getSystemProperty() {
        String line = null;
        BufferedReader reader = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = reader.readLine();
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "UNKNOWN";
    }

    public static boolean isSupportQuickCharge() {
        boolean result = false;
        File file = new File("/system/build.prop");
        try {
            FileReader fis = new FileReader(file);
            BufferedReader br = new BufferedReader(fis);
            String tempString = "";
            while ((tempString = br.readLine()) != null) {
                if (tempString.contains("persist.usb.hvdcp.detect")) {
                    String[] content = tempString.split("=");
                    if (content[1].equalsIgnoreCase("true")) {
                        result = true;
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!result) {
            List<String> quickChargeDevices = Arrays.asList(
                    "ASUS Z008D",
                    "ASUS Z00AD",
                    "FUJITSU F-03F",
                    "FUJITSU F-05F",
                    "HTC 0PJA2",
                    "HTC HTL23",
                    "HTC M9PW",
                    "HTC ONE M8S",
                    "HTC ONE M9PLUS",
                    "HTC ONE_M8 EYE",
                    "LGE LG-F500L",
                    "LGE LG-H810",
                    "LGE LG-H811",
                    "LGE LG-H812",
                    "LGE LG-H815",
                    "LGE LG-H815P",
                    "LGE LG-H815TR",
                    "LGE LG-H815T",
                    "LGE LG-H955",
                    "LGE LG-H959",
                    "LGE LS991",
                    "LGE VS986",
                    "LGE US991",
                    "MOTOROLA NEXUS 6",
                    "MOTOROLA XT1092",
                    "MOTOROLA XT1093",
                    "NEC N-03E",
                    "SAMSUNG SC-04G",
                    "SAMSUNG SC-05G",
                    "SAMSUNG SCV31",
                    "SAMSUNG SM-G890A",
                    "SAMSUNG SM-G920",
                    "SAMSUNG SM-G920A",
                    "SAMSUNG SM-G920L",
                    "SAMSUNG SM-G920R4",
                    "SAMSUNG SM-G920T1",
                    "SAMSUNG SM-G920W8",
                    "SAMSUNG SM-G925A",
                    "SAMSUNG SM-G925K",
                    "SAMSUNG SM-G925L",
                    "SAMSUNG SM-G925W8",
                    "SAMSUNG SM-G9250",
                    "SAMSUNG SM-G9280",
                    "SAMSUNG SM-G9287",
                    "SAMSUNG SM-G9287C",
                    "SAMSUNG SM-G928A",
                    "SAMSUNG SM-G928C",
                    "SAMSUNG SM-G928F",
                    "SAMSUNG SM-G928G",
                    "SAMSUNG SM-G928I",
                    "SAMSUNG SM-G928P",
                    "SAMSUNG SM-G928T",
                    "SAMSUNG SM-G928V",
                    "SAMSUNG SM-N910A",
                    "SAMSUNG SM-N910R4",
                    "SAMSUNG SM-N910T3",
                    "SAMSUNG SM-N910U",
                    "SAMSUNG SM-N915A",
                    "SAMSUNG SM-N915L",
                    "SAMSUNG SM-N915P",
                    "SAMSUNG SM-N9200",
                    "SAMSUNG SM-N9208",
                    "SAMSUNG SM-N920A",
                    "SAMSUNG SM-N920C",
                    "SAMSUNG SM-N920I",
                    "SAMSUNG SM-N920K",
                    "SAMSUNG SM-N920L",
                    "SAMSUNG SM-N920P",
                    "SAMSUNG SM-N920S",
                    "SAMSUNG SM-N920T",
                    "SAMSUNG SM-N920V",
                    "SAMSUNG SM-N920W8",
                    "SAMSUNG-SM-G920AZ",
                    "SHARP SH-02G",
                    "SHARP SH-06E",
                    "SHARP SH-06F",
                    "SHARP SH-08E",
                    "SM-N9100",
                    "SONY SO-03G",
                    "SONY SOT21"
            );

            String device = (Build.MANUFACTURER + " " + Build.MODEL).toUpperCase();
            result = quickChargeDevices.contains(device);
        }
        return result;
    }


    /**
     * 判断手机有无密码或者图像锁
     */
    public static boolean isHaveLock(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.isKeyguardSecure();
        } else {
            return isSecured(context);
        }
    }

    public static boolean isSecured(Context context) {
        boolean isSecured = false;
        String classPath = "com.android.internal.widget.LockPatternUtils";
        try {
            Class<?> lockPatternClass = Class.forName(classPath);
            Object lockPatternObject = lockPatternClass.getConstructor(Context.class).newInstance(context.getApplicationContext());
            Method method = lockPatternClass.getMethod("isSecure");
            isSecured = (boolean) method.invoke(lockPatternObject);
        } catch (Exception e) {
            isSecured = false;
        }
        return isSecured;
    }

    public static String getMiuiVersion() {
        String line = "";
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (Exception ex) {
            return "";
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                }
            }
        }
        return line;
    }

}
