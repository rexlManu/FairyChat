package de.rexlmanu.fairychat.plugin.integration.types;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.integration.Integration;
import de.rexlmanu.fairychat.plugin.integration.chat.PlaceholderSupport;
import de.rexlmanu.fairychat.plugin.utility.LegacySupport;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PlaceholderAPIIntegration implements PlaceholderSupport, Integration {
  private final PluginManager pluginManager;

  @Override
  public boolean available() {
    return this.pluginManager.isPluginEnabled(Constants.PLACEHOLDER_API_NAME);
  }

  @Override
  public TagResolver resolvePlayerPlaceholderWithChatMessage(Player player, Component message) {
    return LegacySupport.papiTag(player);
  }

  @Override
  public TagResolver resolvePlayerPlaceholder(Player player) {
    return LegacySupport.papiTag(player);
  }

  @Override
  public TagResolver resolvePlaceholder() {
    return LegacySupport.papiTag(null);
  }
}
