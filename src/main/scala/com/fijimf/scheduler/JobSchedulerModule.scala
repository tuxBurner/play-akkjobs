package com.fijimf.scheduler

import com.google.inject.AbstractModule
import play.api.Logger

class JobSchedulerModule extends AbstractModule {

  val log = Logger(classOf[JobSchedulerModule])


  override protected def configure(): Unit = {
    bind(classOf[Scheduler]).asEagerSingleton()
  }

}
