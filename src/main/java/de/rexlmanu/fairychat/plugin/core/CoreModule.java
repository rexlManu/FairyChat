package de.rexlmanu.fairychat.plugin.core;

import com.google.inject.AbstractModule;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfigurationProvider;
import de.rexlmanu.fairychat.plugin.core.chatclear.ChatClearService;
import de.rexlmanu.fairychat.plugin.core.chatclear.DefaultChatClearService;
import de.rexlmanu.fairychat.plugin.core.chatclear.redis.RedisChatClearService;
import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.DefaultPlayerChatCooldownService;
import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.PlayerChatCooldownService;
import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.redis.RedisPlayerChatCooldownService;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.PrivateMessagingService;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.defaults.DefaultPrivateMessagingService;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.redis.RedisPrivateMessagingService;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import de.rexlmanu.fairychat.plugin.core.user.defaults.DefaultUserService;
import de.rexlmanu.fairychat.plugin.core.user.redis.RedisUserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoreModule extends AbstractModule {

  private final PluginConfigurationProvider configurationProvider;

  @Override
  protected void configure() {
    if (this.configurationProvider.configuration().redisCredentials().enabled()) {
      this.bind(PrivateMessagingService.class).to(RedisPrivateMessagingService.class);
      this.bind(UserService.class).to(RedisUserService.class);
      this.bind(ChatClearService.class).to(RedisChatClearService.class);
      this.bind(PlayerChatCooldownService.class).to(RedisPlayerChatCooldownService.class);
    } else {
      this.bind(UserService.class).to(DefaultUserService.class);
      this.bind(PrivateMessagingService.class).to(DefaultPrivateMessagingService.class);
      this.bind(ChatClearService.class).to(DefaultChatClearService.class);
      this.bind(PlayerChatCooldownService.class).to(DefaultPlayerChatCooldownService.class);
    }
  }
}
