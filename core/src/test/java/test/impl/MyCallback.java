package test.impl;

import com.github.vizaizai.retry.core.CallBackResult;
import com.github.vizaizai.retry.invocation.Callback;

/**
 * @author liaochongwei
 * @date 2021/1/7 14:59
 */
public class MyCallback implements Callback {
    @Override
    public void complete(CallBackResult result) {
        System.out.println("callback--------------------:"  + result);
    }
}
