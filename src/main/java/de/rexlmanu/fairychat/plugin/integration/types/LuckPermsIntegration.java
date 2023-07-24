package de.rexlmanu.fairychat.plugin.integration.types;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.integration.Integration;
import de.rexlmanu.fairychat.plugin.integration.chat.PlaceholderSupport;
import de.rexlmanu.fairychat.plugin.utility.LegacySupport;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class LuckPermsIntegration implements Integration, PlaceholderSupport {
  private final PluginManager pluginManager;
  private final ServicesManager servicesManager;
  private LuckPerms luckPerms;

  @Override
  public boolean available() {
    return this.pluginManager.getPlugin(Constants.LUCKPERMS_NAME) != null;
  }

  @Override
  public void enable() {
    this.luckPerms = servicesManager.load(LuckPerms.class);
  }

  @Override
  public TagResolver resolvePlayerPlaceholder(Player player) {
    CachedMetaData metaData = this.luckPerms.getPlayerAdapter(Player.class).getMetaData(player);

    return TagResolver.resolver(
        TagResolver.resolver(
            "fc_luckperms_prefix",
            Tag.selfClosingInserting(LegacySupport.parsePossibleLegacy(metaData.getPrefix()))),
        TagResolver.resolver(
            "fc_luckperms_suffix",
            Tag.selfClosingInserting(LegacySupport.parsePossibleLegacy(metaData.getSuffix()))),
        TagResolver.resolver(
            "fc_luckperms_prefixes",
            Tag.selfClosingInserting(
                LegacySupport.parsePossibleLegacy(
                    String.join("", metaData.getPrefixes().values())))),
        TagResolver.resolver(
            "fc_luckperms_suffixes",
            Tag.selfClosingInserting(
                LegacySupport.parsePossibleLegacy(
                    String.join("", metaData.getSuffixes().values())))),
        TagResolver.resolver(
            "fc_luckperms_username_color",
            Tag.inserting(
                LegacySupport.parsePossibleLegacy(metaData.getMetaValue("username-color")))),
        TagResolver.resolver(
            "fc_luckperms_message_color",
            Tag.inserting(
                LegacySupport.parsePossibleLegacy(metaData.getMetaValue("message-color")))));
  }
}
