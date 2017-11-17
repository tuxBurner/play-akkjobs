package com.github.tuxBurner.jobs;

import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import play.libs.Time.CronExpression;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.github.tuxBurner.jobs.JobModule.LOGGER;

public abstract class AbstractAkkaJob implements Runnable {

  /**
   * The Cron-Expression of this job.
   */
  protected CronExpression cronExpression;

  /**
   * The play actorsystem to use for the job handling.
   */
  private final ActorSystem actorSystem;

  /**
   * The current state of the job
   */
  private EJobRunState runState;

  /**
   * Restart this job when it failed ?
   */
  private boolean restartOnFail = true;

  /**
   * The date when the job will be next fired
   */
  private Date nextFireDate;

  /**
   * When the job runs this is the cancallable
   */
  private Cancellable cancellable;

  public AbstractAkkaJob(final ActorSystem actorSystem) throws JobException {

    runState = EJobRunState.STOPPED;

    this.actorSystem = actorSystem;
  }

  /**
   * Schedules the job
   */
  public void scheduleJob() {

    if(EJobRunState.DISABLED.equals(runState) == true) {
      LOGGER.info("Runstate for the job: "+this.getClass().getName()+" is "+EJobRunState.DISABLED+" not going to run");
      return;
    }

    if(cronExpression == null) {
      LOGGER.error("No Cron expression set at: "+this.getClass().getName());
      return;
    }


    final Date now = new Date();

    Date nextExecuteTime = now;

    // no next fire date or is not supposed to be executed immediately
    if(nextFireDate != null || isSubmittedImmediately() == false) {
      nextExecuteTime = cronExpression.getNextValidTimeAfter(now);
    }

    // set the date when to execute the fire date
    nextFireDate = nextExecuteTime;

    final FiniteDuration duration = Duration.create(nextFireDate.getTime() - now.getTime(), TimeUnit.MILLISECONDS);
    runState = EJobRunState.SCHEDULED;
    cancellable = actorSystem.scheduler().scheduleOnce(duration, this, actorSystem.dispatcher());

    if(LOGGER.isDebugEnabled() == true) {
      LOGGER.debug(this.getClass().getName()+" job is running again in: "+duration.toString()+" @ "+nextFireDate);
    }
  }

  @Override
  public void run() {

    // check if the state is not running
    if(EJobRunState.RUNNING.equals(runState) == true) {
      LOGGER.warn(this.getClass().getName()+" job not started because it is still in run mode.");
      scheduleJob();
      return;
    }

    if(LOGGER.isDebugEnabled() == true) {
      LOGGER.debug(this.getClass().getName()+" job is going to run.");
    }


    try {
      runState = EJobRunState.RUNNING;
      runInternal();
      runState = EJobRunState.STOPPED;
    } catch (final Exception e) {
      LOGGER.error("An error happened in the internal implementation of the job: " + this.getClass().getName(), e);
      runState = EJobRunState.ERROR;
      if (restartOnFail == false) {
        runState = EJobRunState.KILLED;
        if (LOGGER.isDebugEnabled() == true) {
          LOGGER.debug("Will not restart the job: " + this.getClass().getName());
        }
        return;
      }
    }

    scheduleJob();
  }

  public void setRestartOnFail(final boolean restartOnFail) {
    this.restartOnFail = restartOnFail;
  }

  /**
   * Here you can implement the actual doing of the job
   */
  public abstract void runInternal();

  /**
   * This is called when the {@link AbstractAkkaJob#stopJob()} is called.
   * You can override this method in your implementation if you need to.
   */
  public void stopInternalJob() {
    // noop
  }

  /**
   * Stops the current job by calling the {@link Cancellable}
   */
  public void stopJob() {
    if(cancellable == null) {
      LOGGER.info("Not stopping the job: "+this.getClass().getName()+" no cancellable found.");
      return;
    }

    if(cancellable.isCancelled() == true) {
      return;
    }

    // some internal clean up by the job needed ?
    stopInternalJob();

    LOGGER.info("Stop for job: "+this.getClass().getName()+" called.");
    cancellable.cancel();
  }

  public CronExpression getCronExpression() {
    return cronExpression;
  }

  public EJobRunState getRunState() {
    return runState;
  }

  public boolean isRestartOnFail() {
    return restartOnFail;
  }

  public void setCronExpression(CronExpression cronExpression) {
    this.cronExpression = cronExpression;
  }

  public ActorSystem getActorSystem() {
    return actorSystem;
  }

  public void setRunState(EJobRunState runState) {
    this.runState = runState;
  }

  public Date getNextFireDate() {
    return nextFireDate;
  }

  public void setNextFireDate(Date nextFireDate) {
    this.nextFireDate = nextFireDate;
  }

  /**
   * When set to true the job is submitted immediately
   * @return true when the job should be executed when it is constructed the first time, false when not.
   */
  public boolean isSubmittedImmediately() {
    return false;
  }
}
