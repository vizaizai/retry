package com.github.vizaizai.retry.store;

import com.github.vizaizai.retry.core.RetryHandler;
import com.github.vizaizai.retry.util.Utils;

/**
 * @author liaochongwei
 * @date 2021/1/7 15:06
 */
public class StoreService {
    private final StoreParameter storeParameter;
    private String classKey;
    private final ObjectStore objectStore;
    public StoreService(StoreParameter storeParameter) {
        this.storeParameter = storeParameter;
        this.objectStore = RetryHandler.DEFAULT_ASYNC_STORE;
    }
    /**
     * 保存异步参数
     */
    public void save() {
        // 执行器和返回函数必须是普通类才可以反序列化
        if (storeParameter.getProcessor() != null && !Utils.isOrdinaryClass(storeParameter.getProcessor())) {
            return;
        }
        if (storeParameter.getVProcessor() != null && !Utils.isOrdinaryClass(storeParameter.getVProcessor())) {
            return;
        }
        if (storeParameter.getCallback() != null && !Utils.isOrdinaryClass(storeParameter.getCallback())) {
            return;
        }
        this.classKey = ClassKeys.getClassKey(storeParameter.getClass());
        storeParameter.setClassKey(this.classKey);
        objectStore.save(storeParameter, this.classKey);
    }

    public void delete() {
        if (classKey == null) {
            return;
        }
        objectStore.delete(this.classKey);
    }
}
