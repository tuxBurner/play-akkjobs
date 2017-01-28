package com.github.tuxBurner.jobs;

/**
 * This is the state of the job the job had on its run
 * 
 * @author tuxburner
 * 
 */
public enum EJobRunState {
  DISABLED,
  RUNNING,
  SCHEDULED,
  STOPPED,
  KILLED,
  ERROR
}
