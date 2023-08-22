package de.rexlmanu.fairychat.plugin.integration.types;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.integration.Integration;
import de.rexlmanu.fairychat.plugin.integration.chat.PlaceholderSupport;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SpoilerChatIntegration implements Integration, PlaceholderSupport {
  private static final char SPOILER_CHAR = '█';
  private static final double SPOILER_PLACE = 1.5;
  // pattern that checks if string contains "||message||" and extracts message
  private static final Pattern SPOILER_PATTERN = Pattern.compile(".*\\|\\|(.*)\\|\\|.*");
  private static final String SPOILER_TAG_REPLACEMENT = "<spoiler:'%s'>";
  private final Provider<PluginConfiguration> configurationProvider;

  @Override
  public boolean available() {
    return true;
  }

  @Override
  public TagResolver resolveChatMessagePlaceholder(Player player, String message) {
    return TagResolver.resolver(
        "spoiler",
        (argumentQueue, context) -> {
          if (argumentQueue.peek() == null) {
            return Tag.selfClosingInserting(Component.empty());
          }

          String hiddenMessage = argumentQueue.pop().value();

          return Tag.selfClosingInserting(
              Component.text(
                      String.valueOf(SPOILER_CHAR)
                          .repeat((int) (hiddenMessage.length() / SPOILER_PLACE)))
                  .color(NamedTextColor.DARK_GRAY)
                  .hoverEvent(
                      HoverEvent.showText(
                          Component.text(hiddenMessage).color(NamedTextColor.GRAY))));
        });
  }

  @Override
  public String resolveChatMessageModifier(Player player, String message) {
    if (!this.configurationProvider.get().spoilerTagsEnabled()) {
      return message;
    }

    var matcher = SPOILER_PATTERN.matcher(message);

    // while message contains spoiler tags, replace them with spoiler tag
    while (matcher.find()) {
      String spoilerContent = matcher.group(1);
      var spoilerTag = String.format(SPOILER_TAG_REPLACEMENT, spoilerContent);
      message = message.replace("||" + spoilerContent + "||", spoilerTag);
      matcher = SPOILER_PATTERN.matcher(message); // Refresh matcher with updated message
    }

    return message;
  }
}
