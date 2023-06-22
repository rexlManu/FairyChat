package de.rexlmanu.fairychat.plugin.core.metrics;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class MetricsModule extends AbstractModule {
  @Provides
  @Singleton
  @Inject
  public Metrics provideMetrics(JavaPlugin plugin) {
    return new Metrics(plugin, Constants.BSTATS_ID);
  }
}
