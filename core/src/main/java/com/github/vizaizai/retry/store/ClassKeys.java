package com.github.vizaizai.retry.store;

import com.github.vizaizai.retry.core.Reboot;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liaochongwei
 * @date 2021/1/7 11:01
 */
public class ClassKeys {

    private ClassKeys() {
    }

    private static final String SOR = "#";
    private static boolean init = false;
    private static final Map<String, AtomicInteger> COUNT = new ConcurrentHashMap<>();

    private static void init() {
        List<String> fileNames = Reboot.DEFAULT_ASYNC_STORE.getFileNames();
        for (String fileName : fileNames) {
            int i = fileName.lastIndexOf(SOR);
            String seqStr = fileName.substring(i + 1);
            try {
                String key = fileName.substring(0, i);
                AtomicInteger value = new AtomicInteger(Integer.parseInt(seqStr));
                AtomicInteger mValue = COUNT.get(key);
                if (mValue == null || mValue.get() < value.get()) {
                    COUNT.put(key, value);
                }
            }catch (Exception ignored) {}
        }
        init = true;
    }

    public static String getClassKey(Class<?> clazz) {
        String name = clazz.getName();
        synchronized (ClassKeys.class) {
            if (!init) {
                init();
            }
            if (!COUNT.containsKey(name)) {
                AtomicInteger value = new AtomicInteger(0);
                COUNT.put(name, value);
                return  name + SOR + value.get();
            }
            AtomicInteger value = COUNT.get(name);
            return name + SOR + value.incrementAndGet();
        }
    }
}
