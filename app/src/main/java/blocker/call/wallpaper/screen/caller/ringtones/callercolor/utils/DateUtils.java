package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;


import android.content.ContentResolver;
import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

/**
 * Created by Admin on 2016/5/6.
 */
public class DateUtils {

    public static final double DOUBLE = 06.;
    public static final double DOUBLE1 = 13.;
    public static final Context mContext = ApplicationEx.getInstance().getBaseContext();

    /**
     * 默认的格式化时间
     *
     * @param time
     * @return
     */
    public static String formatData(long time, Locale locale) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        return df.format(time);
    }

    /**
     * 格式化日期
     *
     * @param time,
     * @param format yyyy/MM/dd E
     * @param locale 获取LanguageSettingUtil中存在的Locale
     * @return
     */
    public static String formatDate(long time, String format, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
        return sdf.format(time);
    }

    /**
     * 判断是否是星期六
     *
     * @return
     */
    public static boolean isSaturday() {
        Calendar cs = new GregorianCalendar();
        cs.setTime(new Date());

        cs.setFirstDayOfWeek(Calendar.SATURDAY);
        int firstWeekDay = cs.getFirstDayOfWeek();
        int dayOfWeek = cs.get(Calendar.DAY_OF_WEEK); //一周的第几天，sunday是第一天

        return firstWeekDay == dayOfWeek;
    }


    /**
     * 根据传入的时间判断是否是星期六
     *
     * @param currentTime
     * @return
     */
    public static boolean isSaturday(long currentTime) {
        Calendar cs = new GregorianCalendar();
        cs.setTimeInMillis(currentTime);

        cs.setFirstDayOfWeek(Calendar.SATURDAY);
        int firstWeekDay = cs.getFirstDayOfWeek();
        int dayOfWeek = cs.get(Calendar.DAY_OF_WEEK); //一周的第几天，sunday是第一天

        return firstWeekDay == dayOfWeek;
    }


    /**
     * 根据传入的时间判断是否是星期天
     *
     * @param currentTime
     * @return
     */
    public static boolean isSunday(long currentTime) {
        Calendar cs = new GregorianCalendar();
        cs.setTimeInMillis(currentTime);

        cs.setFirstDayOfWeek(Calendar.SUNDAY);
        int firstWeekDay = cs.getFirstDayOfWeek();
        int dayOfWeek = cs.get(Calendar.DAY_OF_WEEK); //一周的第几天，sunday是第一天

        return firstWeekDay == dayOfWeek;
    }

    /**
     * 判断当前是当前周的第几天
     *
     * @return
     */
    public static int dayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SATURDAY);
        long time = calendar.getTimeInMillis();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取一周的时间段
     *
     * @param inStall_time
     * @param locale       获取LanguageSettingUtil中存在的Locale
     * @return
     */
    public static String getWeekTime(long inStall_time, Locale locale) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd E", locale);
        String endDate = sdf.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String startDate = sdf.format(calendar.getTime());
        if (inStall_time > calendar.getTimeInMillis())
            startDate = sdf.format(new Date(inStall_time));

        return startDate + " - " + endDate;
    }

    /**
     * 根据传入的时间,获取当前的时间段
     *
     * @param startTime
     * @param locale    获取LanguageSettingUtil中存在的Locale
     * @return
     */
    public static String getTimeBucket(long startTime, long endTime, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd E", locale);
        String startDate = sdf.format(startTime);
        String endDate = sdf.format(endTime);

        return startDate + "～" + endDate;
    }


    /***
     * 给定两个时间，计算两个时间之间差几天
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 差几天
     */
    public static int diffDays(long start, long end) {
        Calendar cs = new GregorianCalendar();
        cs.setTimeInMillis(start);

        Calendar ce = new GregorianCalendar();
        ce.setTimeInMillis(end);

        cs.set(Calendar.HOUR_OF_DAY, 0);
        cs.set(Calendar.MINUTE, 0);
        cs.set(Calendar.SECOND, 0);
        cs.set(Calendar.MILLISECOND, 0);

        ce.set(Calendar.HOUR_OF_DAY, 0);
        ce.set(Calendar.MINUTE, 0);
        ce.set(Calendar.SECOND, 0);
        ce.set(Calendar.MILLISECOND, 0);

        return Math.abs((int) ((ce.getTimeInMillis() - cs.getTimeInMillis()) / (24 * 3600 * 1000)));
    }

    /**
     * 获取推算的时间
     *
     * @param position
     * @param format
     * @param locale
     * @return
     */
    public static String getCalculateTime(int position, String format, Locale locale) {
        Calendar calendar = Calendar.getInstance();
        long dayBefore = 1000l * 60 * 60 * 24 * position;
        long time = calendar.getTimeInMillis() - dayBefore;
        return formatDate(time, format, locale);
    }

    public static long getCalculateTimeInMillis(int position) {
        Calendar calendar = Calendar.getInstance();
        long dayBefore = 1000l * 60 * 60 * 24 * position;
        return calendar.getTimeInMillis() - dayBefore;
    }

    /**
     * 计算当前时间距安装时间多少天
     *
     * @param insTallTime
     * @return
     */
    public static int getDistanceInstallTime(long insTallTime) {
        return diffDays(System.currentTimeMillis(), insTallTime);
    }

    /**
     * 获取当月的天数
     *
     * @return
     */
    public static int getDayOfMonth() {
        Calendar aCalendar = Calendar.getInstance();
        return aCalendar.getActualMaximum(Calendar.DATE);
    }

    /**
     * 获取当月是几号
     *
     * @return
     */
    public static int getCurrentDayOfMonth() {
        Calendar aCalendar = Calendar.getInstance();
        return aCalendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取结算日时间
     *
     * @param day 具体天数
     * @return
     */
    public static long getAssignTime(int day) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int currentMonthLastDay = getCurrentMonthLastDay();
        day = day > currentMonthLastDay ? currentMonthLastDay : day;
        month = day > calendar.get(Calendar.DAY_OF_MONTH) ? month : month + 1;

        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return calendar.getTimeInMillis();
    }


    /**
     * 取得当月天数
     */
    public static int getCurrentMonthLastDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static int getCurrentMonth() {
        Calendar aCalendar = Calendar.getInstance();
        return aCalendar.get(Calendar.MONTH);
    }

    /**
     * 判断传入的时间和当前时间是否是同一天
     *
     * @param time
     * @return
     */
    public static boolean isSameToady(long time) {
        return diffDays(System.currentTimeMillis(), time) == 0 ? true : false;
    }

    //毫秒轉化為時間長度 by huangwen
    public static String timeUtil(Long use_time, Context context) {
        String s = "s";
        String m = "m";
        String h = "h";
        String d = "d";
        String language = LanguageSettingUtil.getLocale(mContext).getLanguage();
        if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
            s = "秒";
            m = "分";
            h = "時";
            d = "天";
        }
        int ss = 1000;
        int min = ss * 60;
        int hour = min * 60;
        int day = 24 * hour;
        int dayCount = (int) (use_time / day);
        int hourCount = (int) (use_time - dayCount * day) / hour;
        int minCount = (int) ((use_time - dayCount * day) - hourCount * hour) / min;
        int ssCount = (int) (((use_time - dayCount * day) - hourCount * hour) - minCount * min) / ss;
        int ms = (int) (((use_time - dayCount * day) - hourCount * hour) - minCount * min) - ssCount * ss;
        if (dayCount == 0 && hourCount == 0 && minCount == 0 && ssCount == 0) {
            return Stringutil.formatNumber(context, 0) + s;
        } else if (dayCount == 0 && hourCount == 0 && minCount == 0) {
            return Stringutil.formatNumber(context, ssCount) + s;
        } else if (dayCount == 0 && hourCount == 0) {
            return Stringutil.formatNumber(context, minCount) + m + Stringutil.formatNumber(context, ssCount) + s;
        } else if (dayCount == 0) {
            return Stringutil.formatNumber(context, hourCount) + h + Stringutil.formatNumber(context, minCount) + m + Stringutil.formatNumber(context, ssCount) + s;
        } else {
            return Stringutil.formatNumber(context, dayCount) + d + Stringutil.formatNumber(context, hourCount) + h + Stringutil.formatNumber(context, minCount) + m + Stringutil.formatNumber(context, ssCount) + s;
        }
    }

    /**
     * 获取时间数组，数组内一个是yyyy/MM/dd，一个是HH:mm
     */
    public static String[] getTimeArray(Context context, long time, Locale locale) {
        String date_ymd = null;//年月日
        String date_hm = null;//小时分
        String date_24 = formatDate(time, "yyyy/MM/dd HH:mm", locale);
        String date_12 = formatDate(time, "yyyy/MM/dd hh:mm", locale);
        String[] dataArray = null;
        String defaultLanguage = LanguageSettingUtil.getInstance(ApplicationEx.getInstance().getBaseContext()).getSystemLang();
        //获得内容提供者
        ContentResolver mResolver = context.getContentResolver();
        //获得系统时间制
        String timeFormat = android.provider.Settings.System.getString(mResolver, android.provider.Settings.System.TIME_12_24);
        //判断时间制
        if ("24".equals(timeFormat)) {
            //24小时制
            dataArray = date_24.split(" ");//dataArray[0]为年月日，dataArray[1]为HH:mm
            if (DateUtils.isToday(time)) {
                date_ymd = context.getResources().getString(R.string.call_log_date_today);
            } else if (DateUtils.isYesterday(time)) {
                date_ymd = context.getResources().getString(R.string.device_time_one_day);
            } else if (isCurrentWeek(time)) {
                date_ymd = getDayInCurrentWeek(context, time);
            } else if (isCurrentYear(time)) {
                if (defaultLanguage.equals("zh") || defaultLanguage.equals("zh_CN") || defaultLanguage.equals("zh_TW")) {
                    String[] ymd = formatData(time, Locale.SIMPLIFIED_CHINESE).split("年");
                    if (ymd != null && ymd.length > 1) {
                        date_ymd = ymd[1];
                    } else {
                        ymd = formatData(time, Locale.TRADITIONAL_CHINESE).split("年");
                        if (ymd.length > 1)
                            date_ymd = ymd[1];
                    }

                    LogUtil.d("dateUtil", "getTimeArray defaultLanguage:" + defaultLanguage + ",date_ymd:" + formatData(time, Locale.TAIWAN));
                } else {
                    date_ymd = formatData(time, Locale.ENGLISH).split(",")[0];
                }
            } else {
                if (defaultLanguage.equals("zh")) {
                    date_ymd = formatData(time, Locale.TAIWAN);
                } else {
                    date_ymd = formatData(time, Locale.ENGLISH);
                }
            }

            date_hm = dataArray[1];
        } else {
            //12小时制
            dataArray = date_12.split(" ");//dataArray[0]为年月日，dataArray[1]为HH:mm
            if (DateUtils.isToday(time)) {
                date_ymd = context.getResources().getString(R.string.call_log_date_today);
            } else if (DateUtils.isYesterday(time)) {
                date_ymd = context.getResources().getString(R.string.device_time_one_day);
            } else if (isCurrentWeek(time)) {
                date_ymd = getDayInCurrentWeek(context, time);
            } else if (isCurrentYear(time)) {
                if (defaultLanguage.equals("zh") || defaultLanguage.equals("zh_CN") || defaultLanguage.equals("zh_TW")) {
                    String[] ymd = formatData(time, Locale.SIMPLIFIED_CHINESE).split("年");
                    if (ymd != null && ymd.length > 1) {
                        date_ymd = ymd[1];
                    } else {
                        ymd = formatData(time, Locale.TRADITIONAL_CHINESE).split("年");
                        if (ymd.length > 1)
                            date_ymd = ymd[1];
                    }
                } else {
                    date_ymd = formatData(time, Locale.ENGLISH).split(",")[0];
                }
            } else {
                if (defaultLanguage.equals("zh")) {
                    date_ymd = formatData(time, Locale.TAIWAN);
                } else {
                    date_ymd = formatData(time, Locale.ENGLISH);
                }
            }

            if (Integer.valueOf(date_24.split(" ")[1].substring(0, 1)) > 0) {
                if (Integer.valueOf(date_24.split(" ")[1].substring(0, 2)) >= 12) {
                    date_hm = dataArray[1] + " PM";
                } else {
                    date_hm = dataArray[1] + " AM";
                }
            } else {
                date_hm = dataArray[1] + " AM";
            }

            LogUtil.d("getTimeArray", "date_12 " + date_12.split(" ")[1] + ",date_24 " + date_24.split(" ")[1] + "," + date_24.split(" ")[1].indexOf(0, 1) + "," + date_24.split(" ")[1].indexOf(0, 2));
        }
        return new String[]{date_ymd, date_hm};
    }

    public static boolean isToday(long time) {
        if (System.currentTimeMillis() - time < 24 * 60 * 60 * 1000) {
            Calendar now = Calendar.getInstance();
            Calendar tmpCalendar = Calendar.getInstance();
            tmpCalendar.setTimeInMillis(time);
            return now.get(Calendar.DAY_OF_YEAR) == tmpCalendar
                    .get(Calendar.DAY_OF_YEAR);

        }
        return false;
    }

    public static boolean isYesterday(long time) {
        if (System.currentTimeMillis() - time < 48 * 60 * 60 * 1000) {
            Calendar now = Calendar.getInstance();
            Calendar tmpCalendar = Calendar.getInstance();
            tmpCalendar.setTimeInMillis(time);
            return (now.get(Calendar.DAY_OF_YEAR) - 1) == tmpCalendar
                    .get(Calendar.DAY_OF_YEAR);

        }
        return false;
    }

    public static boolean isCurrentWeek(long time) {
        if (System.currentTimeMillis() - time < 7 * 24 * 60 * 60 * 1000) {
            Calendar calendar = Calendar.getInstance();
            int week = calendar.get(Calendar.WEEK_OF_MONTH);
            Calendar tmpCalendar = Calendar.getInstance();
            tmpCalendar.setTimeInMillis(time);
            int weekInTime = tmpCalendar.get(Calendar.WEEK_OF_MONTH);
            if (week == weekInTime) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCurrentYear(long time) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        Calendar tmpCalendar = Calendar.getInstance();
        tmpCalendar.setTimeInMillis(time);
        int yearInTime = tmpCalendar.get(Calendar.YEAR);
        if (year == yearInTime) {
            return true;
        }
        return false;
    }

    public static boolean isAfter18Clock() {
        long currentTime = System.currentTimeMillis();
        String date_24 = formatDate(currentTime, "yyyy/MM/dd HH:mm", Locale.ENGLISH);
        String[] HHmm = date_24.split(" ");
        int hour = Integer.parseInt(HHmm[1].substring(0, 2));
        LogUtil.d("dadwadadad", "hour：" + hour + ",HHmm:" + HHmm[1] + ",date_24:" + date_24);
        if (hour > 17) {
            return true;
        }
        return false;
    }

    /**
     * 获取某个时间为当前周的周几
     */
    public static String getDayInCurrentWeek(Context context, long time) {
        //获取当前时间为本月的第几周
        Calendar tmpCalendar = Calendar.getInstance();
        tmpCalendar.setTimeInMillis(time);
        //获取当前时间为本周的第几天
        int day = tmpCalendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 7;
        } else {
            day = day - 1;
        }
        switch (day) {
            case 7:
                return context.getResources().getString(R.string.call_log_date_sunday);
            case 6:
                return context.getResources().getString(R.string.call_log_date_saturday);
            case 5:
                return context.getResources().getString(R.string.call_log_date_friday);
            case 4:
                return context.getResources().getString(R.string.call_log_date_thursday);
            case 3:
                return context.getResources().getString(R.string.call_log_date_wednesday);
            case 2:
                return context.getResources().getString(R.string.call_log_date_tuesday);
            case 1:
                return context.getResources().getString(R.string.call_log_date_monday);
        }
        return null;
    }

    public static int getIntDayInCurrentWeek(Context context, long time) {
        //获取当前时间为本月的第几周
        Calendar tmpCalendar = Calendar.getInstance();
        tmpCalendar.setTimeInMillis(time);
        //获取当前时间为本周的第几天
        int day = tmpCalendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 7;
        } else {
            day = day - 1;
        }
        return day;
    }

    public static String getWmdForTime(long date_time, Locale locale) {
        String datsString = null;
        if (isCurrentYear(date_time)) {
            String language = locale.getLanguage();
            SimpleDateFormat df = null;
            if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
                df = new SimpleDateFormat("MM月dd日EEEE", locale);
            } else {
                df = new SimpleDateFormat("E,MMM dd", locale);
            }
            Date date = new Date(date_time);
//        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,Locale.ENGLISH);
            datsString = df.format(date);
        } else {
            String language = locale.getLanguage();
            SimpleDateFormat df = null;
            if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
                df = new SimpleDateFormat("yyyy年MM月dd日EEEE", locale);
            } else {
                df = new SimpleDateFormat("E,MMM dd,yyyy", locale);
            }
            Date date = new Date(date_time);
