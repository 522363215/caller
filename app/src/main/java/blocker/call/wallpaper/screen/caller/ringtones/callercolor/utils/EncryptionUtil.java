package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.text.TextUtils;

import java.io.RandomAccessFile;
import java.util.HashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;

public class EncryptionUtil {
    private static final String TAG = "EncryptionUtil";
    private static final String DEFAULT_VIDEO_ENCRYPT_MD5 = "*%//*?/**/$3658";

    /**
     * 加密,修改视频文件，在视频文件头部加入一个字符串，从而是视频无法识别
     *
     * @param strFile 源文件绝对路径
     */
    public static void encrypt(String strFile) {
        if (TextUtils.isEmpty(strFile)) return;
        try {
            if (!isEncrypted(strFile) && (strFile.endsWith("mp4") || strFile.endsWith("MP4"))) {
                String randomStr = Stringutil.getRandomStr(strFile);
                if (TextUtils.isEmpty(randomStr)) {
                    randomStr = DEFAULT_VIDEO_ENCRYPT_MD5;
                }
                appendFileHeader(randomStr, strFile);
                saveEncryptionVideoMd5(strFile, randomStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向src文件添加header
     *
     * @param header
     * @param srcPath
     * @throws Exception
     */
    private static void appendFileHeader(String header, String srcPath) throws Exception {
        byte[] headerBytes = header.getBytes("UTF-8");
        RandomAccessFile src = new RandomAccessFile(srcPath, "rw");
        int srcLength = (int) src.length();
        byte[] buff = new byte[srcLength];
        src.read(buff, 0, srcLength);
        src.seek(0);
        src.write(headerBytes);
        src.seek(headerBytes.length);
        src.write(buff);
        src.close();
    }

    private static void saveEncryptionVideoMd5(String strFile, String md5) {
        HashMap<String, String> hashMapData = PreferenceHelper.getHashMapData(PreferenceHelper.PREF_VIDEO_ENCRYPT_MD5_MAP, String.class);
        if (hashMapData == null) {
            hashMapData = new HashMap<>();
        }
        if (hashMapData.get(strFile) == null) {
            String encryptedStr = EncodeUtils.encrypt(md5);
//            LogUtil.d(TAG, "saveEncryptionVideoMd5 md5:" + md5 + ",encrypt before length:" + md5.getBytes().length + ",encryptedStr:" + encryptedStr + ",encrypt after length:" + encryptedStr.getBytes().length);
            hashMapData.put(strFile, encryptedStr);
        }
        PreferenceHelper.putHashMapData(PreferenceHelper.PREF_VIDEO_ENCRYPT_MD5_MAP, hashMapData);
    }

    public static String getEncryptionVideoMd5(String strFile) {
        HashMap<String, String> hashMapData = PreferenceHelper.getHashMapData(PreferenceHelper.PREF_VIDEO_ENCRYPT_MD5_MAP, String.class);
        if (hashMapData == null || hashMapData.get(strFile) == null) {
            return null;
        }
        String encryptedStr = hashMapData.get(strFile);
        String decryptedStr = EncodeUtils.decrypt(encryptedStr);
//        LogUtil.d(TAG, "getEncryptionVideoMd5 encryptedStr:" + encryptedStr + ",decrypted before length:" + encryptedStr.getBytes().length + ",decryptedStr:" + decryptedStr + ",decrypted after length:" + decryptedStr.getBytes().length);
        return decryptedStr;
    }

    public static boolean isEncrypted(String strFile) {
        String encryptionVideoMd5 = getEncryptionVideoMd5(strFile);
        if (TextUtils.isEmpty(encryptionVideoMd5)) {
            return false;
        }
        return true;
    }
}
