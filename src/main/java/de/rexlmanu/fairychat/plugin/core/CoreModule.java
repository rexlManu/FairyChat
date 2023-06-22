package de.rexlmanu.fairychat.plugin.core;

import com.google.inject.AbstractModule;
import de.rexlmanu.fairychat.plugin.configuration.FairyChatConfiguration;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.PrivateMessagingService;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.defaults.DefaultPrivateMessagingService;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.redis.RedisPrivateMessagingService;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import de.rexlmanu.fairychat.plugin.core.user.defaults.DefaultUserService;
import de.rexlmanu.fairychat.plugin.core.user.redis.RedisUserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoreModule extends AbstractModule {

  private final FairyChatConfiguration configuration;

  @Override
  protected void configure() {
    if (configuration.redisCredentials().enabled()) {
      this.bind(PrivateMessagingService.class).to(RedisPrivateMessagingService.class);
      this.bind(UserService.class).to(RedisUserService.class);
    } else {
      this.bind(UserService.class).to(DefaultUserService.class);
      this.bind(PrivateMessagingService.class).to(DefaultPrivateMessagingService.class);
    }
  }
}
