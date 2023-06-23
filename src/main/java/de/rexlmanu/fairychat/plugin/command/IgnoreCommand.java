package de.rexlmanu.fairychat.plugin.command;

import cloud.commandframework.CommandManager;
import com.google.inject.Inject;
import de.rexlmanu.fairychat.plugin.command.argument.UserArgument;
import de.rexlmanu.fairychat.plugin.configuration.Messages;
import de.rexlmanu.fairychat.plugin.core.ignore.UserIgnoreService;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
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
      Messages messages) {
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
                    commandContext
                        .getSender()
                        .sendMessage(miniMessage.deserialize(messages.youCantIgnoreYourself()));
                    return;
                  }

                  if (userIgnoreService.isIgnored(user.uniqueId(), target.uniqueId())) {
                    userIgnoreService.setIgnored(user.uniqueId(), target.uniqueId(), false);
                    commandContext
                        .getSender()
                        .sendMessage(
                            miniMessage.deserialize(
                                messages.youUnignoredUser(),
                                Placeholder.unparsed("name", target.username())));
                    return;
                  }

                  userIgnoreService.setIgnored(user.uniqueId(), target.uniqueId(), true);
                  commandContext
                      .getSender()
                      .sendMessage(
                          miniMessage.deserialize(
                              messages.youIgnoredUser(),
                              Placeholder.unparsed("name", target.username())));
                }));
  }
}
