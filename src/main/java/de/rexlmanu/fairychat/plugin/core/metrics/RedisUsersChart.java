package de.rexlmanu.fairychat.plugin.core.metrics;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.core.user.redis.RedisUserService;
import lombok.RequiredArgsConstructor;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisUsersChart implements MetricsChart {
  private final RedisUserService userService;
  private final Metrics metrics;

  @Override
  public void register() {
    this.metrics.addCustomChart(
        new SingleLineChart("redis_online_users", () -> Math.toIntExact(userService.onlineUsersCount())));
  }
}
