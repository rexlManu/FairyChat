package de.rexlmanu.fairychat.plugin.integration.types;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.integration.Integration;
import de.rexlmanu.fairychat.plugin.integration.chat.PlaceholderSupport;
import de.rexlmanu.fairychat.plugin.utility.LegacySupport;
import de.rexlmanu.fairychat.plugin.utility.TagResolverUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.platform.PlayerAdapter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.Nullable;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class LuckPermsIntegration implements Integration, PlaceholderSupport {
  private final PluginManager pluginManager;
  private final ServicesManager servicesManager;
  private @Nullable RegisteredServiceProvider<LuckPerms> luckPermsService;
  private final Provider<PluginConfiguration> configurationProvider;

  @Override
  public boolean available() {
    return this.pluginManager.getPlugin(Constants.LUCKPERMS_NAME) != null;
  }

  @Override
  public void enable() {
    this.luckPermsService = this.servicesManager.getRegistration(LuckPerms.class);
  }

  @Override
  public TagResolver resolvePlayerPlaceholder(Player player) {
    PlayerAdapter<Player> playerAdapter =
        this.luckPermsService.getProvider().getPlayerAdapter(Player.class);
    CachedMetaData metaData = playerAdapter.getMetaData(player);

    return TagResolver.resolver(
        TagResolver.resolver(
            "fc_luckperms_prefix",
            TagResolverUtil.resolver(this.configurationProvider.get())
                .apply(LegacySupport.parsePossibleLegacy(metaData.getPrefix()))),
        TagResolver.resolver(
            "fc_luckperms_suffix",
            TagResolverUtil.resolver(this.configurationProvider.get())
                .apply(LegacySupport.parsePossibleLegacy(metaData.getSuffix()))),
        TagResolver.resolver(
            "fc_luckperms_prefixes",
            TagResolverUtil.resolver(this.configurationProvider.get())
                .apply(
                    LegacySupport.parsePossibleLegacy(
                        String.join("", metaData.getPrefixes().values())))),
        TagResolver.resolver(
            "fc_luckperms_suffixes",
            TagResolverUtil.resolver(this.configurationProvider.get())
                .apply(
                    LegacySupport.parsePossibleLegacy(
                        String.join("", metaData.getSuffixes().values())))),
        TagResolver.resolver(
            "fc_luckperms_username_color",
            Tag.inserting(
                LegacySupport.parsePossibleLegacy(metaData.getMetaValue("username-color")))),
        TagResolver.resolver(
            "fc_luckperms_message_color",
            Tag.inserting(
                LegacySupport.parsePossibleLegacy(metaData.getMetaValue("message-color")))),
        TagResolver.resolver(
            "fc_luckperms_prefix_by_group",
            (argumentQueue, context) -> {
              String groupName =
                  argumentQueue
                      .popOr("Tag `fc_luckperms_prefix_by_group` requires a group name")
                      .value();

              Group group =
                  this.luckPermsService.getProvider().getGroupManager().getGroup(groupName);
              if (group == null) {
                return Tag.selfClosingInserting(Component.empty());
              }
              User user = playerAdapter.getUser(player);

              if (!user.getInheritedGroups(user.getQueryOptions()).contains(group)) {
                return Tag.selfClosingInserting(Component.empty());
              }

              return TagResolverUtil.resolver(this.configurationProvider.get())
                  .apply(
                      LegacySupport.parsePossibleLegacy(
                          group.getCachedData().getMetaData().getPrefix()));
            }));
  }
}
