package com.fijimf.akkajob

import java.util.UUID

import com.fijimf.scheduler.JobLoader
import com.fijimf.scheduler.job.JobInstance
import org.scalatest.FlatSpec

class JobInstanceSpec extends FlatSpec {

  "A JobInstance " should "generate its own ScheduleJobMessage" in {
    val instance = JobInstance(UUID.randomUUID().toString, new TestJob1(), "ARG", "* * * * ? *")
    val message = instance.createScheduleMessage
    assert(message.className === "com.fijimf.akkajob.TestJob1")
    assert(message.cronString === "* * * * ? *")
    assert(message.runArg === "ARG")

    assert(JobLoader.loadJob(message.className).isDefined)
  }

}
