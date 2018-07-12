package blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.rate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

/**
 * 分享应用信息
 *
 * @author welcone
 */
public class ShareHelper {

    public static void shareInfo2Friends(String extraText, Activity activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, extraText);
        activity.startActivity(shareIntent);
    }

    public static void sharePowerWifi(String extraText, Activity activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, extraText);
        try {
            activity.getPackageManager().getPackageInfo("com.facebook.katana", PackageManager.GET_SIGNATURES);
//			shareIntent.setClassName("com.facebook.katana",
//					"com.facebook.composer.shareintent.ImplicitShareIntentHandler");
            shareIntent.setPackage("com.facebook.katana");
            activity.startActivity(shareIntent);
        } catch (Exception e) {
            Intent shareIntent1 = new Intent(Intent.ACTION_SEND);
            shareIntent1.setType("text/plain");
            shareIntent1.putExtra(Intent.EXTRA_TEXT, extraText);
            try {
                activity.startActivity(shareIntent1);
            } catch (Exception e2) {
                Toast.makeText(activity, activity.getString(R.string.no_app_to_share), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void shareAPKByFile(Context context) {
        Intent share = new Intent(Intent.ACTION_SEND);

        String apkName = context.getString(R.string.app_name);
        apkName = apkName.replace(" ", "");
        String toPath = "ccbackup";
        String fileName = apkName+".apk";
        String newFileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + toPath
                + File.separator;
        String newFileName = newFileDir + fileName;

        backupPowerWifi(context);

        File file = new File(newFileName);
        if (file.exists()) {
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            share.setType("*/*");// 此处可发送多种文件
            try {
                context.startActivity(Intent.createChooser(share, "Share"));
            } catch (Exception e) {
                Toast.makeText(context, R.string.failed_backup, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, R.string.failed_backup, Toast.LENGTH_LONG).show();
        }
    }

    public static String backupPowerWifi(Context mContext) {
        // new file
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return "";
        }
        String packageName = mContext.getPackageName();
        String toPath = "hiblock";
        String fileName = "hicaller.apk";
        String newFileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + toPath
                + File.separator;
        String newFileName = newFileDir + fileName;

//		File file = new File(newFileName);
        String oldFile = null;

        BufferedInputStream bi = null;
        BufferedOutputStream bfos = null;
        try {
            // old file
            oldFile = mContext.getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        if (oldFile != null) {
            try {
                File in = new File(oldFile);

                File toDirs = new File(newFileDir);

                if (!toDirs.exists()) {
                    toDirs.mkdirs();
                }

                File out = new File(newFileName);
                if (!out.exists()) {

                    out.createNewFile();
                } else {

                    out.delete();
                    out.createNewFile();
                }

                bi = new BufferedInputStream(new FileInputStream(in));

                bfos = new BufferedOutputStream(new FileOutputStream(out));
                int count;

                byte[] buffer = new byte[128 * 1024];
                while ((count = bi.read(buffer)) > 0) {

                    bfos.write(buffer, 0, count);
                }
                bfos.flush();
                // return newFileDir;
                return newFileName;
            } catch (Exception e) {
                // return newFileDir;
                return newFileName;
            } finally {
                try {
                    if (bi != null)
                        bi.close();

                    if (bfos != null)
                        bfos.close();
                } catch (Exception e) {
                    return "";
                }
            }
        } else {
            return newFileName;
        }
    }

    public static void shareAPK2friend(Context context, String path) {
        Intent share = new Intent(Intent.ACTION_SEND);
        File file = new File(path);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        share.setType("*/*");// 此处可发送多种文件
        context.startActivity(Intent.createChooser(share, "Share"));
    }
}
