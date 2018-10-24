package com.jk51.commons.base;

import org.apache.commons.lang.StringUtils;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ztq on 2017/12/27
 * Description:
 */
public class Preconditions {

    /**
     * 在参数类型是int而不是Integer的时候，判断该参数是否为零
     * 功能参考{@com.google.common.base.Preconditions#checkNotNull(java.lang.Object)}
     *
     * @param num
     * @return
     */
    public static Integer checkNotZero(int num) {
        if (num == 0) {
            throw new RuntimeException("参数不能为零异常");
        }
        return num;
    }

    public static <T> T passPredicateOrThrow(Predicate<T> predicate, T param) {
        if (predicate.test(checkNotNull(param))) {
            return param;
        }
        throw new RuntimeException();
    }

    public static String checkNotBlank(String str) {
        if (StringUtils.isBlank(str)) {
            throw new RuntimeException("String参数不能为空异常");
        }

        return str;
    }
}
