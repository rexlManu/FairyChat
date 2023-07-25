package de.rexlmanu.fairychat.plugin.core.playerchat.cooldown;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.utility.ExpiringMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultPlayerChatCooldownService implements PlayerChatCooldownService {
  private final Provider<PluginConfiguration> configurationProvider;
  private final ExpiringMap<UUID, Integer> map = new ExpiringMap<>();

  @Override
  public void trigger(UUID playerId) {
    this.map.put(
        playerId,
        this.map.getOptional(playerId).orElse(0) + 1,
        this.configurationProvider.get().chattingCooldown(),
        TimeUnit.SECONDS);
  }

  @Override
  public boolean isCooldowned(UUID playerId) {
    return this.map
        .getOptional(playerId)
        .filter(count -> count >= this.configurationProvider.get().chattingThreshold())
        .isPresent();
  }

  @Override
  public long getTime(UUID playerId) {
    return TimeUnit.NANOSECONDS.toMillis(this.map.getExpirationTime(playerId) - System.nanoTime());
  }

  @Override
  public boolean enabled() {
    return this.configurationProvider.get().chattingCooldown() > 0;
  }
}
