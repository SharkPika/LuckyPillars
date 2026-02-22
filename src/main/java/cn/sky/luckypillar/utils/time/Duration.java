package cn.sky.luckypillar.utils.time;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Duration(long value) {

    public static Duration fromString(String source) {
        if (source.equalsIgnoreCase("perm") || source.equalsIgnoreCase("permanent")) {
            return new Duration(Integer.MAX_VALUE);
        }

        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = Pattern.compile("\\d+\\D+").matcher(source);

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
        return new Duration(!found ? -1 : totalTime * 1000);
    }

}
