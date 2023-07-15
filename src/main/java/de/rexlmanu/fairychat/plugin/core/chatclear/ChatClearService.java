package de.rexlmanu.fairychat.plugin.core.chatclear;

import org.bukkit.entity.Player;

public interface ChatClearService {
  void clearChat(Player player);

  void clearChatAll();
}
