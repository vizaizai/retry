package test;

import com.github.vizaizai.retry.core.Reboot;

/**
 * @author liaochongwei
 * @date 2020/12/15 11:16
 */
public class Test3 {
    /**
     * 测试重启重试
     * @param args
     */
    public static void main(String[] args) {
        Reboot.getInstance().start();
    }

}
