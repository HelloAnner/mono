package com.anner.common.collection;

import java.util.Collection;

/**
 * Created by anner on 2023/3/23
 */
public class CollectionUtils {


    public static <E> boolean isEmpty(Collection<E> list) {
        return list == null || list.isEmpty();
    }

    public static <E> boolean isNotEmpty(Collection<E> list) {
        return !isEmpty(list);
    }
}
