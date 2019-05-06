package com.fijimf.scheduler.job

import com.fijimf.scheduler.ScheduleJobMessage

case class JobInstance(id: String, job: AbstractJob, runArg: String, cronString: String) {
  def createScheduleMessage:ScheduleJobMessage = {
    ScheduleJobMessage(job.getClass.getName, runArg, cronString)
  }
}









