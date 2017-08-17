package com.huawei.om.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by z00383627 on 2017/3/5.
 *
 */
public class DateTimeUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean isLegalDateTime(String dateTime) throws ArrayIndexOutOfBoundsException, NumberFormatException {
        if (null == dateTime) {
            return false;
        }
        try {
            dateFormat.parse(dateTime);
            return true;
        } catch (ParseException e) {
            return false;
        }//TODO 线程不安全
    }

    public static String getDate(String dateTime) {
        if (null == dateTime) {
            return null;
        }
        try {
            return dateTime.substring(0, dateTime.length() - 9);
        }catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public static String getTime(String dateTime) {
        if (null == dateTime) {
            return null;
        }
        try {
            return dateTime.substring(11, dateTime.length());
        }catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public static String getDateTimeSubFiveSecond(String dateTime) {
        if (null == dateTime) {
            return null;
        }
        try {
            String lastPart = dateTime.substring(dateTime.length()-1, dateTime.length());
            int lastValue = Integer.parseInt(lastPart);
            String headPart = dateTime.substring(0, dateTime.length() - 1);
            if (lastValue < 5) {
                headPart += "0";
            }else {
                headPart += "5";
            }
            return headPart;
        }catch (IndexOutOfBoundsException | NumberFormatException e) {
            return null;
        }
    }

    public static String getTimeSubSecond(String dateTime) {
        String tmp = getTime(dateTime);
        if (null == tmp) {
            return null;
        }
        try {
            return tmp.substring(0, tmp.length() - 2) + "00";
        }catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public static String getDateTimeSubSecond(String dateTime) {
        if (null == dateTime) {
            return null;
        }
        try {
            return dateTime.substring(0, dateTime.length() - 2) + "00";
        }catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public static String getDateAndHour(String dateTime) {
        if (null == dateTime) {
            return null;
        }
        String date = getDate(dateTime);
        String time = getTime(dateTime);
        if (null == date || null == time) {
            return null;
        }
        date = date.replace("-", "");
        try {
            time = time.substring(0, 2);
        }catch (IndexOutOfBoundsException e) {
            return null;
        }
        return date + "-" + time;
    }
}
