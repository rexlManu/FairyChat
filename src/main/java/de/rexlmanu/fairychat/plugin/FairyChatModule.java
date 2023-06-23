package de.rexlmanu.fairychat.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import de.rexlmanu.fairychat.plugin.configuration.FairyChatConfiguration;
import de.rexlmanu.fairychat.plugin.utility.ComponentTypeAdapter;
import de.rexlmanu.fairychat.plugin.utility.annotation.PluginLogger;
import java.nio.file.Path;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

@RequiredArgsConstructor
public class FairyChatModule extends AbstractModule {
  private final FairyChatConfiguration configuration;
  private final JavaPlugin plugin;

  @Override
  protected void configure() {
    this.bind(JavaPlugin.class).toInstance(this.plugin);
    this.bind(FairyChatConfiguration.class).toInstance(this.configuration);
    this.bind(Server.class).toInstance(this.plugin.getServer());
    this.bind(PluginManager.class).toInstance(this.plugin.getServer().getPluginManager());
    this.bind(BukkitScheduler.class).toInstance(this.plugin.getServer().getScheduler());
    this.bind(Path.class)
        .annotatedWith(Names.named("dataFolder"))
        .toInstance(this.plugin.getDataFolder().toPath());
    this.bind(Logger.class).annotatedWith(PluginLogger.class).toInstance(this.plugin.getLogger());

    this.bind(MiniMessage.class).toInstance(MiniMessage.miniMessage());
    this.bind(MiniMessage.class)
        .annotatedWith(Names.named("colorMiniMessage"))
        .toInstance(
            MiniMessage.builder()
                .tags(
                    TagResolver.builder()
                        .resolvers(StandardTags.color(), StandardTags.decorations())
                        .build())
                .build());
  }

  @Provides
  @Singleton
  public Gson provideGson() {
    return new GsonBuilder()
        .serializeNulls()
        .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
        .enableComplexMapKeySerialization()
        .create();
  }
}
