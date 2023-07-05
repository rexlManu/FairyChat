package de.rexlmanu.fairychat.plugin.integration.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

public interface PlaceholderSupport {
  TagResolver resolvePlayerPlaceholderWithChatMessage(Player player, Component message);

  TagResolver resolvePlayerPlaceholder(Player player);

  TagResolver resolvePlaceholder();
}
