package com.game.easyexecutor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.game.easyexecutor.Interceptor.IInterceptor;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EasyInterceptor {

	Class<? extends IInterceptor> value();
	boolean isSpringBean() default false;
}
