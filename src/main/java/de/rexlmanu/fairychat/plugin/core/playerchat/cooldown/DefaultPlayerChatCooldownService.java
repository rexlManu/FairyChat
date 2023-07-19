package de.rexlmanu.fairychat.plugin.core.playerchat.cooldown;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.utility.ExpiringMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultPlayerChatCooldownService implements PlayerChatCooldownService {
  private final PluginConfiguration configuration;
  private final ExpiringMap<UUID, Integer> map = new ExpiringMap<>();

  @Override
  public void trigger(UUID playerId) {
    this.map.put(
        playerId,
        this.map.getOptional(playerId).orElse(0) + 1,
        this.configuration.chattingCooldown(),
        TimeUnit.SECONDS);
  }

  @Override
  public boolean isCooldowned(UUID playerId) {
    return this.map
        .getOptional(playerId)
        .filter(count -> count >= this.configuration.chattingThreshold())
        .isPresent();
  }

  @Override
  public long getTime(UUID playerId) {
    return this.map.getExpirationTime(playerId);
  }

  @Override
  public boolean enabled() {
    return this.configuration.chattingCooldown() > 0;
  }
}
