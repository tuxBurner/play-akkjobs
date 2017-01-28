# Play akka job library

This is a simple Play 2.5 module, which allows you to manage jobs.


## Installation (using sbt)

You will need to add the following resolver in your `project/Build.scala` file:

```scala
resolvers += "tuxburner.github.io" at "http://tuxburner.github.io/repo"
```

Add a dependency on the following artifact:

```scala
libraryDependencies += "com.github.tuxBurner" %% "play-akkajobs" % "1.0.0"
```

Activate the module in the `conf/application.conf` like this:

```
play.modules.enabled  += "com.github.tuxBurner.jobs.JobModule"
```

## Logging 

To enable logging or change the level you can configure the logger in your *conf/logback.xml*

```xml
<logger name="modules.jobs.JobModule" level="Debug" />
```


## Usage


You can implement your own job class with extending it with: *com.github.tuxBurner.jobs.AbstractAkkaJob*
 
or you can use one of the implementations which are already there.

The cron expressions you can use are documented under: http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html

### Annotated Job

Just create a class like this  and you will have a job which is called every 15 seconds.

```java
package com.github.tuxBurner.jobs;

import akka.actor.ActorSystem;
import play.Logger;

/**
 * Simple job which is fired every 15 Seconds
 */
@AkkaJob(cronExpression = "0/15 * * * * ?")
public class TestAnnotatedJob extends AbstractAnnotatedJob{

  public TestJob(ActorSystem actorSystem) throws JobException {
    super(actorSystem);
  }

  @Override
  public void runInternal() {
    Logger.debug("Run run");
  }
}
```

### Configured Job

Just create a class like this.

```java
package com.github.tuxBurner.jobs;

import akka.actor.ActorSystem;
import play.Logger;

/**
 * Simple job which is configured via configuration
 */
public class TestConfiguredJob extends AbstractConfigurationJob{

  public TestConfiguredJob(ActorSystem actorSystem) throws JobException {
    super(actorSystem);
  }

  @Override
  public void runInternal() {
    Logger.debug("Run run");
  }
}
```

Now you have to add a jobs section in *application.conf* file

```
jobs {
  com.github.tuxBurner.jobs.TestConfiguredJob {
    enabled = true
    cronExpression = "0 0 0/1 * * ?"
    key1 = "test1"
    key2 = "test1"
  }
}
```

To access *key1* for example in your job runInternal you can do the following:
 
```java
  final String key1 = getConfiguration().getString("key1");
```
