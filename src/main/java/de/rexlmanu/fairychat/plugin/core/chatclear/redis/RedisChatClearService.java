package de.rexlmanu.fairychat.plugin.core.chatclear.redis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.core.chatclear.ChatClearService;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisChatClearService implements ChatClearService {
  private final RedisConnector connector;

  @Override
  public void clearChat(Player player) {
    this.connector.publish(
        Constants.CLEAR_CHAT_CHANNEL,
        new ChatClearData(Constants.SERVER_IDENTITY_ORIGIN, player.getUniqueId()));
  }

  @Override
  public void clearChatAll() {
    this.connector.publish(
        Constants.CLEAR_CHAT_CHANNEL, new ChatClearData(Constants.SERVER_IDENTITY_ORIGIN, null));
  }
}
