package com.github.vizaizai.remote.biz;

/**
 * @author liaochongwei
 * @date 2022/2/18 16:59
 */
public enum BizCodeEnum {

    BEAT("beat","心跳检测"),
    TIMING_TASK("timing_task","定时任务"),
    RETRY_TASK("retry_task","定时任务"),
    ;

    private final String code;
    private final String des;

    BizCodeEnum(String code, String des) {
        this.code = code;
        this.des = des;
    }

    public String getCode() {
        return code;
    }

    public String getDes() {
        return des;
    }
}
