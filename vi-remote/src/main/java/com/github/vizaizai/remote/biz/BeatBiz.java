package com.github.vizaizai.remote.biz;

import com.github.vizaizai.remote.codec.RpcRequest;
import com.github.vizaizai.remote.codec.RpcResponse;
import com.github.vizaizai.remote.utils.Utils;

/**
 * @author liaochongwei
 * @date 2022/2/18 16:43
 */
public class BeatBiz implements Biz<String>{
    @Override
    public RpcResponse<String> execute() {
        RpcRequest request = new RpcRequest();
        //request.setRequestId(Utils.getRequestId());
        //request.setBizCode(BizCodeEnum.BEAT.getCode());
        //request.setParam("ping");
        return null;
    }
}
