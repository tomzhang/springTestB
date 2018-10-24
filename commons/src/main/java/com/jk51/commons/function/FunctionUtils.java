package com.jk51.commons.function;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by ztq on 2018/1/30
 * Description:
 */
public class FunctionUtils {

    /**
     * 如果校验成功，返回Supplier结果，否则为null
     *
     * @param supplier
     * @param predicate
     * @param <R>
     * @return
     */
    @Nullable
    public static <R> R getIfSuccess(Supplier<R> supplier, Predicate<R> predicate) {
        R r = supplier.get();
        if (predicate.test(r)) {
            return r;
        } else {
            return null;
        }
    }

    /**
     * 如果校验成功，返回Supplier结果，否则为null
     *
     * @param supplier
     * @param predicate
     * @param <R>
     * @return
     */
    @Nullable
    public static <R> R getIfSuccessOrElse(Supplier<R> supplier, Predicate<R> predicate, R result) {
        R r = supplier.get();
        if (predicate.test(r)) {
            return supplier.get();
        } else {
            return result;
        }
    }

    /**
     * 如果校验成功，返回Function结果，否则为null
     *
     * @param function
     * @param predicate
     * @param t
     * @param <R>
     * @return
     */
    @Nullable
    public static <T, R> R getIfSuccess(Function<T, R> function, Predicate<R> predicate, T t) {
        R r = function.apply(t);
        if (predicate.test(r)) {
            return r;
        } else {
            return null;
        }
    }

}
