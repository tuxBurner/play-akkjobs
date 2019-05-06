package com.fijimf.scheduler.job

import java.time.LocalDateTime

case class FailedJobInstance(instance: JobInstance, scheduleStart: LocalDateTime, actualStart: LocalDateTime, failedAt: LocalDateTime, exception: Throwable)
