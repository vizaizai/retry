package com.github.vizaizai.retry.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author liaochongwei
 * @date 2020/12/10 16:32
 */
public class Utils {
    private Utils() {
    }
    private static final String[] EMPTY_STRING_ARRAY = {};

    public static final String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";

    public static String format(LocalDateTime date, String patten) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patten);
        return date.format(formatter);
    }


    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }
    public static String[] tokenizeToStringArray(
            String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }

        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] toStringArray(Collection<String> collection) {
        return (!CollUtils.isEmpty(collection) ? collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY);
    }

    /**
     * 获取区间随机数[min,max]
     * @param max max
     * @param min min
     * @return Random
     */
    public static int getRandom(int max, int min) {
        Random random = new SecureRandom();
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * 判断该对象class是否为普通类(匿名、lambda)
     * @param object
     * @return boolean
     */
    public static boolean isOrdinaryClass(Object object) {
        Class<?> cls = object.getClass();
        // 判断匿名内部类
        if (cls.isAnonymousClass()) {
            return false;
        }
        String className = cls.getSimpleName();
        // lambda表达式类
        return !className.matches(".+\\$\\$Lambda\\$.*/\\d*"); // Test1$$Lambda$1/472654579
    }
}
