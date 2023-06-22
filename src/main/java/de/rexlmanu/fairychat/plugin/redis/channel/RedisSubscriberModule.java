package de.rexlmanu.fairychat.plugin.redis.channel;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import lombok.RequiredArgsConstructor;

public class RedisSubscriberModule extends AbstractModule {
  @Override
  protected void configure() {
    RedisSubscriberTypeListener listener = new RedisSubscriberTypeListener();
    requestInjection(listener);
    this.bindListener(
        typeLiteral ->
            Matchers.subclassesOf(RedisChannelSubscriber.class).matches(typeLiteral.getRawType()),
        listener);
  }

  public static class RedisSubscriberTypeListener implements TypeListener {
    @Inject private RedisConnector redisConnector;

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
      encounter.register(new RedisChannelInjectionListener<>(redisConnector));
    }
  }

  @RequiredArgsConstructor
  public static class RedisChannelInjectionListener<I> implements InjectionListener<I> {
    private final RedisConnector redisConnector;

    @Override
    public void afterInjection(I injectee) {
      if (injectee instanceof RedisChannelSubscriber<?> subscriber) {
        subscriber.register(redisConnector);
      }
    }
  }
}
