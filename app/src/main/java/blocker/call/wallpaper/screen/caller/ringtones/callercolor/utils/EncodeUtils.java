package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import com.mopub.test.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2017/9/13.
 */

public class EncodeUtils {
    public static String ENCRYPTION_KEY = "ccphonekey$)!1";
    public static String ENCRYPTION_IV = "4e5Wa71fYoT7MTEL";

    static Key makeKey() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] key = md.digest(ENCRYPTION_KEY.getBytes("UTF-8"));
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            LogUtil.d("error", e.getStackTrace().toString());
        } catch (UnsupportedEncodingException e) {
            LogUtil.d("error", e.getStackTrace().toString());
        }

        return null;
    }

    static AlgorithmParameterSpec makeIv() {
        try {
            return new IvParameterSpec(ENCRYPTION_IV.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LogUtil.d("error", e.getStackTrace().toString());
        }
        return null;
    }

    public static String encrypt(String src) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(), makeIv());
            return Base64.encodeBytes(cipher.doFinal(src.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String src) {
        try {
            byte[] decryptedStr = Base64.decode(src);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, makeKey(), makeIv());
            byte[] originalByte = cipher.doFinal(decryptedStr);
            String originalStr = new String(originalByte);
            return originalStr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
