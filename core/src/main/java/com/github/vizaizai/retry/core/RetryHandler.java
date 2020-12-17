package com.github.vizaizai.retry.core;

import com.github.vizaizai.retry.attempt.AttemptContext;
import com.github.vizaizai.retry.invocation.Callback;
import com.github.vizaizai.retry.invocation.InvocationOperations;
import com.github.vizaizai.retry.loop.TimeLooper;
import com.github.vizaizai.retry.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private InvocationOperations<T> invocationOps;
    /**
     *  重试条件(发生了XX异常)
     */
    private List<Class<? extends Throwable>> retryFor;
    /**
     * 重试规则属性列表
     */
    private List<RetryRuleAttribute> retryRuleAttributes;
    /**
     * 异步回调
     */
    private Callback callback;
    /**
     * 是否异步
     */
    private boolean async = false;
    /**
     * 尝试上下文
     */
    private AttemptContext attemptContext;
    /**
     * 状态
     */
    private RetryStatus status;
    /**
     * 执行结果
     */
    private T result;
    /**
     * 执行目标方法
     */
    public void tryInvocation() {
        this.result = this.invocationOps.execute();
        // 调用正常
        if (!this.invocationOps.haveErr()) {
            this.status = RetryStatus.NO_RETRY;
            this.doCallback(new CallBackResult(status));
            return;
        }
        // 发生了异常,并且满足重试条件
        if (this.retryOn(this.invocationOps.getCause())) {
            // 异步重试
            if (this.async) {
                this.asyncRetry();
            }else {
                this.syncRetry();
            }
        }else {
            status = RetryStatus.NO_RETRY;
            this.invocationOps.throwErr();
        }
    }


    /**
     * 异步重试
     */
    private void asyncRetry() {
        // 调用上限
        if (attemptContext.isMaximum()) {
            log.error("{} retries fail.", attemptContext.getAttempts());
            this.status = RetryStatus.TRY_FAIL;
            // 执行异步回调
            this.doCallback(new CallBackResult(this.status, this.invocationOps.getErrMsg()));
            return;
        }
        status = RetryStatus.RETRYING;
        LocalDateTime nextTime = attemptContext.getNextTime();
        log.info("RETRY[{}]-{}", attemptContext.getAttempts(), Utils.format(nextTime, Utils.FORMAT_LONG));
        TimeLooper.asyncWait(nextTime, ()-> {
            this.result = this.invocationOps.execute();
             //重试成功
            if (!this.invocationOps.haveErr()) {
                this.status = RetryStatus.TRY_OK;
                this.doCallback(new CallBackResult(this.status));
                return;
            }
            this.asyncRetry();
        });
    }
    /**
     * 同步重试
     */
    private void syncRetry() {
        while (true) {
            // 调用上限
            if (attemptContext.isMaximum()) {
                log.error("{} retries fail.", attemptContext.getAttempts());
                this.status = RetryStatus.TRY_FAIL;
                this.invocationOps.throwErr();
            }
            status = RetryStatus.RETRYING;
            LocalDateTime nextTime = attemptContext.getNextTime();
            log.info("RETRY[{}]-{}",attemptContext.getAttempts(), Utils.format(nextTime, Utils.FORMAT_LONG));
            TimeLooper.wait(nextTime);
            this.result = this.invocationOps.execute();
            // 重试成功
            if (!this.invocationOps.haveErr()) {
                this.status = RetryStatus.TRY_OK;
                return;
            }
        }
    }


    private boolean retryOn(Throwable ex) {
        if (log.isDebugEnabled() &&  this.getAttemptContext().getAttempts() == 0) {
            log.debug("Applying rules to determine whether  should retry on {}", ex.getClass());
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
     * 执行回调
     * @param result result
     */
    private void doCallback(CallBackResult result) {
        if (callback != null) {
            callback.complete(result);
        }
    }

    public void setRetryFor(List<Class<? extends Throwable>> retryFor) {
        this.retryFor = retryFor;
        retryRuleAttributes = new ArrayList<>();
        for (Class<?> rbRule : retryFor) {
            retryRuleAttributes.add(new RetryRuleAttribute(rbRule));
        }
    }



    public InvocationOperations<T> getInvocationOps() {
        return invocationOps;
    }

    public void setInvocationOps(InvocationOperations<T> invocationOps) {
        this.invocationOps = invocationOps;
    }

    public List<Class<? extends Throwable>> getRetryFor() {
        return retryFor;
    }



    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public AttemptContext getAttemptContext() {
        return attemptContext;
    }

    public void setAttemptContext(AttemptContext attemptContext) {
        this.attemptContext = attemptContext;
    }

    public RetryStatus getStatus() {
        return status;
    }

    public void setStatus(RetryStatus status) {
        this.status = status;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
