package com.github.vizaizai.retry.core;

/**
 * 重试状态
 * @author liaochongwei
 * @date 2020/12/8 16:51
 */
public enum RetryStatus {
    // 不重试
    NO_RETRY,
    // 重试中
    RETRYING,
    // 全部失败
    TRY_FAIL,
    // 成功
    TRY_OK
    ;
}
