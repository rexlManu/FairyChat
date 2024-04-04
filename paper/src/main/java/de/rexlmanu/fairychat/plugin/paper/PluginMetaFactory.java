package de.rexlmanu.fairychat.plugin.paper;

import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class PluginMetaFactory {
  public AdaptPluginMeta create(JavaPlugin javaPlugin) {
    if (Environment.ENVIRONMENT.isPaper()) {
      return new AdaptPluginMeta(javaPlugin.getPluginMeta().getVersion());
    }

    return new AdaptPluginMeta(javaPlugin.getDescription().getVersion());
  }
}
