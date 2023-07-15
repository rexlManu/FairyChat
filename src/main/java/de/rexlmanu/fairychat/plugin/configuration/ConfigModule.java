package de.rexlmanu.fairychat.plugin.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfigModule extends AbstractModule {

  @Provides
  @Inject
  public PrivateMessagingConfig privateMessaging(FairyChatConfiguration configuration) {
    return configuration.privateMessaging();
  }

  @Provides
  @Inject
  public Messages messages(FairyChatConfiguration configuration) {
    return configuration.messages();
  }

  @Provides
  @Inject
  public BroadcastConfig broadcast(FairyChatConfiguration configuration) {
    return configuration.broadcast();
  }

  @Provides
  @Inject
  public RedisCredentials redisCredentials(FairyChatConfiguration configuration) {
    return configuration.redisCredentials();
  }

  @Provides
  @Inject
  public DatabaseConfig mysql(FairyChatConfiguration configuration) {
    return configuration.database();
  }

  @Provides
  @Inject
  public CustomMessages customMessages(FairyChatConfiguration configuration) {
    return configuration.customMessages();
  }
}
