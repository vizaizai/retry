package com.github.vizaizai.remote.utils;

import io.netty.util.internal.StringUtil;

import java.util.Random;
import java.util.UUID;

/**
 * 工具类
 * @author liaochongwei
 * @date 2022/2/18 16:52
 */
public class Utils {

    public static String getRequestId() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
