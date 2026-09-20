package de.rexlmanu.fairychat.plugin.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.rexlmanu.fairychat.plugin.command.argument.UserParser;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.ignore.UserIgnoreService;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.PrivateMessagingService;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.standard.StringParser;

public class PrivateMessageCommand {

  @Inject
  public PrivateMessageCommand(
      CommandManager<Source> commandManager,
      UserService userService,
      PrivateMessagingService privateMessagingService,
      MiniMessage miniMessage,
      UserFactory userFactory,
      UserIgnoreService userIgnoreService,
      Provider<PluginConfiguration> configurationProvider) {
    commandManager.command(
        commandManager
            .commandBuilder(
                "pm",
                configurationProvider
                    .get()
                    .privateMessaging()
                    .aliases()
                    .getOrDefault("pm", new String[0]))
            .senderType(PlayerSource.class)
            .required("recipient", UserParser.userParser())
            .required("message", StringParser.greedyStringParser())
            .handler(
                commandContext -> {
                  User recipient = commandContext.get("recipient");
                  User user =
                      userService
                          .findUserById(commandContext.sender().source().getUniqueId())
                          .orElseGet(
                              () -> {
                                User createdUser =
                                    userFactory.createFromPlayer(commandContext.sender().source());
                                userService.login(createdUser);
                                return createdUser;
                              });
                  String message = commandContext.get("message");
                  if (user.equals(recipient)) {
                    commandContext
                        .sender()
                        .source()
                        .sendMessage(
                            miniMessage.deserialize(
                                configurationProvider.get().messages().youCantMessageYourself()));
                    return;
                  }

                  if (userIgnoreService.isIgnored(recipient.uniqueId(), user.uniqueId())
                      && !commandContext
                          .sender()
                          .source()
                          .hasPermission("fairychat.bypass.ignore")) {
                    commandContext
                        .sender()
                        .source()
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
            .senderType(PlayerSource.class)
            .required("message", StringParser.greedyStringParser())
            .handler(
                commandContext -> {
                  User user =
                      userService
                          .findUserById(commandContext.sender().source().getUniqueId())
                          .orElseGet(
                              () -> {
                                User createdUser =
                                    userFactory.createFromPlayer(commandContext.sender().source());
                                userService.login(createdUser);
                                return createdUser;
                              });
                  String message = commandContext.get("message");
                  User lastRecipient = privateMessagingService.lastRecipient(user).orElse(null);
                  if (lastRecipient == null) {
                    commandContext
                        .sender()
                        .source()
                        .sendMessage(
                            miniMessage.deserialize(
                                configurationProvider.get().messages().youDidntMessageAnyone()));
                    return;
                  }

                  if (userIgnoreService.isIgnored(lastRecipient.uniqueId(), user.uniqueId())
                      && !commandContext
                          .sender()
                          .source()
                          .hasPermission("fairychat.bypass.ignore")) {
                    commandContext
                        .sender()
                        .source()
                        .sendMessage(
                            miniMessage.deserialize(
                                configurationProvider.get().messages().youCantMessageThisPlayer()));
                    return;
                  }

                  privateMessagingService.sendMessage(user, lastRecipient, message);
                }));
  }
}
