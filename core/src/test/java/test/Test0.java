package test;

import com.github.vizaizai.retry.core.Retry;
import com.github.vizaizai.retry.mode.Modes;
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
        Retry<Void> retry1 = Retry.inject(() -> {
            System.out.println("执行业务方法");
            int random = Utils.getRandom(5, 1);
            System.out.println("rrrr：" + random);
            if (random > 1) {
                throw new RuntimeException("业务出错");
            }
        });
        retry1.mode(Modes.basic(2));
        retry1.max(20,1);
//        retry1.async((e)->{
//            System.out.println("回调了:"+e.getStatus());
//        });
        retry1.preHandler((context)->{
            System.out.println("执行了预处理");
//            if (true) {
//                throw new RuntimeException("12313123");
//            }
            return false;
        });
        retry1.execute();


        // 带有返回值
//        Retry<String> retry2 = Retry.inject(() -> {
//            System.out.println("执行业务方法");
//            if (Utils.getRandom(5,1) > 3) {
//                throw new RuntimeException("业务出错");
//            }
//            return "hello";
//        });
//        System.out.println(retry2.execute());
//
//        // 异步
//        Retry<String> asyncRetry = Retry.inject(() -> {
//            System.out.println("执行业务方法");
//            if (Utils.getRandom(5,1) > 3) {
//                throw new RuntimeException("业务出错");
//            }
//            return "hello";
//        });
//        asyncRetry.async(result -> {
//            System.out.println("异步重试回调");
//        });
//        asyncRetry.execute();
//
//
//
//        Retry<String> retry3 = Retry.inject(() -> {
//            System.out.println("执行业务方法");
//            TimeLooper.sleep(20);
//            if (Utils.getRandom(7,1) > 1) {
//                throw new RuntimeException("业务出错");
//            }
//            return "hello";
//        });
//        // 间隔1秒
//        retry3.mode(Modes.basic(1));
//        retry3.async(result -> {
//            System.out.println(result);
//        });
//        System.out.println(retry3.execute());
//        System.out.println("hhh");

    }




}
