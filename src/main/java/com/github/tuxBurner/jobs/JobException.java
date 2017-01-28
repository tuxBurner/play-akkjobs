package com.github.tuxBurner.jobs;

import java.text.ParseException;

/**
 * An {@link Exception} which is thrown in the {@link JobModule} context.
 * Created by tuxburner on 28.01.17.
 */
public class JobException extends Exception {

  
  public JobException(String message) {
    super(message);
  }

  public JobException(ParseException e) {
    super(e);
  }

  public JobException(String message, Throwable e) {
    super(message,e);
  }
}
