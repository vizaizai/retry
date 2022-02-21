package test;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.retry.timewheel.HashedWheelTimer;
import com.github.vizaizai.retry.util.Utils;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author liaochongwei
 * @date 2022/1/5 10:51
 */
public class Demo1 {
    private static final Logger log = LoggerFactory.getLogger(Demo1.class);


    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(500);
        System.out.println("初始化模拟生产者线程池完成--");
        HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(1,4096, Runtime.getRuntime().availableProcessors());


        AtomicLong n = new AtomicLong(0);
        final CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    int delay = Utils.getRandom(10, 1);
                    long ss = System.currentTimeMillis();
                    hashedWheelTimer.newTimeout(()->{
                        long l = System.currentTimeMillis() - ss;
                        //log.info("Timeout-" + delay + ":耗时" + l);
                        long c = l - (long)delay*1000;
                        latch.countDown();
                        n.addAndGet(c);
                        //TimeLooper.sleep(1);
                        if (c > 100) {
                            System.err.println("误差：" + c);
                        }
                    },delay, TimeUnit.SECONDS);
                }
            });

        }

        latch.await();
        System.err.println("--------------------总误差：" + n.get());

        //818954
        //882620



//        for (int i = 0; i < 100000; i++) {
//            int delay = Utils.getRandom(10, 1);
//            long ss = System.currentTimeMillis();
//            LocalDateTime localDateTime = LocalDateTime.now().plus(delay, ChronoUnit.SECONDS);
//            TimeLooper.asyncWait(localDateTime,()->{
//                long l = System.currentTimeMillis() - ss;
//                log.info("Timeout-" + delay + ":耗时" + l);
//                //误差500ms以上
//                if ((l - (long)delay*1000)>500) {
//                    System.err.println("=======================误差了=======================");
//                }
//            });
//        }
        System.err.println("=======================循环结束=======================");

    }
}
