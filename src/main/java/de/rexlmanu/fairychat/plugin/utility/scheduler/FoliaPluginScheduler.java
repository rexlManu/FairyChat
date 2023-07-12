package de.rexlmanu.fairychat.plugin.utility.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FoliaPluginScheduler implements PluginScheduler {
  private final Server server;
  private final JavaPlugin plugin;

  public void runAsync(Runnable runnable) {
    this.server.getAsyncScheduler().runNow(this.plugin, scheduledTask -> runnable.run());
  }

  public void runDelayedAsync(Runnable runnable, long delayMillis) {
    this.server
        .getAsyncScheduler()
        .runDelayed(
            this.plugin, scheduledTask -> runnable.run(), delayMillis, TimeUnit.MILLISECONDS);
  }
}
