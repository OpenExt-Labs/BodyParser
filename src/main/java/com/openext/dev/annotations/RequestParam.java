package com.openext.dev.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String name();
    boolean required() default false;
    String defaultValue() default "";
    String message() default "";
}
