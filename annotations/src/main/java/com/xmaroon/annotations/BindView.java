package com.xmaroon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xuqingsong
 * bindview注解类
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface BindView {
    int value();
}