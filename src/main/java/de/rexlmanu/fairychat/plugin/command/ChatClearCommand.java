package de.rexlmanu.fairychat.plugin.command;

import com.google.inject.Inject;
import de.rexlmanu.fairychat.plugin.core.chatclear.ChatClearService;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.paper.util.sender.Source;

public class ChatClearCommand {
  @Inject
  public ChatClearCommand(
      CommandManager<Source> commandManager, ChatClearService chatClearService) {
    commandManager.command(
        commandManager
            .commandBuilder("chatclear", "cc")
            .permission("fairychat.command.chatclear")
            .optional("target", PlayerParser.playerParser())
            .handler(
                commandContext ->
                    commandContext
                        .<Player>optional("target")
                        .ifPresentOrElse(
                            chatClearService::clearChat, chatClearService::clearChatAll)));
  }
}