//            DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH);
            datsString = df.format(date);
        }
        return datsString;
    }

    public static String getYmdForTime(long date_time, Locale locale) {
        String datsString = null;
        if (isToday(date_time)) {
            datsString = mContext.getResources().getString(R.string.call_log_date_today);
        } else if (isYesterday(date_time)) {
            datsString = mContext.getResources().getString(R.string.device_time_one_day);
        } else if (isCurrentWeek(date_time)) {
            datsString = getDayInCurrentWeek(mContext, date_time);
        } else if (isCurrentYear(date_time)) {
            String language = locale.getLanguage();
            SimpleDateFormat df = null;
            if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
                df = new SimpleDateFormat("MM月dd日", locale);
            } else {
                df = new SimpleDateFormat("MMM dd", locale);
            }
            Date date = new Date(date_time);
//        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,Locale.ENGLISH);
            datsString = df.format(date);
        } else {
            String language = locale.getLanguage();
            SimpleDateFormat df = null;
            if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
                df = new SimpleDateFormat("yyyy年MM月dd日", locale);
            } else {
                df = new SimpleDateFormat("MMM dd,yyyy", locale);
            }
            Date date = new Date(date_time);
//            DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH);
            datsString = df.format(date);
        }
        return datsString;
    }

    public static String getHmdForTime(long date_time, Locale locale) {
        String datsString = null;
        String pattern;
        if (is24Hour()) {
            pattern = "HH:mm";
        } else {
            String language = locale.getLanguage();
            if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
                pattern = "a h:mm";
            } else {
                pattern = "h:mm a";
            }
        }
        SimpleDateFormat df = new SimpleDateFormat(pattern, locale);
        Date date = new Date(date_time);
