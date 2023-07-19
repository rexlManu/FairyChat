package de.rexlmanu.fairychat.plugin.command;

import cloud.commandframework.CommandManager;
import com.google.inject.Inject;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfigurationProvider;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class FairyChatCommand {
  @Inject
  public FairyChatCommand(
      CommandManager<CommandSender> commandManager,
      MiniMessage miniMessage,
      PluginMeta pluginMeta,
      PluginConfigurationProvider configurationProvider) {
    commandManager.command(
        commandManager
            .commandBuilder("fairychat")
            .handler(
                commandContext ->
                    commandContext
                        .getSender()
                        .sendMessage(
                            miniMessage.deserialize(
                                "<gray>This server is running <click:open_url:'https://github.com/rexlManu/FairyChat'><hover:show_text:'<gray>Cick to visit plugin page</gray>'><gradient:#BE95C4:#9F86C0:#5E548E>FairyChat</gradient> <white>v<version></white></hover></click> by <white><click:open_url:'https://github.com/rexlManu'><hover:show_text:'<gray>Cick to visit author</gray>'>rexlManu</hover></click></white>.</gray>",
                                Placeholder.parsed("version", pluginMeta.getVersion())))));

    commandManager.command(
        commandManager
            .commandBuilder("fairychat")
            .literal("reload")
            .permission("fairychat.command.fairychat.reload")
            .handler(
                commandContext -> {
                  configurationProvider.loadConfig();
                  commandContext
                      .getSender()
                      .sendMessage(
                          miniMessage.deserialize(
                              configurationProvider.configuration().messages().pluginReloaded()));
                }));
  }
}
