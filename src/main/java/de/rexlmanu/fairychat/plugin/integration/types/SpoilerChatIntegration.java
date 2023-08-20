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
  private static char SPOILER_CHAR = '█';
  private static double SPOILER_PLACE = 1.5;
  // pattern that checks if string contains "||message||" and extracts message
  private static Pattern SPOILER_PATTERN = Pattern.compile(".*\\|\\|(.*)\\|\\|.*");
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
    // if message doesn't contain spoiler tags, return original message
    if (!matcher.matches()) {
      return message;
    }

    // if message contains spoiler tags, replace them with spoiler tag
    var spoilerTag = String.format(SPOILER_TAG_REPLACEMENT, matcher.group(1));
    return message.replace("||" + matcher.group(1) + "||", spoilerTag);
  }
}