//        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,
//                Locale.ENGLISH);
        datsString = df.format(date);
        return datsString;
    }

    public static String getHmsForTime(long date_time, Locale locale) {
        String datsString = null;
        if (!isToday(date_time)) {
            datsString = getYmdForTime(date_time, locale);
        } else {
            String pattern;
            if (is24Hour()) {
                pattern = "HH:mm:ss";
            } else {
                String language = locale.getLanguage();
                if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
                    pattern = "a h:mm:ss";
                } else {
                    pattern = "h:mm:ss a";
                }

            }
            SimpleDateFormat df = new SimpleDateFormat(pattern, locale);
            Date date = new Date(date_time);
//        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,
//                Locale.ENGLISH);
            datsString = df.format(date);
        }

        return datsString;
    }

    public static String getHmsForTime2(long date_time, Locale locale) {
        String datsString = null;
        String pattern;
        if (is24Hour()) {
            pattern = "yyyy/MM/dd HH:mm:ss";
        } else {
            String language = locale.getLanguage();
            if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
                pattern = "yyyy/MM/dd a h:mm:ss ";
            } else {
                pattern = "yyyy/MM/dd h:mm:ss a";
            }
        }
        SimpleDateFormat df = new SimpleDateFormat(pattern, locale);
        Date date = new Date(date_time);
