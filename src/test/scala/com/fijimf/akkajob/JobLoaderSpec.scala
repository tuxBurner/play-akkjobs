package com.fijimf.akkajob

import com.fijimf.scheduler.JobLoader
import com.fijimf.scheduler.job.AbstractJob
import org.scalatest.FlatSpec

class JobLoaderSpec extends FlatSpec {

  "A The JobLoader" should "instantiate a subclass of an AbstractJob" in {

    val oj = JobLoader.loadJob("com.fijimf.akkajob.TestJob1")

    assert(oj.isDefined)
    assert(oj.forall(_.isInstanceOf[AbstractJob]))
    assert(oj.forall(_.isInstanceOf[TestJob1]))

  }

  it should "return None for a subclass of an AbstractJob without a zero arg const" in {

    val oj = JobLoader.loadJob("com.fijimf.akkajob.TestJob2")

    assert(oj.isEmpty)
  }

  it should " return None for a class that is not a subclass of AbstractJob" in {

    val oj = JobLoader.loadJob("com.fijimf.akkajob.TestJob3")

    assert(oj.isEmpty)
  }

  it should "return None for an unknown class" in {

    val oj = JobLoader.loadJob("TestJobXXX")
    assert(oj.isEmpty)
    val pj = JobLoader.loadJob("")
    assert(pj.isEmpty)
  }

}
