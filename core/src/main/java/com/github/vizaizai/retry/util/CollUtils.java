package com.github.vizaizai.retry.util;
import java.util.*;

/**
 * 常用工具集合(部分代码来自apache)
 * @author liaochongwei
 * @date 2021/6/22 11:44
 */
public class CollUtils {

    /**
     * 默认初始大小
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    /**
     * 默认增长因子，当Map的size达到 容量*增长因子时，开始扩充Map
     */
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private CollUtils() {
    }
    /*======================================CollectionUtils==========================================*/

    /**
     * 判断集合是否为空
     * @param coll
     * @return boolean
     */
    public static boolean isEmpty(Collection coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * 判断集合是否不为空
     * @param coll
     * @return boolean
     */
    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }


    /*======================================MapUtils==========================================*/
    /**
     * map是否为空
     * @param map
     * @return boolean
     */
    public static boolean isEmpty(Map<?,?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * map是否为非空
     * @param map
     * @return boolean
     */
    public static boolean isNotEmpty(Map<?,?> map) {
        return !isEmpty(map);
    }

    /**
     * 新建一个HashMap
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @return HashMap对象
     */
    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * 新建一个HashMap
     *
     * @param <K>     Key类型
     * @param <V>     Value类型
     * @param size    初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75 + 1
     * @param isOrder Map的Key是否有序，有序返回 {@link LinkedHashMap}，否则返回 {@link HashMap}
     * @return HashMap对象
     * @since 3.0.4
     */
    public static <K, V> Map<K, V> newHashMap(int size, boolean isOrder) {
        int initialCapacity = (int) (size / DEFAULT_LOAD_FACTOR) + 1;
        return isOrder ? new LinkedHashMap<>(initialCapacity) : new HashMap<>(initialCapacity);
    }

    /**
     * 新建一个HashMap
     *
     * @param <K>  Key类型
     * @param <V>  Value类型
     * @param size 初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75 + 1
     * @return HashMap对象
     */
    public static <K, V> Map<K, V> newHashMap(int size) {
        return newHashMap(size, false);
    }


}
