package de.rexlmanu.fairychat.plugin.core.custommessages.redis;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.redis.channel.RedisChannelSubscriber;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Server;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CustomMessageSubscriber implements RedisChannelSubscriber<CustomMessageDto> {
  private final Server server;
  private final Provider<PluginConfiguration> configurationProvider;
  private final BukkitAudiences bukkitAudiences;

  @Override
  public Class<CustomMessageDto> getDataType() {
    return CustomMessageDto.class;
  }

  @Override
  public void handle(CustomMessageDto data) {
    if (Constants.SERVER_IDENTITY_ORIGIN.equals(data.origin())) return;

    this.server.getOnlinePlayers().forEach(player -> this.bukkitAudiences.player(player).sendMessage(data.component()));

    if (this.configurationProvider.get().displayChatInConsole())
      this.bukkitAudiences.sender(this.server.getConsoleSender()).sendMessage(data.component());
  }
}
