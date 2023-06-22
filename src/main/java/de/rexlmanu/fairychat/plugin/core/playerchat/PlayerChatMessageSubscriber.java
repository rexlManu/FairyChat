package de.rexlmanu.fairychat.plugin.core.playerchat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.redis.channel.RedisChannelSubscriber;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PlayerChatMessageSubscriber implements RedisChannelSubscriber<PlayerChatMessageData> {
  private final Server server;

  @Override
  public Class<PlayerChatMessageData> getDataType() {
    return PlayerChatMessageData.class;
  }

  @Override
  public void handle(PlayerChatMessageData data) {
    if (data.origin().equals(Constants.SERVER_IDENTITY_ORIGIN)) return;
    this.server.getOnlinePlayers().forEach(player -> player.sendMessage(data.message()));
  }
}
