package test;

import com.github.vizaizai.retry.attempt.Modes;
import com.github.vizaizai.retry.core.Retry;
import com.github.vizaizai.retry.util.Utils;
import test.impl.Test4VProcessor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author liaochongwei
 * @date 2022/1/13 16:02
 */
public class Test4 {
    public static void main(String[] args) throws InterruptedException {

        int t = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(t);
        int n = 1;
        AtomicLong atomicLong = new AtomicLong(0);
        CountDownLatch latch = new CountDownLatch(t*n);

        long s = System.currentTimeMillis();
        for (int i = 0; i < t; i++) {
            for (int j = 0; j < n; j++) {
                executorService.execute(()->{
                    Retry.inject(new Test4VProcessor())
                            .name("Test4Retry"+ Utils.getRandom(11111,1))
                            .max(10)
                            .mode(Modes.basic(1))
                            .async(e->{
                                Test4VProcessor retryProcessor = (Test4VProcessor) e.getRetryProcessor();
                                atomicLong.addAndGet(System.currentTimeMillis() - retryProcessor.getStartTime());
                                latch.countDown();
                            })
                            .execute();
                });
            }
        }
        System.err.println("==============================注入完成================================");
        latch.await();
        System.out.println("平均耗时：" + atomicLong.get()/1000/t/n);
        System.out.println("总耗时：" + (System.currentTimeMillis() - s)/1000);

    }
}
