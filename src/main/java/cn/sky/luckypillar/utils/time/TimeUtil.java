package cn.sky.luckypillar.utils.time;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtil {
    private static final String HOUR_FORMAT = "%02d:%02d:%02d";
    private static final String MINUTE_FORMAT = "%02d:%02d";
    private static final SimpleDateFormat TIME_TO_REQUEST_FORMAT = new SimpleDateFormat("MM-dd");

    private TimeUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static long getMinecraftDay(long mills) {
        return Math.floorDiv(mills, 36 * 60 * 1000L);
    }

    public static long getMinecraftDay() {
        return getMinecraftDay(System.currentTimeMillis());
    }

    //获取游戏内昼夜时间 (0 ~ 24000 其中0~12000为白天 12000~24000为黑夜)
    public static long getMinecraftTick(long mills) {
        //0~36min
        long time = mills % (36 * 60 * 1000);
        double percent;
        if (time <= 24 * 60 * 1000) { //0~24min
            percent = ((double) time) / (24 * 60 * 1000);
        } else {
            percent = 1 + ((double) time - 24 * 60 * 1000) / (12 * 60 * 1000);
        }
        return Double.valueOf(percent * 12000).longValue();
    }

    public static long getMinecraftTick() {
        return getMinecraftTick(System.currentTimeMillis());
    }

    public static Date getNextDayDate() {
        final long daySpan = 24 * 60 * 60 * 1000;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd '00:00:00'");
        Date startTime;
        try {
            startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdf.format(System.currentTimeMillis()));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        if (System.currentTimeMillis() > startTime.getTime())
            startTime = new Date(startTime.getTime() + daySpan);

        return startTime;
    }

    /**
     * 判断当前时间距离第二天凌晨的毫秒数
     *
     * @return 返回值单位为[ms:毫秒]
     */
    public static Long getMillisecondNextEarlyMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() - System.currentTimeMillis();
    }

    public static String millisToTimer(long millis) {
        long seconds = millis / 1000L;

        if (seconds > 3600L) {
            return String.format(HOUR_FORMAT, seconds / 3600L, seconds % 3600L / 60L, seconds % 60L);
        } else {
            return String.format(MINUTE_FORMAT, seconds / 60L, seconds % 60L);
        }
    }

    /**
     * Return the amount of seconds from milliseconds.
     * Note: We explicitly use 1000.0F (float) instead of 1000L (long).
     *
     * @param millis the amount of time in milliseconds
     * @return the seconds
     */
    public static String millisToSeconds(long millis) {
        return new DecimalFormat("#0.0").format(millis / 1000.0F);
    }

    public static String dateToString(Date date, String secondaryColor) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return new SimpleDateFormat("MMM dd yyyy " + (secondaryColor == null ? "" : secondaryColor) +
                "(hh:mm aa zz)").format(date);
    }

    public static String dateToString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return (new SimpleDateFormat("yyyy年MM月dd日 EEEE HH:mm:ss")).format(date);
    }

    public static Timestamp addDuration(long duration) {
        return truncateTimestamp(new Timestamp(System.currentTimeMillis() + duration));
    }

    public static Timestamp truncateTimestamp(Timestamp timestamp) {
        if (timestamp.toLocalDateTime().getYear() > 2037) {
            timestamp.setYear(2037);
        }

        return timestamp;
    }

    public static Timestamp addDuration(Timestamp timestamp) {
        return truncateTimestamp(new Timestamp(System.currentTimeMillis() + timestamp.getTime()));
    }

    public static Timestamp fromMillis(long millis) {
        return new Timestamp(millis);
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String millisToRoundedTime(long millis) {
        millis += 1L;

        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;

        if (days > 0) {
            return days + " 天 " + (hours - 24 * days) + " 小时";
        } else if (hours > 0) {
            return hours + " 小时 " + (minutes - 60 * hours) + " 分钟";
        } else if (minutes > 0) {
            return minutes + " 分钟 " + (seconds - 60 * minutes) + " 秒";
        } else {
            return seconds + " 秒";
        }
    }

    public static long parseTime(String time) {
        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);

        while (matcher.find()) {
            String s = matcher.group();
            long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            String type = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];

            switch (type) {
                case "s":
                case "second":
                case "秒":
                    totalTime += value;
                    found = true;
                    break;
                case "m":
                case "min":
                case "minute":
                case "分":
                case "分钟":
                    totalTime += value * 60;
                    found = true;
                    break;
                case "h":
                case "hour":
                case "时":
                case "小时":
                    totalTime += value * 60 * 60;
                    found = true;
                    break;
                case "d":
                case "day":
                case "天":
                    totalTime += value * 60 * 60 * 24;
                    found = true;
                    break;
                case "w":
                case "week":
                case "周":
                case "星期":
                case "礼拜":
                    totalTime += value * 60 * 60 * 24 * 7;
                    found = true;
                    break;
                case "M":
                case "Month":
                case "月":
                    totalTime += value * 60 * 60 * 24 * 30;
                    found = true;
                    break;
                case "y":
                case "year":
                case "年":
                    totalTime += value * 60 * 60 * 24 * 365;
                    found = true;
                    break;
            }
        }

        return !found ?  -1 : totalTime * 1000;
    }
}
