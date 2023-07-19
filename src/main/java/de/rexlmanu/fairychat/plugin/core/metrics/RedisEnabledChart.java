package de.rexlmanu.fairychat.plugin.core.metrics;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import lombok.RequiredArgsConstructor;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisEnabledChart implements MetricsChart {
  private final Provider<PluginConfiguration> configurationProvider;
  private final Metrics metrics;

  @Override
  public void register() {
    this.metrics.addCustomChart(
        new SimplePie(
            "redis",
            () -> String.valueOf(configurationProvider.get().redisCredentials().enabled())));
  }
}
