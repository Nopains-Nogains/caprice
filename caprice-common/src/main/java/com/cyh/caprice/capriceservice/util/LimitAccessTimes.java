package com.cyh.caprice.capriceservice.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cuiyuhao
 * 指定提交时间
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitAccessTimes {
    /**
     * 指定时间内不可重复提交，单位：s
     *
     * @return
     */
    long timeout() default 30;
}
