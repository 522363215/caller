package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

import static android.text.TextUtils.isEmpty;

/**
 * Created by zhq on 2017/1/16.
 * 用于查询电话号码的相关信息，如:归属地，手机或座机，运营商等其他信息
 */

public class NumberUtil {
    private static PhoneNumberUtil mUtil;

    private static Locale mLocale;
    public static String mCountryISO = "";
    private static Context mContext;

    static {
        mContext = ApplicationEx.getInstance();
        mUtil = PhoneNumberUtil.getInstance();

        mLocale = mContext.getResources().getConfiguration().locale;
        if (mLocale == null) {
            mLocale = Locale.getDefault();
        }

        mCountryISO = getDefaultCountry();
    }

    public static String getCountryName(String language, String iso) {
        String countryname = "";
        Locale locale = new Locale(language, iso);
        countryname = locale.getDisplayCountry();
        return countryname;
    }

    //US
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
            if(mLocale != null) {
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

    // 从手机通讯录中获取号码名字;
    public static String getNameByNumber(String number) {
        String name = "";
        if (isEmpty(number)) return name;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return name;
            }
        }

//        Cursor callLogQuery = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI,
//                new String[]{
//                        CallLog.Calls.CACHED_NAME,
//                },
//                CallLog.Calls.NUMBER + "=?",
//                new String[]{number},
//                null);
//        if (callLogQuery != null && callLogQuery.getCount() > 0) {
//            callLogQuery.moveToNext();
//            name = callLogQuery.getString(0);
//            callLogQuery.close();
//        }

