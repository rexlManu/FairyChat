package de.rexlmanu.fairychat.plugin.core.mentions;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.MentionConfig;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MentionService {
  private final MentionConfig config;
  private final MiniMessage miniMessage;

  public Component checkMentions(Player viewer, Component message) {

    String miniTextMessage = this.miniMessage.serialize(message);
    if (!miniTextMessage.contains(Constants.HIGHLIGHT_CHAR + viewer.getName())) {
      return message;
    }

    viewer.playSound(
        Sound.sound(
            Key.key(this.config.soundName()),
            Sound.Source.MASTER,
            this.config.soundVolume(),
            this.config.soundPitch()));

    return this.miniMessage.deserialize(
        miniTextMessage.replaceFirst(
            Constants.HIGHLIGHT_CHAR + viewer.getName(),
            this.config.format().replace("<highlight_name>", viewer.getName())));
  }
}
