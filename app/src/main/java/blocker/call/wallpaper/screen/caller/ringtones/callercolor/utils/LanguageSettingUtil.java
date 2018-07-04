package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Locale;

public class LanguageSettingUtil {
    //单例模式-
    private static LanguageSettingUtil instance;
    private Context context;
    //存储当前系统的language设置-
    private String defaultLanguage;
    //存储当前系统Locale-
    private Locale defaultLocale;
    // 存储当前refresh的Locale-
    private Locale refreshLocale;

    public final static String ENGLISH = "en";
    public final static String CHINESE = "zh_rCN";
    public final static String CHINESE_TW = "zh_rTW";
    public final static String ESPANISH = "es";
    public final static String PORTUGUESE = "pt";
    public final static String FRENCH = "fr";
    public final static String ARABIC = "ar";
    public final static String GERMAN = "de";
    public final static String JAPAN = "ja";
    public final static String KOREA = "ko";
    public final static String Thailand = "th";//泰语
    public final static String Russia = "ru";//俄语
    public final static String Turkey = "tr";//土耳其
    public final static String Italy = "it";//意大利
    public final static String INDONESIA = "in";//印尼语(Indonesia)
    public final static String VIETNAMESE = "vi";//越南语(Tiếng Việt)
    public final static String HINDI = "hi";//印第语(हिन्दी)
    public final static String AUTO = "auto";//自动（跟随系统）

    private LanguageSettingUtil(Context paramContext) {
        reloadDefaultLocale();
        this.context = paramContext;
    }

    public void reloadDefaultLocale() {
        //得到系统语言-
        Locale localLocale = Locale.getDefault();
        this.defaultLocale = localLocale;
        if (localLocale.getLanguage().contains(CHINESE)) {
            this.defaultLocale = new Locale(CHINESE);
        }
        LogUtil.d("LanguageSettin", "reloadDefautLocale:" + defaultLocale);

        //保存系统语言到defaultLanguage
        String str = this.defaultLocale.getLanguage();
        this.defaultLanguage = str;
    }

    //检验自身是否被创建-
    public synchronized static LanguageSettingUtil getInstance(Context paramContext) {
        if (instance == null){
            instance = new LanguageSettingUtil(paramContext);
        }
        return instance;
    }

    public String getDefaultLanguage(){
        return defaultLanguage;
    }


//    // 创建Configuration-
//    private Configuration getUpdatedLocaleConfig(String paramString) {
//
//        Configuration localConfiguration = context.getResources()
//                .getConfiguration();
//        Locale localLocale = getLocale(paramString);
//        localConfiguration.locale = localLocale;
//        return localConfiguration;
//    }

    //得到APP配置文件目前的语言设置-
    public String getLanguage() {
        SharedPreferences localSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.context);
        //如果当前程序没有设置language属性就返回系统语言，如果有，就返回以前的-
        return localSharedPreferences.getString("language",
                AUTO);
    }

