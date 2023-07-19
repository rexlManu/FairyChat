package de.rexlmanu.fairychat.plugin.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import com.google.inject.Inject;
import de.rexlmanu.fairychat.plugin.core.chatclear.ChatClearService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatClearCommand {
  @Inject
  public ChatClearCommand(
      CommandManager<CommandSender> commandManager, ChatClearService chatClearService) {
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
