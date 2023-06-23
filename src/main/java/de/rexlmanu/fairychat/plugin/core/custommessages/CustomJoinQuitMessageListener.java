package de.rexlmanu.fairychat.plugin.core.custommessages;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.Messages;
import io.github.miniplaceholders.api.MiniPlaceholders;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CustomJoinQuitMessageListener implements Listener {
  private final Messages messages;
  private final MiniMessage miniMessage;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void handle(PlayerJoinEvent event) {
    if (this.messages.joinMessage() == null || this.messages.joinMessage().isEmpty()) {
      event.joinMessage(null);
      return;
    }
    event.joinMessage(
        this.miniMessage.deserialize(
            this.messages.joinMessage(),
            MiniPlaceholders.getAudienceGlobalPlaceholders(event.getPlayer())));
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void handle(PlayerQuitEvent event) {
    if (this.messages.quitMessage() == null || this.messages.quitMessage().isEmpty()) {
      event.quitMessage(null);
      return;
    }
    event.quitMessage(
        this.miniMessage.deserialize(
            this.messages.quitMessage(),
            MiniPlaceholders.getAudienceGlobalPlaceholders(event.getPlayer())));
  }
}
