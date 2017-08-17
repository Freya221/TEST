package com.huawei.om.utils;

import org.apache.log4j.Logger;

/**
 * Created by z00383627 on 2017/3/7.
 *
 */
public class AlgorithmUtil {
    private static final Logger LOG = Logger.getLogger(AlgorithmUtil.class);

    public static long parseIpToValue(String ip) {
        if (null == ip) {
            return 0L;
        }
        String[] ips = ip.split("\\.");
        if (ips.length != 4) {
            return 0L;
        }
        try {
            return Long.parseLong(ips[0]) * 256L * 256L * 256L + Long.parseLong(ips[1]) * 256L * 256L
                    + Long.parseLong(ips[2]) * 256L + Long.parseLong(ips[3]);
        }catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static int getSpeed(int totalSize, int totalTime) {
        if (totalTime <= 0) {
            return -1;
        }
        if (totalSize < 0) {
            return -1;
        }
        return (int)(((float)totalSize / 1024) / (totalTime / 1000));
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
