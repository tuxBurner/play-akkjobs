package com.fijimf.scheduler.job

import java.time.LocalDateTime

import akka.actor.ActorRef

case class ScheduledJobInstance(instance: JobInstance, listener: ActorRef, scheduleStart: LocalDateTime) {
  def start(at: LocalDateTime): RunningJobInstance = RunningJobInstance(instance, scheduleStart, at)

  def cancel(at: LocalDateTime): CancelledJobInstance = CancelledJobInstance(instance, scheduleStart, at)
}
