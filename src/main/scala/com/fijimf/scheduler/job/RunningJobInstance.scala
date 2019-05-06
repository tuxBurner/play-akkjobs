package com.fijimf.scheduler.job

import java.time.LocalDateTime

case class RunningJobInstance(instance: JobInstance, scheduleStart: LocalDateTime, actualStart: LocalDateTime) {
  def complete(at: LocalDateTime): CompletedJobInstance = CompletedJobInstance(instance, scheduleStart, actualStart, at)

  def fail(at: LocalDateTime, thr: Throwable): FailedJobInstance = FailedJobInstance(instance, scheduleStart, actualStart, at, thr)
}
