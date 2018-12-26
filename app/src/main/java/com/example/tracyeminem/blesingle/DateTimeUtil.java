package com.example.tracyeminem.blesingle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TimeZone;

/**
 * Created by barker on 16-7-5.
 */
public final class DateTimeUtil {

    public static final long ONE_MINUTE = 60;
    public static final long ONE_HOUR = 3600;
    public static final long ONE_DAY = 86400;
    public static final long ONE_MONTH = 2592000;
    public static final long ONE_YEAR = 31104000;

    public static String convertUnixTimeStampToLocalTime(Long timeStamp){
        Date time = new Date(timeStamp * 1000);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(calendar.getTime());
    }
    public static String convertUnixTimeStampToChineseTime(Long timeStamp){
        Date time = new Date(timeStamp * 1000);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static class DateComparator implements Comparator<Date> {
        @Override
        public int compare(Date o1, Date o2) {
            return o1.compareTo(o2);
        }
    }

    public static String convertUTCDateStringToLocalDateString(String UTCString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date createdAtDate = dateFormat.parse(UTCString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createdAtDate);
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        return dateFormat.format(calendar.getTime());
    }

    public static Date convertToStartOfLocalDate(long timestamp) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        Date date = new Date(timestamp * 1000L);
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        return date;
    }

    public static boolean isMidNightTime(long timestamp) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        Date date = new Date(timestamp * 1000L);
        if (date.getHours() == 0 && date.getMinutes() == 0 && date.getSeconds() == 0) {
            return true;
        }
        return false;
    }
    public static String getDateTimeForThread(String createdAt) throws ParseException {
        String[] weeks = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat writeFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm");


        Date createdAtDate = dateFormat.parse(createdAt);
        createdAt = writeFormat.format(createdAtDate);

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesteradayDate = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date oneWeekBeforeDate = calendar.getTime();

        String result = null;

        if(createdAtDate.after(yesteradayDate)){
            result = createdAt.substring(11, 16);
        }else if(createdAtDate.after(oneWeekBeforeDate)){
            calendar.setTime(createdAtDate);
            int week_index = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if(week_index<0){
                week_index = 0;
            }
            result =  weeks[week_index];
        }else if(createdAtDate.before(oneWeekBeforeDate)){
            result =  createdAt.substring(0, 10);
        }
        return result;
    }

    public static String date2TimeStamp(String date_str){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return String.valueOf(sdf.parse(date_str).getTime()/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String secondsToMinute(long seconds){
        String timeString;
        if(seconds >= 60){
            timeString = seconds / ONE_MINUTE + "min" + (seconds % ONE_MINUTE == 0?"":seconds % ONE_MINUTE+"s");
        }else {
            timeString = seconds + "s";
        }
        return timeString;
    }

    public static Long getCurrentTimestampMilli(){
        return System.currentTimeMillis() - System.currentTimeMillis() % 1000;
    }

    public static Long getCurrentTimestamp(){
        return (new Date()).getTime() / 1000L;
    }

    public static String getDateTimeForChatting(Long timeStamp) {
        Date temp = new Date(timeStamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(temp);

        long time = timeStamp / 1000;
        long now = new Date().getTime() / 1000;
        long ago = now - time;
        long realAgo = getTodayMinTimeMillis()/1000 - time;
        if(ago < 10){
            return "刚刚";
        }else if(ago < ONE_MINUTE){
            return ago + "秒前";
        }else if (ago <= ONE_HOUR)
            return ago / ONE_MINUTE + "分钟前";
        else if (ago <= ONE_DAY)
            return ago / ONE_HOUR + "小时前";
        else if (realAgo <= ONE_DAY ) {
            return "昨天" + calendar.get(Calendar.HOUR_OF_DAY) + "点";}
        else if (realAgo <= ONE_DAY * 2)
            return "前天" + calendar.get(Calendar.HOUR_OF_DAY) + "点";
        else if (realAgo <= ONE_MONTH) {
            long day = ago / ONE_DAY;
            return day + "天前" + calendar.get(Calendar.HOUR_OF_DAY) + "点";
        } else if (realAgo <= ONE_YEAR) {
            long month = ago / ONE_MONTH;
            return month + "个月前";
        } else {
            long year = ago / ONE_YEAR;
            return year + "年前";
        }

    }

    public static String GroupChatBannedTime(Long timeStamp){
        Date temp = new Date(timeStamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(temp);

        long time = timeStamp / 1000;
        long now = new Date().getTime() / 1000;
        long future = time - now;
        if(future < 0 ){
            return null;
        }
        if(future < ONE_MINUTE ){
            return "禁言中，约"+future+"秒后解禁";
        }else if (future >= ONE_MINUTE && future <ONE_HOUR){
            return "禁言中，约"+future / ONE_MINUTE+"分钟后解禁";
        }else if(future >= ONE_HOUR && future <ONE_DAY){
            return "禁言中，约"+future / ONE_HOUR+"小时后解禁";
        }else if (future >= ONE_DAY && future < ONE_DAY *3){
            return "禁言中，约"+future / ONE_DAY+"天后解禁";
        }else {
            return "您已经被永久禁言";
        }
    }

    public static String DiscoveryTimeFormat(Long timeStamp){
        Date temp = new Date(timeStamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(temp);

        long time = timeStamp / 1000;
        long now = new Date().getTime() / 1000;
        long ago = now - time;
        long realAgo = getTodayMinTimeMillis()/1000 - time;
        if(ago < ONE_MINUTE * 1){
            return ago + "秒前";
        }else if (ago > ONE_MINUTE*1 && ago <= ONE_HOUR)
            return ago / ONE_MINUTE + "分钟前";
        else if (ago <= ONE_DAY)
            return ago / ONE_HOUR + "小时前";
        else if (realAgo <= ONE_DAY ) {
            return "昨天" + calendar.get(Calendar.HOUR_OF_DAY) + "点";}
        else if (realAgo <= ONE_DAY * 2)
            return "前天" + calendar.get(Calendar.HOUR_OF_DAY) + "点";
        else if (realAgo <= ONE_MONTH) {
            long day = ago / ONE_DAY;
            return day + "天前" + calendar.get(Calendar.HOUR_OF_DAY) + "点";
        } else if (realAgo <= ONE_YEAR) {
            long month = ago / ONE_MONTH;
            return month + "个月前";
        } else {
            long year = ago / ONE_YEAR;
            return year + "年前";
        }
    }

    public static String calculateChatTime(Long lastSendTime) throws Exception {
        long minToday = getTodayMinTimeMillis()/1000;
        Long calculateTime = minToday - lastSendTime;
        SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String d = format.format(lastSendTime*1000);
        Date date=format.parse(d);
        if(calculateTime < 0){
            return formatDateTime(date);
        }else if(calculateTime > 0 && calculateTime < ONE_DAY){
            return "昨天"+" "+formatDateTime(date);
        }else if(calculateTime >= ONE_DAY && calculateTime < ONE_DAY*7){
            return getWeekOfDate(date)+" "+formatDateTime(date);
        }else {
            return d.substring(5,10)+" "+formatDateTime(date);
        }
    }

    public static String convertTimeToYMD(Long time){
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        String date = format.format(time * 1000);
        return date;
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }



    private static Calendar mCalendar = Calendar.getInstance();

    public static long getTodayMinTimeMillis() {

        long currTime = System.currentTimeMillis();
        mCalendar.setTime(new Date(currTime));

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendar.set(year, month, day, 0, 0, 0);
        long minToday = mCalendar.getTimeInMillis();

        return minToday;
    }

    public static long getTodayMaxTimeMillis() {
        long currTime = System.currentTimeMillis();
        mCalendar.setTime(new Date(currTime));

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendar.set(year, month, day, 23, 59, 59);
        long minToday = mCalendar.getTimeInMillis();

        return minToday;
    }

    public static String getGrapOnDemandStartTime(long timeStamp){
        String day = null;
        long minToday = getTodayMinTimeMillis()/1000;
        long maxToday = getTodayMaxTimeMillis()/1000;
        if(minToday<=timeStamp && timeStamp <= maxToday){
            day = "今天";
        }else if(maxToday < timeStamp && timeStamp <= maxToday+ONE_DAY){
            day = "明天";
        }else if(maxToday + ONE_DAY <timeStamp && timeStamp <= maxToday+ONE_DAY*2){
            day = "后天";
        }else {
            day = "昨天";
        }
        SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        SimpleDateFormat fmtTime = new SimpleDateFormat("hh:mm");
        String data = format.format(timeStamp*1000);
        Date date = null;
        try {
            date = format.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int hour = date.getHours();
        String sign = "上午";
        if (hour < 6) {
            sign = "凌晨";
        } else if (hour == 12) {
            sign = "中午";
        } else if (hour > 12 && hour < 19) {
            sign = "下午";
        } else if (hour >= 19){
            sign = "晚上";
        }
        return  day+sign+fmtTime.format(date)+"开始";
    }

    public static String getDateForTransaction(Long timeStamp) {
        Date temp = new Date(timeStamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(temp);
        long minToday = getTodayMinTimeMillis()/1000;
        long maxToday = getTodayMaxTimeMillis()/1000;
        int dow = calendar.get(Calendar.DAY_OF_WEEK);

        long time = timeStamp / 1000;
        long now = new Date().getTime() / 1000;
        long ago = now - time;

        if (minToday<=timeStamp/1000 && timeStamp/1000 <= maxToday)
            return "今天";
        else if (timeStamp/1000 <minToday && timeStamp/1000 >= minToday-ONE_DAY)
            return "昨天";
        else if(dow == Calendar.MONDAY)
            return "周一";
        else if(dow == Calendar.TUESDAY)
            return "周二";
        else if(dow == Calendar.WEDNESDAY)
            return "周三";
        else if(dow == Calendar.THURSDAY)
            return "周四";
        else if(dow == Calendar.FRIDAY)
            return "周五";
        else if(dow == Calendar.SATURDAY)
            return "周六";
        else if(dow == Calendar.SUNDAY)
            return "周日";

        return null;
    }

    public static String getTimeForTransaction(Long timeStamp) {
        Date temp = new Date(timeStamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(temp);
        long time = timeStamp / 1000;
        long now = new Date().getTime() / 1000;
        long ago = now - time;
        String minute;
        if(calendar.get(Calendar.MINUTE)<10){
            minute = "0"+calendar.get(Calendar.MINUTE);
        }else {
            minute = String.valueOf(calendar.get(Calendar.MINUTE));
        }
        if (ago <= ONE_DAY * 2)
            return calendar.get(Calendar.HOUR_OF_DAY) + ":" + minute;
        else
            return String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String formatUnixTime(long timestamp) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        return formatDateTime(new Date(timestamp * 1000L));
    }

    public static String formatDateTime(Date time) {
        SimpleDateFormat fmtTime = new SimpleDateFormat("hh:mm");
        String sign = "上午";
        int hour = time.getHours();
        if (hour < 6) {
            sign = "凌晨";
        } else if (hour == 12) {
            sign = "中午";
        } else if (hour > 12 && hour < 19) {
            sign = "下午";
        } else if (hour >= 19){
            sign = "晚上";
        }
        return sign +" " + fmtTime.format(time);
    }

    public static String formatDateTimeForSelectedBooking(Date time) {
        SimpleDateFormat fmtDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fmtTime = new SimpleDateFormat("hh:mm");
        String sign = "上午";
        int hour = time.getHours();
        if (hour < 6) {
            sign = "凌晨";
        } else if (hour == 12) {
            sign = "中午";
        } else if (hour > 12 && hour < 19) {
            sign = "下午";
        } else if (hour >= 19){
            sign = "晚上";
        }
        return fmtDate.format(time)+" "+ sign +" " + fmtTime.format(time);
    }

    public static LinkedHashMap<String, Long> generateOndemandBookingSlot(int days){
        long firstAvailableSlot;
        long lastAvailableSlot;
        if(days == 0){
            firstAvailableSlot = (DateTimeUtil.getCurrentTimestamp() / 1800 + 1) * 1800;
            lastAvailableSlot = (DateTimeUtil.getCurrentTimestamp() / 86400 + 1) * 86400 - 1800 - 28800;
        }else{
            firstAvailableSlot = (DateTimeUtil.getCurrentTimestamp() / 86400 + days) * 86400 - 28800;
            lastAvailableSlot = firstAvailableSlot + 86400 - 1800;
        }
        LinkedHashMap<String, Long> map = new LinkedHashMap<>();
        for(long slot = firstAvailableSlot; slot <= lastAvailableSlot; slot+=1800){
            map.put(getHumanReadableTimeFromTimestamp(slot*1000), slot);
        }
        return map;
    }

    public static String getHumanReadableTimeFromTimestamp(long timestamp){
        Date date = new Date(timestamp);
        SimpleDateFormat fmtTime = new SimpleDateFormat("hh:mm");
        int hour = date.getHours();
        String sign = "上午";
        if (hour < 6) {
            sign = "凌晨";
        } else if (hour == 12) {
            sign = "中午";
        } else if (hour > 12 && hour < 19) {
            sign = "下午";
        } else if (hour >= 19){
            sign = "晚上";
        }
        return  sign + fmtTime.format(date);
    }
}
