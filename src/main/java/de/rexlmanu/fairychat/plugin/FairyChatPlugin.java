package de.rexlmanu.fairychat.plugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.exlll.configlib.YamlConfigurations;
import de.rexlmanu.fairychat.plugin.command.BroadcastCommand;
import de.rexlmanu.fairychat.plugin.command.CommandModule;
import de.rexlmanu.fairychat.plugin.command.PrivateMessageCommand;
import de.rexlmanu.fairychat.plugin.configuration.ConfigModule;
import de.rexlmanu.fairychat.plugin.configuration.FairyChatConfiguration;
import de.rexlmanu.fairychat.plugin.core.CoreModule;
import de.rexlmanu.fairychat.plugin.core.broadcast.BroadcastChannelSubscriber;
import de.rexlmanu.fairychat.plugin.core.playerchat.PlayerChatListener;
import de.rexlmanu.fairychat.plugin.core.playerchat.PlayerChatMessageSubscriber;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.redis.RedisPrivateMessagingSubscriber;
import de.rexlmanu.fairychat.plugin.core.user.listener.UserBukkitListener;
import de.rexlmanu.fairychat.plugin.permission.PermissionModule;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import de.rexlmanu.fairychat.plugin.redis.channel.RedisSubscriberModule;
import org.bstats.bukkit.Metrics;
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
            new CommandModule(),
            new PermissionModule(),
            new ConfigModule(),
            new CoreModule(this.configuration),
            new RedisSubscriberModule());

    this.injector.getInstance(RedisConnector.class).open();

    this.getServer()
        .getPluginManager()
        .registerEvents(this.injector.getInstance(PlayerChatListener.class), this);

    this.injector.getInstance(UserBukkitListener.class);

    this.registerCommands();
    this.registerSubscribers();

    new Metrics(this, Constants.BSTATS_ID);
  }

  private void registerSubscribers() {
    if (!this.configuration.redisCredentials().enabled()) return;

    this.injector.getInstance(BroadcastChannelSubscriber.class);
    this.injector.getInstance(PlayerChatMessageSubscriber.class);
    this.injector.getInstance(RedisPrivateMessagingSubscriber.class);
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
    this.injector.getInstance(PrivateMessageCommand.class);
  }
}
