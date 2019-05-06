package com.fijimf.scheduler.job

import java.time.LocalDateTime

case class CompletedJobInstance(instance: JobInstance, scheduleStart: LocalDateTime, actualStart: LocalDateTime, succeededAt: LocalDateTime)
