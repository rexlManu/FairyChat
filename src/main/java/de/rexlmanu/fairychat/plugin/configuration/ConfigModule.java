package de.rexlmanu.fairychat.plugin.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfigModule extends AbstractModule {

  @Provides
  @Inject
  public PluginConfiguration pluginConfiguration(PluginConfigurationProvider handler) {
    return handler.configuration();
  }
}
