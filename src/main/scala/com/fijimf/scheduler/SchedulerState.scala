package com.fijimf.scheduler

import java.time.LocalDateTime

import cats.implicits._
import com.fijimf.scheduler.job._
import play.api.Logger

case class SchedulerState
(
  scheduledJobs: List[ScheduledJobInstance] = List.empty[ScheduledJobInstance],
  runningJobs: List[RunningJobInstance] = List.empty[RunningJobInstance],
  completedJobs: List[CompletedJobInstance] = List.empty[CompletedJobInstance],
  failedJobs: List[FailedJobInstance] = List.empty[FailedJobInstance],
  cancelledJobs: List[CancelledJobInstance] = List.empty[CancelledJobInstance],
  failedOperations: List[SchedulerFailure] = List.empty[SchedulerFailure]
) {
  val log = Logger(SchedulerState.getClass)

  def fail(failure: SchedulerFailure): SchedulerState = copy(failedOperations = failure :: failedOperations)

  def scheduleJob(sched: ScheduledJobInstance): SchedulerState = {
    if (scheduledJobs.exists(_.instance.id === sched.instance.id)) {
      fail(DuplicateId(sched.instance.id))
    } else {
      copy(scheduledJobs = sched :: scheduledJobs)
    }
  }

  def startJob(id: String, now: LocalDateTime): SchedulerState = {
    scheduledJobs.find(_.instance.id === id) match {
      case Some(sched) =>
        val running = sched.start(now)
        copy(scheduledJobs.filter(_.instance.id != id), runningJobs = running :: runningJobs)
      case None => fail(JobNotFound(id, ScheduledJob, now))
    }
  }

  def completeJob(id: String, now: LocalDateTime): SchedulerState = {
    runningJobs.find(_.instance.id === id) match {
      case Some(running) =>
        val complete = running.complete(now)
        copy(runningJobs = runningJobs.filter(_.instance.id != id), completedJobs = complete :: completedJobs)
      case None => fail(JobNotFound(id, RunningJob, now))
    }
  }

  def failJob(id: String, now: LocalDateTime, thr: Throwable): SchedulerState = {
    runningJobs.find(_.instance.id === id) match {
      case Some(running) =>
        val failed = running.fail(now, thr)
        copy(runningJobs = runningJobs.filter(_.instance.id != id), failedJobs = failed :: failedJobs)
      case None => fail(JobNotFound(id, RunningJob, now))
    }
  }

  def cancelJob(id: String, now: LocalDateTime): SchedulerState = {
    scheduledJobs.find(_.instance.id === id) match {
      case Some(sched) =>
        val cancelled = sched.cancel(now)
        copy(scheduledJobs.filter(_.instance.id != id), cancelledJobs = cancelled :: cancelledJobs)
      case None => fail(JobNotFound(id, ScheduledJob, now))
    }
  }

  override def toString: String = {
    s"\n-------------\nScheduled Jobs: ${scheduledJobs.size}\n"+
    s"Running Jobs: ${runningJobs.size}\n"+
    s"Completed Jobs: ${completedJobs.size}\n"+
    s"Cancelled Jobs: ${cancelledJobs.size}\n"+
    s"Failed Jobs: ${failedJobs.size}\n"+
      s"FailedOps:\n" + failedOperations.map(op=>s"  ${op.timestamp} ${op.reason} ").mkString("\n")+"-------------\n"
  }
}
