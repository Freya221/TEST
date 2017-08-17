package com.huawei.om.utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author z00383627
 * @version 1.0_20170209
 *
 * support the usage beneath:
 *
 * properties1 = value1
 * properties2 = ${properties1}_value2
 *
 * properties2's value is "value1_value2"
 */
public class PropertiesUtil {
    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}");

    public static String getProperty(Properties properties, String key) {
        if (null == properties || null == key) {
            return null;
        }
        String value = properties.getProperty(key);
        if (null == value) {
            return null;
        }
        Matcher matcher = PATTERN.matcher(value);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String matcherKey = matcher.group(1);
            String matcherValue = properties.getProperty(matcherKey);
            if (matcherValue != null) {
                matcher.appendReplacement(buffer, matcherValue);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