//        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,
//                Locale.ENGLISH);
        datsString = df.format(date);

        return datsString;
    }

    public static String getHmForTime(long date_time, Locale locale) {
        String datsString = null;
        if (!isToday(date_time)) {
            datsString = getYmdForTime(date_time, locale);
        } else {
            String pattern = null;
            if (is24Hour()) {
                pattern = "HH:mm";
            } else {
                String language = locale.getLanguage();
                if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
                    pattern = "a h:mm";
                } else {
                    pattern = "h:mm a";
                }
            }
            SimpleDateFormat df = new SimpleDateFormat(pattern, locale);
            Date date = new Date(date_time);
//        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,
//                Locale.ENGLISH);
            datsString = df.format(date);
        }

        return datsString;
    }

    public static String getYmdForTime2(long date_time, Locale locale) {
        String datsString = null;
        if (isCurrentYear(date_time)) {
            String language = locale.getLanguage();
            SimpleDateFormat df = null;
            if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
                df = new SimpleDateFormat("MM月dd日", locale);
            } else {
                df = new SimpleDateFormat("MMM dd", locale);
            }
            Date date = new Date(date_time);
//        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,Locale.ENGLISH);
            datsString = df.format(date);
        } else {
            String language = locale.getLanguage();
            SimpleDateFormat df = null;
            if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
                df = new SimpleDateFormat("yyyy年MM月dd日", locale);
            } else {
                df = new SimpleDateFormat("MMM dd,yyyy", locale);
            }
            Date date = new Date(date_time);
