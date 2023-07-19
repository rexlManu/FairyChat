package de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.redis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.FairyChatConfiguration;
import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.PlayerChatCooldownService;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisPlayerChatCooldownService implements PlayerChatCooldownService {
  private final RedisConnector connector;
  private final FairyChatConfiguration configuration;

  @Override
  public void trigger(UUID playerId) {
    connector.useResource(
        jedis -> {
          jedis.incr(Constants.CHAT_COOLDOWN_KEY + playerId.toString());
          jedis.expire(
              Constants.CHAT_COOLDOWN_KEY + playerId.toString(), configuration.chattingCooldown());
        });
  }

  @Override
  public boolean isCooldowned(UUID playerId) {
    return connector.useQuery(
        jedis -> {
          String value = jedis.get(Constants.CHAT_COOLDOWN_KEY + playerId.toString());
          return value != null && Integer.parseInt(value) >= configuration.chattingThreshold();
        });
  }

  @Override
  public long getTime(UUID playerId) {
    return connector.useQuery(
        jedis -> jedis.pttl(Constants.CHAT_COOLDOWN_KEY + playerId.toString()));
  }

  @Override
  public boolean enabled() {
    return this.configuration.chattingCooldown() > 0;
  }
}
