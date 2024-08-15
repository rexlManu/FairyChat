package de.rexlmanu.fairychat.plugin.utility;

import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;

public class TagResolverUtil {
  public static Function<Component, Tag> resolver(PluginConfiguration configuration) {
    return configuration.placeholderSelfClosingTags() ? Tag::selfClosingInserting : Tag::inserting;
  }
}
