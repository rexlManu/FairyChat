package de.rexlmanu.fairychat.plugin.core.user.redis.channel;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.core.user.redis.RedisUserService;
import de.rexlmanu.fairychat.plugin.database.redis.channel.RedisChannelSubscriber;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisUserLogoutSubscriber implements RedisChannelSubscriber<UserLogoutDto> {
  private final RedisUserService userService;

  @Override
  public Class<UserLogoutDto> getDataType() {
    return UserLogoutDto.class;
  }

  @Override
  public void handle(UserLogoutDto data) {
    this.userService.removeUser(data.user());
  }
}
