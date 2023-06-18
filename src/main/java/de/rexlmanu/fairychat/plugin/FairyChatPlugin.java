package de.rexlmanu.fairychat.plugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.exlll.configlib.YamlConfigurations;
import de.rexlmanu.fairychat.plugin.command.BroadcastCommand;
import de.rexlmanu.fairychat.plugin.listener.ChatListener;
import de.rexlmanu.fairychat.plugin.module.CommandModule;
import de.rexlmanu.fairychat.plugin.module.GsonModule;
import de.rexlmanu.fairychat.plugin.module.PermissionModule;
import de.rexlmanu.fairychat.plugin.module.PluginModule;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import org.bukkit.plugin.java.JavaPlugin;

public class FairyChatPlugin extends JavaPlugin {
  private FairyChatConfiguration configuration;
  private Injector injector;

  @Override
  public void onEnable() {
    this.setupConfig();

    this.injector =
        Guice.createInjector(
            new PluginModule(this.configuration, this),
            new CommandModule(),
            new PermissionModule(),
            new GsonModule());

    this.injector.getInstance(RedisConnector.class).open();

    this.getServer()
        .getPluginManager()
        .registerEvents(this.injector.getInstance(ChatListener.class), this);

    this.registerCommands();
  }

  @Override
  public void onDisable() {
    this.injector.getInstance(RedisConnector.class).close();
  }

  private void setupConfig() {
    this.configuration =
        YamlConfigurations.update(
            this.getDataFolder().toPath().resolve("config.yml"), FairyChatConfiguration.class);
  }

  private void registerCommands() {
    this.injector.getInstance(BroadcastCommand.class);
  }
}
