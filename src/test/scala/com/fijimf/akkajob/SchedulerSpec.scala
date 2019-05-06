package com.fijimf.akkajob

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.fijimf.scheduler._
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}


class SchedulerSpec extends TestKit(ActorSystem("SchedulerSpec")) with ImplicitSender with FlatSpecLike with BeforeAndAfterAll {
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A Scheduler" should " schedule a job" in {
    val scheduler = system.actorOf(Scheduler.props())
    scheduler ! ScheduleJobMessage("com.fijimf.akkajob.TestEcho", "Hello World", "* * * ? * * ")
    val state = expectMsgType[SchedulerState]
    assert(state.scheduledJobs.size === 1)
    assert(state.runningJobs.size === 0)
    assert(state.completedJobs.size === 0)
    assert(state.failedJobs.size === 0)
    assert(state.cancelledJobs.size === 0)
    assert(state.failedOperations.size === 0)
  }

  it should " fail to schedule a job with a bad cron string schedule a job" in {
    val scheduler = system.actorOf(Scheduler.props())
    scheduler ! ScheduleJobMessage("com.fijimf.akkajob.TestEcho", "Hello World", "* A * * * ")
    val state = expectMsgType[SchedulerState]
    assert(state.scheduledJobs.size === 0)
    assert(state.runningJobs.size === 0)
    assert(state.completedJobs.size === 0)
    assert(state.failedJobs.size === 0)
    assert(state.cancelledJobs.size === 0)
    assert(state.failedOperations.size === 1)
    assert(state.failedOperations.head.isInstanceOf[BadCronString])
  }

  it should " fail to schedule a job with a bad job class " in {
    val scheduler = system.actorOf(Scheduler.props())
    scheduler ! ScheduleJobMessage("com.fijimf.akkajob.TestEchoXYZ", "Hello World", "* * * ? * * ")
    val state = expectMsgType[SchedulerState]
    assert(state.scheduledJobs.size === 0)
    assert(state.runningJobs.size === 0)
    assert(state.completedJobs.size === 0)
    assert(state.failedJobs.size === 0)
    assert(state.cancelledJobs.size === 0)
    assert(state.failedOperations.size === 1)
    assert(state.failedOperations.head.isInstanceOf[CouldNotInstantiateJobInstance])
  }


  it should " handle running a job" in {
    val scheduler = system.actorOf(Scheduler.props())
    scheduler ! ScheduleJobMessage("com.fijimf.akkajob.TestEcho", "Hello World", "* * * ? * *")
    val state = expectMsgType[SchedulerState]
    assert(state.scheduledJobs.size === 1)
    assert(state.runningJobs.size === 0)
    assert(state.completedJobs.size === 0)
    assert(state.failedJobs.size === 0)
    assert(state.cancelledJobs.size === 0)
    assert(state.failedOperations.size === 0)

    Thread.sleep(5000)
    scheduler ! ShowSchedulerState
    val newState = expectMsgType[SchedulerState]
    assert(newState.completedJobs.size === 1)
    assert(newState.scheduledJobs.size===1 || newState.runningJobs.size===1 )
  }

}
