package de.rexlmanu.fairychat.plugin.utility;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PluginScheduler {
  private static final String FOLIA_CLASS = "io.papermc.paper.threadedregions.RegionizedServer";
  private static boolean FOLIA = false;

  static {
    try {
      Class.forName(FOLIA_CLASS);
      FOLIA = true;
    } catch (ClassNotFoundException e) {
      FOLIA = false;
    }
  }

  private final Server server;
  private final JavaPlugin plugin;

  public void runAsync(Runnable runnable) {
    if (FOLIA) {
      this.server.getAsyncScheduler().runNow(this.plugin, scheduledTask -> runnable.run());
    } else {
      this.server.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }
  }

  public void runDelayedAsync(Runnable runnable, long delayMillis) {
    if (FOLIA) {
      this.server
          .getAsyncScheduler()
          .runDelayed(
              this.plugin, scheduledTask -> runnable.run(), delayMillis, TimeUnit.MILLISECONDS);
    } else {
      this.server
          .getScheduler()
          .runTaskLaterAsynchronously(
              this.plugin, runnable, delayMillis * Ticks.SINGLE_TICK_DURATION_MS);
    }
  }
}
