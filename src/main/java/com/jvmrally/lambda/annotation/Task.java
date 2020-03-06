package com.jvmrally.lambda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation to define a repeatable Task
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Task {
    int frequency();

    TimeUnit unit();

    boolean disabled() default false;

    boolean delayStart() default false;
}
