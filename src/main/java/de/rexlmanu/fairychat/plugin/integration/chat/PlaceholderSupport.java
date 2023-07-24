package de.rexlmanu.fairychat.plugin.integration.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

public interface PlaceholderSupport {
  default TagResolver resolvePlayerPlaceholderWithChatMessage(Player player, Component message) {
    return this.resolvePlayerPlaceholder(player);
  }

  default TagResolver resolvePlayerPlaceholder(Player player) {
    return TagResolver.empty();
  }

  default TagResolver resolvePlaceholder() {
    return TagResolver.empty();
  }
}
