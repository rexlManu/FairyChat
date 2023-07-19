package de.rexlmanu.fairychat.plugin.configuration;

import com.google.inject.Singleton;
import de.exlll.configlib.YamlConfigurations;
import java.nio.file.Path;
import java.util.logging.Logger;
import lombok.Getter;

@Singleton
public class PluginConfigurationProvider {
  @Getter private volatile PluginConfiguration configuration;
  private final Path dataFolder;
  private final Logger logger;

  public PluginConfigurationProvider(Path dataFolder, Logger logger) {
    this.dataFolder = dataFolder;
    this.logger = logger;

    this.loadConfig();
  }

  public void loadConfig() {
    try {
      this.configuration =
          YamlConfigurations.update(
              this.dataFolder.resolve("config.yml"), PluginConfiguration.class);
    } catch (Exception e) {
      this.logger.severe(
          """
The configuration file could not be loaded. This can happen if you have made a mistake in the configuration file.
Also make sure that you have the latest version of the configuration file. If that is the case, rename the old configuration file and restart the server.
""");

      throw new RuntimeException("Could not load configuration file.", e);
    }
  }
}
