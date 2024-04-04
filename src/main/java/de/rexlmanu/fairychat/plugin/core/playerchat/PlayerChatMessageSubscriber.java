package de.rexlmanu.fairychat.plugin.core.playerchat;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.ignore.UserIgnoreService;
import de.rexlmanu.fairychat.plugin.core.mentions.MentionService;
import de.rexlmanu.fairychat.plugin.redis.channel.RedisChannelSubscriber;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Server;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PlayerChatMessageSubscriber implements RedisChannelSubscriber<PlayerChatMessageData> {
  private final Server server;
  private final Provider<PluginConfiguration> configurationProvider;
  private final UserIgnoreService userIgnoreService;
  private final MentionService mentionService;
  private final BukkitAudiences bukkitAudiences;

  @Override
  public Class<PlayerChatMessageData> getDataType() {
    return PlayerChatMessageData.class;
  }

  @Override
  public void handle(PlayerChatMessageData data) {
    if (data.origin().equals(Constants.SERVER_IDENTITY_ORIGIN)) return;
    this.server.getOnlinePlayers().stream()
        .filter(
            recipient ->
                !this.userIgnoreService.isIgnored(recipient.getUniqueId(), data.senderId()))
        .forEach(
            player ->
                this.bukkitAudiences
                    .player(player)
                    .sendMessage(this.mentionService.checkMentions(player, data.message())));

    if (!this.configurationProvider.get().displayChatInConsole()) return;

    this.bukkitAudiences.sender(this.server.getConsoleSender()).sendMessage(data.message());
  }
}
