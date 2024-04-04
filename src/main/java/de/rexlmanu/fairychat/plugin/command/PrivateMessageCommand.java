package de.rexlmanu.fairychat.plugin.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import com.google.inject.Inject;
import com.google.inject.Provider;
import de.rexlmanu.fairychat.plugin.command.argument.UserArgument;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.ignore.UserIgnoreService;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.PrivateMessagingService;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrivateMessageCommand {

  @Inject
  public PrivateMessageCommand(
      CommandManager<CommandSender> commandManager,
      UserService userService,
      PrivateMessagingService privateMessagingService,
      MiniMessage miniMessage,
      UserFactory userFactory,
      UserIgnoreService userIgnoreService,
      Provider<PluginConfiguration> configurationProvider,
      BukkitAudiences bukkitAudiences) {
    commandManager.command(
        commandManager
            .commandBuilder(
                "pm",
                configurationProvider
                    .get()
                    .privateMessaging()
                    .aliases()
                    .getOrDefault("pm", new String[0]))
            .senderType(Player.class)
            .argument(UserArgument.of("recipient"))
            .argument(StringArgument.greedy("message"))
            .handler(
                commandContext -> {
                  User recipient = commandContext.get("recipient");
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
                  String message = commandContext.get("message");
                  if (user.equals(recipient)) {
                    bukkitAudiences
                        .sender(commandContext.getSender())
                        .sendMessage(
                            miniMessage.deserialize(
                                configurationProvider.get().messages().youCantMessageYourself()));
                    return;
                  }

                  if (userIgnoreService.isIgnored(recipient.uniqueId(), user.uniqueId())
                      && !commandContext.getSender().hasPermission("fairychat.bypass.ignore")) {
                    bukkitAudiences
                        .sender(commandContext.getSender())
                        .sendMessage(
                            miniMessage.deserialize(
                                configurationProvider.get().messages().youCantMessageThisPlayer()));
                    return;
                  }

                  privateMessagingService.sendMessage(user, recipient, message);
                }));

    commandManager.command(
        commandManager
            .commandBuilder(
                "reply",
                configurationProvider
                    .get()
                    .privateMessaging()
                    .aliases()
                    .getOrDefault("reply", new String[0]))
            .senderType(Player.class)
            .argument(StringArgument.greedy("message"))
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
                  String message = commandContext.get("message");
                  User lastRecipient = privateMessagingService.lastRecipient(user).orElse(null);
                  if (lastRecipient == null) {
                    bukkitAudiences
                        .sender(commandContext.getSender())
                        .sendMessage(
                            miniMessage.deserialize(
                                configurationProvider.get().messages().youDidntMessageAnyone()));
                    return;
                  }

                  if (userIgnoreService.isIgnored(lastRecipient.uniqueId(), user.uniqueId())
                      && !commandContext.getSender().hasPermission("fairychat.bypass.ignore")) {
                    bukkitAudiences
                        .sender(commandContext.getSender())
                        .sendMessage(
                            miniMessage.deserialize(
                                configurationProvider.get().messages().youCantMessageThisPlayer()));
                    return;
                  }

                  privateMessagingService.sendMessage(user, lastRecipient, message);
                }));
  }
}
