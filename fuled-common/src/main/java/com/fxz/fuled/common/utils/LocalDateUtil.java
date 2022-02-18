package com.fxz.fuled.common.utils;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LocalDateUtil {
    public static final String DFS_MMdd = "MMdd";
    public static final String DFS_yyyyMMdd = "yyyyMMdd";
    public static final String DFS_yyMMdd = "yyMMdd";
    public static final String DF_MMdd = "MM-dd";
    public static final String DF_HHmm = "HH:mm";
    public static final String DF_MMddHH = "MM-dd HH";
    public static final String DF_yyyyMM = "yyyy-MM";
    public static final String DF_MM = "MM";
    public static final String DF_yyyyMMdd = "yyyy-MM-dd";
    public static final String DF_yyyyMMddHH = "yyyy-MM-dd HH";
    public static final String DF_yyyyMMddHHmm = "yyyy-MM-dd HH:mm";
    public static final String DF_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String DF_yyyyMMddHHmmssS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DF_yyyyMMddHHmmssFile = "yyyyMMddHHmmss";
    public static final String DF_yyyyMMddHHmmssSSSFile = "yyyyMMddHHmmssSSS";
    public static final String DF_HHmmss = "HH:mm:ss";
    private static String[] availableDF = new String[]{"MMdd", "yyyyMMdd", "yyMMdd", "MM-dd", "MM-dd HH", "HH:mm", "yyyy-MM-dd", "yyyy-MM", "yyyy-MM-dd HH", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS", "HH:mm:ss", "yyyyMMddHHmmss", "yyyyMMddHHmmssSSS"};
    private static final ZoneId zoneId = ZoneId.systemDefault();

    public LocalDateUtil() {
    }

    public static LocalDateTime stringToLocalDateTime(String dateString, String format) {
        return null != dateString && null != format ? LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(format)) : null;
    }

    public static String localDateTimeToString(LocalDateTime dateTime, String format) {
        return null != dateTime && null != format ? dateTime.format(DateTimeFormatter.ofPattern(format)) : null;
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return null == date ? null : date.toInstant().atZone(zoneId).toLocalDateTime();
    }

    public static LocalDate dateToLocalDate(Date date) {
        return null == date ? null : date.toInstant().atZone(zoneId).toLocalDate();
    }

    public static String localDateToString(LocalDate localDate, String format) {
        return null != localDate && null != format ? localDate.format(DateTimeFormatter.ofPattern(format)) : null;
    }

    public static int daysBetween(LocalDate start, LocalDate end) {
        return null != start && null != end ? start.until(end).getDays() : 0;
    }

    public static int monthsBetween(LocalDate start, LocalDate end) {
        return null != start && null != end ? start.until(end).getMonths() : 0;
    }

    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    private static boolean isNullString(String str) {
        return null == str || str.length() <= 0 || str.trim() == "" || str.trim().equals("") || "null".equals(str.trim().toLowerCase());
    }

    public static LocalDateTime strToDate(String strDate) throws ParseException {
        if (isNullString(strDate)) {
            return null;
        } else {
            int _L = strDate.trim().length();
            String format = "";
            switch (_L) {
                case 4:
                    format = "MMdd";
                    break;
                case 5:
                    if (strDate.indexOf("-") != -1) {
                        format = "MM-dd";
                    } else if (strDate.indexOf(":") != -1) {
                        format = "HH:mm";
                    }
                    break;
                case 6:
                    format = "yyMMdd";
                    break;
                case 7:
                    format = "yyyy-MM";
                    break;
                case 8:
                    if (strDate.indexOf("-") != -1) {
                        format = "MM-dd HH";
                    } else {
                        format = "yyyyMMdd";
                    }
                    break;
                case 9:
                case 11:
                case 12:
                case 14:
                case 15:
                case 17:
                case 18:
                case 20:
                default:
                    throw new ParseException("", 0);
                case 10:
                    format = "yyyy-MM-dd";
                    break;
                case 13:
                    format = "yyyy-MM-dd HH";
                    break;
                case 16:
                    format = "yyyy-MM-dd HH:mm";
                    break;
                case 19:
                    format = "yyyy-MM-dd HH:mm:ss";
                    break;
                case 21:
                    format = "yyyy-MM-dd HH:mm:ss.SSS";
            }

            return LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern(format));
        }
    }

    public static String dateToStr(LocalDateTime localDateTime, String format) {
        if (localDateTime == null) {
            return "";
        } else {
            for (int i = 0; i < availableDF.length; ++i) {
                if (availableDF[i].equals(format)) {
                    return localDateTime.format(DateTimeFormatter.ofPattern(format));
                }
            }

            return "";
        }
    }

    public static String dateRangeByMonthStr(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }

        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        return startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "" + endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static LocalDateTime getHourStart(LocalDateTime localDateTime) {
        return localDateTime.withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime getMinuteStart(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.withSecond(0).withNano(0);
    }

    public static LocalDateTime getDateMOPMonth(LocalDateTime localDateTime, int num) {
        return localDateTime == null ? null : localDateTime.plusMonths((long) num);
    }

    public static LocalDateTime getDateMOPHour(LocalDateTime localDateTime, int num) {
        return localDateTime == null ? null : localDateTime.plusHours((long) num);
    }

    public static LocalDateTime getDateMOPMinute(LocalDateTime localDateTime, int num) {
        return localDateTime == null ? null : localDateTime.plusMinutes((long) num);
    }

    public static LocalDateTime addDate(LocalDateTime localDateTime, int day) {
        return localDateTime == null ? null : localDateTime.plusDays((long) day);
    }

    public static String formatTime(long time) {
        long hour = time / 3600L;
        long tempTime = time % 3600L;
        long min = tempTime / 60L;
        long sec = tempTime % 60L;
        return hour + " H" + min + " M" + sec + " S";
    }

    public static String formatTime2(long milliSecondTime) {
        long minuteTime = milliSecondTime / 1000L;
        long tempTime1 = minuteTime % 86400L;
        long hour = minuteTime / 3600L;
        long tempTime = tempTime1 % 3600L;
        long min = tempTime / 60L;
        long sec = tempTime % 60L;
        StringBuffer sb = new StringBuffer();
        if (hour > 0L) {
            sb.append(hour);
        }

        if (min > 0L) {
            sb.append(min);
        }

        if (sec > 0L) {
            sb.append(sec);
        }

        return sb.length() == 0 ? "0" : sb.toString();
    }

    public static String getLogTime() {
        return dateToStr(getNow(), "yyyy-MM-dd HH:mm:ss.SSS");
    }

    public static long getIntervalDaysByTwoDate(LocalDate start, LocalDate end) {
        return null != start && null != end ? Duration.between(start, end).toDays() : 0L;
    }

    public static LocalDateTime getFileStartTime(LocalDateTime localDateTime) {
        return null == localDateTime ? null : localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime getFileEndTime(LocalDateTime localDateTime) {
        return null == localDateTime ? null : localDateTime.withMinute(59).withSecond(59).withNano(999999999);
    }

    public static LocalDateTime getFirstFileStartTime(LocalDateTime localDateTime) {
        return null == localDateTime ? null : localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime getFirstFileEndTime(LocalDateTime localDateTime, int period) {
        return localDateTime == null ? null : localDateTime.withMinute(0).withSecond(0).withNano(999999999).plusHours((long) period).plusSeconds(-1L);
    }

    public static LocalDateTime getNextFileStartTime(LocalDateTime localDateTime, int period) {
        return localDateTime == null ? null : localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0).plusHours((long) period);
    }

    public static LocalDateTime getNextFileEndTime(LocalDateTime localDateTime, int period) {
        return localDateTime == null ? null : localDateTime.plusHours((long) period);
    }

    public static LocalDateTime getTimeEnd(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.withMinute(0).withSecond(0).withNano(999999999).plusSeconds(-1L);
    }

    public static LocalDateTime getTimeStart(LocalDateTime localDateTime, int period) {
        return localDateTime == null ? null : localDateTime.withMinute(0).withSecond(0).withNano(0).plusHours((long) period);
    }

    public static String getFileNameByTwoDate(LocalDateTime startTime, LocalDateTime endTime) {
        String fileStartName = dateToStr(startTime, "yyyy-MM-dd HH:mm:ss").replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
        String fileEndName = dateToStr(endTime, "yyyy-MM-dd HH:mm:ss").replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
        return fileStartName + "~" + fileEndName;
    }

    public static String getNowTime() {
        return dateToStr(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss");
    }

    public static LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    public static String getDuration(long startTime, long endTime) {
        long durTime = endTime - startTime;
        durTime /= 1000L;
        int hour = (int) durTime / 3600;
        int minTmp = (int) durTime % 3600;
        int min = minTmp / 60;
        int sec = minTmp % 60;
        return hour + " H " + min + " M " + sec + " S";
    }

    public static Long calcTimeSecondDifference(long startTime, long endTime) {
        long durTime = endTime - startTime;
        durTime /= 1000L;
        return durTime;
    }

    public static String getStartTimeStr(LocalDateTime localDateTime, String format) {
        return dateToStr(getStartTime(localDateTime), format);
    }

    public static String dateToStr(LocalDateTime localDateTime) {
        String str = dateToStr(localDateTime, "yyyy-MM-dd");
        String[] t = str.split("-");
        str = t[0] + "Y" + t[1] + "M" + t[2] + "D";
        return str;
    }

    public static LocalDateTime getStartTime(LocalDateTime localDateTime) {
        return localDateTime.withSecond(0);
    }

    public static String getEndTimeStr(LocalDateTime localDateTime, String format) {
        return dateToStr(getEndTime(localDateTime), format);
    }

    public static LocalDateTime getEndTime(LocalDateTime localDateTime) {
        return localDateTime.plusSeconds(1L).withNano(0);
    }

    public static LocalDateTime getEndTimeOfPeriod(LocalDateTime localDateTime) {
        return localDateTime.plusSeconds(-1L);
    }

    public static boolean is1970(LocalDateTime localDateTime) {
        return localDateTime.getYear() == 1970;
    }

    public static String fromLongToDDmmSS(long mss) {
        long days = mss / 86400000L;
        long hours = mss % 86400000L / 3600000L;
        long minutes = mss % 3600000L / 60000L;
        long seconds = mss % 60000L / 1000L;
        StringBuffer sb = new StringBuffer();
        if (days > 0L) {
            sb.append(days);
        }

        if (hours > 0L) {
            sb.append(hours);
        }

        if (minutes > 0L) {
            sb.append(minutes);
        }

        if (seconds > 0L) {
            sb.append(seconds);
        }

        return sb.length() == 0 ? "0" : sb.toString();
    }

    public static LocalDateTime getWeekStart(LocalDateTime localDateTime) {
        return localDateTime.with(DayOfWeek.MONDAY);
    }

    public static LocalDateTime getLastMonthStart(LocalDateTime localDateTime) {
        return localDateTime.plusMonths(-1L).withDayOfMonth(1);
    }

    public static LocalDateTime getThisSeasonStart(LocalDateTime localDateTime) {
        int month = getQuarterInMonth(localDateTime.getMonthValue(), true);
        return localDateTime.withMonth(month).withDayOfMonth(1);
    }

    private static int getQuarterInMonth(int month, boolean isQuarterStart) {
        int[] months = new int[]{1, 4, 7, 10};
        if (!isQuarterStart) {
            months = new int[]{3, 6, 9, 12};
        }

        if (month >= 1 && month <= 3) {
            return months[0];
        } else if (month >= 4 && month <= 6) {
            return months[1];
        } else {
            return month >= 7 && month <= 9 ? months[2] : months[3];
        }
    }

    public static synchronized LocalDateTime setDateNumber(LocalDateTime localDateTime, int hour, int minute, int second, int nanoSecond) {
        return localDateTime.withHour(hour).withMinute(minute).withSecond(second).withNano(nanoSecond);
    }

    public static synchronized LocalDateTime addDateMonth(LocalDateTime localDateTime, int addMonth) {
        return localDateTime.plusMonths((long) addMonth);
    }

    public static synchronized LocalDateTime addDateDay(LocalDateTime localDateTime, int addDay) {
        return localDateTime.plusDays((long) addDay);
    }

    public static synchronized LocalDateTime setFullDate(int year, int month, int day) {
        LocalDateTime now = getNow();
        return now.withYear(year).withMonth(month).withDayOfMonth(day).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static synchronized long getDaySub(LocalDate beginDate, LocalDate endDate) {
        return (long) daysBetween(beginDate, endDate);
    }

    public static synchronized long getDayTime(LocalDateTime localDateTime) {
        return getTimestampOfDateTime(localDateTime);
    }

    public static long getYesterdayBegin() {
        LocalDateTime localDateTime = getNow();
        localDateTime.plusDays(-1L).withHour(0).withMinute(0).withSecond(0);
        return getTimestampOfDateTime(localDateTime);
    }

    public static long getYesterdayEnd() {
        LocalDateTime localDateTime = getNow();
        localDateTime.plusDays(-1L).withHour(23).withMinute(59).withSecond(59);
        return getTimestampOfDateTime(localDateTime);
    }

    public static LocalDateTime getYesterday() {
        LocalDateTime localDateTime = getNow();
        return localDateTime.plusDays(-1L);
    }

    public static long getIntervalHoursByTwoDate(LocalDateTime fromDate, LocalDateTime toDate) {
        fromDate.withMinute(0).withSecond(0).withNano(0);
        toDate.withMinute(0).withSecond(0).withNano(0);
        return Duration.between(fromDate, toDate).toHours();
    }

    public static int daysBetween(LocalDate localDate) throws ParseException {
        LocalDate today = LocalDate.now();
        return daysBetween(localDate, today);
    }

    public static String timeStamp2Date(String seconds, String format) {
        if (isNullString(seconds)) {
            return "";
        } else {
            if (seconds.length() == 10) {
                seconds = seconds + "000";
            }

            if (format == null || format.isEmpty()) {
                format = "yyyy-MM-dd HH:mm:ss";
            }

            LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(Long.valueOf(seconds), 0, ZoneOffset.ofHours(8));
            return localDateTimeToString(localDateTime, format);
        }
    }

    public static LocalDateTime getNextWeekMonday(LocalDateTime localDateTime) {
        return localDateTime.with(DayOfWeek.MONDAY).plusDays(7L);
    }

    public static LocalDateTime getThisWeekMonday(LocalDateTime localDateTime) {
        return getWeekStart(localDateTime);
    }

    public static LocalDateTime geLastWeekMonday(LocalDateTime localDateTime) {
        return localDateTime.with(DayOfWeek.MONDAY).plusDays(-7L);
    }

    public static boolean getTimePoor(LocalDateTime end, LocalDateTime now) {
        Duration duration = Duration.between(now, end);
        long days = duration.toDays();
        long minutes = duration.toMinutes();
        if (days == 0L && minutes >= 5L) {
            return false;
        } else {
            return days < 0L;
        }
    }

    public static LocalDateTime utcToCST(String utcStr, String format) throws ParseException {
        if (isNullString(format)) {
            format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        }

        LocalDateTime localDateTime = stringToLocalDateTime(utcStr, format);
        return localDateTime.plusHours(8L);
    }

    public static boolean currInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = getNow();
        if (startDate.isAfter(now)) {
            return false;
        } else {
            return !endDate.isBefore(now);
        }
    }

    public static boolean currInDate(String startTime, String endTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime sTime = LocalDateTime.parse(startTime, dtf);
        LocalDateTime dTime = LocalDateTime.parse(endTime, dtf);
        LocalDateTime nTime = LocalDateTime.now();
        return nTime.isBefore(dTime) && nTime.isAfter(sTime);
    }

    public static boolean isToday(LocalDate localDate) {
        if (localDate == null) {
            return false;
        } else {
            LocalDate today = LocalDate.now();
            return today.compareTo(localDate) == 0;
        }
    }

    public static Date dateTimeToDate(LocalDateTime dateTime) {
        if (null == dateTime) {
            return null;
        } else {
            Instant instant = dateTime.atZone(zoneId).toInstant();
            return Date.from(instant);
        }
    }

    public static LocalDate strToLocalDate(String localDateStr, String format) {
        return null != localDateStr && null != format ? LocalDate.parse(localDateStr, DateTimeFormatter.ofPattern(format)) : null;
    }
}
