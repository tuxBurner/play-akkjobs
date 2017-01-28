package com.github.tuxBurner.jobs;

import akka.actor.ActorSystem;
import play.libs.Time;

import java.lang.annotation.Annotation;
import java.text.ParseException;

import static com.github.tuxBurner.jobs.JobModule.LOGGER;

/**
 * A Job which reads its cron expression via the {@link AkkaJob} annotation.
 * Created by tuxburner on 28.01.17.
 */
@NoInstanceJob
public abstract class AbstractAnnotatedJob extends AbstractAkkaJob {

  public AbstractAnnotatedJob(ActorSystem actorSystem) throws JobException {
    super(actorSystem);

    final Annotation annotation = this.getClass().getAnnotation(AkkaJob.class);
    final AkkaJob akkaJob = (AkkaJob) annotation;

    final String annoCronExpression = akkaJob.cronExpression().trim();

    final boolean validExpression = Time.CronExpression.isValidExpression(annoCronExpression);
    if (validExpression == false) {
      final String message = "The annotated cronExpression: " + annoCronExpression + " is not a valid CronExpression in class: " + this.getClass().getName();
      LOGGER.error(message);
      throw new JobException(message);
    }
    try {
      cronExpression = new Time.CronExpression(annoCronExpression);
    } catch (ParseException e) {
      throw new JobException(e);
    }
  }
}
