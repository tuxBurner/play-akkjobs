package com.fijimf.akkajob

import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.fijimf.scheduler.job.{AbstractJob, JobInstance, ScheduledJobInstance}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, FlatSpecLike}

import scala.concurrent.Future

class RunningJobInstanceSpec extends TestKit(ActorSystem("SchedulerSpec")) with ImplicitSender with FlatSpecLike with BeforeAndAfterAll {
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val job = new AbstractJob {
    override def run(arg: String): Future[String] = ???
  }

  "A RunningJobInstance" should "create a CompletedJobInstance" in {

    val running = ScheduledJobInstance(JobInstance("123-abc-999", job,"","* * * * * *"), testActor, LocalDateTime.now).start(LocalDateTime.now)
    val completed = running.complete(LocalDateTime.now.plusNanos(1000000L))
    assert(completed.instance===running.instance)
    assert(running.scheduleStart === completed.scheduleStart)
    assert(running.actualStart === completed.actualStart)

  }

  "A RunningJobInstance"  should " create a FailedJobInstance" in {
    val running = ScheduledJobInstance(JobInstance("123-abc-999", job,"","* * * * * *"), testActor, LocalDateTime.now).start(LocalDateTime.now)
    val failed = running.fail(LocalDateTime.now.plusNanos(1000000L),new RuntimeException)
    assert(failed.instance===running.instance)
    assert(running.scheduleStart === failed.scheduleStart)
    assert(running.actualStart === failed.actualStart )
  }

}
