package de.rexlmanu.fairychat.plugin.paper;

public interface PluginScheduler {
  void runAsync(Runnable runnable);

  void runDelayedAsync(Runnable runnable, long delayMillis);
}
