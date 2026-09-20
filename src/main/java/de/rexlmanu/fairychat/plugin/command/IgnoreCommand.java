package de.rexlmanu.fairychat.plugin.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.rexlmanu.fairychat.plugin.command.argument.UserParser;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.ignore.UserIgnoreService;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;

public class IgnoreCommand {
  @Inject
  public IgnoreCommand(
      CommandManager<Source> commandManager,
      MiniMessage miniMessage,
      UserService userService,
      UserFactory userFactory,
      UserIgnoreService userIgnoreService,
      Provider<PluginConfiguration> configurationProvider) {
    commandManager.command(
        commandManager
            .commandBuilder("ignore")
            .senderType(PlayerSource.class)
            .required("target", UserParser.userParser())
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
                  User target = commandContext.get("target");

                  if (user.equals(target)) {
                    commandContext
                        .sender()
                        .source()
                        .sendMessage(
                            miniMessage.deserialize(
                                configurationProvider.get().messages().youCantIgnoreYourself()));
                    return;
                  }

                  if (userIgnoreService.isIgnored(user.uniqueId(), target.uniqueId())) {
                    userIgnoreService.setIgnored(user.uniqueId(), target.uniqueId(), false);
                    commandContext
                        .sender()
                        .source()
                        .sendMessage(
                            miniMessage.deserialize(
                                configurationProvider.get().messages().youUnignoredUser(),
                                Placeholder.unparsed("name", target.username())));
                    return;
                  }

                  userIgnoreService.setIgnored(user.uniqueId(), target.uniqueId(), true);
                  commandContext
                      .sender()
                      .source()
                      .sendMessage(
                          miniMessage.deserialize(
                              configurationProvider.get().messages().youIgnoredUser(),
                              Placeholder.unparsed("name", target.username())));
                }));
  }
}
