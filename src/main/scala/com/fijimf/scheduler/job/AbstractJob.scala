package com.fijimf.scheduler.job

import scala.concurrent.Future

trait AbstractJob {
  def run(arg: String): Future[String]
}