//    //如果配置文件中没有语言设置-
//    public Locale getLocale() {
//        String str = getLanguage();
//        return getLocale(str);
//    }
//
//    //创建新Locale并覆盖原Locale-
//    public Locale getLocale(String paramString) {
//        Locale localLocale = new Locale(paramString);
//        Locale.setDefault(localLocale);
//        return localLocale;
//    }

    //刷新显示配置-
    public void refreshLanguage(Context ctx) {
       /* String str = getLanguage();
        Resources localResources = this.context.getResources();
        if (!localResources.getConfiguration().locale.getLanguage().equals(str)) {
            Configuration localConfiguration = getUpdatedLocaleConfig(str);
            // A structure describing general information about a display, such
            // as its size, density, and font scaling.
            DisplayMetrics localDisplayMetrics = localResources
                    .getDisplayMetrics();
            localResources.updateConfiguration(localConfiguration,
                    localDisplayMetrics);
        }*/

        String lang = getLanguage();
        LogUtil.d("refreshLanguage", "lang = " + lang);
        Resources resources = ctx.getResources();
        Configuration config = resources.getConfiguration();

        // if(lang!=null && lang.equals(config.locale.getLanguage())) {
        //         // 若现有语言已经为设置的语言， 则不做refresh
        //         return;
        // }

        Locale locale;
        switch (lang) {
            case ENGLISH:
                locale = new Locale(ENGLISH, "US");
                break;
            case CHINESE:
                locale = Locale.CHINA;
                break;
            case CHINESE_TW:
                locale = Locale.TAIWAN;
                break;
            case ESPANISH:
                locale = new Locale(ESPANISH, "US");
                break;
            case PORTUGUESE:
                locale = new Locale(PORTUGUESE, "BR");
                break;
            case FRENCH:
                locale = new Locale(FRENCH, "FR");
                break;
            case ARABIC:
                locale = new Locale(ARABIC, "SA");
                break;
            case GERMAN:
                locale = new Locale(GERMAN, "DE");
                break;
            case JAPAN:
                locale = new Locale(JAPAN, "JP");
                break;
            case KOREA:
                locale = new Locale(KOREA, "KR");
                break;
            case Thailand:
                locale = new Locale(Thailand, "TH");
                break;
            case Russia:
                locale = new Locale(Russia, "RU");
                break;
            case Turkey:
                locale = new Locale(Turkey, "TR");
                break;
            case Italy:
                locale = new Locale(Italy, "IT");
                break;
            case INDONESIA:
                locale = new Locale(INDONESIA, "ID");
                break;
            case VIETNAMESE:
                locale = new Locale(VIETNAMESE, "VN");
                break;
            case HINDI:
                locale = new Locale(HINDI, "IN");
                break;
            case AUTO:
                locale = this.defaultLocale;
                break;
            default:
                locale = new Locale(ENGLISH, "US");
                break;
        }

        config.locale = locale;
        DisplayMetrics dm = resources.getDisplayMetrics();
        Locale targetLocale = null;
        try {
            resources.updateConfiguration(config, dm);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!lang.equals(AUTO)) {
                    if (ARABIC.equals(lang)) {
                        config.setLayoutDirection(locale);
                    } else {
                        config.setLayoutDirection(null);
                    }
                } else {
                    config.setLayoutDirection(locale);
                }
            }
            targetLocale = locale;
        } catch (Exception e) {
            targetLocale = null;
        } finally {
            if (targetLocale != null) {
                refreshLocale = locale;
                Locale.setDefault(locale);
            }
        }
    }

    //设置系统语言-
    public void saveLanguage(String paramString) {
        PreferenceManager.getDefaultSharedPreferences(this.context).edit()
                .putString("language", paramString).commit();
    }

    //保存系统的语言设置到SharedPreferences-
    public void saveSystemLanguage() {
        PreferenceManager.getDefaultSharedPreferences(this.context).edit()
                .putString("PreSysLanguage", this.defaultLanguage).commit();
    }

    public void checkSysChanged(String cuerSysLanguage) {
        //如果系统语言设置发生变化-
        if (!cuerSysLanguage.equals(PreferenceManager
                .getDefaultSharedPreferences(this.context).getString(
                        "PreSysLanguage", CHINESE))) {
            //如果系统保存了this对象，就在这里修改defaultLanguage的值为当前系统语言cuerSysLanguage
            this.defaultLanguage = cuerSysLanguage;
            saveLanguage(cuerSysLanguage);
            saveSystemLanguage();
        }
    }

    public static String getSystemLang() {
        Locale localLocale = Locale.getDefault();
        String strLang = localLocale.getLanguage();
        if (TextUtils.isEmpty(strLang))
            strLang = "en";
        return strLang;
    }

    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     *
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static String getCountry2Ways(Context context, Locale locale) {
        String country = locale.getCountry();
        if (TextUtils.isEmpty(country)) {
            country = LanguageSettingUtil.getUserCountry(context);
        }
        return country;
    }


    /**
     * 返回当前设置的语言
     *
     * @param context
     * @return
     */
    public static Locale getLocale(Context context) {
        return LanguageSettingUtil.getInstance(context).getCurrentLocale();
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public Locale getCurrentLocale() {
        if (refreshLocale != null) {
            return refreshLocale;
        }
        return Locale.getDefault();
    }

    /**
     * 根据系统语言判断布局是否为翻转布局（eg. ar）
     *
     * @param context
     * @return
     */
    public static boolean isLayoutReverse(Context context) {
//        LanguageSettingUtil.init(context);
//        String defaultLang = LanguageSettingUtil.get().defaultLanguage;
//        return "ar".equals(defaultLang) || "fa".equals(defaultLang);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration config = context.getResources().getConfiguration();
            return (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL);
        } else {
            return false;
        }
    }


}