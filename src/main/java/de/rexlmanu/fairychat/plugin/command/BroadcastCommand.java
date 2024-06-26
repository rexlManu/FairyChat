package de.rexlmanu.fairychat.plugin.command;

import static de.rexlmanu.fairychat.plugin.Constants.BROADCAST_CHANNEL;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import com.google.inject.Inject;
import com.google.inject.Provider;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.broadcast.BroadcastMessageData;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class BroadcastCommand {
  @Inject
  public BroadcastCommand(
      CommandManager<CommandSender> commandManager,
      MiniMessage miniMessage,
      RedisConnector connector,
      Server server,
      Provider<PluginConfiguration> configurationProvider, BukkitAudiences bukkitAudiences) {
    commandManager.command(
        commandManager
            .commandBuilder("broadcast")
            .permission("fairychat.command.broadcast")
            .argument(StringArgument.greedy("message"))
            .handler(
                commandContext -> {
                  String message = commandContext.get("message");
                  Component component =
                      miniMessage.deserialize(
                          configurationProvider.get().broadcast().format(),
                          Placeholder.parsed("message", message));

                  /*
                   * If the redis connector isn't available, we send the message to all online players.
                   */
                  if (!connector.available()) {
                    server.getOnlinePlayers().forEach(player -> bukkitAudiences.player(player).sendMessage(component));
                    return;
                  }

                  connector.publish(
                      BROADCAST_CHANNEL,
                      new BroadcastMessageData(Constants.SERVER_IDENTITY_ORIGIN, component));
                }));
  }
}
