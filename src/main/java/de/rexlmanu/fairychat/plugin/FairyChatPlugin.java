package de.rexlmanu.fairychat.plugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.exlll.configlib.YamlConfigurations;
import de.rexlmanu.fairychat.plugin.command.BroadcastCommand;
import de.rexlmanu.fairychat.plugin.command.CommandModule;
import de.rexlmanu.fairychat.plugin.command.IgnoreCommand;
import de.rexlmanu.fairychat.plugin.command.PrivateMessageCommand;
import de.rexlmanu.fairychat.plugin.configuration.ConfigModule;
import de.rexlmanu.fairychat.plugin.configuration.FairyChatConfiguration;
import de.rexlmanu.fairychat.plugin.core.CoreModule;
import de.rexlmanu.fairychat.plugin.core.broadcast.BroadcastChannelSubscriber;
import de.rexlmanu.fairychat.plugin.core.custommessages.CustomMessageBukkitListener;
import de.rexlmanu.fairychat.plugin.core.custommessages.redis.CustomMessageSubscriber;
import de.rexlmanu.fairychat.plugin.core.ignore.redis.RedisUserIgnoreUpdateSubscriber;
import de.rexlmanu.fairychat.plugin.core.metrics.MetricsModule;
import de.rexlmanu.fairychat.plugin.core.metrics.RedisEnabledChart;
import de.rexlmanu.fairychat.plugin.core.metrics.RedisUsersChart;
import de.rexlmanu.fairychat.plugin.core.playerchat.PlayerChatListener;
import de.rexlmanu.fairychat.plugin.core.playerchat.PlayerChatMessageSubscriber;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.redis.RedisPrivateMessagingSubscriber;
import de.rexlmanu.fairychat.plugin.core.user.listener.UserBukkitListener;
import de.rexlmanu.fairychat.plugin.database.DatabaseClient;
import de.rexlmanu.fairychat.plugin.database.DatabaseModule;
import de.rexlmanu.fairychat.plugin.integration.IntegrationRegistry;
import de.rexlmanu.fairychat.plugin.permission.PermissionModule;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import de.rexlmanu.fairychat.plugin.redis.channel.RedisSubscriberModule;
import de.rexlmanu.fairychat.plugin.utility.scheduler.PluginSchedulerModule;
import de.rexlmanu.fairychat.plugin.utility.update.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public class FairyChatPlugin extends JavaPlugin {
  private FairyChatConfiguration configuration;
  private Injector injector;

  @Override
  public void onEnable() {
    this.setupConfig();

    this.injector =
        Guice.createInjector(
            new FairyChatModule(this.configuration, this),
            new PluginSchedulerModule(),
            new CommandModule(),
            new PermissionModule(),
            new ConfigModule(),
            new CoreModule(this.configuration),
            new RedisSubscriberModule(),
            new DatabaseModule(this.configuration.database()),
            new MetricsModule());

    this.injector.getInstance(RedisConnector.class).open();
    this.injector.getInstance(DatabaseClient.class).open();

    this.getServer()
        .getPluginManager()
        .registerEvents(this.injector.getInstance(PlayerChatListener.class), this);

    this.injector.getInstance(UserBukkitListener.class);

    this.injector.getInstance(IntegrationRegistry.class).init();
    this.registerCommands();
    this.registerSubscribers();
    this.registerMetricCharts();
    this.registerListeners();

    this.injector.getInstance(UpdateChecker.class).checkAndNotify();
  }

  private void registerListeners() {
    this.getServer()
        .getPluginManager()
        .registerEvents(this.injector.getInstance(CustomMessageBukkitListener.class), this);
  }

  private void registerSubscribers() {
    if (!this.configuration.redisCredentials().enabled()) return;

    this.injector.getInstance(BroadcastChannelSubscriber.class);
    this.injector.getInstance(PlayerChatMessageSubscriber.class);
    this.injector.getInstance(RedisPrivateMessagingSubscriber.class);
    this.injector.getInstance(RedisUserIgnoreUpdateSubscriber.class);
    this.injector.getInstance(CustomMessageSubscriber.class);
  }

  @Override
  public void onDisable() {
    this.injector.getInstance(RedisConnector.class).close();
    this.injector.getInstance(DatabaseClient.class).close();
  }

  private void setupConfig() {
    this.configuration =
        YamlConfigurations.update(
            this.getDataFolder().toPath().resolve("config.yml"), FairyChatConfiguration.class);
  }

  private void registerCommands() {
    this.injector.getInstance(BroadcastCommand.class);
    this.injector.getInstance(PrivateMessageCommand.class);
    this.injector.getInstance(IgnoreCommand.class);
  }

  private void registerMetricCharts() {
    this.injector.getInstance(RedisEnabledChart.class).register();
    if (this.configuration.redisCredentials().enabled()) {
      this.injector.getInstance(RedisUsersChart.class).register();
    }
  }
}
