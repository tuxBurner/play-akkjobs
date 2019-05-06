package com.fijimf.scheduler

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

import akka.actor
import akka.actor.{Actor, Props}
import cats.implicits._
import com.fijimf.scheduler.job.{JobInstance, ScheduledJobInstance}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

case class ScheduleJobMessage(className: String, runArg: String, cronString: String)

case class StartJobMessage(id: String)

case class SuccessJobMessage(id: String, succeededAt: LocalDateTime)

case class FailJobMessage(id: String, failedAt: LocalDateTime, throwable: Throwable)

case class CancelJobMessage(id: String, cancelledAt: LocalDateTime)

case object ShowSchedulerState

object Scheduler {
  def props(): Props = Props(new Scheduler)
}

class Scheduler extends Actor {

  import scala.concurrent.duration._

  val scheduler: actor.Scheduler = context.system.scheduler
  implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = handler(SchedulerState())

  def handler(state: SchedulerState): Receive = {
    case ScheduleJobMessage(className, runArg, cronString) =>
      val newState: SchedulerState =CronParser.findInterval(cronString) match {
        case Some(next) =>
           createJobInstance(className, runArg, cronString) match {
            case Some(s) =>
              val sch = ScheduledJobInstance(s, sender(), next)
              val interval: Long = ChronoUnit.MILLIS.between( LocalDateTime.now, next)
              if (interval > 0) {
                scheduler.scheduleOnce(interval.milliseconds, () => self ! StartJobMessage(sch.instance.id))
                state.scheduleJob(sch)
              } else {
                state.fail(ScheduledIntervalInThePast(cronString,next))
              }
            case None =>
              state.fail(CouldNotInstantiateJobInstance(className))
          }
        case None =>
          state.fail(BadCronString(cronString))
      }
      context become handler(newState)
      sender() ! newState
    case StartJobMessage(id) =>
      state.scheduledJobs.find(_.instance.id === id).foreach(sch => {
        val jobInstance = sch.instance
        val listener = sch.listener
        jobInstance.job.run(jobInstance.runArg).onComplete {
          case Success(_) => self ! SuccessJobMessage(id, LocalDateTime.now())
          case Failure(thr) => self ! FailJobMessage(id, LocalDateTime.now(), thr)
        }
        self ! jobInstance.createScheduleMessage
      })
      val newState = state.startJob(id, LocalDateTime.now())
      context become handler(newState)
      sender() ! newState
    case SuccessJobMessage(id, completedAt) =>
      val newState = state.completeJob(id, completedAt)
      context become handler(newState)
    case FailJobMessage(id, failedAt, thr) =>
      val newState = state.failJob(id, failedAt, thr)
      context become handler(newState)
    case CancelJobMessage(id, cancelledAt) =>
      val newState = state.cancelJob(id, cancelledAt)
      context become handler(newState)
      sender() ! newState
    case ShowSchedulerState => sender() ! state
    case _ =>
  }

  private def createJobInstance(className: String, runArg: String, cronString: String): Option[JobInstance] = {
    JobLoader.loadJob(className).map(JobInstance(UUID.randomUUID().toString, _, runArg, cronString))
  }


}


