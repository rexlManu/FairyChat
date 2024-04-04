package de.rexlmanu.fairychat.plugin.command;

import cloud.commandframework.CommandManager;
import com.google.inject.Inject;
import com.google.inject.Provider;
import de.rexlmanu.fairychat.plugin.command.argument.UserArgument;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.ignore.UserIgnoreService;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand {
  @Inject
  public IgnoreCommand(
      CommandManager<CommandSender> commandManager,
      MiniMessage miniMessage,
      UserService userService,
      UserFactory userFactory,
      UserIgnoreService userIgnoreService,
      Provider<PluginConfiguration> configurationProvider,
      BukkitAudiences bukkitAudiences) {
    commandManager.command(
        commandManager
            .commandBuilder("ignore")
            .argument(UserArgument.of("target"))
            .handler(
                commandContext -> {
                  User user =
                      userService
                          .findUserById(((Player) commandContext.getSender()).getUniqueId())
                          .orElseGet(
                              () -> {
                                User createdUser =
                                    userFactory.createFromPlayer(
                                        (Player) commandContext.getSender());
                                userService.login(createdUser);
                                return createdUser;
                              });
                  User target = commandContext.get("target");

                  if (user.equals(target)) {
                    bukkitAudiences
                        .sender(commandContext.getSender())
                        .sendMessage(
                            miniMessage.deserialize(
                                configurationProvider.get().messages().youCantIgnoreYourself()));
                    return;
                  }

                  if (userIgnoreService.isIgnored(user.uniqueId(), target.uniqueId())) {
                    userIgnoreService.setIgnored(user.uniqueId(), target.uniqueId(), false);
                    bukkitAudiences
                        .sender(commandContext.getSender())
                        .sendMessage(
                            miniMessage.deserialize(
                                configurationProvider.get().messages().youUnignoredUser(),
                                Placeholder.unparsed("name", target.username())));
                    return;
                  }

                  userIgnoreService.setIgnored(user.uniqueId(), target.uniqueId(), true);
                  bukkitAudiences
                      .sender(commandContext.getSender())
                      .sendMessage(
                          miniMessage.deserialize(
                              configurationProvider.get().messages().youIgnoredUser(),
                              Placeholder.unparsed("name", target.username())));
                }));
  }
}
