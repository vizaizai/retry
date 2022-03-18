package com.github.vizaizai.retry.core;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.retry.exception.PreRetryHandleException;
import com.github.vizaizai.retry.handler.RetryProcessor;
import com.github.vizaizai.retry.loop.TimeLooper;
import com.github.vizaizai.retry.util.Utils;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**`
 * 重试上下文
 * @author liaochongwei
 * @date 2020/12/8 16:47
 */
public class RetryHandler<T> {
    private static final Logger log = LoggerFactory.getLogger(RetryHandler.class);
    /**
     * 目标方法操作
     */
    private RetryProcessor<T> retryProcessor;
    /**
     *  重试条件(发生了XX异常)
     */
    private List<Class<? extends Throwable>> retryFor;
    /**
     * 重试规则属性列表
     */
    private List<RetryRuleAttribute> retryRuleAttributes;
    /**
     * 是否异步
     */
    private boolean async = false;
    /**
     * 重试上下文
     */
    private RetryContext retryContext;
    /**
     * 执行结果
     */
    private T result;
    /**
     * 异步重试中处理器
     */
    private static final Map<String,RetryHandler<?>> asyncRetryingHandlers = new ConcurrentHashMap<>();
    /**
     * 执行目标方法
     */
    public void tryExecute() {
        // 未执行过，先执行一次
        if (retryContext.getAttempts() == 0) {
            // 执行一次正常调用
            this.result = this.retryProcessor.execute();
            // 调用正常
            if (!this.retryProcessor.haveErr()) {
                if (this.async) {
                    this.doPostHandle(RetryResult.ok(this.retryProcessor.getRetryTask(), this.retryContext, this.result));
                }
                return;
            }
            // 发生了异常,并且满足重试条件
            if (this.retryOn(this.retryProcessor.getCause())) {
                this.doRetry();
            }else {
                this.retryProcessor.throwErr();
            }
        }else {
            this.doRetry();
        }

    }

    private void doRetry() {
        // 异步重试
        if (this.async) {
            this.asyncRetry();
        }else {
            this.syncRetry();
        }

    }

    /**
     * 异步重试
     */
    private void asyncRetry() {
        Object retryTask = this.retryProcessor.getRetryTask();
        // 调用上限
        if (retryContext.isMaximum()) {
            log.error("Retry[{}{}] fail",this.retryProcessor.getName(), retryContext.getAttempts());
            this.retryContext.setStatus(RetryStatus.TRY_FAIL);
            // 执行异步回调
            this.doPostHandle(RetryResult.fail(retryTask, this.retryContext, this.retryProcessor.getCause()));
            return;
        }
        this.addRetryingHandler();
        this.retryContext.setStatus(RetryStatus.RETRYING);
        LocalDateTime nextTime = retryContext.getNextRetryTime();
        log.info("Retry[{}{}] will be execute at {}", this.retryProcessor.getName(),retryContext.getAttempts(), Utils.format(nextTime, Utils.FORMAT_LONG));
        TimeLooper.asyncWaitV2(nextTime, ()-> {
            this.result = this.retryProcessor.execute();
            this.removeRetryingHandler();
             //重试成功
            if (!this.retryProcessor.haveErr()) {
                this.retryContext.setStatus(RetryStatus.TRY_OK);
                this.doPostHandle(RetryResult.ok(retryTask, retryContext, this.result));
            }else if (this.retryOn(this.retryProcessor.getCause())) {// 满足重试条件
                this.asyncRetry();
            }else {
                this.retryContext.setStatus(RetryStatus.TRY_ABORT);
                // 重试失败，且不满足重试条件，则直接异步返回
                this.doPostHandle(RetryResult.fail(retryTask, retryContext, this.retryProcessor.getCause()));
            }

        });
    }
    /**
     * 同步重试
     */
    private void syncRetry() {
        while (true) {
            // 调用上限
            if (retryContext.isMaximum()) {
                log.error("Retry[{}{}] fail",this.retryProcessor.getName(), retryContext.getAttempts());
                this.retryContext.setStatus(RetryStatus.TRY_FAIL);
                this.retryProcessor.throwErr();
            }
            this.retryContext.setStatus(RetryStatus.RETRYING);
            LocalDateTime nextTime = retryContext.getNextRetryTime();
            log.info("Retry[{}{}] will be execute at {}",this.retryProcessor.getName(),retryContext.getAttempts(), Utils.format(nextTime, Utils.FORMAT_LONG));
            TimeLooper.wait(nextTime);
            this.result = this.retryProcessor.execute();
            // 重试成功
            if (!this.retryProcessor.haveErr()) {
                this.retryContext.setStatus(RetryStatus.TRY_OK);
                return;
            }
            if (!this.retryOn(this.retryProcessor.getCause())) {// 不满足重试条件，直接抛出异常
                this.retryProcessor.throwErr();
            }
        }
    }


    private boolean retryOn(Throwable ex) {
        // 排除预处理异常，直接返回false
        if (ex instanceof PreRetryHandleException) {
            return false;
        }
        if (log.isDebugEnabled() &&  retryContext.getAttempts() == 0) {
            log.debug("Applying rules to determine whether should retry on {}", ex.getClass());
        }

        RetryRuleAttribute winner = null;
        int deepest = Integer.MAX_VALUE;
        if (this.retryRuleAttributes != null) {
            for (RetryRuleAttribute rule : this.retryRuleAttributes) {
                int depth = rule.getDepth(ex);
                if (depth >= 0 && depth < deepest) {
                    deepest = depth;
                    winner = rule;
                }
            }
        }

        if (log.isDebugEnabled()) {
            if (winner == null) {
                log.debug("No relevant retry rule found: applying default rules");
            }else {
                log.debug("Winning retry rule is: {}", winner);
            }
        }
        return winner != null;
    }
    /**
     * 执行后置回调处理
     * @param result result
     */
    private void doPostHandle(RetryResult<T> result) {
        retryProcessor.postHandle(result);
    }

    public void setRetryFor(List<Class<? extends Throwable>> retryFor) {
        this.retryFor = retryFor;
        retryRuleAttributes = new ArrayList<>();
        for (Class<?> rbRule : retryFor) {
            retryRuleAttributes.add(new RetryRuleAttribute(rbRule));
        }
    }

    private void addRetryingHandler() {
        if (this.retryProcessor.getName() != null) {
            asyncRetryingHandlers.put(this.retryProcessor.getName(), this);
        }
    }

    private void removeRetryingHandler() {
        if (this.retryProcessor.getName() != null) {
            asyncRetryingHandlers.remove(this.retryProcessor.getName());
        }
    }

    public static Map<String,RetryHandler<?>>  getAsyncRetryingHandlers() {
        return asyncRetryingHandlers;
    }

    public RetryProcessor<T> getRetryProcessor() {
        return retryProcessor;
    }

    public void setRetryProcessor(RetryProcessor<T> retryProcessor) {
        this.retryProcessor = retryProcessor;
    }

    public List<Class<? extends Throwable>> getRetryFor() {
        return retryFor;
    }


    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public RetryContext getRetryContext() {
        return retryContext;
    }

    public void setRetryContext(RetryContext retryContext) {
        this.retryContext = retryContext;
    }


    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }


}
