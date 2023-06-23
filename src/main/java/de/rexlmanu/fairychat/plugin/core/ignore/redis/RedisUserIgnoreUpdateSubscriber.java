package de.rexlmanu.fairychat.plugin.core.ignore.redis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.core.ignore.DefaultUserIgnoreService;
import de.rexlmanu.fairychat.plugin.redis.channel.RedisChannelSubscriber;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisUserIgnoreUpdateSubscriber implements RedisChannelSubscriber<UserIgnoreDto> {
  private final DefaultUserIgnoreService userIgnoreService;

  @Override
  public Class<UserIgnoreDto> getDataType() {
    return UserIgnoreDto.class;
  }

  @Override
  public void handle(UserIgnoreDto data) {
    this.userIgnoreService.invalidate(data.key());
  }
}
