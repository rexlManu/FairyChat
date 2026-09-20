package de.rexlmanu.fairychat.plugin.core.broadcast;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.redis.channel.RedisChannelSubscriber;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BroadcastChannelSubscriber implements RedisChannelSubscriber<BroadcastMessageData> {
  private final Server server;

  @Override
  public Class<BroadcastMessageData> getDataType() {
    return BroadcastMessageData.class;
  }

  @Override
  public void handle(BroadcastMessageData data) {
    this.server.getOnlinePlayers().forEach(player -> player.sendMessage(data.message()));
  }
}
