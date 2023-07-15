package de.rexlmanu.fairychat.plugin.core.chatclear;

import static de.rexlmanu.fairychat.plugin.Constants.CLEAR_CHAT_MAX_LINES;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.Messages;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.entity.Player;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultChatClearService implements ChatClearService {
  private final Server server;
  private final Messages messages;
  private final MiniMessage miniMessage;

  @Override
  public void clearChat(Player player) {
    for (int i = 0; i < CLEAR_CHAT_MAX_LINES; i++) {
      player.sendMessage(Component.empty());
    }
    player.sendMessage(this.miniMessage.deserialize(this.messages.chatCleared()));
  }

  @Override
  public void clearChatAll() {
    this.server.getOnlinePlayers().forEach(this::clearChat);
  }
}
