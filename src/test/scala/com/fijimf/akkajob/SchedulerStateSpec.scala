package com.fijimf.akkajob

import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.fijimf.scheduler.job.{AbstractJob, JobInstance, ScheduledJobInstance}
import com.fijimf.scheduler.SchedulerState
import org.scalatest.{BeforeAndAfterAll, FlatSpec, FlatSpecLike}

import scala.concurrent.Future

class SchedulerStateSpec extends TestKit(ActorSystem("SchedulerSpec")) with ImplicitSender with FlatSpecLike with BeforeAndAfterAll {
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val job = new AbstractJob {
    override def run(arg: String): Future[String] = ???
  }

  val instanceA = ScheduledJobInstance(JobInstance("A-123-456", job, "", "* * * * * *"), testActor, LocalDateTime.now.plusHours(1L))
  val instanceB = ScheduledJobInstance(JobInstance("B-123-456", job, "", "* * * * * *"), testActor, LocalDateTime.now.plusHours(1L))

  "SchedulerState" should "be empty initially" in {
    val state = SchedulerState()
    assert(state.scheduledJobs.isEmpty)
    assert(state.runningJobs.isEmpty)
    assert(state.completedJobs.isEmpty)
    assert(state.failedJobs.isEmpty)
    assert(state.cancelledJobs.isEmpty)
    assert(state.failedOperations.isEmpty)
  }

  it should " schedule a job" in {
    val state = SchedulerState().scheduleJob(instanceA)
    assert(state.scheduledJobs.size === 1)
    assert(state.runningJobs.isEmpty)
    assert(state.completedJobs.isEmpty)
    assert(state.failedJobs.isEmpty)
    assert(state.cancelledJobs.isEmpty)
    assert(state.failedOperations.isEmpty)
  }

  it should " reject a job with the same id" in {
    val state = SchedulerState()
      .scheduleJob(instanceA)
      .scheduleJob(instanceA)
    assert(state.scheduledJobs.size === 1)
    assert(state.runningJobs.isEmpty)
    assert(state.completedJobs.isEmpty)
    assert(state.failedJobs.isEmpty)
    assert(state.cancelledJobs.isEmpty)
    assert(state.failedOperations.size == 1)
  }


  it should "start a job" in {
    val state = SchedulerState()
      .scheduleJob(instanceA)
      .startJob(instanceA.instance.id, LocalDateTime.now)
    assert(state.scheduledJobs.isEmpty)
    assert(state.runningJobs.size === 1)
    assert(state.completedJobs.isEmpty)
    assert(state.failedJobs.isEmpty)
    assert(state.cancelledJobs.isEmpty)
    assert(state.failedOperations.isEmpty)
  }

  it should " fail to start an unmatched id" in {
    val state = SchedulerState()
      .scheduleJob(instanceA)
      .startJob(instanceB.instance.id, LocalDateTime.now)
    assert(state.scheduledJobs.size === 1)
    assert(state.runningJobs.isEmpty)
    assert(state.completedJobs.isEmpty)
    assert(state.failedJobs.isEmpty)
    assert(state.cancelledJobs.isEmpty)
    assert(state.failedOperations.size == 1)
  }

  it should "cancel a job" in {
    val state = SchedulerState()
      .scheduleJob(instanceA)
      .cancelJob("A-123-456", LocalDateTime.now)
    assert(state.scheduledJobs.isEmpty)
    assert(state.runningJobs.isEmpty)
    assert(state.completedJobs.isEmpty)
    assert(state.failedJobs.isEmpty)
    assert(state.cancelledJobs.size === 1)
    assert(state.failedOperations.isEmpty)
  }

  it should " fail to cancel an unmatched id" in {
    val state = SchedulerState()
      .scheduleJob(instanceA)
      .cancelJob(instanceB.instance.id, LocalDateTime.now)
    assert(state.scheduledJobs.size === 1)
    assert(state.runningJobs.isEmpty)
    assert(state.completedJobs.isEmpty)
    assert(state.failedJobs.isEmpty)
    assert(state.cancelledJobs.isEmpty)
    assert(state.failedOperations.size == 1)
  }


  it should "success a job" in {
    val state = SchedulerState()
      .scheduleJob(instanceA)
      .startJob("A-123-456", LocalDateTime.now)
      .completeJob("A-123-456", LocalDateTime.now)
    assert(state.scheduledJobs.isEmpty)
    assert(state.runningJobs.isEmpty)
    assert(state.completedJobs.size === 1)
    assert(state.failedJobs.isEmpty)
    assert(state.cancelledJobs.isEmpty)
    assert(state.failedOperations.isEmpty)
  }

  it should " fail to success an unmatched id" in {
    val state = SchedulerState()
      .scheduleJob(instanceA)
      .startJob("A-123-456", LocalDateTime.now)
      .completeJob(instanceB.instance.id, LocalDateTime.now)
    assert(state.scheduledJobs.isEmpty)
    assert(state.runningJobs.size === 1)
    assert(state.completedJobs.isEmpty)
    assert(state.failedJobs.isEmpty)
    assert(state.cancelledJobs.isEmpty)
    assert(state.failedOperations.size == 1)
  }

  it should "fail a job" in {
    val state = SchedulerState()
      .scheduleJob(instanceA)
      .startJob("A-123-456", LocalDateTime.now)
      .failJob("A-123-456", LocalDateTime.now, new RuntimeException)
    assert(state.scheduledJobs.isEmpty)
    assert(state.runningJobs.isEmpty)
    assert(state.completedJobs.isEmpty)
    assert(state.failedJobs.size === 1)
    assert(state.cancelledJobs.isEmpty)
    assert(state.failedOperations.isEmpty)
  }

  it should " fail to fail an unmatched id" in {
    val state = SchedulerState()
      .scheduleJob(instanceA)
      .startJob("A-123-456", LocalDateTime.now)
      .failJob(instanceB.instance.id, LocalDateTime.now, new RuntimeException)
    assert(state.scheduledJobs.isEmpty)
    assert(state.runningJobs.size === 1)
    assert(state.completedJobs.isEmpty)
    assert(state.failedJobs.isEmpty)
    assert(state.cancelledJobs.isEmpty)
    assert(state.failedOperations.size == 1)
  }


}
