package com.github.vizaizai.retry.loop;

import com.github.vizaizai.retry.invocation.VCallback;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 异步循环上下文
 * @author liaochongwei
 * @date 2020/12/9 10:04
 */
public class LoopContext {

    private LoopContext() {
    }
    /**
     * 循环等待线程
     */
    private static Thread loopThread;
    /**
     * 异步待执行任务列表
     */
    private static final CopyOnWriteArrayList<TimeExecution> EXECUTION_LIST = new CopyOnWriteArrayList<>();
    /**
     * 已执行的任务ID列表
     */
    private static final Set<String> DONE_IDS = new HashSet<>();
    /**
     * 回调函数线程池
     */
    private static final ExecutorService callbackExecutorService;

    static {
        ThreadFactory basicThreadFactory = new BasicThreadFactory.Builder().namingPattern("loop-callback-thread-%d").build();
        callbackExecutorService = Executors.newCachedThreadPool(basicThreadFactory);
    }

    public static List<TimeExecution> executionList() {
        return EXECUTION_LIST;
    }

    public static boolean isRunning() {
        return loopThread != null && loopThread.isAlive();
    }


    public static void finish() {
        synchronized (LoopContext.class) {
            if (EXECUTION_LIST.isEmpty()
                    && isRunning()) {
                loopThread.interrupt();
            }

        }
    }

    /**
     * 标记为已执行
     * @param execution 任务
     */
    public static void done(TimeExecution execution) {
        execution.setDone(true);
        DONE_IDS.add(execution.getId());
        // 执行回调
        callbackExecutorService.submit(() -> execution.getCallback().complete());
    }

    /**
     * 删除已执行的任务
     */
    public static void rmDone() {
        boolean removeRet = EXECUTION_LIST.removeIf(e -> e.isDone() && DONE_IDS.contains(e.getId()));
        if (removeRet) {
            DONE_IDS.clear();
        }
    }


    public static void doAsync(LocalDateTime exeTime, VCallback callback, Runnable runnable) {
        EXECUTION_LIST.addIfAbsent(new TimeExecution(exeTime, callback));
        synchronized (LoopContext.class) {
            if(isRunning()) {
                return;
            }
            loopThread = new Thread(runnable);
            loopThread.setName("loop-wait-thread");
            loopThread.start();
        }
    }
}
