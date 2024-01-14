package de.rexlmanu.fairychat.plugin.core.playerchat.cooldown;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.strategy.CooldownStrategy;
import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.strategy.CooldownStrategyType;
import de.rexlmanu.fairychat.plugin.utility.ExpiringMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultPlayerChatCooldownService implements PlayerChatCooldownService {
  private final Provider<PluginConfiguration> configurationProvider;
  private final ExpiringMap<UUID, CooldownStrategy.CooldownState> map = new ExpiringMap<>();
  private final CooldownStrategy<CooldownStrategy.CooldownState> cooldownStrategy;

  @Override
  public void trigger(UUID playerId, Component message) {
    CooldownStrategy.CooldownState state =
        this.cooldownStrategy.nextMessage(playerId, message, this.map.get(playerId));
    this.map.put(playerId, state, state.getCurrentCooldown(), TimeUnit.MILLISECONDS);
  }

  @Override
  public boolean isCooldowned(UUID playerId) {
    return this.cooldownStrategy.throttled(playerId, this.map.get(playerId));
  }

  @Override
  public long getTime(UUID playerId) {
    return TimeUnit.NANOSECONDS.toMillis(this.map.getExpirationTime(playerId) - System.nanoTime());
  }

  @Override
  public boolean enabled() {
    return this.configurationProvider.get().cooldownStrategy() != CooldownStrategyType.DISABLED;
  }
}
