package de.rexlmanu.fairychat.plugin.core.chatclear.redis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.core.chatclear.DefaultChatClearService;
import de.rexlmanu.fairychat.plugin.redis.channel.RedisChannelSubscriber;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ChatClearChannelSubscriber implements RedisChannelSubscriber<ChatClearData> {
  private final DefaultChatClearService chatClearService;

  @Override
  public Class<ChatClearData> getDataType() {
    return ChatClearData.class;
  }

  @Override
  public void handle(ChatClearData data) {
    if (data.targetId() != null) {
      if (Bukkit.getPlayer(data.targetId()) == null) return;
      this.chatClearService.clearChat(Bukkit.getPlayer(data.targetId()));
      return;
    }
    this.chatClearService.clearChatAll();
  }
}
