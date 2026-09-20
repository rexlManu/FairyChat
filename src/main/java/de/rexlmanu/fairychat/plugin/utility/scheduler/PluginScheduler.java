package de.rexlmanu.fairychat.plugin.utility.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

/** Uses Paper's async scheduler, which is also available on Folia. */
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PluginScheduler {
  private final JavaPlugin plugin;

  public void runAsync(Runnable runnable) {
    this.plugin.getServer().getAsyncScheduler().runNow(this.plugin, task -> runnable.run());
  }

  public void runDelayedAsync(Runnable runnable, long delayMillis) {
    this.plugin
        .getServer()
        .getAsyncScheduler()
        .runDelayed(this.plugin, task -> runnable.run(), delayMillis, TimeUnit.MILLISECONDS);
  }
}
