package com.github.vizaizai.retry.util;

import com.github.vizaizai.retry.loop.TimeLooper;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.vizaizai.retry.loop.TimeLooper.sleep;

/**
 * @author liaochongwei
 * @date 2020/12/14 16:34
 */
public class Demo {
    private static final Logger log = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        AtomicInteger integer = new AtomicInteger(0);

        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("loop-thread-%d").build();
        ExecutorService executorService = new ThreadPoolExecutor(0, 50,
                100L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(50),
                threadFactory, new EnterQueuePolicy());

        for (int i = 0; i < 1000; i++) {
            try {
                executorService.submit(() -> {
                    sleep(100);
                    integer.incrementAndGet();
                    log.info("执行了");
                });
            }catch (RejectedExecutionException e) {
                e.printStackTrace();
            }

        }

//        while (true) {
//            log.info("count: {}",integer.get());
//            sleep(1000);
//        }
    }
}
