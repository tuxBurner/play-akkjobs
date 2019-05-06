package com.fijimf.scheduler

import java.time.{LocalDateTime, ZoneId}
import java.util.Date

import play.libs.Time.CronExpression

import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.Try

object CronParser {
  def findInterval(cronString: String, buffer:FiniteDuration=3.seconds): Option[LocalDateTime] = {
    for {
      cron <- Try(new CronExpression(cronString)).toOption
    } yield {
      LocalDateTime.ofInstant(cron.getNextValidTimeAfter(new Date()).toInstant.plusMillis(buffer.toMillis), ZoneId.systemDefault())
    }
  }
}
