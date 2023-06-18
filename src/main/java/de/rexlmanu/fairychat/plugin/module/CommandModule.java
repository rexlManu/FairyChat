package de.rexlmanu.fairychat.plugin.module;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class CommandModule extends AbstractModule {
  @Provides
  @Singleton
  public final CommandManager<CommandSender> provideCommandManager(
      JavaPlugin javaPlugin, Injector injector) {
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

      //      new MinecraftExceptionHandler<CommandSender>()
      //          .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION,
      //              e -> Component.text("You don't have permission to do this!")
      //                  .style(builder -> builder.color(NamedTextColor.RED)))
      //          .apply(commandManager, sender -> sender);

      return commandManager;
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize the CommandManager");
    }
  }
}
