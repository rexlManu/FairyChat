package de.rexlmanu.fairychat.plugin.module;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import de.rexlmanu.fairychat.plugin.permission.DummyPermissionProvider;
import de.rexlmanu.fairychat.plugin.permission.LuckPermsPermissionProvider;
import de.rexlmanu.fairychat.plugin.permission.PermissionProvider;
import de.rexlmanu.fairychat.plugin.utility.annotation.PluginLogger;
import java.util.logging.Logger;
import org.bukkit.Server;

public class PermissionModule extends AbstractModule {
  @Provides
  public PermissionProvider providePermissionProvider(
      Server server, Injector injector, @PluginLogger Logger logger) {
    if (server.getPluginManager().isPluginEnabled("LuckPerms")) {
      return injector.getInstance(LuckPermsPermissionProvider.class);
    }
    logger.warning("No permission provider found, using dummy provider.");
    return new DummyPermissionProvider();
  }
}
