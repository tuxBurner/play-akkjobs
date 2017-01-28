package com.github.tuxBurner.jobs;

import akka.actor.Scheduler;
import com.google.inject.AbstractModule;
import play.Logger;

/**
 * This is a job Module which loads all classes which implemets {@link AbstractAkkaJob} and adds them to the {@link Scheduler}
 * 
 * @author tuxburner
 *
 */
public class JobModule extends AbstractModule {

  /**
   * The Logger for this Module.
   * Can be configured in your conf/logback.xml
   * &lt;logger name="com.github.tuxBurner.jobs.JobModule" level="Debug" /&gt;
   */
  public static Logger.ALogger LOGGER = Logger.of(JobModule.class);


  @Override
  protected void configure() {
    bind(JobClassLoader.class).asEagerSingleton();
  }
}
