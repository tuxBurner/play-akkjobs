package com.fijimf.scheduler.job

import java.time.LocalDateTime

case class CancelledJobInstance(instance: JobInstance, scheduleStart: LocalDateTime, cancelledAt: LocalDateTime)
