package de.rexlmanu.fairychat.plugin.integration.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

public interface ChatPlaceholder {
  TagResolver resolve(Player player, Component message);
}
