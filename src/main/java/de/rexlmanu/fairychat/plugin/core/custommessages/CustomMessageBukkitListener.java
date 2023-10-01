package de.rexlmanu.fairychat.plugin.core.custommessages;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.custommessages.redis.CustomMessageDto;
import de.rexlmanu.fairychat.plugin.integration.IntegrationRegistry;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CustomMessageBukkitListener implements Listener {
  private final MiniMessage miniMessage;
  private final IntegrationRegistry registry;
  private final Provider<PluginConfiguration> configurationProvider;
  private final RedisConnector connector;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void handlePlayerJoin(PlayerJoinEvent event) {
    if (event.getPlayer().hasPermission("fairychat.messages.join.ignore")) {
      event.joinMessage(null);
      return;
    }
    this.handleEventMessage(
        event.getPlayer(),
        event::joinMessage,
        event::joinMessage,
        TagResolver::empty,
        this.configurationProvider.get().messages().joinMessage(),
        this.configurationProvider.get().customMessages().broadcastJoinMessages());
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void handlePlayerQuit(PlayerQuitEvent event) {
    if (event.getPlayer().hasPermission("fairychat.messages.join.ignore")) {
      event.quitMessage(null);
      return;
    }
    this.handleEventMessage(
        event.getPlayer(),
        event::quitMessage,
        event::quitMessage,
        TagResolver::empty,
        this.configurationProvider.get().messages().quitMessage(),
        this.configurationProvider.get().customMessages().broadcastQuitMessages());
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void handlePlayerDeath(PlayerDeathEvent event) {
    if (event.deathMessage() instanceof TranslatableComponent translatableComponent) {
      event.deathMessage(this.replaceFirstArgument(event.getPlayer(), translatableComponent));
    }
    this.handleEventMessage(
        event.getPlayer(),
        event::deathMessage,
        event::deathMessage,
        () -> Placeholder.component("death_message", event.deathMessage()),
        this.configurationProvider.get().messages().deathMessage(),
        this.configurationProvider.get().customMessages().broadcastDeathMessages());
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void handlePlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
    if (event.message() instanceof TranslatableComponent translatableComponent) {
      event.message(this.replaceFirstArgument(event.getPlayer(), translatableComponent));
    }
    this.handleEventMessage(
        event.getPlayer(),
        event::message,
        event::message,
        () -> Placeholder.component("advancement_message", event.message()),
        this.configurationProvider.get().messages().advancementDoneMessage(),
        this.configurationProvider.get().customMessages().broadcastAdvancementMessages());
  }

  private void handleEventMessage(
      Player player,
      Supplier<Component> messageSupplier,
      Consumer<Component> messageConsumer,
      Supplier<TagResolver> placeholders,
      String configValue,
      boolean broadcast) {
    if (messageSupplier.get() == null) return;

    if (configValue == null || configValue.isEmpty()) {
      messageConsumer.accept(null);
      return;
    }

    messageConsumer.accept(
        this.miniMessage.deserialize(
            configValue,
            placeholders.get(),
            TagResolver.resolver(
                this.registry.getPlaceholderSupports().stream()
                    .map(placeholderSupport -> placeholderSupport.resolvePlayerPlaceholder(player))
                    .toList())));

    if (!broadcast || !this.connector.available()) return;

    this.connector.publish(
        Constants.CUSTOM_MESSAGE_CHANNEL,
        new CustomMessageDto(Constants.SERVER_IDENTITY_ORIGIN, messageSupplier.get()));
  }

  private Component replaceFirstArgument(Player player, TranslatableComponent component) {
    ArrayList<Component> args = new ArrayList<>(component.args());

    args.set(
        0,
        this.miniMessage.deserialize(
            this.configurationProvider.get().customMessages().customName(),
            TagResolver.resolver(
                this.registry.getPlaceholderSupports().stream()
                    .map(placeholderSupport -> placeholderSupport.resolvePlayerPlaceholder(player))
                    .toList())));

    return component.args(args);
  }
}
