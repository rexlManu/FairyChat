package de.rexlmanu.fairychat.plugin.utility;

import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyFormat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LegacySupport {

  private static final char[] LEGACY_COLOR_CODES = {
    // Colors
    '0',
    '1',
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9',
    'a',
    'b',
    'c',
    'd',
    'e',
    'f',
    'A',
    'B',
    'C',
    'D',
    'E',
    'F',
    // Decorations
    'k',
    'l',
    'm',
    'n',
    'o',
    'r',
    'K',
    'L',
    'M',
    'N',
    'O',
    'R'
  };
  // https://github.com/Hexaoxide/Carbon/blob/2.1/api/src/main/java/net/draycia/carbon/api/util/ColorUtils.java#L40
  private static final Pattern LEGACY_RGB_PATTERN =
      Pattern.compile(
          "[§&]x[§&]([0-9a-fA-F])[§&]([0-9a-fA-F])[§&]([0-9a-fA-F])[§&]([0-9a-fA-F])[§&]([0-9a-fA-F])[§&]([0-9a-fA-F])");
  // https://github.com/Hexaoxide/Carbon/blob/2.1/api/src/main/java/net/draycia/carbon/api/util/ColorUtils.java#L42
  private static final Pattern LEGACY_PLUGIN_RGB_PATTERN =
      Pattern.compile(
          "[§&]#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])");
  private static final String RGB_REPLACEMENT = "<#$1$2$3$4$5$6>";

  // https://github.com/Hexaoxide/Carbon/blob/2.1/api/src/main/java/net/draycia/carbon/api/util/ColorUtils.java#L42
  public static String replaceLegacyWithTags(String input) {
    String output = LEGACY_RGB_PATTERN.matcher(input).replaceAll(RGB_REPLACEMENT);

    output = LEGACY_PLUGIN_RGB_PATTERN.matcher(output).replaceAll(RGB_REPLACEMENT);

    for (char legacyCode : LEGACY_COLOR_CODES) {
      LegacyFormat format = LegacyComponentSerializer.parseChar(legacyCode);
      if (format == null) continue;

      if (format.color() != null) {
        output = output.replaceAll("[&§]" + legacyCode, "<" + format.color().asHexString() + ">");
      }

      if (format.decoration() != null) {
        output = output.replaceAll("[&§]" + legacyCode, "<" + format.decoration().name() + ">");
      }
    }

    return output;
  }

  public static @NotNull TagResolver papiTag(final @NotNull Player player) {
    return TagResolver.resolver(
        "papi",
        (argumentQueue, context) -> {
          // Get the string placeholder that they want to use.
          final String papiPlaceholder =
              argumentQueue.popOr("papi tag requires an argument").value();

          // Then get PAPI to parse the placeholder for the given player.
          final String parsedPlaceholder =
              PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');

          // We need to turn this ugly legacy string into a nice component.
          final Component componentPlaceholder =
              MiniMessage.miniMessage().deserialize(replaceLegacyWithTags(parsedPlaceholder));

          // Finally, return the tag instance to insert the placeholder!
          return Tag.selfClosingInserting(componentPlaceholder);
        });
  }
}
