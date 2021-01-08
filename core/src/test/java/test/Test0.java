package test;

import com.github.vizaizai.retry.attempt.Modes;
import com.github.vizaizai.retry.core.Retry;
import com.github.vizaizai.retry.invocation.Processor;
import com.github.vizaizai.retry.loop.TimeLooper;
import com.github.vizaizai.retry.util.Utils;

/**
 * @author liaochongwei
 * @date 2020/12/15 11:16
 */
public class Test0 {

    /**
     * 基本使用
     * @param args
     */
    public static void main(String[] args) {

        // 默认重试三次，没有间隔时间，同步，retryFor：RuntimeException或者Error
//        Retry<Void> retry1 = Retry.inject(() -> {
//            System.out.println("执行业务方法");
//            if (true) {
//                throw new RuntimeException("业务出错");
//            }
//        });
//        retry1.execute();


        // 带有返回值
//        Retry<String> retry2 = Retry.inject(() -> {
//            System.out.println("执行业务方法");
//            if (Utils.getRandom(5,1) > 3) {
//                throw new RuntimeException("业务出错");
//            }
//            return "hello";
//        });
//        System.out.println(retry2.execute());

        // 异步
        Retry.inject(new Processor<Object>() {
            @Override
            public Object execute() throws Throwable {
                return null;
            }
        });
        Retry<String> retry3 = Retry.inject(() -> {
            System.out.println("执行业务方法");
            TimeLooper.sleep(20);
            if (Utils.getRandom(7,1) > 1) {
                throw new RuntimeException("业务出错");
            }
            return "hello";
        });
        // 间隔1秒
        retry3.mode(Modes.basic(1));
        retry3.async(result -> {
            System.out.println(result);
        });
        System.out.println(retry3.execute());
        System.out.println("hhh");

    }




}
