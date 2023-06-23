package de.rexlmanu.fairychat.plugin.core.user.redis.channel;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.core.user.redis.RedisUserService;
import de.rexlmanu.fairychat.plugin.database.redis.channel.RedisChannelSubscriber;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisUserLoginSubscriber implements RedisChannelSubscriber<UserLoginDto> {
  private final RedisUserService userService;

  @Override
  public Class<UserLoginDto> getDataType() {
    return UserLoginDto.class;
  }

  @Override
  public void handle(UserLoginDto data) {
    this.userService.addUser(data.user());
  }
}
