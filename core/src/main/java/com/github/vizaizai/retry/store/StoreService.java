package com.github.vizaizai.retry.store;

import com.github.vizaizai.retry.core.Reboot;
import com.github.vizaizai.retry.util.Utils;

/**
 * @author liaochongwei
 * @date 2021/1/7 15:06
 */
public class StoreService {
    private final AsyncRebootParameter asyncRebootParameter;
    private String classKey;
    private final ObjectStore objectStore;
    public StoreService(AsyncRebootParameter asyncRebootParameter) {
        this.asyncRebootParameter = asyncRebootParameter;
        this.objectStore = Reboot.getInstance().getObjectStore();
    }
    /**
     * 保存异步参数
     */
    public void save() {
        // 执行器和返回函数必须是普通类才可以反序列化
        if (asyncRebootParameter.getProcessor() != null && !Utils.isOrdinaryClass(asyncRebootParameter.getProcessor())) {
            return;
        }
        if (asyncRebootParameter.getVProcessor() != null && !Utils.isOrdinaryClass(asyncRebootParameter.getVProcessor())) {
            return;
        }
        if (asyncRebootParameter.getCallback() != null && !Utils.isOrdinaryClass(asyncRebootParameter.getCallback())) {
            return;
        }
        this.classKey = ClassKeys.getClassKey(asyncRebootParameter.getClass());
        asyncRebootParameter.setClassKey(this.classKey);
        objectStore.save(asyncRebootParameter, this.classKey);
    }

    public void delete() {
        if (classKey == null) {
            return;
        }
        objectStore.delete(this.classKey);
    }
}
