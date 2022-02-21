package test.impl;

import com.github.vizaizai.retry.core.RetryResult;
import com.github.vizaizai.retry.invocation.Callback;

/**
 * @author liaochongwei
 * @date 2021/1/7 14:59
 */
public class MyCallback implements Callback<String> {
    @Override
    public void complete(RetryResult<String> result) {
        switch (result.getStatus()) {
            case NO_RETRY: // 没有重试直接返回的
                System.out.println("未触发重试,执行返回值:" + result.getValue());
                break;
            case TRY_OK: // 重试成功
                System.out.println("重试成功，执行返回值:" + result.getValue());
                break;
            case TRY_FAIL: // 全部失败
                System.out.println("重试失败，发生异常:" + result.getCause());
                break;
        }
    }
}
