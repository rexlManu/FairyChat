package de.rexlmanu.fairychat.plugin.utility.scheduler;

public interface PluginScheduler {
  void runAsync(Runnable runnable);

  void runDelayedAsync(Runnable runnable, long delayMillis);
}
