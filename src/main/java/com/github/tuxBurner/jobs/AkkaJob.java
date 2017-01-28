package com.github.tuxBurner.jobs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This Annotation is for creating a Job in the akka stack
 */

/**
 * @author tuxburner
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AkkaJob {

  String cronExpression();

}
