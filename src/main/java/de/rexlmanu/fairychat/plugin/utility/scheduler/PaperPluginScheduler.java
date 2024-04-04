package de.rexlmanu.fairychat.plugin.utility.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.paper.PluginScheduler;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PaperPluginScheduler implements PluginScheduler {
  private final Server server;
  private final JavaPlugin plugin;

  public void runAsync(Runnable runnable) {
    this.server.getScheduler().runTaskAsynchronously(this.plugin, runnable);
  }

  public void runDelayedAsync(Runnable runnable, long delayMillis) {
    this.server
        .getScheduler()
        .runTaskLaterAsynchronously(
            this.plugin, runnable, delayMillis * Ticks.SINGLE_TICK_DURATION_MS);
  }
}
