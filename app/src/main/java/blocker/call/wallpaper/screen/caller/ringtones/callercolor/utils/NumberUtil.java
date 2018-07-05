package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.Locale;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;

import static android.text.TextUtils.isEmpty;

public class NumberUtil {
    private static ApplicationEx mContext;
    private static Locale mLocale;
    private static String mCountryISO = "";

    static {
        mContext = ApplicationEx.getInstance();

        mLocale = mContext.getResources().getConfiguration().locale;
        if (mLocale == null) {
            mLocale = Locale.getDefault();
        }

        mCountryISO = getDefaultCountry();
    }

    public static String getDefaultCountry() {
        String country = "";

        final TelephonyManager tm =
                (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        //国家缩写
        if (tm != null) {
//            LogUtil.d("cidlog", "tm status: " + tm.getSimState() + ", iso: " + tm.getSimCountryIso());
            country = tm.getSimCountryIso();
            if (isEmpty(country) || country.length() < 2) {
                country = tm.getNetworkCountryIso();
            }
        }

        mLocale = LanguageSettingUtil.getLocale(mContext);
        if (isEmpty(country) || country.length() < 2) {
            if (mLocale != null) {
                country = mLocale.getCountry();
                if (mLocale.getLanguage().equals("zh") && !TextUtils.isEmpty(country) && country.equalsIgnoreCase("TW")) {
                    country = "TW";
                }
                LogUtil.d("cidlog", "locale country: " + country);
            }
        }
        country = country.toUpperCase();
        mCountryISO = country;


//        if (mLocale != null && mLocale.getLanguage().equals("zh")) {
//            country = "CN";
////            LogUtil.d("cidlog", "mLocale.getLanguage(): " + mLocale.getLanguage() + ", country: " + mLocale.getCountry());
//            String cc = mLocale.getCountry();
//            if (!TextUtils.isEmpty(cc) && cc.equalsIgnoreCase("TW")) {
//                country = "TW";
//            }
//        }

        LogUtil.d("cidlog", "getDefaultCountry mCountryISO: " + mCountryISO);
        return country;
    }
}
