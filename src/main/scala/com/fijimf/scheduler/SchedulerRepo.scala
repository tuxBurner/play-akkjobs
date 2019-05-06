package com.fijimf.scheduler

import scala.concurrent.Future

trait SchedulerRepo {
  def bootstrapJobs: Future[List[(String, String, String)]]

  def saveJob(className: String, runArg: String, cronString: String): Future[Boolean]

  def deleteJob(className: String, runArg: String, cronString: String): Future[Boolean]
}