        Cursor phoneQuery = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                },
                "REPLACE(REPLACE(REPLACE(REPLACE(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",')','')" + ",' ','')" + ",'(','')" + ",'-','') Like ?",
                new String[]{"%" + number},
                null);
        if (phoneQuery != null && phoneQuery.moveToFirst()) {
            do {
                String cursorNumber = NumberUtil.getNumberByPattern(phoneQuery.getString(0));
//                LogUtil.d("hiblock", "cursorNumber:" + cursorNumber + ",nationalNumber:" + number);
                if (Math.abs(cursorNumber.length() - number.length()) <= 5) {
                    name = phoneQuery.getString(1);
                    break;
                }
            } while (phoneQuery.moveToNext());
            phoneQuery.close();
        }

        return name;
    }

    // 获取当前国家代码;
    public static int getCurrentCountryCode() {
        try {
//            LogUtil.d("getcurrentCountryCode","mCountryISO:"+mCountryISO+",ccode:"+mUtil.getCountryCodeForRegion(mCountryISO));
            return mUtil.getCountryCodeForRegion(mCountryISO);
        } catch (Exception e) {
            LogUtil.e("getcurrentCountryCode", "error:" + e.getMessage());
        }

        return -1;
    }

    @NonNull
    private static Phonenumber.PhoneNumber getPhoneNumber(String number) {
        try {
            return mUtil.parse(number, mCountryISO);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取国家区号(数字),该方法所有号码查询的基础，此时传入的号码如果不带区号则视号码为当前国家的号码，
     * 传入的号码带国家区号，则会获取到相应国家号码的区号
     */
    public static String getCCodeForNumber(String number) {
        String CCode = "" + getCurrentCountryCode();
        try {
            CCode = "" + mUtil.parse(number, mCountryISO).getCountryCode();
            LogUtil.d("getCCodeForNumber", "number:" + number + ",ccode:" + CCode);
        } catch (NumberParseException e) {
//            LogUtil.e("getCCodeForNumber", "error:" + e.getMessage());
//            e.printStackTrace();
        }
        return CCode;
    }

    /**
     * 获取国家代码(英文)，获取号码的国家缩写，获取号码的其他信息都会用到该属性
     */
    public static String getCountryISOForNumber(String number) {
        String countryISO = null;
        try {
            String CCode = getCCodeForNumber(number);
            countryISO = mUtil.getRegionCodeForCountryCode(Integer.parseInt(CCode));
            LogUtil.d("Power Caller", "NumberUtil getCountryISOForNumber: number:" + number + ",countryIso:" + countryISO);
        } catch (Exception e) {
            LogUtil.e("Power Caller", "NumberUtil getCountryISOForNumber: " + e.getMessage());
            return mCountryISO;
        }
        return countryISO;
    }

    public static String getIsoByCC(String cc) {
        LogUtil.d("NumberUtil", "getIsoByCC cc: " + cc);
        if (isEmpty(cc)) {
            return "";
        }
        return mUtil.getRegionCodeForCountryCode(Integer.valueOf(cc));
    }

    public static String getNumberByPattern(String rawNum) {
//        LogUtil.d("NumberUtil", "getNumberByPattern rawNum: " + rawNum);
        String number = "";

        if (rawNum == null || rawNum.trim().length() == 0) {
            return number;
        }

        String first = "";
        if (rawNum.trim().startsWith("+")) {
            first = "+";
        }

        rawNum = rawNum.trim();

        String regex = "\\d*";
        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(rawNum);

        StringBuilder sb = new StringBuilder();
        sb.append(first);
        while (m.find()) {
            if (!"".equals(m.group())) {
                sb.append(m.group());
//                LogUtil.d(LogUtil.TAG, "getNumberByPattern match: "+m.group());
            }
        }


        number = sb.toString();
//        LogUtil.d("NumberUtil", "getNumberByPattern number: " + number);


        return number;
    }

    // 格式化号码;
    public static String getFormatNumber(String number) {
        try {
            if (isInvalid(number)) {
                Phonenumber.PhoneNumber pn = mUtil.parse(number, mCountryISO);
                String num = String.valueOf(pn.getNationalNumber());
                return num;
            }
        } catch (Exception e) {
            LogUtil.e("hicaller", "该号码不能格式化：" + e.getMessage());
        }
        return number;
    }

    public static String getFormatNumberForDb(String number) {
        if (TextUtils.isEmpty(number)) {
            return number;
        }

        String dbNumber = number;
        String numberRaw = "";
        try {
            number = number.replace(" ", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace("–", "")
                    .replace("-", "");
            if (number.startsWith("+") || number.matches("^\\d*$")) {
                numberRaw = number;
            } else {
                numberRaw = geNumberbyRaw(number); //55+5656, to +5656; 55=5656 to 5656,86173 5665 5656
            }
            String s = mUtil.format(mUtil.parse(numberRaw, mCountryISO), PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            s = getNumberByPattern(s);
            dbNumber = getCCodeForNumber(numberRaw) + s;
            LogUtil.d("numberUtil", "getFormatNumberForDb s:" + s + ",dbNumber:" + dbNumber);
        } catch (NumberParseException e) {
//            LogUtil.e("getFormatNumberForDb", "该号码无法格式化：" + e.getErrorType().name());
            if (number.matches("^\\d*$")) {
                dbNumber = getCCodeForNumber(numberRaw) + numberRaw;
            }
            // 非数字字符情况下返回空字符;
        }
//        LogUtil.d("endcall", "getFormatNumberForDb number: " + number + ", dbNumber: " + dbNumber);
        return dbNumber;
    }

    public static String getFormatNumberForForServer(String number) {
        if (TextUtils.isEmpty(number)) {
            return number;
        }
        number = number.replace(" ", "");
        String dbNumber = null;
        String numberRaw = "";
        try {
            number = number.replace(" ", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace("–", "")
                    .replace("-", "");
            if (number.startsWith("+") || number.matches("^\\d*$")) {
                numberRaw = number;
            } else {
                numberRaw = geNumberbyRaw(number); //55+5656, to +5656; 55=5656 to 5656
            }
            String s = mUtil.format(mUtil.parse(numberRaw, mCountryISO), PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            s = getNumberByPattern(s);
            dbNumber = getCCodeForNumber(numberRaw) + s;
            LogUtil.d("numberUtil", "getFormatNumberForForServer s:" + s + ",dbNumber:" + dbNumber);
        } catch (NumberParseException e) {
//            LogUtil.e("getFormatNumberForDb", "该号码无法格式化：" + e.getErrorType().name());
            if (number.matches("^\\d*$")) {
                dbNumber = getCCodeForNumber(numberRaw) + numberRaw;
            }
            // 非数字字符情况下返回空字符;
        }
//        LogUtil.d("endcall", "getFormatNumberForDb number: " + number + ", dbNumber: " + dbNumber);
        return dbNumber;
    }

    // 获取号码类型;
    public static String getNumberType(String number) {
        try {
            Phonenumber.PhoneNumber parse = mUtil.parse(number, mCountryISO);
            //return "+"+ getCountryCode() + mUtil.getNumberType(parse).name();
            String type = mUtil.getNumberType(parse).name();
            return isEmpty(type) ? "UNKNOWN" : type;
        } catch (NumberParseException e) {
            e.toString();
            return "UNKNOWN";
        }
    }

    public static String getNumberTypeMultLanguage(Context context, String type) {
        if ("FIXED_LINE".equals(type)) {
            return context.getResources().getString(R.string.FIXED_LINE);
        } else if ("MOBILE".equals(type)) {
            return context.getResources().getString(R.string.MOBILE);
        } else if ("FIXED_LINE_OR_MOBILE".equals(type)) {
            return context.getResources().getString(R.string.FIXED_LINE_OR_MOBILE);
        } else if ("TOLL_FREE".equals(type)) {
            return context.getResources().getString(R.string.TOLL_FREE);
        } else if ("PREMIUM_RATE".equals(type)) {
            return context.getResources().getString(R.string.PREMIUM_RATE);
        } else if ("SHARED_COST".equals(type)) {
            return context.getResources().getString(R.string.SHARED_COST);
        } else if ("VOIP".equals(type)) {
            return context.getResources().getString(R.string.phone_detail_unknown);
        } else if ("PERSONAL_NUMBER".equals(type)) {
            return context.getResources().getString(R.string.PERSONAL_NUMBER);
        } else if ("PAGER".equals(type)) {
            return context.getResources().getString(R.string.phone_detail_unknown);
        } else if ("UAN".equals(type)) {
            return context.getResources().getString(R.string.phone_detail_unknown);
        } else if ("VOICEMAIL".equals(type)) {
            return context.getResources().getString(R.string.phone_detail_unknown);
        } else if ("UNKNOWN".equals(type)) {
            return context.getResources().getString(R.string.phone_detail_unknown);
        }
        return context.getResources().getString(R.string.phone_detail_unknown);
    }

    // 获取号码归属地;
    public static String getNumberLocation(String number) {
        try {
            mLocale = LanguageSettingUtil.getLocale(mContext);
//            if (mLocale != null && mLocale.getLanguage().equals("zh")) {
//                mCountryISO = "CN";
////                LogUtil.d("cidlog", "getNumberLocation mLocale.getLanguage(): " + mLocale.getLanguage() + ", country: " + mLocale.getCountry());
//                String cc = mLocale.getCountry();
//                if (!TextUtils.isEmpty(cc) && cc.equalsIgnoreCase("TW")) {
//                    mCountryISO = "TW";
//                }
//            }
            LogUtil.d("cidlog", "getNumberLocation mCountryISO: " + mCountryISO);
            Phonenumber.PhoneNumber phoneNumber = getPhoneNumber(number);
            String loc = "";

            if (TextUtils.isEmpty(loc)) {
                String regionCode = mUtil.getRegionCodeForCountryCode(phoneNumber.getCountryCode());
                loc = getRegionDisplayName(regionCode, mLocale);
//                LogUtil.d("numut", "getNumberLocation loc: "+loc+", number: "+number+", regioncode: "+regionCode+", country: "+loc);
            }
            return loc == "" ? getCountryName(mLocale.getLanguage(), mCountryISO) : loc;
        } catch (Exception e) {
            return getCountryName(mLocale.getLanguage(), mCountryISO);
        }
    }

    public static String getRegionDisplayName(String regionCode, Locale language) {
        return regionCode != null && !regionCode.equals("ZZ") && !regionCode.equals("001") ? (new Locale("", regionCode)).getDisplayCountry(language) : "";
    }

    public static int getCountryCodeNew(String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        int countryCode = -1;
//        number = "+8860970654793"; // real taiwan number example: +8860970654793
        try {
            // phone must begin with '+'
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(number, mCountryISO);
            countryCode = numberProto.getCountryCode();

//            numberProto.setCountryCode(countryCode);
//            numberProto.setNationalNumber(12012012);

//            String mblocation = mPhoneGeocoder.getDescriptionForNumber(numberProto, mLocale);
            //new getCountryCodeNew: 886, number: +8860970654793, location: 台湾
            //new getCountryCodeNew: 886, number: +8860970654793, location: Taiwan

//            LogUtil.d("numut", "new getCountryCodeNew: " + countryCode + ", number: " + number + ", location: " + mblocation);
        } catch (NumberParseException e) {
            LogUtil.e("numut", "new getCountryCodeNew exception: " + e.getMessage());
        }
        return countryCode;
    }

    // 验证号码是否有效;
    public static boolean isInvalid(String number) {
        try {
            return mUtil.isValidNumber(getPhoneNumber(number));
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean isInvalidUpload(String number) {
        return true;
    }

    /**
     * 号码本地化处理，当前国家的号码不显示区号，国际号码显示区号且带加号(主要用于界面显示使用)
     */
    public static String getLocalizationNumber(String number) {
        try {
            if (!number.contains("*") && !number.contains("#")) {
                String formatNumber = PhoneNumberUtil.getInstance().format(getPhoneNumber(number), PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
                String currentCCode = getCurrentCountryCode() + "";
                String numberCCode = getCCodeForNumber(number);
                if (!numberCCode.equals("UNKNOWN")) {
                    if (numberCCode.equals(currentCCode)) {
                        return formatNumber;
                    } else if (formatNumber.startsWith(numberCCode)) {
                        //return formatNumber.substring(currentCCode.length());
                        return "+" + formatNumber;
                    } else {
                        return PhoneNumberUtil.getInstance().format(getPhoneNumber(number), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                    }
                }
                LogUtil.d("getLocalizationNumber", "number:" + number + ",formatNumber:" + formatNumber + ",currentCCode:" + currentCCode + ",numberCCode:" + numberCCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() != null)
                LogUtil.d("getLocalizationNumber", "error:" + e.getMessage());
        }
        return number;
    }

    /**
     * 从通话记录中获取号码的归属地，android 5.0 以上才有效
     *
     * @param number :该号码如果带有国家区号可能会查找不到，取决于通话记录中是否带国家区号，通话记录中带有国家区号，该属性可带不可带都可以查找到，
     *               通话记录中不带国家区号，则该属性一定不能带国家区号
     */
    public static String getNumberLocationForCallLog(String number) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Uri uri = CallLog.Calls.CONTENT_URI;
            Cursor cursor = null;
            String location = null;
            String num = number.replace(" ", "");
            String[] project = new String[]{CallLog.Calls.GEOCODED_LOCATION};
            String selection = "REPLACE(REPLACE(REPLACE(REPLACE(" + CallLog.Calls.NUMBER + ",')','')" + ",' ','')" + ",'(','')" + ",'-','') Like ?";
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
//                    ToastUtils.showToast(mContext, "No permission to read calllog");
                    return null;
                }
            }

            try {
                cursor = mContext.getContentResolver().query(uri, project, selection, new String[]{"%" + num}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        location = cursor.getString(0);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }
            return location;
        }
        ToastUtils.showToast(mContext, "android 5.0 以上才能获取到归属地");
        return null;
    }

    public static int getCountryCode() {
        return mUtil.getCountryCodeForRegion(mCountryISO);
    }

    public static int getCountryCode(String country) {
        return mUtil.getCountryCodeForRegion(country);
    }

    // 获取号码归属地;
    public static String getNumberLocation(String number, String countryIso) {
        try {
            mLocale = LanguageSettingUtil.getLocale(mContext);
            Phonenumber.PhoneNumber phoneNumber = getPhoneNumber(number, countryIso);

            String loc = "";

            if (TextUtils.isEmpty(loc)) {
                String regionCode = mUtil.getRegionCodeForCountryCode(phoneNumber.getCountryCode());
                loc = getRegionDisplayName(regionCode, mLocale);
//                LogUtil.d("numut", "getNumberLocation loc: "+loc+", number: "+number+", regioncode: "+regionCode+", country: "+loc);
            }


            return loc == "" ? mLocale.getDisplayCountry() : loc;
        } catch (Exception e) {
            return mLocale.getDisplayCountry();
        }
    }

    private static Phonenumber.PhoneNumber getPhoneNumber(String number, String countryIso) {
        countryIso = isEmpty(countryIso) ? mCountryISO : countryIso;
        try {
            LogUtil.d("numberUtil", "PhoneNumber  PhoneNumber:" + mUtil.parse(number, countryIso));
            return mUtil.parse(number, countryIso);
        } catch (NumberParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getNumberType(String number, String countryIso) {
        try {
            return mUtil.getNumberType(mUtil.parse(number, countryIso)).name();
        } catch (NumberParseException e) {
            //e.printStackTrace();
            LogUtil.e("getNumberType", "号码解析错误:" + e.getErrorType().name());
            return "Unknow";
        }
    }

    private static String getCountryByGeo() {
        String country = ""; //US

//        Geocoder geocoder = new Geocoder(ApplicationEx.getInstance());
//
//        boolean isLocationSuccess = false; //判断GPS定位是否启动
//        boolean isNetwork = false; //判断网络定位是否启动
//
//
//            LocationManager lm
//                    = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
//
//            if (lm != null) {
//                //通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//                isLocationSuccess = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
////                if(!isLocationSuccess){
////                    //通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
////                    isLocationSuccess = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
////                }
//                if(isLocationSuccess){
//
//                    double lat = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();//need permission check
//                    double lng = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
//
//                    try {
//                        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
//                        Address obj = addresses.get(0);
//                        String add = obj.getAddressLine(0);
//
//                        add = add + "\n" + obj.getCountryName();
//                        add = add + "\n" + obj.getCountryCode();
//                        add = add + "\n" + obj.getAdminArea();
//                        add = add + "\n" + obj.getPostalCode();
//                        add = add + "\n" + obj.getSubAdminArea();
//                        add = add + "\n" + obj.getLocality();
//                        add = add + "\n" + obj.getSubThoroughfare();
//
//                        LogUtil.d("IGA", "Address" + add);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }

        return country;
    }

    // 号码格式为：有效号码格式为+8613890548849，非有效号码不做任何处理;
    public static String getNormalizedNumber(String number, String cCode) {
        String num = number;
        try {
            if (isInvalid(number)) {
                Phonenumber.PhoneNumber pn = mUtil.parse(number, mCountryISO);
                String nationalNumber = String.valueOf(pn.getNationalNumber());
                num = TextUtils.isEmpty(nationalNumber) ? number : "+" + cCode + nationalNumber;
            } else {
                num = number;
            }
        } catch (Exception e) {
            LogUtil.e("hicaller", "该号码不能格式化：" + e.getMessage());
        }
        if (num.length() < number.length()) {
            return number;
        }
        return num;
    }

    // 号码格式为：有效号码格式为+8613890548849，非有效号码不做任何处理;
    public static String getNormalizedNumber(String dbNumber) {
        try {
            String number = "+" + dbNumber;
            if (getFormatNumberForDb(number).equals(dbNumber)) {
                return number;
            }
        } catch (Exception e) {
            LogUtil.e("hicaller", "该号码不能格式化：" + e.getMessage());
        }
        return dbNumber;
    }

    private static Map<String, String> country_to_indicative = new HashMap<String, String>();

    static {
        country_to_indicative.put("AF", "+93");
        country_to_indicative.put("AL", "+355");
        country_to_indicative.put("DZ", "+213");
        country_to_indicative.put("AS", "+1684");
        country_to_indicative.put("AD", "+376");
        country_to_indicative.put("AO", "+244");
        country_to_indicative.put("AI", "+1264");
        country_to_indicative.put("AG", "+1268");
        country_to_indicative.put("AR", "+54");
        country_to_indicative.put("AM", "+374");
        country_to_indicative.put("AU", "+61");
        country_to_indicative.put("AW", "+297");
        country_to_indicative.put("AT", "+43");
        country_to_indicative.put("AZ", "+994");
        country_to_indicative.put("BS", "+1242");
        country_to_indicative.put("BH", "+973");
        country_to_indicative.put("BD", "+880");
        country_to_indicative.put("BB", "+1246");
        country_to_indicative.put("BY", "+375");
        country_to_indicative.put("BE", "+32");
        country_to_indicative.put("BZ", "+501");
        country_to_indicative.put("BJ", "+229");
        country_to_indicative.put("BM", "+1441");
        country_to_indicative.put("BT", "+975");
        country_to_indicative.put("BO", "+591");
        country_to_indicative.put("BA", "+387");
        country_to_indicative.put("BW", "+267");
        country_to_indicative.put("BR", "+55");
        country_to_indicative.put("BN", "+673");
        country_to_indicative.put("BG", "+359");
        country_to_indicative.put("BF", "+226");
        country_to_indicative.put("BI", "+257");
        country_to_indicative.put("KH", "+855");
        country_to_indicative.put("CM", "+237");
        country_to_indicative.put("CA", "+1");
        country_to_indicative.put("CV", "+238");
        country_to_indicative.put("CF", "+236");
        country_to_indicative.put("TD", "+235");
        country_to_indicative.put("CL", "+56");
        country_to_indicative.put("CN", "+86");
        country_to_indicative.put("CO", "+57");
        country_to_indicative.put("KM", "+269");
        country_to_indicative.put("CD", "+243");
        country_to_indicative.put("CG", "+242");
        country_to_indicative.put("CR", "+506");
        country_to_indicative.put("CI", "+225");
        country_to_indicative.put("HR", "+385");
        country_to_indicative.put("CU", "+53");
        country_to_indicative.put("CY", "+357");
        country_to_indicative.put("CZ", "+420");
        country_to_indicative.put("DK", "+45");
        country_to_indicative.put("DJ", "+253");
        country_to_indicative.put("DM", "+1767");
        country_to_indicative.put("DO", "+1829");
        country_to_indicative.put("EC", "+593");
        country_to_indicative.put("EG", "+20");
        country_to_indicative.put("SV", "+503");
        country_to_indicative.put("GQ", "+240");
        country_to_indicative.put("ER", "+291");
        country_to_indicative.put("EE", "+372");
        country_to_indicative.put("ET", "+251");
        country_to_indicative.put("FJ", "+679");
        country_to_indicative.put("FI", "+358");
        country_to_indicative.put("FR", "+33");
        country_to_indicative.put("GA", "+241");
        country_to_indicative.put("GM", "+220");
        country_to_indicative.put("GE", "+995");
        country_to_indicative.put("DE", "+49");
        country_to_indicative.put("GH", "+233");
        country_to_indicative.put("GR", "+30");
        country_to_indicative.put("GD", "+1473");
        country_to_indicative.put("GT", "+502");
        country_to_indicative.put("GN", "+224");
        country_to_indicative.put("GW", "+245");
        country_to_indicative.put("GY", "+592");
        country_to_indicative.put("HT", "+509");
        country_to_indicative.put("HN", "+504");
        country_to_indicative.put("HU", "+36");
        country_to_indicative.put("IS", "+354");
        country_to_indicative.put("IN", "+91");
        country_to_indicative.put("ID", "+62");
        country_to_indicative.put("IR", "+98");
        country_to_indicative.put("IQ", "+964");
        country_to_indicative.put("IE", "+353");
        country_to_indicative.put("IL", "+972");
        country_to_indicative.put("IT", "+39");
        country_to_indicative.put("JM", "+1876");
        country_to_indicative.put("JP", "+81");
        country_to_indicative.put("JO", "+962");
        country_to_indicative.put("KZ", "+7");
        country_to_indicative.put("KE", "+254");
        country_to_indicative.put("KI", "+686");
        country_to_indicative.put("KP", "+850");
        country_to_indicative.put("KR", "+82");
        country_to_indicative.put("KW", "+965");
        country_to_indicative.put("KG", "+996");
        country_to_indicative.put("LA", "+856");
        country_to_indicative.put("LV", "+371");
        country_to_indicative.put("LB", "+961");
        country_to_indicative.put("LS", "+266");
        country_to_indicative.put("LR", "+231");
        country_to_indicative.put("LY", "+218");
        country_to_indicative.put("LI", "+423");
        country_to_indicative.put("LT", "+370");
        country_to_indicative.put("LU", "+352");
        country_to_indicative.put("MK", "+389");
        country_to_indicative.put("MG", "+261");
        country_to_indicative.put("MW", "+265");
        country_to_indicative.put("MY", "+60");
        country_to_indicative.put("MV", "+960");
        country_to_indicative.put("ML", "+223");
        country_to_indicative.put("MT", "+356");
        country_to_indicative.put("MH", "+692");
        country_to_indicative.put("MR", "+222");
        country_to_indicative.put("MU", "+230");
        country_to_indicative.put("MX", "+52");
        country_to_indicative.put("FM", "+691");
        country_to_indicative.put("MD", "+373");
        country_to_indicative.put("MC", "+377");
        country_to_indicative.put("MN", "+976");
        country_to_indicative.put("ME", "+382");
        country_to_indicative.put("MA", "+212");
        country_to_indicative.put("MZ", "+258");
        country_to_indicative.put("MM", "+95");
        country_to_indicative.put("NA", "+264");
        country_to_indicative.put("NR", "+674");
        country_to_indicative.put("NP", "+977");
        country_to_indicative.put("NL", "+31");
        country_to_indicative.put("NZ", "+64");
        country_to_indicative.put("NI", "+505");
        country_to_indicative.put("NE", "+227");
        country_to_indicative.put("NG", "+234");
        country_to_indicative.put("NO", "+47");
        country_to_indicative.put("OM", "+968");
        country_to_indicative.put("PK", "+92");
        country_to_indicative.put("PW", "+680");
        country_to_indicative.put("PA", "+507");
        country_to_indicative.put("PG", "+675");
        country_to_indicative.put("PY", "+595");
        country_to_indicative.put("PE", "+51");
        country_to_indicative.put("PH", "+63");
        country_to_indicative.put("PL", "+48");
        country_to_indicative.put("PT", "+351");
        country_to_indicative.put("QA", "+974");
        country_to_indicative.put("RO", "+40");
        country_to_indicative.put("RU", "+7");
        country_to_indicative.put("RW", "+250");
        country_to_indicative.put("KN", "+1869");
        country_to_indicative.put("LC", "+1758");
        country_to_indicative.put("VC", "+1784");
        country_to_indicative.put("WS", "+685");
        country_to_indicative.put("SM", "+378");
        country_to_indicative.put("ST", "+239");
        country_to_indicative.put("SA", "+966");
        country_to_indicative.put("SN", "+221");
        country_to_indicative.put("RS", "+381");
        country_to_indicative.put("SC", "+248");
        country_to_indicative.put("SL", "+232");
        country_to_indicative.put("SG", "+65");
        country_to_indicative.put("SK", "+421");
        country_to_indicative.put("SI", "+386");
        country_to_indicative.put("SB", "+677");
        country_to_indicative.put("SO", "+252");
        country_to_indicative.put("ZA", "+27");
        country_to_indicative.put("ES", "+34");
        country_to_indicative.put("LK", "+94");
        country_to_indicative.put("SD", "+249");
        country_to_indicative.put("SR", "+597");
        country_to_indicative.put("SZ", "+268");
        country_to_indicative.put("SE", "+46");
        country_to_indicative.put("CH", "+41");
        country_to_indicative.put("SY", "+963");
        country_to_indicative.put("TJ", "+992");
        country_to_indicative.put("TZ", "+255");
        country_to_indicative.put("TH", "+66");
        country_to_indicative.put("TL", "+670");
        country_to_indicative.put("TG", "+228");
        country_to_indicative.put("TO", "+676");
        country_to_indicative.put("TT", "+1868");
        country_to_indicative.put("TN", "+216");
        country_to_indicative.put("TR", "+90");
        country_to_indicative.put("TM", "+993");
        country_to_indicative.put("TV", "+688");
        country_to_indicative.put("UG", "+256");
        country_to_indicative.put("UA", "+380");
        country_to_indicative.put("AE", "+971");
        country_to_indicative.put("GB", "+44");
        country_to_indicative.put("US", "+1");
        country_to_indicative.put("UY", "+598");
        country_to_indicative.put("UZ", "+998");
        country_to_indicative.put("VU", "+678");
        country_to_indicative.put("VA", "+39");
        country_to_indicative.put("VE", "+58");
        country_to_indicative.put("VN", "+84");
        country_to_indicative.put("YE", "+967");
        country_to_indicative.put("ZM", "+260");
        country_to_indicative.put("ZW", "+263");
        country_to_indicative.put("GE", "+995");
        country_to_indicative.put("TW", "+886");
        country_to_indicative.put("AZ", "+994");
        country_to_indicative.put("MD", "+373");
        country_to_indicative.put("SO", "+252");
        country_to_indicative.put("GE", "+995");
        country_to_indicative.put("AU", "+61");
        country_to_indicative.put("CX", "+61");
        country_to_indicative.put("CC", "+61");
        country_to_indicative.put("NF", "+672");
        country_to_indicative.put("NC", "+687");
        country_to_indicative.put("PF", "+689");
        country_to_indicative.put("YT", "+262");
        country_to_indicative.put("GP", "+590");
        country_to_indicative.put("GP", "+590");
        country_to_indicative.put("PM", "+508");
        country_to_indicative.put("WF", "+681");
        country_to_indicative.put("PF", "+689");
        country_to_indicative.put("CK", "+682");
        country_to_indicative.put("NU", "+683");
        country_to_indicative.put("TK", "+690");
        country_to_indicative.put("GG", "+44");
        country_to_indicative.put("IM", "+44");
        country_to_indicative.put("JE", "+44");
        country_to_indicative.put("AI", "+1264");
        country_to_indicative.put("BM", "+1441");
        country_to_indicative.put("IO", "+246");
        country_to_indicative.put("VG", "+1284");
        country_to_indicative.put("KY", "+1345");
        country_to_indicative.put("FK", "+500");
        country_to_indicative.put("GI", "+350");
        country_to_indicative.put("MS", "+1664");
        country_to_indicative.put("PN", "+870");
        country_to_indicative.put("SH", "+290");
        country_to_indicative.put("TC", "+1649");
        country_to_indicative.put("MP", "+1670");
        country_to_indicative.put("PR", "+1");
        country_to_indicative.put("AS", "+1684");
        country_to_indicative.put("GU", "+1671");
        country_to_indicative.put("VI", "+1340");
        country_to_indicative.put("HK", "+852");
        country_to_indicative.put("MO", "+853");
        country_to_indicative.put("FO", "+298");
        country_to_indicative.put("GL", "+299");
        country_to_indicative.put("GF", "+594");
        country_to_indicative.put("GP", "+590");
        country_to_indicative.put("MQ", "+596");
        country_to_indicative.put("RE", "+262");
        country_to_indicative.put("AX", "+35818");
        country_to_indicative.put("AW", "+297");
        country_to_indicative.put("AN", "+599");
        country_to_indicative.put("SJ", "+47");
        country_to_indicative.put("AC", "+247");
        country_to_indicative.put("TA", "+290");
        country_to_indicative.put("AQ", "+6721");
        country_to_indicative.put("CS", "+381");
        country_to_indicative.put("PS", "+970");
        country_to_indicative.put("EH", "+212");
    }


    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static String geNumberbyRaw(String number) {
        String newStr = "";
        if (TextUtils.isEmpty(number)) {
            return newStr;
        }

        if (number.length() > 2) {
            for (int i = 0; i < number.length(); i++) {
                char ch = number.charAt(i);
                if (!Character.isDigit(ch)) {
                    if (ch == '+') {
                        newStr = number.substring(i);
                    } else {
                        if (i <= number.length() - 1) {
                            newStr = number.substring(i + 1);
                        }
                    }
                    break;
                }
            }
        }
        return newStr;
    }
    public static boolean isValidNumberNew(String number){
        return isValidNumberNew(number, mCountryISO);
    }

    public static boolean isValidNumberNew(String number, String mCountryISO){
        boolean is = true;
        try {
            LogUtil.d("isValidNumberNew", "number :" + number+", iso: "+mCountryISO);
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phonenumber= phoneNumberUtil.parse(number, mCountryISO);
            is = phoneNumberUtil.isValidNumber(phonenumber);
            LogUtil.d("isValidNumberNew", "isValidNumber :" + is+", phonenumber: "+phonenumber.toString());
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e("isValidNumberNew", "isValidNumber number: "+number+", exception: "+e.getMessage());
        }

        return is;
    }

}
