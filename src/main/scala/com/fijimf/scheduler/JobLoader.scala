package com.fijimf.scheduler

import com.fijimf.scheduler.job.AbstractJob
import org.reflections.Reflections
import org.reflections.scanners.{SubTypesScanner, TypeAnnotationsScanner}
import org.reflections.util.{ClasspathHelper, ConfigurationBuilder}

import scala.collection.JavaConverters._
import scala.util.Try

object JobLoader {
  val reflections: Reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forClassLoader).setScanners(new TypeAnnotationsScanner, new SubTypesScanner))

  def loadJob(className: String): Option[AbstractJob] = {
    Try {
      reflections.getSubTypesOf(classOf[AbstractJob]).asScala.toList
        .find(_.getName == className)
        .map(_.getConstructor().newInstance())
    }.toOption.flatten
  }
}
