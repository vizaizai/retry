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
public class Demo2 {
    private static final Logger log = LoggerFactory.getLogger(Demo2.class);


    public static void main(String[] args) throws InterruptedException {

        HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(200,2048, Runtime.getRuntime().availableProcessors(),200,2000);

        hashedWheelTimer.newTimeout(()->{
            System.out.println("12123");
        },200, TimeUnit.MILLISECONDS);
    }
}
