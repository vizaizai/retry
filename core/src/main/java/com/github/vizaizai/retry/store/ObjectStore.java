package com.github.vizaizai.retry.store;

import java.util.List;

/**
 * 对象存储(用于异步重试)
 * @author liaochongwei
 * @date 2021/1/6 15:43
 */
public interface ObjectStore {

    void save(Object source, String key);

    List<Object> load();

    void delete(String key);
}
