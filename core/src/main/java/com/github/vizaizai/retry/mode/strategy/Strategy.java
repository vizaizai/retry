package com.github.vizaizai.retry.mode.strategy;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liaochongwei
 * @date 2020/12/8 15:56
 */
public interface Strategy extends Serializable {
    /**
     * 下次执行时间
     * @param count 调用序号
     * @return time
     */
    LocalDateTime nextExecutionTime(Integer count);
}
