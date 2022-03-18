package test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author liaochongwei
 * @date 2022/3/11 12:47
 */
public class Test5 {
    public static void main(String[] args) {
        ZoneId zone = ZoneId.systemDefault();
        System.out.println(LocalDateTime.ofInstant(Instant.ofEpochMilli(1641812510478L),zone));
    }
}
