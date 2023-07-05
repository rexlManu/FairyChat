package de.rexlmanu.fairychat.plugin.integration.types;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.integration.Integration;
import de.rexlmanu.fairychat.plugin.integration.chat.ChatPlaceholder;
import io.github.miniplaceholders.api.MiniPlaceholders;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MiniPlaceholdersIntegration implements Integration, ChatPlaceholder {
  private final PluginManager pluginManager;

  @Override
  public boolean available() {
    return this.pluginManager.isPluginEnabled("MiniPlaceholders");
  }

  @Override
  public void enable() {}

  @Override
  public TagResolver resolve(Player player, Component message) {
    return MiniPlaceholders.getAudienceGlobalPlaceholders(player);
  }
}
