package de.rexlmanu.fairychat.plugin.core.custommessages;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.Messages;
import de.rexlmanu.fairychat.plugin.integration.IntegrationRegistry;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
  private final IntegrationRegistry registry;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void handle(PlayerJoinEvent event) {
    if (this.messages.joinMessage() == null || this.messages.joinMessage().isEmpty()) {
      event.joinMessage(null);
      return;
    }

    event.joinMessage(
        this.miniMessage.deserialize(
            this.messages.joinMessage(),
            TagResolver.resolver(
                this.registry.getPlaceholderSupports().stream()
                    .map(
                        placeholderSupport ->
                            placeholderSupport.resolvePlayerPlaceholder(event.getPlayer()))
                    .toList())));
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
            TagResolver.resolver(
                this.registry.getPlaceholderSupports().stream()
                    .map(
                        placeholderSupport ->
                            placeholderSupport.resolvePlayerPlaceholder(event.getPlayer()))
                    .toList())));
  }
}
