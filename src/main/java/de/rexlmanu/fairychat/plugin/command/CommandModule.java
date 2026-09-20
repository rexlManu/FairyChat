package de.rexlmanu.fairychat.plugin.command;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;

public class CommandModule extends AbstractModule {
  @Provides
  @Singleton
  public CommandManager<Source> provideCommandManager(
      JavaPlugin plugin,
      UserService userService,
      MiniMessage miniMessage,
      Provider<PluginConfiguration> configurationProvider) {
    PaperCommandManager<Source> manager =
        PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(plugin);

    manager
        .parameterInjectorRegistry()
        .registerInjector(UserService.class, (context, annotations) -> userService);
    manager
        .captionRegistry()
        .registerProvider(
            CaptionProvider.constantProvider(
                Caption.of("argument.parse.failure.user"), "Player '<input>' was not found."));
    manager
        .exceptionController()
        .registerHandler(
            InvalidSyntaxException.class,
            context ->
                context
                    .context()
                    .sender()
                    .source()
                    .sendMessage(
                        miniMessage.deserialize(
                            configurationProvider.get().messages().invalidSyntax(),
                            Placeholder.unparsed("syntax", context.exception().correctSyntax()))));
    return manager;
  }
}
