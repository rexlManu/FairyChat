package de.rexlmanu.fairychat.plugin.permission;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.utility.annotation.PluginLogger;
import java.util.logging.Logger;
import org.bukkit.Server;

public class PermissionModule extends AbstractModule {
  @Provides
  public PermissionProvider providePermissionProvider(
      Server server, Injector injector, @PluginLogger Logger logger) {
    if (server.getPluginManager().isPluginEnabled(Constants.LUCKPERMS_NAME)) {
      return injector.getInstance(LuckPermsPermissionProvider.class);
    }
    logger.warning("No permission provider found, using dummy provider.");
    return new DummyPermissionProvider();
  }
}
