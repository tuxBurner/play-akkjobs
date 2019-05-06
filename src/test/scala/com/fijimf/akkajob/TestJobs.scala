package com.fijimf.akkajob

import com.fijimf.scheduler.job.AbstractJob

import scala.concurrent.Future


class TestJob1() extends AbstractJob {
  override def run(arg: String): Future[String] = ???
}

class TestJob2(bad: String) extends AbstractJob {
  override def run(arg: String): Future[String] = ???
}

class TestJob3() {
  def run(arg: String): Future[String] = ???
}


class TestEcho() extends AbstractJob {
  override def run(arg: String): Future[String] = Future.successful(arg)
}

class TestLongEcho() extends AbstractJob {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def run(arg: String): Future[String] = Future {
    Thread.sleep(20000L)
    arg
  }
}

class TestBadEcho() extends AbstractJob {
  override def run(arg: String): Future[String] = Future.failed(new RuntimeException("Failed"))
}


