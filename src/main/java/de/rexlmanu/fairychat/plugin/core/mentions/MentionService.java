package de.rexlmanu.fairychat.plugin.core.mentions;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MentionService {
  private final Provider<PluginConfiguration> configurationProvider;
  private final MiniMessage miniMessage;
  
    public Component checkMentions(Player viewer, Component message) {

    String miniTextMessage = this.miniMessage.serialize(message);
    if (!miniTextMessage.contains(Constants.HIGHLIGHT_CHAR + viewer.getName())) {
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
            Constants.HIGHLIGHT_CHAR + viewer.getName(),
            this.configurationProvider.get().mention().format().replace("<highlight_name>", viewer.getName())));
  }
}
