package de.rexlmanu.fairychat.plugin.utility.event;

import com.google.inject.Inject;
import de.rexlmanu.fairychat.plugin.paper.event.FairyAsyncChatEvent;
import de.rexlmanu.fairychat.plugin.paper.event.FairyEventHandler;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SpigotEventHandler implements FairyEventHandler {
  private final BukkitAudiences bukkitAudiences;
  private final Server server;

  @EventHandler
  public void handle(AsyncPlayerChatEvent event) {
    FairyAsyncChatEvent chatEvent =
        new FairyAsyncChatEvent(
            event.isAsynchronous(),
            event.getPlayer(),
            event.getRecipients().stream()
                .map(this.bukkitAudiences::player)
                .collect(Collectors.toSet()),
            (source, sourceDisplayName, message, viewer) -> {
              String formattedMessage =
                  event
                      .getFormat()
                      .formatted(
                          source.getDisplayName(),
                          LegacyComponentSerializer.legacySection().serialize(message));

              return LegacyComponentSerializer.legacySection().deserialize(formattedMessage);
            },
            LegacyComponentSerializer.legacySection().deserialize(event.getMessage()));

    this.server.getPluginManager().callEvent(chatEvent);

    if (chatEvent.isCancelled()) {
      event.setCancelled(chatEvent.isCancelled());
      return;
    }

    event.getRecipients().clear();

    event
        .getRecipients()
        .addAll(
            chatEvent.viewers().stream()
                .map(
                    audience -> {
                      return audience.get(Identity.UUID).map(this.server::getPlayer).orElse(null);
                    })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
    event.setMessage(LegacyComponentSerializer.legacySection().serialize(chatEvent.message()));

    event.setFormat(
        LegacyComponentSerializer.legacySection()
            .serialize(
                chatEvent
                    .renderer()
                    .render(
                        event.getPlayer(),
                        LegacyComponentSerializer.legacySection()
                            .deserialize(event.getPlayer().getDisplayName()),
                        chatEvent.message(),
                        Audience.empty()))
            .replace("%", "%%"));
  }
}
