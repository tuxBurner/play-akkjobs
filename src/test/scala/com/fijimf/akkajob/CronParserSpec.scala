package com.fijimf.akkajob

import java.time.LocalDateTime

import com.fijimf.scheduler.CronParser
import org.scalatest.FlatSpec

class CronParserSpec extends FlatSpec {
  "A CronParser" should "Parse valid cron tab strings" in {
    val now = LocalDateTime.now
    val examples = List(
      ("* * * ? * *", "Every second"),
      ("0 * * ? * *", "Every minute at zero seconds"),
      ("0/5 * * ? * *", "Every five seconds starting at 0 seconds"),
      ("0 0 12 * * ?", "Fire at 12:00 PM (noon) every day"),
      ("0 15 10 ? * *", "Fire at 10:15 AM every day"),
      ("0 15 10 * * ?", "Fire at 10:15 AM every day"),
      ("0 15 10 * * ? *", "Fire at 10:15 AM every day"),
      ("0 15 10 * * ? 2065", "Fire at 10:15 AM every day during the year 2065"),
      ("0 * 14 * * ?", "Fire every minute starting at 2:00 PM and ending at 2:59 PM, every day"),
      ("0 0/5 14 * * ?", "Fire every 5 minutes starting at 2:00 PM and ending at 2:55 PM, every day"),
      ("0 0/5 14,18 * * ?", "Fire every 5 minutes starting at 2:00 PM and ending at 2:55 PM, AND fire every 5 minutes starting at 6:00 PM and ending at 6:55 PM, every day"),
      ("0 0-5 14 * * ?", "Fire every minute starting at 2:00 PM and ending at 2:05 PM, every day"),
      ("0 10,44 14 ? 3 WED", "Fire at 2:10 PM and at 2:44 PM every Wednesday in the month of March"),
      ("0 15 10 ? * MON-FRI", "Fire at 10:15 AM every Monday, Tuesday, Wednesday, Thursday and Friday"),
      ("0 15 10 15 * ?", "Fire at 10:15 AM on the 15th day of every month"),
      ("0 15 10 L * ?", "Fire at 10:15 AM on the last day of every month"),
      ("0 15 10 ? * 6L", "Fire at 10:15 AM on the last Friday of every month"),
      ("0 15 10 ? * 6L", "Fire at 10:15 AM on the last Friday of every month"),
      ("0 15 10 ? * 6L 2062-2065	", "Fire at 10:15 AM on every last friday of every month during the years 2062, 2063, 2064, and 2065"),
      ("0 15 10 ? * 6#3	", "Fire at 10:15 AM on the third Friday of every month"),
      ("0 0 12 1/5 * ?	", "Fire at 12 PM (noon) every 5 days every month, starting on the first day of the month"),
      ("0 11 11 11 11 ?	", "Fire every November 11 at 11:11 AM")
    )
    examples.foreach{case (s,t)=>
      val mt = CronParser.findInterval(s)
      assert(mt.map(_.isAfter(now)) === Some(true))
        println("%28s ==> %-28s %s".format(s, mt.map(_.toString).getOrElse("~Error~"), t))
    }
  }

}
