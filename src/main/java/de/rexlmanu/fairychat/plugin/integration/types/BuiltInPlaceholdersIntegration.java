package de.rexlmanu.fairychat.plugin.integration.types;

import com.google.inject.Inject;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.integration.Integration;
import de.rexlmanu.fairychat.plugin.integration.chat.PlaceholderSupport;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BuiltInPlaceholdersIntegration implements Integration, PlaceholderSupport {
  private final PluginConfiguration configuration;

  @Override
  public boolean available() {
    return true;
  }

  @Override
  public TagResolver resolvePlayerPlaceholderWithChatMessage(Player player, Component message) {
    return this.resolvePlayerPlaceholder(player);
  }

  @Override
  public TagResolver resolvePlayerPlaceholder(Player player) {
    return TagResolver.resolver(
        Placeholder.parsed("sender_name", player.getName()),
        Placeholder.component("sender_displayname", player.displayName()),
        this.resolvePlaceholder());
  }

  @Override
  public TagResolver resolvePlaceholder() {
    return TagResolver.resolver(Placeholder.parsed("server_name", configuration.serverName()));
  }
}
