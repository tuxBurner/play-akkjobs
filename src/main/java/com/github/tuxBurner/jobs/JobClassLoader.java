package com.github.tuxBurner.jobs;

import akka.actor.ActorSystem;
import com.google.inject.Singleton;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import play.inject.ApplicationLifecycle;
import play.libs.F;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This searches for all {@link Class}s annotated with {@link AkkaJob} and starts them.
 *
 * @author tuxburner
 */
@Singleton
public class JobClassLoader {

  /**
   * Set of the jobs which where found and loaded
   */
  public final Set<AbstractAkkaJob> jobs = new HashSet<AbstractAkkaJob>();

  /**
   * The {@link ActorSystem} handling the jobs.
   */
  private final ActorSystem actorSystem;

  @Inject
  JobClassLoader(final ActorSystem actorSystem, final ApplicationLifecycle lifecycle) {
    this.actorSystem = actorSystem;

    // when the application is stopping shut down the jobs  to prevent 
    lifecycle.addStopHook(() -> {
      for (AbstractAkkaJob job : jobs) {
        job.stopJob();
      }
      return F.Promise.pure(null);
    });

    onStart();
  }

  public void onStart() {
    final Reflections reflections = new Reflections(
        new ConfigurationBuilder().setUrls(
            ClasspathHelper.forClassLoader()).setScanners(
            new TypeAnnotationsScanner(), new SubTypesScanner()));
    Set<Class<? extends AbstractAkkaJob>> classes = reflections.getSubTypesOf(AbstractAkkaJob.class);

    // filter out the once we dont want to instantiate
    classes = classes.stream().filter(cl -> cl.isAnnotationPresent(NoInstanceJob.class) == false).collect(Collectors.toSet());

    if (JobModule.LOGGER.isInfoEnabled() == true) {
      JobModule.LOGGER.info("Found: " + classes.size() + " of the type: " + AbstractAkkaJob.class + ".");
    }

    for (final Class clazz : classes) {
      JobModule.LOGGER.debug("Trying to load class: " + clazz.getCanonicalName());
      try {
        final Class<AbstractAkkaJob> abstractJobClass = clazz;
        final Constructor<AbstractAkkaJob> constructor = abstractJobClass.getConstructor(ActorSystem.class);
        if (constructor == null) {
          continue;
        }
        final AbstractAkkaJob newInstance = constructor.newInstance(actorSystem);
        if (newInstance == null) {
          continue;
        }
        jobs.add(newInstance);

        newInstance.scheduleJob();

      } catch (final NoSuchMethodException e) {
        JobModule.LOGGER.error("Could not find default constructor with no parameters in: " + clazz, e);
      } catch (final SecurityException e) {
        JobModule.LOGGER.error("Error while initializing class: " + clazz, e);
      } catch (final InstantiationException e) {
        JobModule.LOGGER.error("Error while initializing class: " + clazz, e);
      } catch (final IllegalAccessException e) {
        JobModule.LOGGER.error("Error while initializing class: " + clazz, e);
      } catch (final IllegalArgumentException e) {
        JobModule.LOGGER.error("Error while initializing class: " + clazz, e);
      } catch (final InvocationTargetException e) {
        JobModule.LOGGER.error("Error while initializing class: " + clazz, e);
      }
    }
  }
}
