package de.rexlmanu.fairychat.plugin.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.Messages;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class CommandModule extends AbstractModule {
  @Provides
  @Singleton
  public final CommandManager<CommandSender> provideCommandManager(
      JavaPlugin javaPlugin, Injector injector, MiniMessage miniMessage, Messages messages) {
    try {
      Function<CommandSender, CommandSender> mapper = Function.identity();

      PaperCommandManager<CommandSender> commandManager =
          new PaperCommandManager<>(
              javaPlugin,
              AsynchronousCommandExecutionCoordinator.simpleCoordinator(),
              mapper,
              mapper);

      if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
        commandManager.registerAsynchronousCompletions();
      }

      commandManager
          .parameterInjectorRegistry()
          .registerInjectionService(context -> injector.getInstance(context.getSecond()));

      commandManager.registerExceptionHandler(
          InvalidSyntaxException.class,
          (sender, e) ->
              sender.sendMessage(
                  miniMessage.deserialize(
                      messages.invalidSyntax(),
                      Placeholder.unparsed("syntax", e.getCorrectSyntax()))));

      return commandManager;
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize the CommandManager");
    }
  }
}
