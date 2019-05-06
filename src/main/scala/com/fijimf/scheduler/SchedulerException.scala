package com.fijimf.scheduler

import java.time.LocalDateTime

sealed trait JobLookUpType

case object ScheduledJob extends JobLookUpType

case object RunningJob extends JobLookUpType


sealed trait SchedulerFailure {
  def reason: String

  def timestamp: LocalDateTime
}

case class BadCronString(cronStr: String, timestamp: LocalDateTime = LocalDateTime.now()) extends SchedulerFailure {
  def reason = s"The cron string '$cronStr' is not valid."
}

case class ScheduledIntervalInThePast(cronStr: String, next:LocalDateTime, timestamp: LocalDateTime = LocalDateTime.now()) extends SchedulerFailure {
  def reason = s"For cron string '$cronStr' the scheduler calculated an next runDate in the past $next"
}

case class CouldNotInstantiateJobInstance(jobClass: String, timestamp: LocalDateTime = LocalDateTime.now()) extends SchedulerFailure {
  def reason = s"Unable to instantiate an object for class '$jobClass'."
}

case class DuplicateId(id: String, timestamp: LocalDateTime = LocalDateTime.now()) extends SchedulerFailure {
  def reason = s"Attempt to schedule a job with an existing ID: $id"
}

case class JobNotFound(id: String, lookupType: JobLookUpType, timestamp: LocalDateTime = LocalDateTime.now()) extends SchedulerFailure {
  def reason = s"Unable to find job with ID: $id"
}

