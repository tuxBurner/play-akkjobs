package com.fijimf.akkajob

import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.fijimf.scheduler.job.{AbstractJob, JobInstance, ScheduledJobInstance}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}

import scala.concurrent.Future

class ScheduledJobInstanceSpec extends TestKit(ActorSystem("SchedulerSpec")) with ImplicitSender with FlatSpecLike with BeforeAndAfterAll {
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val job = new AbstractJob {
    override def run(arg: String): Future[String] = ???
  }

  "A SchaduledJobInstance" should "create a RunningJobInstanceSpec" in {
    val sched = ScheduledJobInstance(JobInstance("123-abc-999", job, "", "* * * * * *"), testActor, LocalDateTime.now)
    val running = sched.start(LocalDateTime.now.plusNanos(1000000L))
    assert(sched.instance === running.instance)
    assert(running.scheduleStart === sched.scheduleStart)
    assert(running.actualStart.isAfter(sched.scheduleStart))
  }

  "A SchaduledJobInstance" should "create a CancelledJobInstance" in {
    val sched = ScheduledJobInstance(JobInstance("123-abc-999", job, "", "* * * * * *"), testActor, LocalDateTime.now)
    val cancelled = sched.cancel(LocalDateTime.now.plusNanos(1000000L))
    assert(sched.instance === cancelled.instance)
    assert(cancelled.scheduleStart === sched.scheduleStart)
    assert(cancelled.cancelledAt.isAfter(sched.scheduleStart))
  }

}
