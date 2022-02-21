package com.github.vizaizai.remote.biz;

import com.github.vizaizai.remote.codec.RpcResponse;

/**
 * 业务执行接口
 * @author liaochongwei
 * @date 2022/2/18 16:34
 */
public interface Biz<T> {

    RpcResponse<T> execute();
}
