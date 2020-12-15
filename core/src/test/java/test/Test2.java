package test;

import com.github.vizaizai.retry.attempt.Modes;
import com.github.vizaizai.retry.core.Retry;
import com.github.vizaizai.retry.exception.RetryException;
import com.github.vizaizai.retry.loop.TimeLooper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liaochongwei
 * @date 2020/12/15 11:16
 */
public class Test2 {
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);
    /**
     * 测试模式
     * @param args
     */
    public static void main(String[] args) {

        Retry.inject(() -> {
            double random = Math.random();
            TimeLooper.sleep(1);
            if (random > 0.1) {
                throw new RetryException("发生错误啦");
            }
        })
                .mode(Modes.cron("13,37,58 * * * * ?")) // 不支持年份
                //.mode(Modes.basic(1))
                //.mode(Modes.arithmetic(1, 1, ChronoUnit.SECONDS))
                //.mode(Modes.geometric(1D, 2D, ChronoUnit.SECONDS))
                .max(3)
                .async(e-> {
                    atomicInteger.incrementAndGet();
                    System.out.println("callback--------------------:"  + e);
                })
                .retryFor(RetryException.class)
                .execute();
    }


    /**
     * try {
     *             //写入字节流
     *             ByteArrayOutputStream out = new ByteArrayOutputStream();
     *             ObjectOutputStream obs = new ObjectOutputStream(out);
     *             obs.writeObject(processor);
     *             obs.close();
     *             System.out.println("a");
     *             //分配内存，写入原始对象，生成新对象
     *             ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
     *             ObjectInputStream ois = new ObjectInputStream(ios);
     *             //返回生成的新对象
     *             processor = (Processor)ois.readObject();
     *             ois.close();
     *             System.out.println("b");
     *         } catch (Exception e) {
     *             e.printStackTrace();
     *             System.out.println("c");
     *         }
     */
}
