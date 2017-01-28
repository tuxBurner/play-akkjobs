package com.github.tuxBurner.jobs;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import play.libs.Time;

import java.text.ParseException;


/**
 * A {@link AbstractAkkaJob} implementation which takes its configuration from the {@link Config} of the application.
 * To configure a job you must define a config object
 * jobs.&lt;className&gt;.cronExpression and  jobs.&lt;className&gt;.enabled
 * 
 * Created by tuxburner on 28.01.17.
 */
@NoInstanceJob
public abstract class AbstractConfigurationJob extends AbstractAkkaJob {

  /**
   * The {@link Config} of this job so you can access it in your implementation.
   */
  private final Config configuration;

  
  public AbstractConfigurationJob(ActorSystem actorSystem) throws JobException {
    super(actorSystem);

    try {
      Config config = ConfigFactory.load().getConfig("jobs." + this.getClass().getName());
      boolean enabled = config.getBoolean("enabled");
      if (enabled == false) {
        setRunState(EJobRunState.DISABLED);
      }

      String cronExpression = config.getString("cronExpression");
      cronExpression = StringUtils.trim(cronExpression);
      if (StringUtils.isBlank(cronExpression) == true) {
        throw new JobException(this.getClass().getName() + " has no cronExpression at jobs." + this.getClass().getName());
      }

      this.cronExpression = new Time.CronExpression(cronExpression);
      this.configuration = config;

    } catch (ConfigException.Missing | ParseException e) {
      final String message = this.getClass().getName() + " has a configuration problem.";
      throw new JobException(message, e);
    }
  }

  public Config getConfiguration() {
    return configuration;
  }
}
