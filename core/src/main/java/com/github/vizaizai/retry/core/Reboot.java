package com.github.vizaizai.retry.core;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.retry.loop.TimeLooper;
import com.github.vizaizai.retry.store.ObjectFileStore;
import com.github.vizaizai.retry.store.StoreParameter;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author liaochongwei
 * @date 2021/1/7 17:06
 */
public class Reboot {
    private static final Logger log = LoggerFactory.getLogger(Reboot.class);
    private ObjectFileStore objectFileStore;
    private Reboot() {
    }
    public static void init() {
        Reboot reboot = new Reboot();
        reboot.objectFileStore = RetryHandler.DEFAULT_ASYNC_STORE;
        TimeLooper.sleep(5000);
        List<Object> objects = reboot.objectFileStore.load();
        for (Object object : objects) {
            if (object instanceof StoreParameter) {
                reboot.doRetry((StoreParameter) object);
            }
        }
    }

    public void doRetry(StoreParameter parameter) {
        try {
            Retry<?> retry;
            if (parameter.getProcessor() != null) {
                retry = Retry.inject(parameter.getProcessor());
            }else if (parameter.getVProcessor() != null) {
                retry = Retry.inject(parameter.getVProcessor());
            }else {
                return;
            }
            retry.max(parameter.getMaxAttempts())
                    .mode(parameter.getMode())
                    .retryFor(parameter.getRetryFor())
                    .async(parameter.getCallback());
            retry.execute();
            objectFileStore.delete(parameter.getClassKey());
        }catch (Exception e) {
            log.error("Reboot retry error - {}", parameter.getClassKey());
        }
    }


}
