package de.rexlmanu.fairychat.plugin.integration.types;

import de.rexlmanu.fairychat.plugin.integration.Integration;
import de.rexlmanu.fairychat.plugin.integration.chat.PlaceholderSupport;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

public class BuiltInPlaceholdersIntegration implements Integration, PlaceholderSupport {
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
        Placeholder.unparsed("sender_name", player.getName()),
        Placeholder.component("sender_displayname", player.displayName())
    );
  }

  @Override
  public TagResolver resolvePlaceholder() {
    return TagResolver.empty();
  }
}
