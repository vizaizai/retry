package com.github.vizaizai.retry.core;

import com.github.vizaizai.logging.LoggerFactory;
import com.github.vizaizai.retry.loop.TimeLooper;
import com.github.vizaizai.retry.store.AsyncRebootParameter;
import com.github.vizaizai.retry.store.ObjectFileStore;
import com.github.vizaizai.retry.store.ObjectStore;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author liaochongwei
 * @date 2021/1/7 17:06
 */
public class Reboot {
    private static final Logger log = LoggerFactory.getLogger(Reboot.class);
    /**
     * 默认异步存储
     */
    public static final ObjectFileStore DEFAULT_ASYNC_STORE = new ObjectFileStore();
    /**
     * 对象存储
     */
    private ObjectStore objectStore;
    /**
     * Reboot实例
     */
    private static final Reboot reboot = new Reboot();
    private Reboot() {
    }

    public static Reboot getInstance() {
        return reboot;
    }

    public void configStore(ObjectStore objectStore) {
        reboot.objectStore = objectStore;
    }

    public void start() {
        TimeLooper.sleep(500);
        List<Object> objects = getObjectStore().load();
        for (Object object : objects) {
            if (object instanceof AsyncRebootParameter) {
                this.doRetry((AsyncRebootParameter) object);
            }
        }
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private void doRetry(AsyncRebootParameter parameter) {
        try {
            Retry retry;
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
            DEFAULT_ASYNC_STORE.delete(parameter.getClassKey());
        }catch (Exception e) {
            log.error("Reboot retry error - {}", parameter.getClassKey());
        }
    }

    public ObjectStore getObjectStore() {
        return objectStore == null ? DEFAULT_ASYNC_STORE : objectStore;
    }
}
