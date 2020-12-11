package com.github.vizaizai.retry.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liaochongwei
 * @date 2020/12/10 16:32
 */
public class Utils {
    private Utils() {
    }
    public static final String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";

    public static String format(LocalDateTime date, String patten) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patten);
        return date.format(formatter);
    }
}
