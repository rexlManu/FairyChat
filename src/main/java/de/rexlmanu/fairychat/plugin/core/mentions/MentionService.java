package de.rexlmanu.fairychat.plugin.core.mentions;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.ignore.UserIgnoreService;
import de.rexlmanu.fairychat.plugin.integration.IntegrationRegistry;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MentionService {
  private final Provider<PluginConfiguration> configurationProvider;
  private final MiniMessage miniMessage;
  private final IntegrationRegistry registry;
  private final UserIgnoreService userIgnoreService;

  public Component checkMentions(Player viewer, Component message) {

    String miniTextMessage = this.miniMessage.serialize(message);
    Component mentionNameComponent =
        this.miniMessage.deserialize(
            this.configurationProvider.get().mention().mentionNameFormat(),
            TagResolver.resolver(
                this.registry.getPlaceholderSupports().stream()
                    .map(placeholderSupport -> placeholderSupport.resolvePlayerPlaceholder(viewer))
                    .toList()));

    String mentionName =
        this.miniMessage.stripTags(this.miniMessage.serialize(mentionNameComponent));
    if (!miniTextMessage.contains(Constants.HIGHLIGHT_CHAR + mentionName)) {
      return message;
    }

    viewer.playSound(
        Sound.sound(
            Key.key(this.configurationProvider.get().mention().soundName()),
            Sound.Source.MASTER,
            this.configurationProvider.get().mention().soundVolume(),
            this.configurationProvider.get().mention().soundPitch()));

    return this.miniMessage.deserialize(
        miniTextMessage.replaceFirst(
            Constants.HIGHLIGHT_CHAR + mentionName,
            this.configurationProvider.get().mention().format()),
        Placeholder.component(
            "highlight_name",
            this.miniMessage.deserialize(
                this.configurationProvider.get().mention().highlightFormat(),
                TagResolver.resolver(
                    this.registry.getPlaceholderSupports().stream()
                        .map(
                            placeholderSupport ->
                                placeholderSupport.resolvePlayerPlaceholder(viewer))
                        .toList()))));
  }
}
