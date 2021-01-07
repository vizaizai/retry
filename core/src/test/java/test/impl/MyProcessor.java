package test.impl;

import com.github.vizaizai.retry.invocation.Processor;

/**
 * @author liaochongwei
 * @date 2021/1/6 17:31
 */
public class MyProcessor implements Processor<String> {
    @Override
    public String execute() throws Throwable {
        System.out.println("啦啦啦");
        return "hah";
    }
}
