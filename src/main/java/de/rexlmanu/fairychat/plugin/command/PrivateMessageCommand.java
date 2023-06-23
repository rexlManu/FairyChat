package de.rexlmanu.fairychat.plugin.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import com.google.inject.Inject;
import de.rexlmanu.fairychat.plugin.command.argument.UserArgument;
import de.rexlmanu.fairychat.plugin.configuration.Messages;
import de.rexlmanu.fairychat.plugin.configuration.PrivateMessagingConfig;
import de.rexlmanu.fairychat.plugin.core.ignore.UserIgnoreService;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.PrivateMessagingService;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrivateMessageCommand {

  @Inject
  public PrivateMessageCommand(
      CommandManager<CommandSender> commandManager,
      PrivateMessagingConfig config,
      UserService userService,
      PrivateMessagingService privateMessagingService,
      MiniMessage miniMessage,
      UserFactory userFactory,
      UserIgnoreService userIgnoreService,
      Messages messages) {
    commandManager.command(
        commandManager
            .commandBuilder("pm", config.aliases().getOrDefault("pm", new String[0]))
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
                    commandContext
                        .getSender()
                        .sendMessage(miniMessage.deserialize(messages.youCantMessageYourself()));
                    return;
                  }

                  if (userIgnoreService.isIgnored(recipient.uniqueId(), user.uniqueId())
                      && !commandContext.getSender().hasPermission("fairychat.bypass.ignore")) {
                    commandContext
                        .getSender()
                        .sendMessage(miniMessage.deserialize(messages.youCantMessageThisPlayer()));
                    return;
                  }

                  privateMessagingService.sendMessage(user, recipient, message);
                }));

    commandManager.command(
        commandManager
            .commandBuilder("reply", config.aliases().getOrDefault("reply", new String[0]))
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
                    commandContext
                        .getSender()
                        .sendMessage(miniMessage.deserialize(messages.youDidntMessageAnyone()));
                    return;
                  }

                  if (userIgnoreService.isIgnored(lastRecipient.uniqueId(), user.uniqueId())
                      && !commandContext.getSender().hasPermission("fairychat.bypass.ignore")) {
                    commandContext
                        .getSender()
                        .sendMessage(miniMessage.deserialize(messages.youCantMessageThisPlayer()));
                    return;
                  }

                  privateMessagingService.sendMessage(user, lastRecipient, message);
                }));
  }
}
