package de.rexlmanu.fairychat.plugin.core.chatclear;

import static de.rexlmanu.fairychat.plugin.Constants.CLEAR_CHAT_MAX_LINES;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.entity.Player;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultChatClearService implements ChatClearService {
  private final Server server;
  private final Provider<PluginConfiguration> configurationProvider;
  private final MiniMessage miniMessage;
  private final BukkitAudiences bukkitAudiences;

  @Override
  public void clearChat(Player player) {
    for (int i = 0; i < CLEAR_CHAT_MAX_LINES; i++) {
      this.bukkitAudiences.player(player).sendMessage(Component.empty());
    }
    this.bukkitAudiences
        .player(player)
        .sendMessage(
            this.miniMessage.deserialize(
                this.configurationProvider.get().messages().chatCleared()));
  }

  @Override
  public void clearChatAll() {
    this.server.getOnlinePlayers().forEach(this::clearChat);
  }
}
