package com.github.vizaizai.retry.loop;

import com.github.vizaizai.retry.invocation.VCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 时间循环
 * @author liaochongwei
 * @date 2020/12/9 9:31
 */
public class TimeLooper {
    private static final Logger log = LoggerFactory.getLogger(TimeLooper.class);

    private TimeLooper() {
    }


    public static void wait(LocalDateTime exeTime) {
        while (true) {
            if (LocalDateTime.now().isAfter(exeTime)) {
                return;
            }
            sleep1s();
        }

    }

    /**
     * 异步等待
     * @param exeTime 执行时间
     * @param callback 回调
     */
    public static void asyncWait(LocalDateTime exeTime, VCallback callback) {
        LoopContext.doAsync(exeTime, callback, ()-> {
            while (true) {
                List<TimeExecution> timeExecutions = LoopContext.executionList();
                if (timeExecutions.isEmpty()) {
                    LoopContext.finish();
                    log.info("loop wait thread end.");
                    return;
                }
                for (TimeExecution execution : timeExecutions) {
                    if (LocalDateTime.now().isAfter(execution.getExeTime())) {
                        LoopContext.done(execution);
                        break;
                    }
                }
                // 移除已执行执行任务
                LoopContext.rmDone();
                sleep1s();
            }

        });

    }


    private static void sleep1s() {
        try {
            TimeUnit.SECONDS.sleep(1);
        }catch (Exception e) {
            log.error("Thread sleep exception：{}", e.getMessage());
        }
    }
}
