package de.rexlmanu.fairychat.plugin.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import com.google.inject.Inject;
import de.rexlmanu.fairychat.plugin.configuration.Messages;
import de.rexlmanu.fairychat.plugin.core.chatclear.ChatClearService;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatClearCommand {
  @Inject
  public ChatClearCommand(
      CommandManager<CommandSender> commandManager,
      MiniMessage miniMessage,
      ChatClearService chatClearService,
      Messages config) {
    commandManager.command(
        commandManager
            .commandBuilder("chatclear", "cc")
            .permission("fairychat.command.chatclear")
            .argument(PlayerArgument.optional("target"))
            .handler(
                commandContext ->
                    commandContext
                        .<Player>getOptional("target")
                        .ifPresentOrElse(
                            chatClearService::clearChat, chatClearService::clearChatAll)));
  }
}
