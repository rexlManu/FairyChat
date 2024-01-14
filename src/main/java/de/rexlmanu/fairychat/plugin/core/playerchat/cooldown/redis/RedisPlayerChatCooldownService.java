package de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.redis;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.PlayerChatCooldownService;
import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.strategy.CooldownStrategy;
import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.strategy.CooldownStrategyType;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisPlayerChatCooldownService implements PlayerChatCooldownService {
  private final RedisConnector connector;
  private final Provider<PluginConfiguration> configurationProvider;
  private final CooldownStrategy<CooldownStrategy.CooldownState> cooldownStrategy;

  @Override
  public void trigger(UUID playerId, Component message) {
    this.connector.useResource(
        jedis -> {
          String previousStateAsString =
              jedis.get(Constants.CHAT_COOLDOWN_KEY + playerId.toString());
          CooldownStrategy.CooldownState newState =
              this.cooldownStrategy.nextMessage(
                  playerId,
                  message,
                  previousStateAsString == null
                      ? null
                      : this.cooldownStrategy.deserialize(previousStateAsString));

          jedis.set(Constants.CHAT_COOLDOWN_KEY + playerId, newState.serialize());
          jedis.expire(
              Constants.CHAT_COOLDOWN_KEY + playerId, newState.getCurrentCooldown() / 1000);
        });
  }

  @Override
  public boolean isCooldowned(UUID playerId) {
    return this.connector.useQuery(
        jedis -> {
          String previousStateAsString =
              jedis.get(Constants.CHAT_COOLDOWN_KEY + playerId.toString());
          return this.cooldownStrategy.throttled(
              playerId,
              previousStateAsString == null
                  ? null
                  : this.cooldownStrategy.deserialize(previousStateAsString));
        });
  }

  @Override
  public long getTime(UUID playerId) {
    return this.connector.useQuery(
        jedis -> jedis.pttl(Constants.CHAT_COOLDOWN_KEY + playerId.toString()));
  }

  @Override
  public boolean enabled() {
    return this.configurationProvider.get().cooldownStrategy() != CooldownStrategyType.DISABLED;
  }
}