//            DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH);
            datsString = df.format(date);
        }
        return datsString;
    }

    /**
     * 判断是否是24小时制
     */
    public static boolean is24Hour() {
        //获得内容提供者
        ContentResolver mResolver = mContext.getContentResolver();
        //获得系统时间制
        String timeFormat = android.provider.Settings.System.getString(mResolver, android.provider.Settings.System.TIME_12_24);
        if ("24".equals(timeFormat)) {
            return true;
        } else {
            return false;
        }
    }

    public static long getLongForTime(String format, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long time = 0;
        //得到毫秒数
        try {
            time = sdf.parse(date).getTime();
        } catch (ParseException e) {
            LogUtil.e("dateutils", "getLongForTime e:" + e.getMessage());
        }
        LogUtil.d("dateutils", "getLongForTime time:" + time);
        return time;
    }


    public static String getHmForTime(long date_time, Locale locale, boolean is24Hour) {
        String datsString = null;
        String pattern;
        if (is24Hour) {
            pattern = "HH:mm";
        } else {
            String language = locale.getLanguage();
            if (language.equals("zh") || language.equals("zh_CN") || language.equals("zh_TW")) {
                pattern = "a h:mm";
            } else {
                pattern = "h:mm a";
            }
        }
        SimpleDateFormat df = new SimpleDateFormat(pattern, locale);
        Date date = new Date(date_time);
//        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,
//                Locale.ENGLISH);
        datsString = df.format(date);
        return datsString;
    }

    /**
     * 判断是否在该段时间内，只适用于当天
     *
     * @param startHour 开始的小时。24小时制
     * @param endHour   结束的小时，24小时制
     */
    public static boolean isInHourTheDay(int startHour, int endHour) {
        Calendar mCalendar = Calendar.getInstance();
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= startHour && hour < endHour) {
            return true;
        }
        return false;
    }

}
