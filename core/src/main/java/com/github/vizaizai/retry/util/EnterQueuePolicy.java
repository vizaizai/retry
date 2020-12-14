package com.github.vizaizai.retry.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author liaochongwei
 * @date 2020/12/14 17:19
 */
public class EnterQueuePolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        executor.execute(r);
        System.out.println("满了我丢");
    }

    public static void main(String[] args) throws InterruptedException {


        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        String take = queue.take();
    }
}
