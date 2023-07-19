package de.rexlmanu.fairychat.plugin.core.custommessages.redis;

import com.google.inject.Inject;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.redis.channel.RedisChannelSubscriber;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CustomMessageSubscriber implements RedisChannelSubscriber<CustomMessageDto> {
  private final Server server;
  private final PluginConfiguration configuration;

  @Override
  public Class<CustomMessageDto> getDataType() {
    return CustomMessageDto.class;
  }

  @Override
  public void handle(CustomMessageDto data) {
    if (Constants.SERVER_IDENTITY_ORIGIN.equals(data.origin())) return;

    this.server.getOnlinePlayers().forEach(player -> player.sendMessage(data.component()));

    if (configuration.displayChatInConsole())
      this.server.getConsoleSender().sendMessage(data.component());
  }
}
