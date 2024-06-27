package com.jason.jasontools.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年06月24日
 */
public class ArrayUtil {

    public static boolean isEmpty(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Object[] objects) {
        if (objects == null || objects.length == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }


    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

}
