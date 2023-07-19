package de.rexlmanu.fairychat.plugin.configuration;

import com.google.inject.Singleton;
import de.exlll.configlib.YamlConfigurations;
import java.nio.file.Path;
import lombok.Getter;

@Singleton
public class PluginConfigurationProvider {
  @Getter private volatile PluginConfiguration configuration;
  private final Path dataFolder;

  public PluginConfigurationProvider(Path dataFolder) {
    this.dataFolder = dataFolder;

    this.loadConfig();
  }

  public void loadConfig() {
    this.configuration =
        YamlConfigurations.update(this.dataFolder.resolve("config.yml"), PluginConfiguration.class);
  }
}
