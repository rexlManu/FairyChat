package de.rexlmanu.fairychat.plugin.integration.types;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.integration.Integration;
import de.rexlmanu.fairychat.plugin.integration.chat.PlaceholderSupport;
import io.github.miniplaceholders.api.MiniPlaceholders;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MiniPlaceholdersIntegration implements Integration, PlaceholderSupport {
  private final PluginManager pluginManager;

  @Override
  public boolean available() {
    return this.pluginManager.getPlugin("MiniPlaceholders") != null;
  }

  @Override
  public TagResolver resolvePlayerPlaceholderWithChatMessage(Player player, Component message) {
    return MiniPlaceholders.getAudienceGlobalPlaceholders(player);
  }

  @Override
  public TagResolver resolvePlayerPlaceholder(Player player) {
    return MiniPlaceholders.getAudienceGlobalPlaceholders(player);
  }

  @Override
  public TagResolver resolvePlaceholder() {
    return MiniPlaceholders.getGlobalPlaceholders();
  }
}
